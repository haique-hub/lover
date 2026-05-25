import request from '../utils/request'

export function getAnniversaryList() {
  return request.get('/anniversaries')
}

export function getAnniversarySummary() {
  return request.get('/anniversaries/summary')
}

export function getAnniversaryDetail(id) {
  return request.get(`/anniversaries/${id}`)
}

export function createAnniversary(data) {
  return request.post('/anniversaries', data)
}

export function updateAnniversary(id, data) {
  return request.put(`/anniversaries/${id}`, data)
}

export function deleteAnniversary(id) {
  return request.delete(`/anniversaries/${id}`)
}
