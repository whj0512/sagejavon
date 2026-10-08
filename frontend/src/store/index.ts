import type { App } from 'vue'
import { defineStore } from 'pinia'
import { store } from './helper'

export function setupStore(app: App) {
  app.use(store)
}

export * from './modules'
export * from './helper'

export const xmindMap = defineStore('XMindMap', {
  state: () => ({
    openThumb: Boolean(Number(localStorage.getItem('openThumb'))),
    readOnly: Boolean(Number(localStorage.getItem('readOnly'))),
    immersion: false,
  }),
})

export default store
