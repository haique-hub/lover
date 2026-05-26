<template>
  <div class="anniversary-page">
    <section class="page-card anniversary-hero fade-rise">
      <div class="panel-frame anniversary-hero-grid">
        <div class="anniversary-copy">
          <span class="eyebrow">Memories worth keeping</span>
          <h1 class="page-title">纪念日</h1>
          <p class="page-subtitle anniversary-subtitle">
            {{ heroText }}
          </p>
          <div class="anniversary-actions">
            <el-button type="primary" size="large" @click="openCreateDialog">新增纪念日</el-button>
            <el-button size="large" @click="scrollToList">查看全部</el-button>
          </div>
        </div>
        <div class="anniversary-hero-side">
          <div class="countdown-card">
            <span class="countdown-label">下一个重要日子</span>
            <template v-if="summary.nextAnniversary">
              <strong>{{ countdownHeadline }}</strong>
              <p>{{ summary.nextAnniversary.title }}</p>
              <div class="countdown-meta">
                <span>{{ formatAnniversaryDisplay(summary.nextAnniversary) }}</span>
                <span>{{ getAnniversaryScopeLabel(summary.nextAnniversary.relationScope) }}</span>
              </div>
            </template>
            <template v-else>
              <strong>还没有设置</strong>
              <p>先放进一个重要日期，让空间开始有期待。</p>
            </template>
          </div>
          <div class="countdown-stats">
            <div class="mini-stat">
              <span>全部纪念日</span>
              <strong>{{ summary.totalCount || 0 }}</strong>
            </div>
            <div class="mini-stat">
              <span>今天发生</span>
              <strong>{{ summary.todayCount || 0 }}</strong>
            </div>
          </div>
        </div>
      </div>
    </section>

    <section class="grid-two fade-rise">
      <div class="page-card panel-frame">
        <div class="page-header">
          <div>
            <span class="eyebrow">Upcoming notes</span>
            <h2 class="section-title">即将到来</h2>
            <p class="page-subtitle">把临近的重要日子提前留在眼前，不必等到那一天才想起。</p>
          </div>
        </div>
        <el-empty v-if="!summary.upcomingAnniversaries?.length" description="还没有即将到来的纪念日" />
        <div v-else class="upcoming-stack">
          <button
            v-for="item in summary.upcomingAnniversaries"
            :key="item.id"
            type="button"
            class="upcoming-item"
            @click="openDetail(item)"
          >
            <div class="upcoming-item-copy">
              <span class="upcoming-type">{{ getAnniversaryTypeLabel(item.type) }}</span>
              <strong>{{ item.title }}</strong>
              <p>{{ formatAnniversaryDisplay(item) }} · {{ formatDaysText(item.daysUntilNext) }}</p>
            </div>
            <span class="upcoming-badge">{{ getAnniversaryScopeLabel(item.relationScope) }}</span>
          </button>
        </div>
      </div>

      <div class="page-card panel-frame">
        <div class="page-header">
          <div>
            <span class="eyebrow">About reminders</span>
            <h2 class="section-title">提醒方式</h2>
            <p class="page-subtitle">第一版先把提醒节奏和回忆信息稳稳放好，后面再扩展更强的互动。</p>
          </div>
        </div>
        <div class="guide-list">
          <div class="guide-card">
            <span>提前提醒</span>
            <strong>支持当天 / 1 天 / 3 天 / 7 天</strong>
            <p>先把你们最常用的提醒节奏放进去，避免太复杂。</p>
          </div>
          <div class="guide-card">
            <span>封面回忆</span>
            <strong>可从最近照片里直接挑一张</strong>
            <p>让重要日子一眼就有画面，不需要额外上传。</p>
          </div>
        </div>
      </div>
    </section>

    <section id="anniversary-list" class="page-card fade-rise">
      <div class="panel-frame">
        <div class="page-header anniversary-list-header">
          <div>
            <span class="eyebrow">All dates</span>
            <h2 class="section-title">全部纪念日</h2>
            <p class="page-subtitle">按最近一次发生顺序排列，今天、即将到来和已经过去的都能看得很清楚。</p>
          </div>
          <el-button type="primary" @click="openCreateDialog">新增纪念日</el-button>
        </div>

        <el-empty v-if="!anniversaries.length" description="还没有纪念日，先创建第一个吧" />

        <div v-else class="anniversary-grid">
          <article
            v-for="item in anniversaries"
            :key="item.id"
            class="anniversary-card"
            @click="openDetail(item)"
          >
            <div class="anniversary-cover" :class="{ 'anniversary-cover--plain': !item.coverPhotoUrl }">
              <img v-if="item.coverPhotoUrl" :src="item.coverPhotoUrl" class="cover-image anniversary-cover-image" />
              <div v-else class="anniversary-cover-fallback">{{ getFallbackMark(item) }}</div>
              <div class="anniversary-overlay">
                <span class="anniversary-chip">{{ getAnniversaryTypeLabel(item.type) }}</span>
                <span class="anniversary-chip anniversary-chip--soft">{{ getAnniversaryScopeLabel(item.relationScope) }}</span>
              </div>
            </div>

            <div class="anniversary-body">
              <div class="anniversary-topline">
                <span>{{ formatAnniversaryDisplay(item) }}</span>
                <span>{{ getAnniversaryRepeatLabel(item.repeatType) }}</span>
              </div>
              <h3>{{ item.title }}</h3>
              <p class="anniversary-description">{{ item.description || '还没有写下这一天的说明。' }}</p>
              <div class="anniversary-foot">
                <div class="anniversary-count">
                  <strong>{{ getCardCountText(item) }}</strong>
                  <span>{{ getCardHintText(item) }}</span>
                </div>
                <span class="anniversary-owner">{{ item.ownerNickname || '你' }}</span>
              </div>
            </div>
          </article>
        </div>
      </div>
    </section>

    <el-dialog
      v-model="editorVisible"
      :title="editingId ? '编辑纪念日' : '新增纪念日'"
      width="760px"
      @closed="resetForm"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top" class="anniversary-form">
        <div class="form-grid">
          <el-form-item label="纪念日名称" prop="title">
            <el-input v-model="form.title" maxlength="100" placeholder="比如：第一次见面、在一起纪念日" />
          </el-form-item>
          <el-form-item label="纪念日类型" prop="type">
            <el-select v-model="form.type" placeholder="请选择类型">
              <el-option
                v-for="option in anniversaryTypeOptions"
                :key="option.value"
                :label="option.label"
                :value="option.value"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="日期类型" prop="calendarType">
            <el-segmented v-model="form.calendarType" :options="anniversaryCalendarOptions" />
          </el-form-item>
          <el-form-item v-if="form.calendarType === 'SOLAR'" label="日期" prop="anniversaryDate">
            <el-date-picker
              v-model="form.anniversaryDate"
              type="date"
              value-format="YYYY-MM-DD"
              format="YYYY 年 MM 月 DD 日"
              :teleported="false"
              :popper-class="'anniversary-date-popper'"
              placeholder="请选择日期"
              style="width: 100%"
            />
            <p class="field-hint">
              按公历日期记录，适合生日、纪念日和普通提醒。
            </p>
          </el-form-item>
          <div v-else class="lunar-picker-group">
            <el-form-item label="农历年份" prop="lunarYear">
              <el-select v-model="form.lunarYear" placeholder="请选择农历年份">
                <el-option
                  v-for="option in lunarYearOptions"
                  :key="option.value"
                  :label="option.label"
                  :value="option.value"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="农历月份" prop="lunarMonth">
              <el-select v-model="form.lunarMonth" placeholder="请选择农历月份">
                <el-option
                  v-for="option in lunarMonthOptions"
                  :key="option.value"
                  :label="option.label"
                  :value="option.value"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="农历日期" prop="lunarDay">
              <el-select v-model="form.lunarDay" placeholder="请选择农历日期">
                <el-option
                  v-for="option in lunarDayOptions"
                  :key="option.value"
                  :label="option.label"
                  :value="option.value"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="闰月">
              <el-switch
                v-model="form.lunarLeapMonth"
                inline-prompt
                active-text="是"
                inactive-text="否"
              />
              <p class="field-hint">如果这个纪念日落在农历闰月，请打开这里；若下一年没有对应闰月，系统会自动继续往后找到下一次出现的年份。</p>
            </el-form-item>
            <div class="lunar-preview-card">
              <span class="lunar-preview-label">当前农历选择</span>
              <strong>{{ lunarPreviewText }}</strong>
              <p>更适合生日、传统节日和你们想按农历认真记住的特别日子。</p>
            </div>
          </div>
          <el-form-item label="归属" prop="relationScope">
            <el-select v-model="form.relationScope" placeholder="请选择归属">
              <el-option
                v-for="option in anniversaryScopeOptions"
                :key="option.value"
                :label="option.label"
                :value="option.value"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="重复方式" prop="repeatType">
            <el-segmented v-model="form.repeatType" :options="anniversaryRepeatOptions" />
          </el-form-item>
          <el-form-item label="提醒节奏" prop="reminderDays">
            <el-select v-model="form.reminderDays" placeholder="请选择提醒方式">
              <el-option
                v-for="option in anniversaryReminderOptions"
                :key="option.value"
                :label="option.label"
                :value="option.value"
              />
            </el-select>
          </el-form-item>
        </div>

        <el-form-item label="一句说明" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            maxlength="255"
            show-word-limit
            :rows="4"
            placeholder="给这一天留一句话，比如那天的天气、心情，或者为什么重要。"
          />
        </el-form-item>

        <el-form-item label="封面照片">
          <div class="photo-picker">
            <button
              type="button"
              class="photo-choice photo-choice--empty"
              :class="{ active: form.coverPhotoId === null }"
              @click="form.coverPhotoId = null"
            >
              <span>不设置封面</span>
            </button>
            <button
              v-for="photo in recentPhotos"
              :key="photo.id"
              type="button"
              class="photo-choice"
              :class="{ active: form.coverPhotoId === photo.id }"
              @click="form.coverPhotoId = photo.id"
            >
              <img :src="photo.fileUrl" class="photo-choice-image" />
              <span>{{ photo.description || photo.fileName }}</span>
            </button>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editorVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitForm">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="detailVisible" title="纪念日详情" width="680px">
      <template v-if="activeAnniversary">
        <div class="anniversary-detail">
          <div class="anniversary-detail-cover" :class="{ 'anniversary-detail-cover--plain': !activeAnniversary.coverPhotoUrl }">
            <img v-if="activeAnniversary.coverPhotoUrl" :src="activeAnniversary.coverPhotoUrl" class="cover-image anniversary-detail-image" />
            <div v-else class="anniversary-cover-fallback anniversary-cover-fallback--large">{{ getFallbackMark(activeAnniversary) }}</div>
          </div>
          <div class="anniversary-detail-copy">
            <div class="detail-tags">
              <el-tag>{{ getAnniversaryTypeLabel(activeAnniversary.type) }}</el-tag>
              <el-tag>{{ getAnniversaryCalendarLabel(activeAnniversary.calendarType) }}</el-tag>
              <el-tag>{{ getAnniversaryScopeLabel(activeAnniversary.relationScope) }}</el-tag>
              <el-tag>{{ getAnniversaryRepeatLabel(activeAnniversary.repeatType) }}</el-tag>
            </div>
            <h2>{{ activeAnniversary.title }}</h2>
            <p class="detail-date">{{ formatAnniversaryDisplay(activeAnniversary) }}</p>
            <p class="detail-text">{{ activeAnniversary.description || '这一天还没有额外说明。' }}</p>
            <div class="detail-metrics">
              <div class="detail-metric">
                <span>距离下一次</span>
                <strong>{{ getCardCountText(activeAnniversary) }}</strong>
              </div>
              <div v-if="activeAnniversary.daysSinceStart != null" class="detail-metric">
                <span>已经走过</span>
                <strong>{{ activeAnniversary.daysSinceStart }} 天</strong>
              </div>
            </div>
          </div>
        </div>
      </template>
      <template #footer>
        <div class="detail-actions">
          <el-button @click="detailVisible = false">关闭</el-button>
          <el-button v-if="canEditActiveAnniversary" type="primary" @click="startEdit">编辑</el-button>
          <el-button v-if="canEditActiveAnniversary" type="danger" plain @click="handleDelete">删除</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUserInfo } from '../utils/auth'
