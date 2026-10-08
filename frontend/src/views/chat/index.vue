<script setup lang="ts">
import type { Ref } from 'vue'
import { computed, onMounted, onUnmounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import {
  NAutoComplete,
  NButton,
  NImageGroup,
  NInput,
  NSpace,
  useDialog,
  useMessage,
} from 'naive-ui'
import html2canvas from 'html2canvas'
import { storeToRefs } from 'pinia'
import { Message } from './components'
import HeaderComponent from './components/Header/index.vue'
import { useScroll } from './hooks/useScroll'
import { useChat } from './hooks/useChat'
import { useUsingContext } from './hooks/useUsingContext'
import { smartQueryStream } from './api/smart_query_stream'
import { chatMessage } from './api/chat_message'
import { useBasicLayout } from '@/hooks/useBasicLayout'
import { useChatStore, usePromptStore } from '@/store'
import { updateKnowledgeNodes } from './api/update_knowledge_graph'
import { t } from '@/locales'
import QuestionHover from '@/components/question-list/QuestionHover.vue'
import { base_url } from '../../utils/python-request'
import { rag_url } from '../../utils/rag_url'
let controller = new AbortController()

// const openLongReply = import.meta.env.VITE_GLOB_OPEN_LONG_REPLY === 'true'

const route = useRoute()
const dialog = useDialog()
const ms = useMessage()

const chatStore = useChatStore()

const { isMobile } = useBasicLayout()
const { addChat, updateChat, updateChatSome, getChatByUuidAndIndex } = useChat()
const { scrollRef, scrollToBottom, scrollToBottomIfAtBottom } = useScroll()
const { usingContext, toggleUsingContext } = useUsingContext()

const uuid = localStorage.getItem('active-uuid')

const dataSources = computed(() =>
  chatStore.getChatByUuid(+localStorage.getItem('active-uuid')),
)
console.log(dataSources)
const getEnabledNetwork = computed(() => chatStore.getEnabledNetwork)
// const conversationList = computed(() => dataSources.value.filter(item => (!item.inversion && !!item.conversationOptions)))

// chatStore.clearChatByUuid(+uuid)

const prompt = ref<string>('')
const loading = ref<boolean>(false)
const inputRef = ref<Ref | null>(null)

// 添加PromptStore
const promptStore = usePromptStore()

// 使用storeToRefs，保证store修改后，联想部分能够重新渲染
const { promptList: promptTemplate } = storeToRefs<any>(promptStore)

// 未知原因刷新页面，loading 状态不会重置，手动重置
dataSources.value.forEach((item, index) => {
  if (item.loading)
    updateChatSome(+localStorage.getItem('active-uuid'), index, {
      loading: false,
    })
})
function handleSubmit() {
  onConversation()
}

async function onConversation() {
  const message = prompt.value
  if (loading.value || !message.trim()) return
  const chatId = Number(localStorage.getItem('active-uuid'))

  // 将用户的聊天保存到数据库
  chatMessage({
    chatId,
    role: 0,
    content: message,
  })
    .then((res) => {
      if (res.status === 200) {
        console.log('数据库添加成功')
      }
    })
    .catch((err) => {
      console.error('新增失败:', err)
    })

  // 先添加用户消息
  addChat(chatId, {
    dateTime: new Date().toLocaleString(),
    text: message,
    inversion: true,
    error: false,
    conversationOptions: null,
    requestOptions: { prompt: message, options: null },
  })

  prompt.value = ''
  await generateAnswer(message, chatId)
}

async function onRegenerate(index: number) {
  if (loading.value) return
  const chatId = Number(localStorage.getItem('active-uuid'))
  const answer = getChatByUuidAndIndex(chatId, index)
  if (!answer || answer.inversion) return

  // Older messages may not contain requestOptions; recover their preceding query.
  const previousQuestion = dataSources.value
    .slice(0, index)
    .reverse()
    .find((item) => item.inversion)
  const message = answer.requestOptions?.prompt || previousQuestion?.text
  if (!message?.trim()) {
    ms.warning('找不到原始问题，无法重新生成，请重新发送问题')
    return
  }

  await generateAnswer(message, chatId, index)
}

async function generateAnswer(
  message: string,
  chatId: number,
  retryIndex?: number,
) {
  controller = new AbortController()
  const requestController = controller
  const pendingAnswer: Chat.Chat = {
    dateTime: new Date().toLocaleString(),
    text: 'SageJavon is thinking....',
    loading: true,
    inversion: false,
    error: false,
    conversationOptions: null,
    requestOptions: { prompt: message, options: null },
  }
  if (retryIndex === undefined) addChat(chatId, pendingAnswer)
  else updateChat(chatId, retryIndex, pendingAnswer)

  const responseIndex = retryIndex ?? dataSources.value.length - 1
  scrollToBottom()
  loading.value = true

  try {
    // 发起后端请求获取模型响应
    const response = await fetch(
      rag_url + '/open_kf_api/queries/smart_query_stream',
      {
        method: 'POST',
        signal: requestController.signal,
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          query: message,
          user_id: '9ddc73e1-4992-4618-9e58-5bdf57bf3b91',
        }),
      },
    )

    if (!response.ok) {
      const errorBody = await response.json().catch(() => null)
      throw new Error(
        errorBody?.message || `请求失败（HTTP ${response.status}）`,
      )
    }
    if (!response.body) throw new Error('服务器未返回响应内容')
    scrollToBottom()

    const reader = response.body
      .pipeThrough(new TextDecoderStream())
      .getReader()
    let textContent = ''

    // 使用逐字输出的方式
    const writer = async () => {
      while (true) {
        const { value, done } = await reader.read()
        if (done) break

        // 每次获取一个字符并加入到当前文本中
        textContent += value

        // 更新聊天内容，逐字显示
        updateChat(chatId, responseIndex, {
          dateTime: new Date().toLocaleString(),
          text: textContent, // 将每次读取到的内容更新到 chat
          inversion: false,
          error: false,
          loading: false,
          conversationOptions: {},
          requestOptions: { prompt: message, options: {} },
        })
        scrollToBottom()
      }
    }

    // 启动逐字输出
    try {
      await writer()
    } finally {
      reader.releaseLock()
    }
    const finalResponse = textContent
    if (!finalResponse) throw new Error('模型未返回回答，请重试')

    // 更新知识图谱
    updateKnowledgeNodes(localStorage.getItem('user-id'), message).catch(
      (error) => console.error('更新知识图谱失败:', error),
    )

    // 将模型的回复保存到数据库中
    chatMessage({
      chatId,
      role: 1,
      content: finalResponse,
    })
      .then((res) => {
        if (res.status === 200) {
          console.log('数据库添加成功')
        }
      })
      .catch((err) => {
        console.error('新增失败:', err)
      })
  } catch (error: any) {
    console.error('发生错误:', error)
    updateChatSome(chatId, responseIndex, {
      text:
        error.name === 'AbortError'
          ? '已停止生成'
          : error.message || '请求失败，请检查算法服务是否正常运行',
      error: error.name !== 'AbortError',
      loading: false,
    })
    scrollToBottom()
  } finally {
    if (controller === requestController) loading.value = false
  }
}

