<template>
  <div class="auth-page">
    <section class="auth-card page-card fade-rise">
      <div class="hero-block register-hero">
        <span class="eyebrow">Start your shared space</span>
        <h1>注册一个只属于你们两个人的甜蜜空间。</h1>
        <p>创建账号后会自动登录，你可以立刻生成绑定码，邀请另一半一起把生活认真收藏起来。</p>
        <div class="hero-badges">
          <span class="stat-chip">Moments</span>
          <span class="stat-chip">Albums</span>
          <span class="stat-chip">Messages</span>
        </div>
      </div>
      <div class="form-shell">
        <div class="form-head">
          <span class="form-kicker">Create account</span>
          <h2>开始建立你们的空间</h2>
        </div>
        <el-form ref="formRef" :model="form" :rules="rules" label-position="top" @submit.prevent>
          <el-form-item label="用户名" prop="username">
            <el-input v-model="form.username" placeholder="4-20位字母、数字或下划线" size="large" />
          </el-form-item>
          <el-form-item label="昵称" prop="nickname">
            <el-input v-model="form.nickname" placeholder="请输入昵称" size="large" />
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input v-model="form.password" show-password placeholder="请输入密码" size="large" />
          </el-form-item>
          <el-button class="full-btn" type="primary" size="large" :loading="submitting" @click="handleRegister">
            注册并登录
          </el-button>
          <div class="auth-footer">
            <span>已经有账号？</span>
            <RouterLink to="/login">去登录</RouterLink>
          </div>
        </el-form>
      </div>
    </section>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter, RouterLink } from 'vue-router'
import { ElMessage } from 'element-plus'
import { register } from '../api/auth'
import { setAuth } from '../utils/auth'

const router = useRouter()
const formRef = ref()
const submitting = ref(false)

const form = reactive({
  username: '',
  nickname: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleRegister = async () => {
  await formRef.value.validate()
  submitting.value = true
  try {
    const data = await register(form)
    setAuth(data)
    ElMessage.success('注册成功')
    router.push('/')
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.auth-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 28px 24px;
}

.auth-card {
  width: min(960px, 100%);
  padding: 34px;
  display: grid;
  grid-template-columns: 1.08fr 0.92fr;
  gap: 24px;
}

.register-hero {
  background:
    radial-gradient(circle at top left, rgba(255, 223, 194, 0.46), transparent 34%),
    linear-gradient(145deg, rgba(255, 247, 238, 0.94), rgba(255, 238, 243, 0.96));
}

.hero-block {
  padding: 32px;
  border-radius: 28px;
  border: 1px solid rgba(140, 92, 103, 0.12);
}

.hero-block h1 {
  margin: 0;
  font-family: var(--font-serif);
  font-size: clamp(2.6rem, 5vw, 4.2rem);
  line-height: 0.96;
  font-weight: 400;
  letter-spacing: -0.04em;
}

.hero-block p {
  max-width: 500px;
  margin: 18px 0 0;
  color: var(--muted);
  line-height: 1.8;
}

.hero-badges {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  margin-top: 26px;
}

.form-shell {
  padding: 28px;
  border-radius: 28px;
  border: 1px solid rgba(140, 92, 103, 0.12);
  background: rgba(255, 255, 255, 0.58);
}

.form-head {
  margin-bottom: 12px;
}

.form-head h2 {
  margin: 6px 0 0;
  font-family: var(--font-serif);
  font-size: 34px;
  font-weight: 400;
}

.form-kicker {
  color: var(--muted);
  font-size: 12px;
  letter-spacing: 0.18em;
  text-transform: uppercase;
}

.full-btn {
  width: 100%;
  margin-top: 12px;
}

.auth-footer {
  margin-top: 18px;
  text-align: center;
  color: var(--muted);
}

.auth-footer a {
  margin-left: 6px;
  color: #b55f79;
  font-weight: 700;
}

@media (max-width: 768px) {
  .auth-card {
    grid-template-columns: 1fr;
    padding: 18px;
  }

  .hero-block h1 {
    font-size: 3rem;
  }

  .hero-block,
  .form-shell {
    padding: 24px;
  }
}
</style>
