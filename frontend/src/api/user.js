import request from '../utils/request'

export function updateProfile(data) {
  return request.put('/user/profile', data)
}

export function uploadAvatar(data) {
  return request.post('/user/avatar', data)
}