import { getRecentPhotoList } from '../api/photo'
import {
  createAnniversary,
  deleteAnniversary,
  getAnniversaryList,
  getAnniversarySummary,
  updateAnniversary
} from '../api/anniversary'
import {
  anniversaryCalendarOptions,
  anniversaryReminderOptions,
  anniversaryRepeatOptions,
  anniversaryScopeOptions,
  anniversaryTypeOptions,
  buildLunarYearOptions,
  formatAnniversaryDisplay,
  formatDaysText,
  formatLunarSelection,
  getAnniversaryCalendarLabel,
  getAnniversaryHeroText,
  getAnniversaryRepeatLabel,
  getAnniversaryScopeLabel,
  getAnniversaryTypeLabel,
  lunarDayOptions,
  lunarMonthOptions
} from '../utils/anniversary'

const anniversaries = ref([])
const recentPhotos = ref([])
const currentUser = ref(getUserInfo())
const lunarYearOptions = buildLunarYearOptions()
const summary = ref({
  totalCount: 0,
  todayCount: 0,
  upcomingCount: 0,
  nextAnniversary: null,
  upcomingAnniversaries: []
})
const editorVisible = ref(false)
const detailVisible = ref(false)
const submitting = ref(false)
const editingId = ref(null)
const activeAnniversary = ref(null)
const formRef = ref()
const form = reactive({
  title: '',
  type: 'LOVE_DAY',
  calendarType: 'SOLAR',
  anniversaryDate: '',
  lunarYear: new Date().getFullYear(),
  lunarMonth: 1,
  lunarDay: 1,
  lunarLeapMonth: false,
  relationScope: 'BOTH',
  repeatType: 'YEARLY',
  reminderDays: 3,
  coverPhotoId: null,
  description: ''
})

