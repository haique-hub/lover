<template>
  <div class="home-page">
    <section class="page-card hero-panel fade-rise">
      <div class="hero-grid panel-frame">
        <div class="hero-copy">
          <span class="eyebrow">For your shared days</span>
          <div class="hero-title-block">
            <div class="hero-greeting">欢迎回来，</div>
            <h1 class="hero-title">
              <span class="hero-name">{{ currentUser?.nickname || '亲爱的' }}</span>
              <span class="hero-ending">。</span>
            </h1>
          </div>
          <p class="page-subtitle hero-subtitle">
            把日常、纪念和一句句想说的话，轻轻地放进同一个空间里。这里不需要热闹，只需要你们彼此看得见。
          </p>
          <div class="hero-actions">
            <RouterLink to="/albums">
              <el-button type="primary" size="large">进入相册</el-button>
            </RouterLink>
            <RouterLink to="/couple">
              <el-button size="large">查看绑定</el-button>
            </RouterLink>
          </div>
        </div>
        <div class="hero-aside">
          <div class="aside-card">
            <span class="aside-label">当前状态</span>
            <strong>{{ coupleInfo.bound ? '已绑定' : '等待建立连接' }}</strong>
            <p>{{ coupleInfo.bound ? `你和 ${coupleInfo.partner?.nickname || '对方'} 正在共享这个甜蜜空间。` : '生成绑定码后，对方就能进入同一个温柔角落。' }}</p>
          </div>
          <div class="hero-note">
            <span class="stat-chip">只属于两个人</span>
            <span class="stat-chip">甜蜜纪念</span>
          </div>
        </div>
      </div>
    </section>

    <section class="grid-two home-panels fade-rise">
      <div class="page-card panel-frame status-panel">
        <div class="page-header">
          <div>
            <span class="eyebrow">Relationship</span>
            <h2 class="section-title">情侣状态</h2>
            <p class="page-subtitle">查看当前绑定信息，或继续邀请对方加入这个空间。</p>
          </div>
        </div>
        <template v-if="coupleInfo.bound">
          <div class="statement-card">
            <div class="statement-label">已绑定对象</div>
            <div class="statement-number">{{ coupleInfo.partner?.nickname }}</div>
            <div class="statement-meta">绑定时间 · {{ formatDateTime(coupleInfo.bindTime) }}</div>
            <RouterLink to="/couple">
              <el-button link type="primary">查看详情</el-button>
            </RouterLink>
          </div>
        </template>
        <template v-else>
          <div class="statement-card">
            <div class="statement-label">尚未绑定</div>
            <div class="statement-number statement-number--small">等待输入绑定码</div>
            <p class="section-note">你可以先生成一枚绑定码，或者直接输入对方给你的 8 位邀请码，把空间正式连在一起。</p>
            <div class="code-row" v-if="coupleInfo.bindCode">
              <span>当前绑定码</span>
              <strong>{{ coupleInfo.bindCode }}</strong>
            </div>
            <RouterLink to="/couple">
              <el-button type="primary">去绑定</el-button>
            </RouterLink>
          </div>
        </template>
      </div>

      <div class="page-card panel-frame overview-panel">
        <div class="page-header">
          <div>
            <span class="eyebrow">Overview</span>
            <h2 class="section-title">空间总览</h2>
            <p class="page-subtitle">眼下已经沉淀下来的内容，很适合继续往里慢慢添。</p>
          </div>
        </div>
        <div class="overview-grid">
          <div class="metric-card metric-card--large">
            <span>相册数量</span>
            <strong>{{ albums.length }}</strong>
            <p>每一个相册都可以继续扩展、上传和评论。</p>
          </div>
          <div class="metric-card">
            <span>纪念日数量</span>
            <strong>{{ anniversarySummary.totalCount || 0 }}</strong>
            <p>{{ anniversarySummary.nextAnniversary ? `${anniversarySummary.nextAnniversary.title}${formatDaysText(anniversarySummary.nextAnniversary.daysUntilNext)}` : '先记下一个重要日子，让空间开始有期待。' }}</p>
          </div>
        </div>
      </div>
    </section>

    <section class="grid-two home-panels fade-rise">
      <div class="page-card panel-frame memory-panel">
        <div class="page-header">
          <div>
            <span class="eyebrow">Upcoming date</span>
            <h2 class="section-title">最近纪念日</h2>
            <p class="page-subtitle">重要的那一天不用等到临近才想起，提前一点点准备，会更有心意。</p>
          </div>
          <RouterLink to="/anniversaries">
            <el-button type="primary">管理纪念日</el-button>
          </RouterLink>
        </div>
        <template v-if="anniversarySummary.nextAnniversary">
          <div class="memory-highlight">
            <div class="memory-copy">
              <span class="memory-label">{{ getAnniversaryTypeLabel(anniversarySummary.nextAnniversary.type) }}</span>
              <h3>{{ anniversarySummary.nextAnniversary.title }}</h3>
              <p>{{ formatAnniversaryDate(anniversarySummary.nextAnniversary.nextOccurrenceDate) }} · {{ formatDaysText(anniversarySummary.nextAnniversary.daysUntilNext) }}</p>
            </div>
            <div class="memory-pill">
              <strong>{{ anniversarySummary.nextAnniversary.today ? '今天' : anniversarySummary.nextAnniversary.daysUntilNext }}</strong>
              <span>{{ anniversarySummary.nextAnniversary.today ? '记得留下新回忆' : '天后到来' }}</span>
            </div>
          </div>
          <div class="memory-list" v-if="anniversarySummary.upcomingAnniversaries?.length">
            <button
              v-for="item in anniversarySummary.upcomingAnniversaries"
              :key="item.id"
              type="button"
              class="memory-item"
              @click="goAnniversaries"
            >
              <span>{{ item.title }}</span>
              <strong>{{ formatDaysText(item.daysUntilNext) }}</strong>
            </button>
          </div>
        </template>
        <template v-else>
          <div class="statement-card">
            <div class="statement-label">还没有纪念日</div>
            <div class="statement-number statement-number--small">把第一天记下来吧</div>
            <p class="section-note">恋爱纪念日、生日、第一次见面，任何一个重要节点都值得被认真留下。</p>
            <RouterLink to="/anniversaries">
              <el-button type="primary">去添加</el-button>
            </RouterLink>
          </div>
        </template>
      </div>

      <div class="page-card panel-frame overview-panel">
        <div class="page-header">
          <div>
            <span class="eyebrow">Gentle rhythm</span>
            <h2 class="section-title">纪念提醒</h2>
            <p class="page-subtitle">第一版先把简单但有用的提醒节奏放进来，不打扰，却足够刚好。</p>
          </div>
        </div>
        <div class="overview-grid overview-grid--single">
          <div class="metric-card">
            <span>今天发生</span>
            <strong>{{ anniversarySummary.todayCount || 0 }}</strong>
            <p>{{ anniversarySummary.todayCount ? '今天是值得好好记录的一天。' : '今天没有纪念日，也可以继续平静地过。' }}</p>
          </div>
          <div class="metric-card">
            <span>即将到来</span>
            <strong>{{ anniversarySummary.upcomingCount || 0 }}</strong>
            <p>支持当天、提前 1 天、3 天、7 天的提醒节奏。</p>
          </div>
        </div>
      </div>
    </section>

    <section class="page-card latest-albums fade-rise">
      <div class="panel-frame">
        <div class="page-header">
          <div>
            <span class="eyebrow">Recent collections</span>
            <h2 class="section-title">最近相册</h2>
            <p class="page-subtitle">点击任意相册，就能继续浏览你们已经留下来的画面。</p>
          </div>
        </div>
        <el-empty v-if="!albums.length" description="还没有相册，先创建一个吧" />
        <div v-else class="grid-three">
          <article v-for="album in albums.slice(0, 6)" :key="album.id" class="album-card" @click="goAlbum(album.id)">
            <img v-if="album.coverUrl" :src="album.coverUrl" class="cover-image album-cover" />
            <div v-else class="album-cover album-cover-fallback">Love</div>
            <div class="album-body">
              <div class="album-meta-row">
                <span>{{ album.ownerNickname }}</span>
                <span>{{ album.createTime ? formatDateTime(album.createTime).slice(0, 10) : 'Recently' }}</span>
              </div>
              <h3>{{ album.name }}</h3>
              <p class="muted">{{ album.description || '这个相册还没有描述' }}</p>
            </div>
          </article>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRouter, RouterLink } from 'vue-router'
