import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '../router'
import { clearAuth, getToken } from './auth'

const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 15000
})

request.interceptors.request.use((config) => {
  const token = getToken()
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

request.interceptors.response.use(
  (response) => {
    const result = response.data
    if (result.code !== 200) {
      ElMessage.error(result.message || '请求失败')
      return Promise.reject(new Error(result.message || '请求失败'))
    }
    return result.data
  },
  (error) => {
    const message = error.response?.data?.message || error.message || '请求失败'
    if (error.response?.status === 401 || message.includes('登录')) {
      clearAuth()
      if (router.currentRoute.value.path !== '/login') {
        router.push('/login')
      }
    }
    ElMessage.error(message)
    return Promise.reject(error)
  }
)

export default request
