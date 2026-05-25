import { createRouter, createWebHistory } from 'vue-router'
import { getToken } from '../utils/auth'

const routes = [
  {
    path: '/login',
    name: 'login',
    component: () => import('../views/LoginView.vue'),
    meta: { guestOnly: true }
  },
  {
    path: '/register',
    name: 'register',
    component: () => import('../views/RegisterView.vue'),
    meta: { guestOnly: true }
  },
  {
    path: '/',
    name: 'home',
    component: () => import('../views/HomeView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/couple',
    name: 'couple',
    component: () => import('../views/CoupleView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/anniversaries',
    name: 'anniversaries',
    component: () => import('../views/AnniversaryView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/albums',
    name: 'albums',
    component: () => import('../views/AlbumListView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/albums/:id',
    name: 'albumDetail',
    component: () => import('../views/AlbumDetailView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/photos/:id',
    name: 'photoDetail',
    component: () => import('../views/PhotoDetailView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/profile',
    name: 'profile',
    component: () => import('../views/ProfileView.vue'),
    meta: { requiresAuth: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = getToken()
  if (to.meta.requiresAuth && !token) {
    next('/login')
    return
  }
  if (to.meta.guestOnly && token) {
    next('/')
    return
  }
  next()
})

export default router