import { getAlbumList } from '../api/album'
import { getAnniversarySummary } from '../api/anniversary'
import { getCoupleInfo } from '../api/couple'
import { getUserInfo } from '../utils/auth'
import {
  formatAnniversaryDate,
  formatDaysText,
  getAnniversaryTypeLabel
} from '../utils/anniversary'

const router = useRouter()
const currentUser = ref(getUserInfo())
const coupleInfo = ref({ bound: false })
const albums = ref([])
const anniversarySummary = ref({
  totalCount: 0,
  todayCount: 0,
  upcomingCount: 0,
  nextAnniversary: null,
  upcomingAnniversaries: []
})

const formatDateTime = (value) => (value ? value.replace('T', ' ') : '--')
const goAlbum = (id) => router.push(`/albums/${id}`)
const goAnniversaries = () => router.push('/anniversaries')

const loadData = async () => {
  const [couple, albumList, anniversaryData] = await Promise.all([
    getCoupleInfo(),
    getAlbumList(),
    getAnniversarySummary()
  ])
  coupleInfo.value = couple
  albums.value = albumList
  anniversarySummary.value = anniversaryData
}

onMounted(loadData)
</script>

<style scoped>
.home-page {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.hero-panel {
  min-height: 430px;
}

.hero-panel::before {
  background:
    radial-gradient(circle at 12% 16%, rgba(255, 207, 223, 0.26), transparent 24%),
    radial-gradient(circle at 84% 22%, rgba(255, 224, 203, 0.2), transparent 22%),
    radial-gradient(circle at 72% 82%, rgba(255, 206, 228, 0.14), transparent 18%);
}

.hero-grid {
  display: grid;
  grid-template-columns: minmax(0, 1.35fr) minmax(280px, 0.65fr);
  gap: 30px;
  align-items: end;
}

.hero-copy {
  max-width: 720px;
}

.hero-title-block {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.hero-greeting {
  font-family: var(--font-serif);
  font-size: clamp(2rem, 4vw, 3.1rem);
  line-height: 0.98;
  letter-spacing: -0.03em;
  color: var(--muted-strong);
}

.hero-title {
  margin: 0;
  display: flex;
  flex-wrap: wrap;
  align-items: baseline;
  gap: 0.06em;
  font-family: var(--font-serif);
  font-size: clamp(3.1rem, 7vw, 6.2rem);
  line-height: 0.96;
  font-weight: 400;
  letter-spacing: -0.045em;
  white-space: normal;
  overflow: visible;
  text-overflow: clip;
}

.hero-name {
  max-width: 100%;
  word-break: break-word;
  overflow-wrap: anywhere;
}

.hero-ending {
  flex: 0 0 auto;
}

.hero-subtitle {
  max-width: 560px;
}

.hero-actions {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  margin-top: 28px;
}

.hero-aside {
  display: flex;
  flex-direction: column;
  gap: 16px;
  align-self: stretch;
}

.aside-card,
.statement-card,
.metric-card {
  padding: 22px;
  border-radius: 24px;
  border: 1px solid rgba(210, 123, 147, 0.12);
  background: rgba(255, 255, 255, 0.52);
}

.aside-card {
  margin-top: auto;
}

.aside-card strong {
  display: block;
  margin: 10px 0 12px;
  font-family: var(--font-serif);
  font-size: 34px;
  line-height: 1;
  font-weight: 400;
}

.aside-card p,
.metric-card p {
  margin: 0;
  color: var(--muted);
  line-height: 1.7;
}

.aside-label,
.statement-label,
.metric-card span,
.album-meta-row {
  font-size: 11px;
  letter-spacing: 0.14em;
  text-transform: uppercase;
  color: var(--muted);
}

.hero-note {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.statement-card {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 14px;
}

.statement-number {
  font-family: var(--font-serif);
  font-size: 44px;
  line-height: 0.94;
}

.statement-number--small {
  font-size: 34px;
}

.statement-meta {
  color: var(--muted);
}

.code-row {
  display: flex;
  gap: 10px;
  align-items: baseline;
  flex-wrap: wrap;
  font-size: 13px;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  color: var(--muted);
}

.overview-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
}

.overview-grid--single {
  grid-template-columns: 1fr;
}

.metric-card {
  min-height: 198px;
  display: flex;
  flex-direction: column;
}

.metric-card strong {
  display: block;
  margin: auto 0 14px;
  font-family: var(--font-serif);
  font-size: 54px;
  line-height: 0.95;
  font-weight: 400;
}

.metric-card--large strong {
  font-size: 66px;
}

.memory-panel {
  display: flex;
  flex-direction: column;
}

.memory-highlight {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 18px;
  padding: 22px;
  border-radius: 24px;
  border: 1px solid rgba(210, 123, 147, 0.14);
  background: rgba(255, 247, 249, 0.7);
}

.memory-copy h3 {
  margin: 10px 0 10px;
  font-family: var(--font-serif);
  font-size: 38px;
  line-height: 0.98;
  font-weight: 400;
}

.memory-copy p {
  margin: 0;
  color: var(--muted);
}

.memory-label {
  font-size: 11px;
  letter-spacing: 0.16em;
  text-transform: uppercase;
  color: var(--muted);
}

.memory-pill {
  min-width: 144px;
  padding: 18px;
  border-radius: 24px;
  background: rgba(255, 255, 255, 0.82);
  text-align: center;
}

.memory-pill strong {
  display: block;
  font-family: var(--font-serif);
  font-size: 48px;
  line-height: 0.92;
  font-weight: 400;
}

.memory-pill span {
  display: block;
  margin-top: 8px;
  color: var(--muted);
  font-size: 13px;
}

.memory-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-top: 16px;
}

.memory-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 18px;
  padding: 16px 18px;
  border: 1px solid rgba(210, 123, 147, 0.12);
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.52);
  color: inherit;
  cursor: pointer;
  transition:
    transform 180ms ease,
    border-color 180ms ease,
    box-shadow 180ms ease;
}

