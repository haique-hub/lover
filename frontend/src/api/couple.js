import request from '../utils/request'

export function getCoupleInfo() {
  return request.get('/couple/info')
}

export function generateBindCode() {
  return request.post('/couple/code')
}

export function bindCouple(data) {
  return request.post('/couple/bind', data)
}

export function unbindCouple() {
  return request.post('/couple/unbind')
}