const rules = {
  title: [{ required: true, message: '请输入纪念日名称', trigger: 'blur' }],
  type: [{ required: true, message: '请选择纪念日类型', trigger: 'change' }],
  calendarType: [{ required: true, message: '请选择日期类型', trigger: 'change' }],
  anniversaryDate: [{
    validator: (_, value, callback) => {
      if (form.calendarType === 'SOLAR' && !value) {
        callback(new Error('请选择日期'))
        return
      }
      callback()
    },
    trigger: 'change'
  }],
  lunarYear: [{
    validator: (_, value, callback) => {
      if (form.calendarType === 'LUNAR' && !value) {
        callback(new Error('请选择农历年份'))
        return
      }
      callback()
    },
    trigger: 'change'
  }],
  lunarMonth: [{
    validator: (_, value, callback) => {
      if (form.calendarType === 'LUNAR' && !value) {
        callback(new Error('请选择农历月份'))
        return
      }
      callback()
    },
    trigger: 'change'
  }],
  lunarDay: [{
    validator: (_, value, callback) => {
      if (form.calendarType === 'LUNAR' && !value) {
        callback(new Error('请选择农历日期'))
        return
      }
      callback()
    },
    trigger: 'change'
  }],
  relationScope: [{ required: true, message: '请选择归属', trigger: 'change' }],
  repeatType: [{ required: true, message: '请选择重复方式', trigger: 'change' }],
  reminderDays: [{ required: true, message: '请选择提醒节奏', trigger: 'change' }]
}

