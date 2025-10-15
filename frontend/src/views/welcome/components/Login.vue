<template>
  <div class="login-container">
    <div class="login-bg"></div>

    <!-- Loading 遮罩 -->
    <LoadingOverlay :visible="loading" :text="loadingText" />
    <!-- 模态框：语言选择 -->
    <div v-if="showLanguageModal">
      <n-modal preset="dialog" :mask-closable="false" title="选择学习语言">
        <div class="language-select">
          <button
            class="lang-btn java"
            style="cursor: pointer"
            @click.stop="selectLanguage('java')"
          >
            Java
          </button>
          <button
            class="lang-btn python"
            style="cursor: pointer"
            @click.stop="selectLanguage('python')"
          >
            Python
          </button>
        </div>
      </n-modal>
    </div>
    <div class="form-box" v-if="!showLanguageModal">
      <h2 class="form-title">{{ t(isLogin ? 'login' : 'register') }}</h2>

      <div class="input-group">
        <input
          v-model="account"
          type="text"
          :placeholder="t('accountPlaceholder')"
        />
        <input
          v-model="password"
          type="password"
          :placeholder="t('passwordPlaceholder')"
        />
        <input
          v-if="!isLogin"
          v-model="confirmPassword"
          type="password"
          :placeholder="t('confirmPasswordPlaceholder')"
        />
      </div>

      <button
        class="submit-btn"
        @click="isLogin ? handleLogin() : handleRegister()"
      >
        {{ t(isLogin ? 'login' : 'register') }}
      </button>

      <p class="switch-mode">
        <span>{{ t(isLogin ? 'noAccount' : 'hasAccount') }}</span>
        <a @click="toggleMode">{{ t(isLogin ? 'register' : 'login') }}</a>
      </p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { t } from '@/locales'
import { useMessage } from 'naive-ui'
import { register, login } from './api'
import LoadingOverlay from '@/components/LoadingOverlay/LoadingOverlay.vue'
import { router } from '../../../router/index'

const message = useMessage()
const account = ref('')
const password = ref('')
const confirmPassword = ref('')
const isLogin = ref(true)
const loading = ref(false)
const loadingText = ref('')
const showLanguageModal = ref(false)
const modalMounted = ref(false)

onMounted(() => {
  modalMounted.value = true
})

const showMsg = (type: 'success' | 'error' | 'warning', content: string) => {
  message[type](content, { closable: true, duration: 3000 })
}

function toggleMode() {
  isLogin.value = !isLogin.value
  confirmPassword.value = ''
}

function validateFields() {
  if (!account.value || !password.value) {
    showMsg('warning', t('fillAllFields'))
    return false
  }
  if (!isLogin.value && password.value !== confirmPassword.value) {
    showMsg('warning', t('passwordMismatch'))
    return false
  }
  return true
}

async function handleLogin() {
  if (!validateFields()) return
  loading.value = true
  loadingText.value = t('loggingIn') // 你可以在 locales 里定义 "loggingIn": "登录中..."
  try {
    const res = await login(account.value, password.value)
    if (res.status === 200) {
      showMsg('success', t('loginSuccess'))
      localStorage.setItem('userInfo', JSON.stringify(res))
      localStorage.setItem('user-token', res.data.data.token)
      localStorage.setItem('user-id', res.data.data.id)
      showLanguageModal.value = true
    } else showMsg('error', t('loginFailed'))
  } catch {
    showMsg('error', t('networkError'))
  } finally {
    loading.value = false
  }
}

async function handleRegister() {
  if (!validateFields()) return
  loading.value = true
  loadingText.value = t('registering') // 你可以在 locales 定义 "registering": "注册中..."
  try {
    const res = await register(account.value, password.value)
    if (res.data.code === 'ALREADY_REGISTER')
      showMsg('error', t('registerFailed'))
    else if (res.status === 200 && res.data.code === 'SUCCESS') {
      showMsg('success', t('registerSuccess'))
      isLogin.value = true
      password.value = ''
      confirmPassword.value = ''
    } else showMsg('error', t('registerFailed'))
  } catch {
    showMsg('error', t('networkError'))
  } finally {
    loading.value = false
  }
}

// 用户选择语言
function selectLanguage(lang: string) {
  localStorage.setItem('assistantType', lang)
  showLanguageModal.value = false
  message.success(`已选择 ${lang.toUpperCase()} 学习模式`)
  router.push(`/chat/${lang.toUpperCase()}`)
}
</script>

<style scoped>
/* 背景与居中 */
.login-container {
  position: relative;
  display: flex;
  justify-content: center;
  align-items: center;
  overflow: hidden;
}

/* 背景装饰模糊层 */
.login-bg {
  position: absolute;
  width: 100%;
  height: 100%;
  opacity: 0.4;
}

/* 表单容器 */
.form-box {
  position: relative;
  z-index: 1;
  width: 360px;
  padding: 40px 36px;
  border-radius: 16px;
  text-align: center;
  transition: all 0.3s ease;
}
.form-box:hover {
  transform: translateY(-4px);
}

/* 标题 */
.form-title {
  font-size: 26px;
  color: #333;
  font-weight: 600;
  margin-bottom: 24px;
}

/* 输入框组 */
.input-group input {
  width: 100%;
  padding: 12px 14px;
  margin-bottom: 14px;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  font-size: 14px;
  background: #f9f9f9;
  transition: all 0.2s;
}
.input-group input:focus {
  outline: none;
  border-color: #ff7e7e;
  background: #fff;
  box-shadow: 0 0 5px rgba(255, 126, 126, 0.3);
}

/* 提交按钮 */
.submit-btn {
  width: 100%;
  padding: 12px;
  background: linear-gradient(90deg, #ff7e7e, #fc6868);
  color: #fff;
  border: none;
  border-radius: 8px;
  font-size: 15px;
  cursor: pointer;
  font-weight: 600;
  transition: all 0.3s;
}
.submit-btn:hover {
  background: linear-gradient(90deg, #ff8b8b, #ff6a6a);
  transform: translateY(-1px);
}

/* 切换模式 */
.switch-mode {
  margin-top: 18px;
  font-size: 14px;
  color: #555;
}
.switch-mode a {
  color: #fc6868;
  font-weight: 500;
  margin-left: 6px;
  cursor: pointer;
  transition: color 0.2s;
}
.switch-mode a:hover {
  color: #ff4d4d;
}
.language-select {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-top: 16px;
}

.lang-btn {
  padding: 10px 24px;
  font-size: 15px;
  font-weight: 600;
  border-radius: 8px;
  cursor: pointer;
  border: none;
  color: white;
  transition: all 0.3s;
  z-index: 9999;
}
.lang-btn.java {
  background: linear-gradient(45deg, #0074d9, #00a8ff);
}
.lang-btn.java:hover {
  background: linear-gradient(45deg, #0088ff, #4dc3ff);
}
.lang-btn.python {
  background: linear-gradient(45deg, #f9ca24, #f0932b);
}
.lang-btn.python:hover {
  background: linear-gradient(45deg, #fbc531, #e1a52a);
}
</style>
