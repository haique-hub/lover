<template>
  <div class="grid-two">
    <section class="page-card panel-frame fade-rise">
      <div class="page-header">
        <div>
          <span class="eyebrow">Connection</span>
          <h1 class="page-title">情侣绑定</h1>
          <p class="page-subtitle">通过绑定码建立情侣关系。绑定完成后，你们会共享同一个相册和互动空间。</p>
        </div>
      </div>

      <template v-if="coupleInfo.bound">
        <div class="status-card">
          <div class="muted">当前绑定对象</div>
          <h2>{{ coupleInfo.partner?.nickname }}</h2>
          <p class="muted">绑定时间：{{ formatDateTime(coupleInfo.bindTime) }}</p>
          <el-button type="danger" plain @click="handleUnbind">解除绑定</el-button>
        </div>
      </template>

      <template v-else>
        <div class="bind-code-card">
          <div class="muted">我的绑定码</div>
          <div class="bind-code">{{ coupleInfo.bindCode || '尚未生成' }}</div>
          <p v-if="coupleInfo.bindCode" class="muted helper-text">
            如果你改为输入对方的绑定码完成绑定，当前这枚绑定码会自动失效。
          </p>
          <div class="code-actions">
            <el-button type="primary" @click="handleGenerateCode">生成/刷新绑定码</el-button>
            <el-button v-if="coupleInfo.bindCode" @click="copyCode">复制绑定码</el-button>
          </div>
        </div>
      </template>
    </section>

    <section class="page-card panel-frame fade-rise">
      <div class="page-header">
        <div>
          <span class="eyebrow">Invite code</span>
          <h2 class="section-title">输入绑定码</h2>
          <p class="page-subtitle">拿到对方的绑定码后，在这里完成绑定，让这个空间正式属于你们两个人。</p>
        </div>
      </div>
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
        <el-form-item label="绑定码" prop="bindCode">
          <el-input v-model="form.bindCode" maxlength="8" show-word-limit placeholder="请输入8位绑定码" />
        </el-form-item>
        <el-button type="primary" :disabled="coupleInfo.bound" @click="handleBind">确认绑定</el-button>
      </el-form>
    </section>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { bindCouple, generateBindCode, getCoupleInfo, unbindCouple } from '../api/couple'

const formRef = ref()
const coupleInfo = ref({ bound: false })
const form = reactive({
  bindCode: ''
})

const rules = {
  bindCode: [{ required: true, message: '请输入绑定码', trigger: 'blur' }]
}

const formatDateTime = (value) => (value ? value.replace('T', ' ') : '--')

const loadData = async () => {
  coupleInfo.value = await getCoupleInfo()
}

const handleGenerateCode = async () => {
  const data = await generateBindCode()
  coupleInfo.value.bindCode = data.bindCode
  ElMessage.success('绑定码已生成')
}

const handleBind = async () => {
  await formRef.value.validate()
  const data = await bindCouple(form)
  coupleInfo.value = data
  ElMessage.success('绑定成功')
  form.bindCode = ''
  formRef.value?.clearValidate()
}

const handleUnbind = async () => {
  await ElMessageBox.confirm('解绑后需要重新生成绑定码才能再次绑定，确认继续吗？', '提示', {
    type: 'warning'
  })
  await unbindCouple()
  ElMessage.success('已解除绑定')
  form.bindCode = ''
  formRef.value?.clearValidate()
  await loadData()
}

const copyCode = async () => {
  await navigator.clipboard.writeText(coupleInfo.value.bindCode)
  ElMessage.success('绑定码已复制')
}

onMounted(loadData)
</script>

<style scoped>
.status-card,
.bind-code-card {
  display: flex;
  flex-direction: column;
  gap: 16px;
  padding: 22px;
  border-radius: 24px;
  border: 1px solid rgba(210, 123, 147, 0.14);
  background: rgba(255, 247, 249, 0.72);
}

.status-card h2 {
  margin: 0;
  font-family: var(--font-serif);
  font-size: 42px;
  line-height: 0.98;
  font-weight: 400;
}

.bind-code {
  font-family: var(--font-serif);
  font-size: 42px;
  font-weight: 400;
  letter-spacing: 0.18em;
  color: #b5627d;
}

.helper-text {
  margin: 0;
}

.code-actions {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}
</style>
