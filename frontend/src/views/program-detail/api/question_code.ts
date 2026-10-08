// src/components/api/question.ts
import javaRequest from '@/utils/request'
import type { AxiosResponse } from 'axios'

export interface Request {
  answer: string
  id: number
  submitNum: number
}

function questionCode(query: Request): Promise<AxiosResponse> {
  const { answer, id, submitNum } = query
  // 当前后端使用 @RequestBody String，按原始代码文本发送。
  return javaRequest.post('/question/code', answer, {
    params: { id, submitNum },
    headers: {
      'X-Xh-Env': 'prod',
      'X-Xh-Lane': '',
      'Content-Type': 'text/plain',
    },
  })
}

export { questionCode }