const heroText = computed(() => getAnniversaryHeroText(summary.value.nextAnniversary))
const lunarPreviewText = computed(() => formatLunarSelection(form))
const canEditActiveAnniversary = computed(
  () => !!activeAnniversary.value && activeAnniversary.value.userId === currentUser.value?.id
)
const countdownHeadline = computed(() => {
  const next = summary.value.nextAnniversary
  if (!next) {
    return '还没有设置'
  }
  return next.today ? '就是今天' : `${next.daysUntilNext} 天后`
})

const loadData = async () => {
  const [anniversaryList, anniversarySummary, photos] = await Promise.all([
    getAnniversaryList(),
    getAnniversarySummary(),
    getRecentPhotoList()
  ])
  anniversaries.value = anniversaryList
  summary.value = anniversarySummary
  recentPhotos.value = photos
}

const scrollToList = () => {
  document.getElementById('anniversary-list')?.scrollIntoView({ behavior: 'smooth', block: 'start' })
}

const resetForm = () => {
  editingId.value = null
  form.title = ''
  form.type = 'LOVE_DAY'
  form.calendarType = 'SOLAR'
  form.anniversaryDate = ''
  form.lunarYear = new Date().getFullYear()
  form.lunarMonth = 1
  form.lunarDay = 1
  form.lunarLeapMonth = false
  form.relationScope = 'BOTH'
  form.repeatType = 'YEARLY'
  form.reminderDays = 3
  form.coverPhotoId = null
  form.description = ''
  formRef.value?.clearValidate()
}

const openCreateDialog = () => {
  resetForm()
  editorVisible.value = true
}

