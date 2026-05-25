<template>
  <div class="auth-page">
    <section class="auth-card page-card fade-rise">
      <div class="hero-block">
        <span class="eyebrow">For private memories</span>
        <h1>把喜欢、想念和纪念，留在只属于两个人的地方。</h1>
        <p>登录后就可以开始绑定情侣、创建相册、上传照片，把每一次互动都认真保存下来。</p>
        <div class="hero-badges">
          <span class="stat-chip">Private archive</span>
          <span class="stat-chip">Sweet and quiet</span>
        </div>
      </div>
      <div class="form-shell">
        <div class="form-head">
          <span class="form-kicker">Sign in</span>
          <h2>欢迎回来</h2>
        </div>
        <el-form ref="formRef" :model="form" :rules="rules" label-position="top" @submit.prevent>
          <el-form-item label="用户名" prop="username">
            <el-input v-model="form.username" placeholder="请输入用户名" size="large" />
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input v-model="form.password" show-password placeholder="请输入密码" size="large" />
          </el-form-item>
          <el-button class="full-btn" type="primary" size="large" :loading="submitting" @click="handleLogin">
            登录
          </el-button>
          <div class="auth-footer">
            <span>还没有账号？</span>
            <RouterLink to="/register">去注册</RouterLink>
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
import { login } from '../api/auth'
import { setAuth } from '../utils/auth'

const router = useRouter()
const formRef = ref()
const submitting = ref(false)

const form = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  await formRef.value.validate()
  submitting.value = true
  try {
    const data = await login(form)
    setAuth(data)
    ElMessage.success('登录成功')
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

.hero-block {
  padding: 32px;
  border-radius: 28px;
  border: 1px solid rgba(140, 92, 103, 0.12);
  background:
    radial-gradient(circle at top left, rgba(255, 203, 218, 0.42), transparent 34%),
    linear-gradient(145deg, rgba(255, 241, 243, 0.92), rgba(255, 247, 238, 0.96));
}

.hero-block h1 {
  margin: 0;
  font-family: var(--font-serif);
  font-size: clamp(2.8rem, 5vw, 4.5rem);
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
