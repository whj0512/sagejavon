# 智能问答流式请求排障与部署

`usage=None` 表示该片段没有 token 统计，通常不是模型失败。只有非空
usage 才记录 INFO。接口按原有协议返回正文文本，前端通过 fetch reader
读取；不要给正文添加 SSE 心跳，否则现有前端会把心跳显示为答案。

## 等待边界

可在 algorithm 的 `.env` 中配置（无需修改 API key）：

```dotenv
LLM_CONNECT_TIMEOUT_SECONDS=10
LLM_READ_TIMEOUT_SECONDS=60
LLM_FIRST_ANSWER_TIMEOUT_SECONDS=45
LLM_STREAM_MAX_SECONDS=180
```

连接超时为 10 秒，每次网络读取的无数据等待上限为 60 秒。
接口等待首个正文的独立上限为 45 秒，超过后返回带中文提示和 CORS 的
HTTP 502（retcode=-30001），不继续占用请求线程等到代理产生 HTML 504。
首正文读取转移到后台线程，每个进程最多 4 个正在启动的模型调用；超时后
仍未退出的调用继续占用该限额，满额时新请求返回 503。迟到正文会被丢弃并
关闭上游流，不保存为成功回答。Python 不能强制停止阻塞线程；底层 HTTP
仍依靠读取超时退出。该上限从 check_smart_query 缓存检查之后开始。
OpenAI/DeepSeek/Ollama 关闭 SDK 自动重试。固定版本 zhipuai 2.0.1
没有实际自动重试循环，同样设置显式 HTTP 超时。SparkAI 使用独立
WebSocket 调用，不适用这些 HTTP 客户端超时。

180 秒限制在模型调用返回、每个解析出的 chunk 到达时检查，覆盖持续输出
reasoning/metadata 却没有正文的情况；它不是整个请求的硬性截止时间。
阻塞中的网络读取由 SDK 的读取超时结束，模型调用之前的本地缓存/语言检测
以及回答后的数据库写入也不属于该 HTTP 超时。

启动失败（包括首个正文之前的超时）返回带 CORS 的 HTTP 502。
已发送正文后发生失败会中断响应并记录异常，不把不完整回答保存为成功记录。
正常完成、异常和客户端关闭响应时均关闭上游 HTTP 流。

## Gunicorn 与 Nginx

在服务器的 algorithm 目录，以现有服务账号及 Python 环境运行：

```bash
gunicorn -c gunicorn_config.py rag_gpt_app:app
```

