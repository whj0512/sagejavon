<template>
  <div class="ai-bg">
    <div class="glass-container">
      <header class="header"></header>

      <!-- 头像 / 昵称 -->

      <n-card class="card" embedded :bordered="false">
        <div class="card-title"><span>昵称</span></div>
        <n-input
          v-model:value="name"
          placeholder="请输入昵称"
          @keydown.enter.prevent="saveField('nickname', name)"
        />
        <div class="row-actions">
          <n-button
            size="small"
            secondary
            type="primary"
            :loading="loadingKeys.nickname"
            @click="saveField('nickname', name)"
            >保存昵称</n-button
          >
        </div>
      </n-card>

      <!-- 性别 / 助手类型 -->
      <n-card class="card" embedded :bordered="false">
        <div class="card-title"><span>性别</span></div>
        <n-radio-group v-model:value="gender" class="pill-group" name="gender">
          <n-radio-button value="1">男</n-radio-button>
          <n-radio-button value="0">女</n-radio-button>
        </n-radio-group>
        <div class="row-actions">
          <n-button
            size="small"
            secondary
            type="primary"
            :loading="loadingKeys.gender"
            @click="saveField('gender', gender)"
            >保存性别</n-button
          >
        </div>
      </n-card>

      <n-card class="card" embedded :bordered="false">
        <div class="card-title">
          <span>助手类型</span>
          <small class="tip">影响对话风格与提示词</small>
        </div>
        <n-radio-group
          v-model:value="assistantType"
          class="pill-group"
          name="assistant"
        >
          <n-radio-button value="java">Java 小助手</n-radio-button>
          <n-radio-button value="python">Python 小助手</n-radio-button>
        </n-radio-group>
        <div class="row-actions">
          <n-button
            size="small"
            secondary
            type="primary"
            :loading="loadingKeys.assistant"
            @click="saveField('assistant', assistantType)"
            >保存助手类型</n-button
          >
        </div>
      </n-card>

      <div class="footer-actions">
        <n-button type="error" @click="showModal = true">退出登录</n-button>
      </div>
    </div>

    <!-- 退出登录弹窗 -->
    <n-modal
      v-model:show="showModal"
      :mask-closable="false"
      preset="dialog"
      title="退出登录"
      content="是否确认退出登录？"
      positive-text="确认"
      negative-text="取消"
      @positive-click="onPositiveClick"
      @negative-click="onNegativeClick"
    />
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import {
  NButton,
  NInput,
  useMessage,
  NModal,
  NCard,
  NRadioGroup,
  NRadioButton,
  NAvatar,
  NDivider,
  NGradientText,
} from 'naive-ui'
import { getUserInfo } from './api/info/get_user_info'
import updateUserInfoAPI from '@/views/chat/api/update_user_info'

const message = useMessage()

// state
const avatar = ref('')
const name = ref('')
const gender = ref('') // "1" | "0"
const assistantType = ref(localStorage.getItem('assistantType') || 'java')

const showModal = ref(false)
const loading = ref(false)
const savingAll = ref(false)
const loadingKeys = ref({
  portrait: false,
  nickname: false,
  gender: false,
  assistant: false,
})

const serverSnapshot = ref({
  nickname: '',
  portrait: '',
  gender: '',
  assistant: assistantType.value,
})

// 派生：是否有未保存更改
const isDirty = computed(() => {
  return (
    serverSnapshot.value.nickname !== name.value ||
    serverSnapshot.value.portrait !== avatar.value ||
    String(serverSnapshot.value.gender ?? '') !== String(gender.value ?? '') ||
    serverSnapshot.value.assistant !== assistantType.value
  )
})

// 头像安全兜底 & 初始字母
const safeAvatar = computed(() =>
  isValidUrl(avatar.value) ? avatar.value : '',
)
const initials = computed(() => (name.value?.trim()?.[0] || '友').toUpperCase())

// helpers
function isValidUrl(u) {
  try {
    const x = new URL(u)
    return ['http:', 'https:'].includes(x.protocol)
  } catch {
    return false
  }
}

// 读取用户信息
async function getUserInfoF() {
  try {
    loading.value = true
    const res = await getUserInfo()
    if (res?.status === 200) {
      const d = res.data?.data || {}
      avatar.value = d.portrait || ''
      name.value = d.nickname || ''
      gender.value = String(d.gender ?? '')
      // 后端若返回助手类型可在此覆盖
      if (d.assistant) {
        assistantType.value = d.assistant
        localStorage.setItem('assistantType', d.assistant)
      }
      serverSnapshot.value = {
        nickname: name.value,
        portrait: avatar.value,
        gender: gender.value,
        assistant: assistantType.value,
      }
    } else {
      message.warning('获取用户信息失败')
    }
  } catch (e) {
    console.error(e)
    message.error('网络异常，获取失败')
  } finally {
    loading.value = false
  }
}