function handleExport() {
  if (loading.value) return

  const d = dialog.warning({
    title: t('chat.exportImage'),
    content: t('chat.exportImageConfirm'),
    positiveText: t('common.yes'),
    negativeText: t('common.no'),
    onPositiveClick: async () => {
      try {
        d.loading = true
        const ele = document.getElementById('image-wrapper')
        const canvas = await html2canvas(ele as HTMLDivElement, {
          useCORS: true,
        })
        const imgUrl = canvas.toDataURL('image/png')
        const tempLink = document.createElement('a')
        tempLink.style.display = 'none'
        tempLink.href = imgUrl
        tempLink.setAttribute('download', 'chat-shot.png')
        if (typeof tempLink.download === 'undefined')
          tempLink.setAttribute('target', '_blank')

        document.body.appendChild(tempLink)
        tempLink.click()
        document.body.removeChild(tempLink)
        window.URL.revokeObjectURL(imgUrl)
        d.loading = false
        ms.success(t('chat.exportSuccess'))
        Promise.resolve()
      } catch (error: any) {
        console.error('error', error)
        ms.error(t('chat.exportFailed'))
      } finally {
        d.loading = false
      }
    },
  })
}

function handleDelete(index: number) {
  if (loading.value) return

  dialog.warning({
    title: t('chat.deleteMessage'),
    content: t('chat.deleteMessageConfirm'),
    positiveText: t('common.yes'),
    negativeText: t('common.no'),
    onPositiveClick: () => {
      chatStore.deleteChatByUuid(+localStorage.getItem('active-uuid'), index)
    },
  })
}

function handleToggleNetwork() {
  chatStore.toggleNetwork()
}

function handleClear() {
  if (loading.value) return

  dialog.warning({
    title: t('chat.clearChat'),
    content: t('chat.clearChatConfirm'),
    positiveText: t('common.yes'),
    negativeText: t('common.no'),
    onPositiveClick: () => {
      chatStore.clearChatByUuid(+localStorage.getItem('active-uuid'))
    },
  })
}

function handleEnter(event: KeyboardEvent) {
  if (!isMobile.value) {
    if (event.key === 'Enter' && !event.shiftKey) {
      event.preventDefault()
      handleSubmit()
    }
  } else {
    if (event.key === 'Enter' && event.ctrlKey) {
      event.preventDefault()
      handleSubmit()
    }
  }
}

function handleStop() {
  if (loading.value) {
    controller.abort()
    loading.value = false
  }
}

// 可优化部分
// 搜索选项计算，这里使用value作为索引项，所以当出现重复value时渲染异常(多项同时出现选中效果)
// 理想状态下其实应该是key作为索引项,但官方的renderOption会出现问题，所以就需要value反renderLabel实现
const searchOptions = computed(() => {
  if (prompt.value.startsWith('/')) {
    return promptTemplate.value
      .filter((item: { key: string }) =>
        item.key
          .toLowerCase()
          .includes(prompt.value.substring(1).toLowerCase()),
      )
      .map((obj: { value: any }) => {
        return {
          label: obj.value,
          value: obj.value,
        }
      })
  } else {
    return []
  }
})

