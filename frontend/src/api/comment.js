import request from '../utils/request'

export function getPhotoComments(photoId) {
  return request.get(`/comments/photo/${photoId}`)
}

export function createComment(data) {
  return request.post('/comments', data)
}

export function deleteComment(commentId) {
  return request.delete(`/comments/${commentId}`)
}
