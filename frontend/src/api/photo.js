import request from '../utils/request'

export function getPhotoListByAlbum(albumId) {
  return request.get(`/photos/album/${albumId}`)
}

export function getRecentPhotoList() {
  return request.get('/photos/recent')
}

export function getPhotoDetail(photoId) {
  return request.get(`/photos/${photoId}`)
}

export function uploadPhoto(data) {
  return request.post('/photos/upload', data)
}

export function deletePhoto(photoId) {
  return request.delete(`/photos/${photoId}`)
}
