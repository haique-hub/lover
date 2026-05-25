<template>
  <div class="app-shell">
    <header v-if="showLayout" class="topbar">
      <div class="topbar-left">
        <button type="button" class="brand" @click="goHome">
          <span class="brand-mark">L</span>
          <span class="brand-copy">
            <span class="brand-title">Lovers Space</span>
            <span class="brand-subtitle">Private Memory Archive</span>
          </span>
        </button>
        <div class="layout-caption">For two, and only two.</div>
      </div>
      <nav class="nav-links">
        <button v-if="showBackButton" type="button" class="nav-action nav-back" @click="goBack">返回</button>
        <RouterLink to="/" class="nav-link">首页</RouterLink>
        <RouterLink to="/anniversaries" class="nav-link">纪念日</RouterLink>
        <RouterLink to="/albums" class="nav-link">相册</RouterLink>
        <RouterLink to="/couple" class="nav-link">绑定</RouterLink>
        <RouterLink to="/profile" class="nav-link">我的</RouterLink>
        <button type="button" class="nav-action" @click="logout">退出</button>
      </nav>
    </header>
    <main :class="['main-content', { compact: !showLayout }]">
      <RouterView />
    </main>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter, useRoute, RouterLink, RouterView } from 'vue-router'
import { ElMessage } from 'element-plus'
import { clearAuth, getToken } from './utils/auth'

const router = useRouter()
const route = useRoute()

const showLayout = computed(() => !['/login', '/register'].includes(route.path) && !!getToken())
const showBackButton = computed(() => showLayout.value && route.path !== '/')

const goHome = () => {
  router.push('/')
}

const goBack = () => {
  if (window.history.length > 1) {
    router.back()
    return
  }
  if (route.path.startsWith('/photos/')) {
    router.push('/albums')
    return
  }
  if (route.path.startsWith('/albums/')) {
    router.push('/albums')
    return
  }
  router.push('/')
}

const logout = () => {
  clearAuth()
  ElMessage.success('已退出登录')
  router.push('/login')
}
</script>