// 单字段保存（实则整体提交，后端按需更新）
async function saveField(field, value) {
  try {
    loadingKeys.value[field] = true
    const payload = {
      nickname: name.value,
      portrait: avatar.value,
      gender: Number(gender.value),
      assistant: assistantType.value,
    }
    const res = await updateUserInfoAPI(payload)
    if (res?.status === 200) {
      localStorage.setItem('userInfo', JSON.stringify(payload))
      if (field === 'assistant') {
        localStorage.setItem('assistantType', assistantType.value)
        message.success('助手类型已保存，正在刷新....')
        setTimeout(() => {
          window.location.reload()
        }, 600)
      }

      message.success('已保存')
      serverSnapshot.value = { ...payload, gender: String(payload.gender) }
    } else {
      message.warning('保存失败')
    }
  } catch (e) {
    console.error(e)
    message.error('保存出错')
  } finally {
    loadingKeys.value[field] = false
  }
}

// 保存全部
async function saveAll() {
  if (!isDirty.value) return
  try {
    savingAll.value = true
    const payload = {
      nickname: name.value,
      portrait: avatar.value,
      gender: Number(gender.value),
      assistant: assistantType.value,
    }
    const res = await updateUserInfoAPI(payload)
    if (res?.status === 200) {
      localStorage.setItem('userInfo', JSON.stringify(payload))
      localStorage.setItem('assistantType', assistantType.value)
      message.success('全部已保存')
      serverSnapshot.value = { ...payload, gender: String(payload.gender) }
    } else {
      message.warning('保存失败')
    }
  } catch (e) {
    console.error(e)
    message.error('保存出错')
  } finally {
    savingAll.value = false
  }
}

// 退出登录
function onNegativeClick() {
  showModal.value = false
}
function onPositiveClick() {
  localStorage.removeItem('user-token')
  localStorage.removeItem('user-id')
  window.location.href = '/'
}

onMounted(() => {
  getUserInfoF()
})

// 跟随助手类型变化，先本地存一份，避免刷新丢失
watch(assistantType, (val) => {
  localStorage.setItem('assistantType', val)
})
</script>

<style scoped>
/* === 动态霓虹渐变背景 === */
@keyframes floatBg {
  0% {
    background-position: 0% 0%, 100% 0%, 50% 100%;
  }
  50% {
    background-position: 10% 5%, 90% 10%, 50% 95%;
  }
  100% {
    background-position: 0% 0%, 100% 0%, 50% 100%;
  }
}

/* === 毛玻璃容器 === */
.glass-container {
  backdrop-filter: blur(12px) saturate(120%);
  background: linear-gradient(
    180deg,
    rgba(255, 255, 255, 0.1),
    rgba(255, 255, 255, 0.04)
  );
  border: 1px solid rgba(255, 255, 255, 0.18);
  border-radius: 18px;
  padding: 22px 22px 26px;
  position: relative;
}

/* 霓虹边缘微光 */
.glass-container::after {
  content: '';
  position: absolute;
  inset: 0;
  border-radius: 18px;
  pointer-events: none;
  box-shadow: 0 0 40px rgba(126, 220, 255, 0.12),
    0 0 60px rgba(178, 139, 255, 0.08);
}

/* 标题区 */
.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.title {
  letter-spacing: 0.3px;
}
.header-actions :deep(.n-button) {
  transition: transform 0.2s ease;
}
.header-actions :deep(.n-button:hover) {
  transform: translateY(-1px);
}

@media (max-width: 860px) {
  .grid-two {
    grid-template-columns: 1fr;
  }
}

/* 卡片 */
.card {
  margin-bottom: 10px;
  border-radius: 14px;
  background: rgba(12, 18, 34, 0.55);
  box-shadow: 0 8px 18px rgba(0, 0, 0, 0.35);
  border: 1px solid rgba(120, 180, 255, 0.15);
  transition: transform 0.22s ease, box-shadow 0.22s ease,
    border-color 0.22s ease;
}
.card:hover {
  transform: translateY(-2px);
  box-shadow: 0 14px 28px rgba(0, 0, 0, 0.45);
  border-color: rgba(122, 252, 255, 0.28);
}
.card-title {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  margin-bottom: 6px;
  font-weight: 600;
  letter-spacing: 0.2px;
}
.tip {
  opacity: 0.9;
}

/* 头像行 */
.avatar-row {
  display: flex;
  gap: 10px;
  align-items: center;
}
.avatar {
  box-shadow: 0 0 0 2px rgba(126, 220, 255, 0.2);
}
.flex-1 {
  flex: 1;
}

.row-actions {
  margin-top: 5px;
  display: flex;
  gap: 8px;
}

/* 胶囊式单选组 */
.pill-group :deep(.n-radio-button) {
  border-radius: 999px !important;
  margin-right: 8px;
}
.pill-group :deep(.n-radio-button .n-radio-button__state-border) {
  border-radius: 999px !important;
}
.pill-group :deep(.n-radio-button--checked) {
  box-shadow: 0 0 0 2px rgba(122, 252, 255, 0.25) inset,
    0 0 18px rgba(122, 252, 255, 0.18);
}

/* 页脚操作 */
.footer-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

/* 输入 hover 微动效 */
:deep(.n-input) {
  transition: box-shadow 0.2s ease, transform 0.2s ease;
}
:deep(.n-input:hover) {
  box-shadow: 0 0 0 1px rgba(126, 220, 255, 0.18),
    0 6px 14px rgba(0, 0, 0, 0.35);
}
/* Divider 细光效 */
:deep(.n-divider) {
  --n-color: rgba(126, 220, 255, 0.18);
}
</style>