const fillForm = (item) => {
  form.title = item.title
  form.type = item.type
  form.calendarType = item.calendarType || 'SOLAR'
  form.anniversaryDate = item.anniversaryDate
  form.lunarYear = item.lunarYear || new Date().getFullYear()
  form.lunarMonth = item.lunarMonth || 1
  form.lunarDay = item.lunarDay || 1
  form.lunarLeapMonth = !!item.lunarLeapMonth
  form.relationScope = item.relationScope
  form.repeatType = item.repeatType
  form.reminderDays = item.reminderDays ?? 3
  form.coverPhotoId = item.coverPhotoId ?? null
  form.description = item.description || ''
}

const openDetail = (item) => {
  activeAnniversary.value = item
  detailVisible.value = true
}

const startEdit = () => {
  if (!activeAnniversary.value) {
    return
  }
  editingId.value = activeAnniversary.value.id
  fillForm(activeAnniversary.value)
  detailVisible.value = false
  editorVisible.value = true
}

const submitForm = async () => {
  await formRef.value.validate()
  submitting.value = true
  const payload = {
    title: form.title,
    type: form.type,
    calendarType: form.calendarType,
    anniversaryDate: form.anniversaryDate,
    lunarYear: form.lunarYear,
    lunarMonth: form.lunarMonth,
    lunarDay: form.lunarDay,
    lunarLeapMonth: form.lunarLeapMonth,
    relationScope: form.relationScope,
    repeatType: form.repeatType,
    reminderDays: form.reminderDays,
    coverPhotoId: form.coverPhotoId,
    description: form.description
  }
  try {
    if (editingId.value) {
      await updateAnniversary(editingId.value, payload)
      ElMessage.success('纪念日已更新')
    } else {
      await createAnniversary(payload)
      ElMessage.success('纪念日已创建')
    }
    editorVisible.value = false
    resetForm()
    await loadData()
  } finally {
    submitting.value = false
  }
}

const handleDelete = async () => {
  if (!activeAnniversary.value) {
    return
  }
  await ElMessageBox.confirm('确认删除这个纪念日吗？删除后无法恢复。', '提示', { type: 'warning' })
  await deleteAnniversary(activeAnniversary.value.id)
  ElMessage.success('纪念日已删除')
  detailVisible.value = false
  activeAnniversary.value = null
  await loadData()
}

const getFallbackMark = (item) => {
  if (item.type === 'BIRTHDAY') {
    return 'B'
  }
  if (item.type === 'LOVE_DAY') {
    return 'L'
  }
  if (item.type === 'MEMORY_DAY') {
    return 'M'
  }
  return 'D'
}

const getCardCountText = (item) => {
  if (item.today) {
    return '就是今天'
  }
  if (item.daysUntilNext == null) {
    return '已经过去'
  }
  return `${item.daysUntilNext} 天`
}

const getCardHintText = (item) => {
  if (item.daysUntilNext == null) {
    return `从 ${formatAnniversaryDisplay(item, false)} 开始算起`
  }
  return item.daysSinceStart != null ? `已经一起记住了 ${item.daysSinceStart} 天` : `还没到这一天`
}

onMounted(loadData)
</script>