配置使用 3 个 gthread worker，每个 4 个线程。线程 worker 可以在模型网络
等待期间继续向 master 发送心跳；`timeout=120` 是 worker 健康检测，
不是单个请求的时限。参考 [Gunicorn worker 设计](https://docs.gunicorn.org/en/stable/design.html)。
部署后需重启现有 systemd 服务；确认 ExecStart 使用该配置，且没有通过
命令行覆盖 worker 类型。仓库配置监听 7000，线上实际监听及代理目标应保持一致。

### 当前已提供的 7100 部署命令

当前线上启动参数为 `--workers 1 --bind 127.0.0.1:7100 --timeout 300`，
未显式指定 `-c gunicorn_config.py` 或线程 worker。除非默认的
`gunicorn.conf.py` 或 `GUNICORN_CMD_ARGS` 另有设置，它使用一个 sync worker，
同一时间只处理一个请求，慢模型请求会让后续请求排队。Gunicorn 不会自动
加载名为 `gunicorn_config.py` 的文件。

在现有 systemd unit 的 ExecStart 中使用以下单行命令，保持已有
WorkingDirectory（应能找到 rag_gpt_app.py 和 gunicorn_config.py）：

```ini
ExecStart=/root/miniconda3/envs/sagejavon/bin/python3.10 /root/miniconda3/envs/sagejavon/bin/gunicorn -c gunicorn_config.py --workers 1 --worker-class gthread --threads 4 --bind 127.0.0.1:7100 --timeout 300 rag_gpt_app:app
```

单 worker 数量和监听地址沿用现状，新增线程并发与显式配置加载；命令行
参数覆盖配置文件中的 workers、bind、timeout。部署应用修复后再重启，
否则仅修改启动参数不会加入首正文 45 秒等待上限。

```bash
sudo systemctl daemon-reload
sudo systemctl restart sagejavon-v2-algorithm
sudo journalctl -u sagejavon-v2-algorithm -n 40 --no-pager
```

服务名以实际 unit 为准。启动日志应显示 `Using worker: gthread` 和监听
`127.0.0.1:7100`。若使用 systemctl edit 添加 drop-in，必须先用空的
`ExecStart=` 清除旧值，再填写新命令。

用户提供的 Nginx `proxy_buffering off`、`proxy_read_timeout 360s`、
`proxy_send_timeout 360s` 可以保留，但必须作用于实际 `/open_kf_api/` 请求，
且 upstream 为 `127.0.0.1:7100`。无需继续延长超时。
若新应用确实在 45 秒返回 502，仍出现 504，则重点检查错误 location、外层
网关、请求排队或旧进程；使用同一请求的直连/代理耗时和日志判断。

在 **实际生效** 的模型接口 Nginx location 中设置；有多层代理时逐层检查：

```nginx
proxy_buffering off;
proxy_cache off;
gzip off;
proxy_read_timeout 240s;
proxy_send_timeout 240s;
```

修改后执行 `sudo nginx -t`，通过后再 reload。保留现有 proxy_pass 和路径
重写规则，不要直接替换整个站点配置。Flask 还会发送 `X-Accel-Buffering: no`。

当前前端 `rag_url=''`，发送的是同源 `/open_kf_api/queries/smart_query_stream`，
因此仅修改 `/model/` location 不会作用于这个请求。可参考相邻的
`nginx-smart-query.conf.example`，把 `/open_kf_api/` location 加到实际站点的
server 块中，避免落到通用 `/` 或前端代理。若已有该 location，请修改现有块，
不要重复添加。相邻配置示例已按本次提供的线上监听使用 7100；仓库默认
Gunicorn 监听仍为 7000，线上通过命令行覆盖。其他部署需核对实际监听。

504 表示网关等待上游超时；并不等同于 token usage 警告。
Nginx 默认 `proxy_read_timeout` 为 60 秒，它限制两次读取间的静默时间，
而非整个回答的时长。模型可能持续发送 reasoning/metadata，SDK 的读取超时
没有触发，但接口尚未发送首正文，网关仍然看不到响应。参考
[Nginx 官方文档](https://nginx.org/en/docs/http/ngx_http_proxy_module.html#proxy_read_timeout)。
首正文上限应小于最外层网关读取超时，并留出排队/调度余量；45 秒针对
常见的 60 秒网关，若外层网关更短，需要一起调整。应用改动不能修复网关
连错端口、请求排队或尚未部署新代码等情况。

## 线上验证

先查看实际监听、Gunicorn 启动参数、systemd ExecStart 和 `sudo nginx -T`。
对照同一问题的请求开始、首个正文耗时和完成日志，并搜索
`WORKER TIMEOUT`、`Model stream interrupted`、`Unable to start model stream`。

```bash
sudo ss -lntp | grep -E ':7000|:7100'
sudo systemctl cat sagejavon-v2-algorithm
sudo journalctl -u sagejavon-v2-algorithm --since '10 minutes ago' --no-pager
sudo nginx -T 2>&1 | grep -n -A25 -B5 'open_kf_api\|proxy_read_timeout'
```

服务名以实际 systemd unit 为准；宝塔环境请使用实际运行的 Nginx 二进制
进行 `-T`、`-t` 和 reload。error.log 中 `while reading response header from upstream`
表示首响应等待超时，`while connecting to upstream` 则检查监听/连接路径。

以下示例假定实际监听 7000；若线上是 7100，请使用实际端口。

```bash
curl -N -i --max-time 240 \
  -H 'Content-Type: application/json' \
  -d '{"query":"解释面向对象","user_id":"stream-smoke-test"}' \
  http://127.0.0.1:7000/open_kf_api/queries/smart_query_stream
```

再对前端实际使用的完整代理 URL 运行相同 POST。该请求会调用真实模型并
保存测试用户的问答历史。直接请求及时出字而代理请求延后，排查代理缓冲；
直连也延后则结合首正文耗时和上游异常排查模型。GET 405、OPTIONS 成功或
健康检查成功均不能证明问答链路可用。
