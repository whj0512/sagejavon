import type { App } from 'vue'
import { createI18n } from 'vue-i18n'
import merge from 'lodash/merge'
import mindMapMessages from '@/lang'
import enUS from './en-US'
import koKR from './ko-KR'
import zhCN from './zh-CN'
import zhTW from './zh-TW'
import ruRU from './ru-RU'
import { useAppStoreWithOut } from '@/store/modules/app'
import type { Language } from '@/store/modules/app/helper'
const STORAGE_KEY = 'app-language'
const supportedLocales: Language[] = [
  'zh-CN',
  'zh-TW',
  'en-US',
  'ko-KR',
  'ru-RU',
]
const storedLocale =
  localStorage.getItem(STORAGE_KEY) ||
  localStorage.getItem('SIMPLE_MIND_MAP_LANG')
const normalizedLocale =
  storedLocale === 'en'
    ? 'en-US'
    : storedLocale === 'zh'
    ? 'zh-CN'
    : storedLocale
const savedLocale: Language = supportedLocales.includes(
  normalizedLocale as Language,
)
  ? (normalizedLocale as Language)
  : 'zh-CN'
const appStore = useAppStoreWithOut()
appStore.setLanguage(savedLocale)

const englishMessages = merge({}, mindMapMessages.en, enUS)
const chineseMessages = merge({}, mindMapMessages.zh, zhCN)

const i18n = createI18n({
  legacy: false,
  locale: savedLocale,
  fallbackLocale: 'en-US',
  messages: {
    'en-US': englishMessages,
    'ko-KR': koKR,
    'zh-CN': chineseMessages,
    'zh-TW': zhTW,
    'ru-RU': ruRU,
    en: englishMessages,
    zh: chineseMessages,
  },
})

export const t = i18n.global.t

export function setLocale(locale: Language) {
  i18n.global.locale.value = locale
  appStore.setLanguage(locale)
  localStorage.setItem(STORAGE_KEY, locale) // ✅ 语言切换时保存
}

export function setupI18n(app: App) {
  app.use(i18n)
}

export default i18n
