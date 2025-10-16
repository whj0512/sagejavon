// src/utils/request.ts
import axios from 'axios'
// config.ts
export const BASE_URL = 'http://117.72.59.61/:8080'
const assistantType = localStorage.getItem('assistantType') || 'java'

const dynamicBaseURL =
  assistantType === 'python' ? `${BASE_URL}/python` : BASE_URL

const service = axios.create({
  baseURL: dynamicBaseURL,
  timeout: 300000,
  headers: {
    'Content-Type': 'application/json',
  },
})

service.interceptors.request.use((config) => {
  config.headers['token'] = localStorage.getItem('user-token') || ''
  return config
})

export default service
