const TOKEN_KEY = 'lovers_token'
const USER_KEY = 'lovers_user'

export function getToken() {
  return localStorage.getItem(TOKEN_KEY) || ''
}

export function setToken(token) {
  localStorage.setItem(TOKEN_KEY, token)
}

export function clearToken() {
  localStorage.removeItem(TOKEN_KEY)
}

export function getUserInfo() {
  const raw = localStorage.getItem(USER_KEY)
  return raw ? JSON.parse(raw) : null
}

export function setUserInfo(userInfo) {
  localStorage.setItem(USER_KEY, JSON.stringify(userInfo))
}

export function clearUserInfo() {
  localStorage.removeItem(USER_KEY)
}

export function setAuth(payload) {
  setToken(payload.token)
  setUserInfo(payload.userInfo)
}

export function clearAuth() {
  clearToken()
  clearUserInfo()
}