<style scoped>
.anniversary-page {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.anniversary-hero::before {
  background:
    radial-gradient(circle at 18% 18%, rgba(255, 205, 219, 0.26), transparent 24%),
    radial-gradient(circle at 82% 24%, rgba(255, 227, 212, 0.22), transparent 24%),
    radial-gradient(circle at 76% 80%, rgba(240, 178, 201, 0.14), transparent 18%);
}

.anniversary-hero-grid {
  display: grid;
  grid-template-columns: minmax(0, 1.2fr) minmax(320px, 0.8fr);
  gap: 28px;
  align-items: stretch;
}

.anniversary-subtitle {
  max-width: 620px;
}

.anniversary-actions {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  margin-top: 28px;
}

.anniversary-hero-side {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.countdown-card,
.mini-stat,
.guide-card,
.upcoming-item {
  border-radius: 24px;
  border: 1px solid rgba(210, 123, 147, 0.12);
  background: rgba(255, 255, 255, 0.56);
}

.countdown-card {
  padding: 24px;
  min-height: 220px;
  display: flex;
  flex-direction: column;
}

.countdown-label,
.upcoming-type,
.anniversary-topline,
.mini-stat span {
  font-size: 11px;
  letter-spacing: 0.14em;
  text-transform: uppercase;
  color: var(--muted);
}

.countdown-card strong {
  margin: auto 0 14px;
  font-family: var(--font-serif);
  font-size: 54px;
  line-height: 0.92;
  font-weight: 400;
}

.countdown-card p {
  margin: 0 0 12px;
  color: var(--muted-strong);
  font-size: 17px;
  line-height: 1.5;
}

.countdown-meta {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  color: var(--muted);
}

.countdown-stats,
.guide-list {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
}

.mini-stat,
.guide-card {
  padding: 18px 20px;
}

.mini-stat strong,
.guide-card strong {
  display: block;
  margin: 10px 0 8px;
  font-family: var(--font-serif);
  font-size: 34px;
  line-height: 1;
  font-weight: 400;
  color: var(--muted-strong);
}

.guide-card strong {
  font-size: 26px;
  line-height: 1.2;
}

.mini-stat span,
.guide-card span {
  display: block;
}

.mini-stat p,
.guide-card p {
  margin: 0;
  color: var(--muted);
  line-height: 1.7;
}

.upcoming-stack {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.upcoming-item {
  width: 100%;
  padding: 20px 22px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 18px;
  text-align: left;
  cursor: pointer;
  transition:
    transform 220ms ease,
    border-color 220ms ease,
    box-shadow 220ms ease;
}

.upcoming-item:hover {
  transform: translateY(-2px);
  border-color: rgba(210, 123, 147, 0.22);
  box-shadow: 0 20px 34px rgba(145, 88, 103, 0.08);
}

.upcoming-item-copy strong {
  display: block;
  margin: 10px 0 8px;
  font-family: var(--font-serif);
  font-size: 32px;
  line-height: 1;
  font-weight: 400;
}

.upcoming-item-copy p {
  margin: 0;
  color: var(--muted);
}

.upcoming-badge {
  flex: 0 0 auto;
  padding: 10px 14px;
  border-radius: 999px;
  background: rgba(255, 241, 244, 0.78);
  color: #a2556d;
  font-size: 12px;
  font-weight: 700;
}

.anniversary-list-header {
  align-items: center;
}

.anniversary-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 20px;
}

.anniversary-card {
  overflow: hidden;
  border-radius: 28px;
  border: 1px solid rgba(210, 123, 147, 0.14);
  background: rgba(255, 255, 255, 0.58);
  cursor: pointer;
  transition:
    transform 220ms ease,
    box-shadow 220ms ease,
    border-color 220ms ease;
}

.anniversary-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 22px 44px rgba(145, 88, 103, 0.1);
  border-color: rgba(210, 123, 147, 0.24);
}

.anniversary-cover {
  position: relative;
  height: 220px;
  overflow: hidden;
  background:
    linear-gradient(145deg, rgba(255, 232, 238, 0.9), rgba(255, 245, 241, 0.88)),
    linear-gradient(180deg, #fff7f5 0%, #fff2f0 100%);
}

.anniversary-cover--plain {
  display: grid;
  place-items: center;
}

.anniversary-cover-image,
.anniversary-detail-image {
  height: 100%;
}

.anniversary-cover-fallback {
  display: grid;
  place-items: center;
  width: 100%;
  height: 100%;
  font-family: var(--font-serif);
  font-size: 76px;
  line-height: 1;
  color: rgba(162, 85, 109, 0.7);
}

.anniversary-cover-fallback--large {
  font-size: 88px;
}

.anniversary-overlay {
  position: absolute;
  inset: auto 16px 16px;
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.anniversary-chip {
  padding: 8px 12px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.82);
  color: var(--muted-strong);
  font-size: 12px;
  font-weight: 700;
}

.anniversary-chip--soft {
  background: rgba(255, 240, 244, 0.84);
  color: #a2556d;
}

.anniversary-body {
  padding: 18px 18px 20px;
}

.anniversary-topline {
  display: flex;
  justify-content: space-between;
  gap: 12px;
}

.anniversary-body h3 {
  margin: 12px 0 10px;
  font-family: var(--font-serif);
  font-size: 34px;
  line-height: 1;
  font-weight: 400;
}

.anniversary-description {
  margin: 0;
  min-height: 46px;
  color: var(--muted);
  line-height: 1.75;
}

.anniversary-foot {
  margin-top: 18px;
  display: flex;
  align-items: end;
  justify-content: space-between;
  gap: 14px;
}

.anniversary-count strong {
  display: block;
  font-family: var(--font-serif);
  font-size: 30px;
  line-height: 1;
  font-weight: 400;
}

.anniversary-count span,
.anniversary-owner {
  color: var(--muted);
  font-size: 13px;
  line-height: 1.6;
}

.anniversary-form {
  padding-top: 8px;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 0 18px;
}

.lunar-picker-group {
  display: contents;
}

.lunar-preview-card {
  align-self: end;
  min-height: 132px;
  padding: 18px 20px;
  border-radius: 22px;
  border: 1px solid rgba(210, 123, 147, 0.12);
  background:
    linear-gradient(180deg, rgba(255, 250, 247, 0.88) 0%, rgba(255, 244, 246, 0.78) 100%);
}

.lunar-preview-label {
  display: block;
  font-size: 11px;
  letter-spacing: 0.16em;
  text-transform: uppercase;
  color: var(--muted);
}

.lunar-preview-card strong {
  display: block;
  margin: 12px 0 10px;
  font-family: var(--font-serif);
  font-size: 28px;
  line-height: 1.15;
  font-weight: 400;
  color: var(--muted-strong);
}

.lunar-preview-card p {
  margin: 0;
  color: var(--muted);
  line-height: 1.7;
  font-size: 13px;
}

.field-hint {
  margin: 10px 2px 0;
  color: var(--muted);
  font-size: 12px;
  line-height: 1.6;
}

.photo-picker {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12px;
}

.photo-choice {
  padding: 8px;
  border: 1px solid rgba(210, 123, 147, 0.12);
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.72);
  cursor: pointer;
  text-align: left;
  transition:
    transform 180ms ease,
    border-color 180ms ease,
    box-shadow 180ms ease;
}

.photo-choice:hover,
.photo-choice.active {
  transform: translateY(-2px);
  border-color: rgba(210, 123, 147, 0.24);
  box-shadow: 0 16px 28px rgba(145, 88, 103, 0.08);
}

.photo-choice--empty {
  min-height: 136px;
  display: grid;
  place-items: center;
}

.photo-choice-image {
  width: 100%;
  height: 110px;
  object-fit: cover;
  border-radius: 14px;
  display: block;
}

.photo-choice span {
  display: block;
  margin-top: 8px;
  color: var(--muted-strong);
  font-size: 12px;
  line-height: 1.5;
}

.anniversary-detail {
  display: grid;
  grid-template-columns: minmax(240px, 300px) minmax(0, 1fr);
  gap: 24px;
  align-items: start;
}

.anniversary-detail-cover {
  overflow: hidden;
  border-radius: 24px;
  height: 320px;
  background:
    linear-gradient(145deg, rgba(255, 232, 238, 0.9), rgba(255, 245, 241, 0.88)),
    linear-gradient(180deg, #fff7f5 0%, #fff2f0 100%);
}

.anniversary-detail-cover--plain {
  display: grid;
  place-items: center;
}

.detail-tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  margin-bottom: 16px;
}

.anniversary-detail-copy h2 {
  margin: 0;
  font-family: var(--font-serif);
  font-size: 42px;
  line-height: 0.98;
  font-weight: 400;
}

.detail-date {
  margin: 14px 0 12px;
  color: var(--muted);
}

.detail-text {
  margin: 0;
  color: var(--muted-strong);
  line-height: 1.85;
}

.detail-metrics {
  margin-top: 22px;
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px;
}

.detail-metric {
  padding: 18px;
  border-radius: 20px;
  background: rgba(255, 247, 249, 0.8);
  border: 1px solid rgba(210, 123, 147, 0.12);
}

.detail-metric span {
  display: block;
  color: var(--muted);
  font-size: 12px;
  letter-spacing: 0.12em;
  text-transform: uppercase;
}

.detail-metric strong {
  display: block;
  margin-top: 10px;
  font-family: var(--font-serif);
  font-size: 34px;
  line-height: 1;
  font-weight: 400;
}

.detail-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  width: 100%;
}

@media (max-width: 1100px) {
  .anniversary-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .photo-picker {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }
}

@media (max-width: 900px) {
  .anniversary-hero-grid,
  .anniversary-detail,
  .form-grid,
  .countdown-stats,
  .guide-list {
    grid-template-columns: 1fr;
  }

  .anniversary-grid {
    grid-template-columns: 1fr;
  }

  .photo-picker {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 640px) {
  .upcoming-item,
  .anniversary-foot {
    flex-direction: column;
    align-items: flex-start;
  }

  .photo-picker {
    grid-template-columns: 1fr;
  }
}
</style>
