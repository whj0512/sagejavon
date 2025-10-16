import axios from 'axios'
// Python 后端服务实例
const pythonRequest = axios.create({
  baseURL: 'http://117.72.59.61/model',
  timeout: 150000,
  headers: {
    'Content-Type': 'application/json',
    Authorization: 'Bearer <your-token>', // 可替换为动态 header 逻辑
  },
})

pythonRequest.interceptors.request.use((config) => {
  // 可选：注入 user_id、动态 token 等
  return config
})

export default pythonRequest
