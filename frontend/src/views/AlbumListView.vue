<template>
  <div class="album-list-page">
    <section class="page-card panel-frame fade-rise">
      <div class="page-header">
        <div>
          <span class="eyebrow">Shared albums</span>
          <h1 class="page-title">相册管理</h1>
          <p class="page-subtitle">你和另一半都能看到这里的相册。每一组照片，都是一段被好好保存下来的日常。</p>
        </div>
        <el-button type="primary" @click="openCreateDialog">新建相册</el-button>
      </div>

      <el-empty v-if="!albums.length" description="还没有相册，点击右上角开始创建" />

      <div v-else class="grid-three">
        <article v-for="album in albums" :key="album.id" class="album-card">
          <img v-if="album.coverUrl" :src="album.coverUrl" class="cover-image album-cover" @click="goDetail(album.id)" />
          <div v-else class="album-cover album-cover-fallback" @click="goDetail(album.id)">Love</div>
          <div class="album-card-body">
            <div class="album-meta-row">
              <span>{{ album.ownerNickname }}</span>
              <span>{{ album.createTime ? album.createTime.replace('T', ' ').slice(0, 10) : 'Recently' }}</span>
            </div>
            <h3>{{ album.name }}</h3>
            <p class="muted">{{ album.description || '这个相册还没有描述' }}</p>
            <div class="album-actions">
              <el-button link type="primary" @click="goDetail(album.id)">查看</el-button>
              <template v-if="album.userId === currentUser?.id">
                <el-button link @click="openEditDialog(album)">编辑</el-button>
                <el-button link type="danger" @click="handleDelete(album.id)">删除</el-button>
              </template>
            </div>
          </div>
        </article>
      </div>
    </section>

    <el-dialog v-model="dialogVisible" :title="editingAlbumId ? '编辑相册' : '新建相册'" width="520px">
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
        <el-form-item label="相册名称" prop="name">
          <el-input v-model="form.name" maxlength="100" show-word-limit />
        </el-form-item>
        <el-form-item label="相册描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="4" maxlength="255" show-word-limit />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitForm">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { createAlbum, deleteAlbum, getAlbumList, updateAlbum } from '../api/album'
import { getUserInfo } from '../utils/auth'

const router = useRouter()
const currentUser = ref(getUserInfo())
const albums = ref([])
const dialogVisible = ref(false)
const editingAlbumId = ref(null)
const submitting = ref(false)
const formRef = ref()
const form = reactive({
  name: '',
  description: ''
})

const rules = {
  name: [{ required: true, message: '请输入相册名称', trigger: 'blur' }]
}

const resetForm = () => {
  form.name = ''
  form.description = ''
  editingAlbumId.value = null
}

const loadAlbums = async () => {
  albums.value = await getAlbumList()
}

const goDetail = (id) => {
  router.push(`/albums/${id}`)
}

const openCreateDialog = () => {
  resetForm()
  dialogVisible.value = true
}

const openEditDialog = (album) => {
  editingAlbumId.value = album.id
  form.name = album.name
  form.description = album.description || ''
  dialogVisible.value = true
}

const submitForm = async () => {
  await formRef.value.validate()
  submitting.value = true
  try {
    if (editingAlbumId.value) {
      await updateAlbum(editingAlbumId.value, form)
      ElMessage.success('相册已更新')
    } else {
      await createAlbum(form)
      ElMessage.success('相册已创建')
    }
    dialogVisible.value = false
    await loadAlbums()
  } finally {
    submitting.value = false
  }
}

const handleDelete = async (id) => {
  await ElMessageBox.confirm('删除相册会同时删除下面的照片和评论，确认继续吗？', '提示', { type: 'warning' })
  await deleteAlbum(id)
  ElMessage.success('相册已删除')
  await loadAlbums()
}

onMounted(loadAlbums)
</script>

<style scoped>
.album-list-page :deep(.el-empty) {
  padding: 44px 0 18px;
}

.album-card {
  overflow: hidden;
  border-radius: 28px;
  border: 1px solid rgba(210, 123, 147, 0.14);
  background: rgba(255, 255, 255, 0.58);
  transition:
    transform 220ms ease,
    box-shadow 220ms ease,
    border-color 220ms ease;
}

.album-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 20px 40px rgba(145, 88, 103, 0.1);
  border-color: rgba(210, 123, 147, 0.22);
}

.album-cover {
  height: 260px;
  cursor: pointer;
}

.album-cover-fallback {
  display: flex;
  align-items: end;
  justify-content: flex-start;
  height: 260px;
  padding: 22px;
  cursor: pointer;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0) 0%, rgba(150, 93, 108, 0.18) 100%),
    linear-gradient(145deg, #ffd8e1, #fff1e7 58%, #f8c3d1);
  color: #fffdfa;
  font-family: var(--font-serif);
  font-size: 40px;
  font-weight: 400;
}

.album-card-body {
  padding: 18px 18px 22px;
}

.album-meta-row {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 12px;
  color: var(--muted);
  font-size: 11px;
  letter-spacing: 0.14em;
  text-transform: uppercase;
}

.album-card-body h3 {
  margin: 0 0 10px;
  font-family: var(--font-serif);
  font-size: 30px;
  line-height: 1.02;
  font-weight: 400;
}

.album-actions {
  display: flex;
  gap: 10px;
  margin-top: 14px;
  flex-wrap: wrap;
}

@media (max-width: 768px) {
  .album-cover,
  .album-cover-fallback {
    height: 220px;
  }
}
</style>
