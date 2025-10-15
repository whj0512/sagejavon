<template>
  <div class="indexHeaderContainer" :class="{ active: active }">
    <div class="headerContent">
      <div class="logoBox">
        <span class="logo"></span>
        <span class="title">SageJavon</span>
      </div>
      <div class="nav">
        <div
          class="navItem"
          @click="jumpTop"
          :class="{ active: activeTab === 'home' }"
        >
          {{ t('home') }}
        </div>
        <!-- <div class="navItem" @click="useOnline">
          {{ t('online') }}
        </div>
        <div class="navItem" @click="jumpClient" :class="{ active: activeTab === 'client' }">
          {{ t('client') }}
        </div>
        <div class="navItem" @click="jumpHelp">
          {{ t('help') }}
        </div>
        <div class="navItem" @click="goToGitHub">
          {{ t('github') }}
        </div> -->
        <!-- 🌐 语言切换按钮 -->
        <div class="navItem" @click="toggleLang">
          {{ currentLocale === 'zh-CN' ? 'EN' : '中' }}
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, onUnmounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import i18n, { t as globalT, setLocale } from '@/locales'
import goToGitHub from './api/goToGitHub'
import { useAppStoreWithOut } from '@/store/modules/app' // 可选同步到 store

const t = globalT
const router = useRouter()
const active = ref(false)
const activeTab = ref('home')
const clientEl = ref<HTMLElement | null>(null)

const currentLocale = ref(i18n.global.locale.value)
const appStore = useAppStoreWithOut() // 可选

const toggleLang = () => {
  const newLocale = currentLocale.value === 'zh-CN' ? 'en-US' : 'zh-CN'
  currentLocale.value = newLocale
  setLocale(newLocale)
  appStore.setLanguage?.(newLocale) // ✅ 可选：更新 Pinia 中的语言状态
}

onMounted(() => {
  window.addEventListener('scroll', onScroll)
  clientEl.value = document.querySelector('.placeholder')
})

onUnmounted(() => {
  window.removeEventListener('scroll', onScroll)
})

const onScroll = () => {
  active.value = window.scrollY > 0
  const offsetTop = clientEl.value?.offsetTop ?? 0
  if (
    window.scrollY + window.innerHeight >= offsetTop &&
    window.scrollY <= offsetTop + (clientEl.value?.offsetHeight ?? 0)
  ) {
    activeTab.value = 'client'
  } else {
    activeTab.value = 'home'
  }
}

const useOnline = () => {
  router.push('/')
}

const jumpHelp = () => {
  router.push('/help/zh/') // 可根据 currentLocale 动态拼路径
}

const jumpTop = () => {
  window.scrollTo(0, 0)
}

const jumpClient = () => {
  if (clientEl.value) {
    window.scrollTo(0, clientEl.value.offsetTop - 76)
  }
}
</script>

<style lang="less" scoped>
.indexHeaderContainer {
  position: fixed;
  left: 0;
  top: 0;
  width: 100%;
  height: 76px;
  transition: all 0.2s;
  background-color: rgba(255, 255, 255, 0.48);
  backdrop-filter: blur(20px);
  border-bottom: 1px solid rgba(255, 255, 255, 0.9);
  z-index: 99;

  &.active {
    background-color: #fff;
    box-shadow: 0 5px 30px -10px rgba(0, 0, 0, 0.1);
  }

  .headerContent {
    height: 100%;
    max-width: 1140px;
    margin: 0 auto;
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0 12px;

    .logoBox {
      display: flex;
      align-items: center;

      .logo {
        width: 22px;
        height: 22px;
        background-image: url('../../../assets/sagejavon.png');
        background-size: cover;
        margin-right: 5px;
      }

      .title {
        color: #000;
        font-size: 20px;
      }
    }

    .nav {
      display: flex;
      align-items: center;

      .navItem {
        color: #828f99;
        font-size: 15px;
        transition: all 0.5s;
        margin-right: 40px;
        cursor: pointer;

        &:last-of-type {
          margin-right: 0;
        }

        &.active {
          color: #fc6868;
        }

        &:hover {
          color: #fc6868;
        }

        a {
          text-decoration: none;
          color: #828f99;

          &:hover {
            color: #fc6868;
          }
        }
      }
    }
  }
}

.simple-nav {
  display: none;
}

@media screen and (max-width: 646px) {
  .indexHeaderContainer .headerContent .nav {
    display: none;
  }

  .simple-nav {
    display: block;
  }
}
</style>
