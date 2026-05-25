import request from '../utils/request'

export function getAlbumList() {
  return request.get('/albums')
}

export function getAlbumDetail(id) {
  return request.get(`/albums/${id}`)
}

export function createAlbum(data) {
  return request.post('/albums', data)
}

export function updateAlbum(id, data) {
  return request.put(`/albums/${id}`, data)
}

export function deleteAlbum(id) {
  return request.delete(`/albums/${id}`)
}
