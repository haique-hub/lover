<template>
  <div class="album-detail-page">
    <section class="page-card panel-frame fade-rise">
      <div class="detail-toolbar">
        <el-button class="back-button" @click="goBack">返回相册列表</el-button>
      </div>
      <div class="page-header">
        <div>
          <span class="eyebrow">Album details</span>
          <h1 class="page-title">{{ album.name || '相册详情' }}</h1>
          <p class="page-subtitle">
            {{ album.description || '这个相册还没有描述。' }} 创建者：{{ album.ownerNickname || '--' }}
          </p>
        </div>
        <el-button type="primary" @click="uploadDialogVisible = true">上传照片</el-button>
      </div>

      <el-empty v-if="!photos.length" description="还没有照片，先上传第一张吧" />

      <div v-else class="photo-grid">
        <article v-for="photo in photos" :key="photo.id" class="photo-card">
          <img :src="photo.fileUrl" class="photo-image preview" @click="goPhoto(photo.id)" />
          <div class="photo-card-body">
            <div class="photo-card-label">甜蜜片段</div>
            <h3>{{ photo.description || photo.fileName }}</h3>
            <p class="muted">上传者：{{ photo.uploaderNickname }}</p>
            <div class="photo-actions">
              <el-button link type="primary" @click="goPhoto(photo.id)">查看详情</el-button>
              <el-button
                v-if="canDeletePhoto(photo)"
                link
                type="danger"
                @click="handleDeletePhoto(photo.id)"
              >
                删除
              </el-button>
            </div>
          </div>
        </article>
      </div>
    </section>

    <el-dialog v-model="uploadDialogVisible" title="上传照片" width="560px" @closed="resetUploadForm">
      <el-form ref="uploadFormRef" :model="uploadForm" :rules="uploadRules" label-position="top">
        <el-form-item label="照片文件" prop="file">
          <el-upload
            ref="uploadRef"
            class="upload-box"
            drag
            :auto-upload="false"
            :limit="1"
            :file-list="fileList"
            :on-change="handleFileChange"
            :on-remove="handleFileRemove"
            :on-exceed="handleFileExceed"
            :before-upload="() => false"
          >
            <div class="el-upload__text">拖拽图片到这里，或点击上传</div>
            <template #tip>
              <div class="el-upload__tip">支持 jpg / jpeg / png / gif / webp，单张不超过 10MB</div>
            </template>
          </el-upload>
        </el-form-item>
        <el-form-item label="照片描述" prop="description">
          <el-input v-model="uploadForm.description" maxlength="255" show-word-limit />
        </el-form-item>
        <el-form-item label="排序值" prop="sortOrder">
          <el-input-number v-model="uploadForm.sortOrder" :min="0" :max="9999" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="handleCancelUpload">取消</el-button>
        <el-button type="primary" :loading="uploading" @click="submitUpload">确认上传</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAlbumDetail } from '../api/album'
import { deletePhoto, getPhotoListByAlbum, uploadPhoto } from '../api/photo'
import { getUserInfo } from '../utils/auth'

const route = useRoute()
const router = useRouter()
const currentUser = ref(getUserInfo())
const albumId = computed(() => Number(route.params.id))
const album = ref({})
const photos = ref([])
const uploadDialogVisible = ref(false)
const uploading = ref(false)
const uploadFormRef = ref()
const uploadRef = ref()
const fileList = ref([])
const uploadForm = reactive({
  file: null,
  description: '',
  sortOrder: 0
})

const uploadRules = {
  file: [{ required: true, message: '请选择照片文件', trigger: 'change' }]
}

const loadData = async () => {
  const [albumData, photoList] = await Promise.all([getAlbumDetail(albumId.value), getPhotoListByAlbum(albumId.value)])
  album.value = albumData
  photos.value = photoList
}

const canDeletePhoto = (photo) => photo.userId === currentUser.value?.id || album.value.userId === currentUser.value?.id

const handleFileChange = (uploadFile, uploadFiles) => {
  uploadForm.file = uploadFile.raw
  fileList.value = uploadFiles.slice(-1)
  uploadFormRef.value?.validateField('file').catch(() => {})
}

const handleFileRemove = () => {
  uploadForm.file = null
  fileList.value = []
  uploadFormRef.value?.validateField('file').catch(() => {})
}

const handleFileExceed = (files) => {
  const latestFile = files[files.length - 1]
  if (!latestFile) {
    return
  }
  uploadForm.file = latestFile
  fileList.value = [{
    name: latestFile.name,
    url: '',
    raw: latestFile
  }]
}

const resetUploadForm = () => {
  uploadForm.file = null
  fileList.value = []
  uploadRef.value?.clearFiles()
  uploadForm.description = ''
  uploadForm.sortOrder = 0
  uploadFormRef.value?.clearValidate()
}

const handleCancelUpload = () => {
  uploadDialogVisible.value = false
  resetUploadForm()
}

const submitUpload = async () => {
  await uploadFormRef.value.validate()
  uploading.value = true
  try {
    const formData = new FormData()
    formData.append('albumId', albumId.value)
    formData.append('description', uploadForm.description)
    formData.append('sortOrder', uploadForm.sortOrder)
    formData.append('file', uploadForm.file)
    await uploadPhoto(formData)
    ElMessage.success('照片上传成功')
    uploadDialogVisible.value = false
    resetUploadForm()
    await loadData()
  } catch (error) {
    console.error('Photo upload failed', error)
  } finally {
    uploading.value = false
  }
}

const goPhoto = (id) => {
  router.push(`/photos/${id}`)
}

const goBack = () => {
  router.push('/albums')
}

const handleDeletePhoto = async (photoId) => {
  await ElMessageBox.confirm('确认删除这张照片吗？', '提示', { type: 'warning' })
  await deletePhoto(photoId)
  ElMessage.success('照片已删除')
  await loadData()
}

onMounted(loadData)
</script>

<style scoped>
.detail-toolbar {
  margin-bottom: 18px;
}

.back-button {
  min-width: 132px;
}

.back-button::before {
  content: "←";
  margin-right: 6px;
}

.photo-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 20px;
}

.photo-card {
  overflow: hidden;
  border-radius: 26px;
  border: 1px solid rgba(210, 123, 147, 0.14);
  background: rgba(255, 255, 255, 0.58);
  transition:
    transform 220ms ease,
    box-shadow 220ms ease,
    border-color 220ms ease;
}

.photo-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 20px 40px rgba(145, 88, 103, 0.08);
  border-color: rgba(210, 123, 147, 0.22);
}

.preview {
  aspect-ratio: 1 / 1;
  cursor: pointer;
}

.photo-card-body {
  padding: 16px 18px 20px;
}

.photo-card-label {
  margin-bottom: 10px;
  color: var(--muted);
  font-size: 11px;
  letter-spacing: 0.16em;
  text-transform: uppercase;
}

.photo-card-body h3 {
  margin: 0 0 10px;
  font-family: var(--font-serif);
  font-size: 28px;
  line-height: 1.04;
  font-weight: 400;
}

.photo-actions {
  display: flex;
  gap: 10px;
  margin-top: 14px;
  flex-wrap: wrap;
}

@media (max-width: 768px) {
  .photo-grid {
    grid-template-columns: 1fr;
  }
}
</style>
