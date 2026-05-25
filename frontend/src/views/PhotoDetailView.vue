<template>
  <div class="photo-detail-page">
    <section class="page-card photo-panel panel-frame fade-rise">
      <div class="detail-toolbar">
        <el-button class="back-button" @click="goBack">返回相册</el-button>
      </div>
      <div class="photo-layout">
        <div class="photo-preview-wrap">
          <img v-if="photo.fileUrl" :src="photo.fileUrl" class="detail-photo" />
        </div>
        <div class="photo-meta">
          <div class="page-header detail-header">
            <div>
              <span class="eyebrow">Photo details</span>
              <h1 class="page-title">{{ photo.description || photo.fileName || '照片详情' }}</h1>
              <p class="page-subtitle">
                上传者：{{ photo.uploaderNickname || '--' }} · 上传时间：{{ formatDateTime(photo.createTime) }}
              </p>
            </div>
            <el-button v-if="canDeletePhoto" type="danger" plain @click="handleDeletePhoto">删除照片</el-button>
          </div>

          <div class="comment-form">
            <div class="form-title">{{ replyTarget ? `回复 ${replyTarget.userNickname}` : '发表评论' }}</div>
            <el-input
              v-model="commentForm.content"
              type="textarea"
              :rows="4"
              maxlength="500"
              show-word-limit
              placeholder="写点温柔的话吧"
            />
            <div class="comment-form-actions">
              <el-button v-if="replyTarget" @click="clearReply">取消回复</el-button>
              <el-button type="primary" :loading="commentSubmitting" @click="submitComment">发送评论</el-button>
            </div>
          </div>

          <div class="comment-section">
            <div class="section-title">评论区</div>
            <el-empty v-if="!comments.length" description="还没有评论，来留下第一句吧" />
            <CommentThread
              v-else
              :comments="comments"
              :current-user-id="currentUser?.id || 0"
              :photo-owner-id="photo.userId || 0"
              @reply="setReplyTarget"
              @delete="handleDeleteComment"
            />
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import CommentThread from '../components/CommentThread.vue'
import { createComment, deleteComment, getPhotoComments } from '../api/comment'
import { deletePhoto, getPhotoDetail } from '../api/photo'
import { getUserInfo } from '../utils/auth'

const route = useRoute()
const router = useRouter()
const currentUser = ref(getUserInfo())
const photoId = computed(() => Number(route.params.id))
const photo = ref({})
const comments = ref([])
const replyTarget = ref(null)
const commentSubmitting = ref(false)
const commentForm = reactive({
  content: ''
})

const formatDateTime = (value) => (value ? value.replace('T', ' ') : '--')

const canDeletePhoto = computed(() => photo.value.userId === currentUser.value?.id)

const loadData = async () => {
  const [photoData, commentList] = await Promise.all([getPhotoDetail(photoId.value), getPhotoComments(photoId.value)])
  photo.value = photoData
  comments.value = commentList
}

const clearReply = () => {
  replyTarget.value = null
}

const setReplyTarget = (comment) => {
  replyTarget.value = comment
}

const submitComment = async () => {
  if (!commentForm.content.trim()) {
    ElMessage.warning('请输入评论内容')
    return
  }
  commentSubmitting.value = true
  try {
    await createComment({
      photoId: photoId.value,
      content: commentForm.content,
      parentId: replyTarget.value?.id || 0,
      replyUserId: replyTarget.value?.userId || null
    })
    commentForm.content = ''
    replyTarget.value = null
    ElMessage.success('评论成功')
    await loadData()
  } finally {
    commentSubmitting.value = false
  }
}

const handleDeleteComment = async (commentId) => {
  await ElMessageBox.confirm('删除后该评论下的回复也会一起删除，确认继续吗？', '提示', { type: 'warning' })
  await deleteComment(commentId)
  ElMessage.success('评论已删除')
  await loadData()
}

const handleDeletePhoto = async () => {
  await ElMessageBox.confirm('确认删除这张照片吗？', '提示', { type: 'warning' })
  await deletePhoto(photoId.value)
  ElMessage.success('照片已删除')
  router.push('/albums')
}

const goBack = () => {
  if (photo.value.albumId) {
    router.push(`/albums/${photo.value.albumId}`)
    return
  }
  router.push('/albums')
}

onMounted(loadData)
</script>

<style scoped>
.detail-toolbar {
  margin-bottom: 18px;
}

.back-button {
  min-width: 118px;
}

.back-button::before {
  content: "←";
  margin-right: 6px;
}

.photo-layout {
  display: grid;
  grid-template-columns: 1.1fr 0.9fr;
  gap: 24px;
}

.photo-preview-wrap {
  border-radius: 28px;
  overflow: hidden;
  border: 1px solid rgba(210, 123, 147, 0.12);
  background: rgba(255, 255, 255, 0.68);
}

.detail-photo {
  width: 100%;
  display: block;
}

.comment-form,
.comment-section {
  padding: 22px;
  border-radius: 24px;
  border: 1px solid rgba(210, 123, 147, 0.12);
  background: rgba(255, 255, 255, 0.62);
}

.comment-form {
  margin-top: 16px;
}

.form-title,
.section-title {
  margin-bottom: 14px;
  font-family: var(--font-serif);
  font-size: 28px;
  line-height: 1.04;
  font-weight: 400;
}

.comment-form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 12px;
}

.comment-section {
  margin-top: 18px;
}

@media (max-width: 768px) {
  .photo-layout {
    grid-template-columns: 1fr;
  }
}
</style>