.memory-item:hover {
  transform: translateY(-2px);
  border-color: rgba(210, 123, 147, 0.22);
  box-shadow: 0 16px 28px rgba(145, 88, 103, 0.08);
}

.memory-item span {
  color: var(--muted-strong);
  font-weight: 600;
}

.memory-item strong {
  font-family: var(--font-serif);
  font-size: 24px;
  line-height: 1;
  font-weight: 400;
}

.album-card {
  overflow: hidden;
  border-radius: 28px;
  border: 1px solid rgba(210, 123, 147, 0.14);
  background: rgba(255, 255, 255, 0.56);
  cursor: pointer;
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
  height: 280px;
}

.album-cover-fallback {
  display: flex;
  align-items: end;
  justify-content: flex-start;
  padding: 22px;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0) 0%, rgba(125, 76, 88, 0.22) 100%),
    linear-gradient(135deg, #ffd8e2 0%, #fff0e6 40%, #f7bdd0 100%);
  font-family: var(--font-serif);
  font-size: 38px;
  color: #fffefb;
}

.album-body {
  padding: 18px 18px 22px;
}

.album-meta-row {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 12px;
}

.album-body h3 {
  margin: 0 0 10px;
  font-family: var(--font-serif);
  font-size: 30px;
  line-height: 1.02;
  font-weight: 400;
}

.latest-albums :deep(.el-empty) {
  padding: 36px 0 18px;
}

@media (max-width: 960px) {
  .hero-grid {
    grid-template-columns: 1fr;
  }

  .hero-title {
    font-size: clamp(2.6rem, 11vw, 4.6rem);
  }

  .hero-greeting {
    font-size: clamp(1.8rem, 7vw, 2.5rem);
  }
}

@media (max-width: 768px) {
  .overview-grid {
    grid-template-columns: 1fr;
  }

  .memory-highlight {
    flex-direction: column;
    align-items: flex-start;
  }

  .memory-pill {
    width: 100%;
  }

  .album-cover {
    height: 220px;
  }

  .metric-card strong,
  .metric-card--large strong,
  .statement-number {
    font-size: 42px;
  }
}

.status-panel,
.overview-panel,
.latest-albums {
  overflow: hidden;
}
</style>
