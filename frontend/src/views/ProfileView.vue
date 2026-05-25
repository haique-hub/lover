<template>
  <section class="page-card profile-panel panel-frame fade-rise">
    <div class="page-header">
      <div>
        <span class="eyebrow">Personal profile</span>
        <h1 class="page-title">个人中心</h1>
        <p class="page-subtitle">可以在这里完善昵称、头像、性别和个性签名。</p>
      </div>
    </div>

    <div class="grid-two profile-layout">
      <div class="profile-preview">
        <div class="avatar-box">
          <img v-if="form.avatar" :src="form.avatar" class="preview-avatar" />
          <div v-else class="preview-avatar preview-avatar-fallback">{{ form.nickname?.slice(0, 1) || '爱' }}</div>
        </div>
        <h2>{{ form.nickname || '--' }}</h2>
        <p class="muted">{{ form.signature || '这个人很懒，还没写个性签名。' }}</p>
      </div>

      <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="form.nickname" maxlength="20" show-word-limit />
        </el-form-item>
        <el-form-item label="头像上传">
          <el-upload
            ref="avatarUploadRef"
            class="avatar-upload"
            :show-file-list="false"
            :auto-upload="false"
            :limit="1"
            accept="image/jpeg,image/png,image/gif,image/webp"
            :on-change="handleAvatarChange"
            :before-upload="() => false"
          >
            <el-button :loading="avatarUploading">上传头像</el-button>
          </el-upload>
          <p class="upload-tip">支持 jpg / jpeg / png / gif / webp，单张不超过 10MB</p>
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-select v-model="form.gender" style="width: 100%">
            <el-option label="保密" :value="0" />
            <el-option label="男" :value="1" />
            <el-option label="女" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="个性签名" prop="signature">
          <el-input v-model="form.signature" type="textarea" :rows="4" maxlength="255" show-word-limit />
        </el-form-item>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">保存资料</el-button>
      </el-form>
    </div>
  </section>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getCurrentUser } from '../api/auth'
import { updateProfile, uploadAvatar } from '../api/user'
import { setUserInfo } from '../utils/auth'

const formRef = ref()
const avatarUploadRef = ref()
const submitting = ref(false)
const avatarUploading = ref(false)
const form = reactive({
  nickname: '',
  avatar: '',
  gender: 0,
  signature: ''
})

const rules = {
  nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }]
}

const loadData = async () => {
  const data = await getCurrentUser()
  form.nickname = data.nickname
  form.avatar = data.avatar || ''
  form.gender = data.gender ?? 0
  form.signature = data.signature || ''
}

const handleAvatarChange = async (uploadFile) => {
  const rawFile = uploadFile.raw
  if (!rawFile) {
    return
  }
  avatarUploading.value = true
  try {
    const formData = new FormData()
    formData.append('file', rawFile)
    const data = await uploadAvatar(formData)
    form.avatar = data.avatar || ''
    setUserInfo(data)
    ElMessage.success('头像已更新')
  } catch (error) {
    console.error('Avatar upload failed', error)
  } finally {
    avatarUploadRef.value?.clearFiles()
    avatarUploading.value = false
  }
}

const handleSubmit = async () => {
  await formRef.value.validate()
  submitting.value = true
  try {
    const data = await updateProfile({
      nickname: form.nickname,
      gender: form.gender,
      signature: form.signature
    })
    form.avatar = data.avatar || form.avatar
    setUserInfo(data)
    ElMessage.success('资料已更新')
  } finally {
    submitting.value = false
  }
}

onMounted(loadData)
</script>

<style scoped>
.profile-layout {
  align-items: start;
}

.profile-preview {
  padding: 26px;
  border-radius: 28px;
  border: 1px solid rgba(210, 123, 147, 0.14);
  background: rgba(255, 246, 248, 0.72);
  text-align: center;
}

.avatar-box {
  display: flex;
  justify-content: center;
}

.upload-tip {
  margin: 10px 0 0;
  color: var(--muted);
  font-size: 13px;
}

.preview-avatar {
  width: 120px;
  height: 120px;
  object-fit: cover;
  border-radius: 50%;
}

.preview-avatar-fallback {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(145deg, #ffd7e1, #fff0e4);
  color: #aa5d73;
  font-family: var(--font-serif);
  font-size: 42px;
  font-weight: 400;
}

.profile-preview h2 {
  margin: 18px 0 10px;
  font-family: var(--font-serif);
  font-size: 40px;
  line-height: 0.98;
  font-weight: 400;
}
</style>