// value反渲染key
const renderOption = (option: { label: string }) => {
  for (const i of promptTemplate.value) {
    if (i.value === option.label) return [i.key]
  }
  return []
}

const placeholder = computed(() => {
  if (isMobile.value) return t('placeholderMobile')
  return t('placeholder')
})

const buttonDisabled = computed(() => {
  return loading.value || !prompt.value || prompt.value.trim() === ''
})

const footerClass = computed(() => {
  let classes = ['p-4']
  if (isMobile.value)
    classes = [
      'sticky',
      'left-0',
      'bottom-0',
      'right-0',
      'p-2',
      'pr-3',
      'overflow-hidden',
    ]
  return classes
})

onMounted(() => {
  scrollToBottom()
  if (inputRef.value && !isMobile.value) inputRef.value?.focus()
})

onUnmounted(() => {
  if (loading.value) controller.abort()
})
</script>

<template>
  <div
    style="background-color: rgba(3, 34, 81, 0.1)"
    class="flex flex-col w-full h-full"
  >
    <QuestionHover :index="0" />
    <HeaderComponent
      v-if="isMobile"
      :using-context="usingContext"
      @export="handleExport"
      @handle-clear="handleClear"
    />
    <main class="flex-1 overflow-hidden">
      <div
        id="scrollRef"
        ref="scrollRef"
        class="h-full overflow-hidden overflow-y-auto"
      >
        <div
          id="image-wrapper"
          class="w-full max-w-screen-xl m-auto dark:bg-[#101014]"
          :class="[isMobile ? 'p-2' : 'p-4']"
        >
          <template v-if="!dataSources.length">
            <div
              class="flex items-center flex-col justify-center mt-4 text-center"
            >
              <NImageGroup>
                <NSpace align="center" justify="center" />
              </NImageGroup>
            </div>
          </template>
          <template v-else>
            <Message
              v-for="(item, index) of dataSources"
              :key="index"
              :date-time="item.dateTime"
              :text="item.text"
              :inversion="item.inversion"
              :error="item.error"
              :loading="item.loading"
              @regenerate="onRegenerate(index)"
              @delete="handleDelete(index)"
            />
            <div class="sticky bottom-0 left-0 flex justify-center">
              <NButton v-if="loading" type="warning" @click="handleStop">
                <template #icon>
                  <SvgIcon icon="ri:stop-circle-line" />
                </template>
                {{ t('common.stopResponding') }}
              </NButton>
            </div>
          </template>
        </div>
      </div>
    </main>
    <footer :class="footerClass">
      <div class="flex items-center justify-between space-x-2">
        <HoverButton
          :tooltip="
            getEnabledNetwork
              ? '点击关闭联网功能，关闭联网能极大加快响应速度'
              : '点击开启联网功能，开启后会自动从互联网获得信息来回答您'
          "
        >
          <!-- <span class="text-xl text-[#4f555e]" @click="handleClear">
              <span style="color: #2979ff; width: 20px; display: inline-block;" v-if="getEnabledNetwork">联网开启</span>
              <span style="color: red; width: 20px; display: inline-block;" v-if="!getEnabledNetwork">联网关闭</span>
            </span> -->
          <!-- <n-switch v-model:value="getEnabledNetwork" @update:value="handleToggleNetwork" /> -->
          <SvgIcon
            :style="getEnabledNetwork ? { color: 'rgba(3, 34, 81, 1)' } : ''"
            class="text-lg"
            icon="zondicons:network"
            @click="handleToggleNetwork"
          />
        </HoverButton>
        <NAutoComplete
          v-model:value="prompt"
          :options="searchOptions"
          :render-label="renderOption"
        >
          <template #default="{ handleInput, handleBlur, handleFocus }">
            <NInput
              style="border-radius: 20px"
              ref="inputRef"
              v-model:value="prompt"
              type="textarea"
              :placeholder="placeholder"
              :autosize="{ minRows: 1, maxRows: isMobile ? 4 : 8 }"
              @input="handleInput"
              @focus="handleFocus"
              @blur="handleBlur"
              @keypress="handleEnter"
            />
          </template>
        </NAutoComplete>
        <HoverButton v-if="!isMobile" @click="handleExport">
          <span class="text-xl text-[#4f555e] dark:text-white">
            <SvgIcon icon="ri:download-2-line" />
          </span>
        </HoverButton>

        <HoverButton v-if="!isMobile" @click="toggleUsingContext">
          <span
            class="text-xl"
            :class="{
              'text-[#4f555e]': usingContext,
              'text-[#E44446FF]': !usingContext,
            }"
          >
            <SvgIcon icon="ri:chat-history-line" />
          </span>
        </HoverButton>

        <NButton
          style="background: #ff6100"
          :disabled="buttonDisabled"
          @click="handleSubmit()"
        >
          <template #icon>
            <span style="color: #ffffff">
              <SvgIcon icon="ri:send-plane-fill" />
            </span>
          </template>
        </NButton>
      </div>
    </footer>
  </div>
</template>
