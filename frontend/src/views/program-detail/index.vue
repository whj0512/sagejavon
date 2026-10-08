<template>
  <div class="full-height">
    <BackToHome />
    <QuestionHover :index="2" />
    <PanelBox class="program-detail-container">
      <template #question>
        <div class="sidebar">
          <div class="tabs">
            <div class="circle-flex">
              <div class="circle"></div>
              <div
                @click="activeTab = 'content'"
                :class="{ active: activeTab === 'content' }"
              >
                {{ t('contentTab') }}
              </div>
            </div>
            <div class="circle-flex">
              <div class="circle"></div>
              <div
                @click="activeTab = 'history'"
                :class="{ active: activeTab === 'history' }"
              >
                {{ t('historyTab') }}
              </div>
            </div>
          </div>

          <div class="content">
            <div v-if="activeTab === 'content'">
              <div v-if="isPageLoading">正在加载题目…</div>
              <div v-else-if="loadError" role="alert">
                {{ loadError }}
                <NButton v-if="questionId !== null" @click="loadQuestion"
                  >重试</NButton
                >
              </div>
              <div v-else-if="programDetail" class="program-content">
                <div class="sub-section">
                  <span class="tag">{{ programDetail.difficulty }}</span>
                  <span
                    class="knowledge-point"
                    v-for="(knowledge, index) in programDetail.knowledgeConcept"
                    :key="index"
                  >
                    <i class="icon-tag"></i> {{ knowledge.knowledge }}
                  </span>
                </div>
                <pre class="code-block">
                  <v-md-preview :text="programDetail.questionText"></v-md-preview>
                </pre>
              </div>
            </div>

            <div v-if="activeTab === 'history'">
              <div v-if="records.length !== 0">
                <history-list :records="records"></history-list>
              </div>
              <div v-else>{{ t('noHistoryRecords') }}</div>
            </div>
          </div>

          <!-- Feedback Buttons -->
          <div class="feedback-buttons">
            <button
              @click="toggleLike"
              :class="{ liked: isLiked }"
              :disabled="!programDetail || isReviewing"
              class="feedback-button"
            >
              <span class="icon">👍</span>
              <span>{{ t('likeButton') }}</span>
            </button>
            <button
              @click="toggleDislike"
              :class="{ disliked: isDisliked }"
              :disabled="!programDetail || isReviewing"
              class="feedback-button"
            >
              <span class="icon">👎</span>
              <span>{{ t('dislikeButton') }}</span>
            </button>
          </div>
        </div>
      </template>

      <template #code>
        <div class="main">
          <div class="tabs-two">
            <div class="circle-flex">
              <div class="circle"></div>
              <button
                @click="activeTab = 'content'"
                :class="{ active: activeTab === 'content' }"
              >
                {{ t('submitCode') }}
              </button>
            </div>
          </div>
          <div class="editor">
            <monacoEditor
              v-model="code"
              :language="language"
              width="100%"
              height="100%"
            ></monacoEditor>
          </div>
          <NButton
            v-if="!isLoading"
            style="width: 100%; margin-top: 5px"
            type="primary"
            :disabled="isPageLoading || !programDetail"
            @click="submitCode"
          >
            {{ t('submitCode') }}
          </NButton>
          <NButton
            v-if="isLoading"
            style="width: 100%; margin-top: 5px"
            type="info"
            disabled
          >
            {{ t('scoringInProgress') }}
          </NButton>
        </div>
      </template>
    </PanelBox>

    <NModal
      v-model:show="showModal"
      class="custom-card"
      preset="card"
      :style="bodyStyle"
      :title="'Your Score: ' + score"
      size="huge"
      :bordered="false"
      :segmented="segmented"
    >
      Correct Answer:
      {{ correctAnswer == null ? 'Not Available' : correctAnswer }}
      <v-md-preview :text="suggestion"></v-md-preview>
    </NModal>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { NButton, NModal, useMessage } from 'naive-ui'
import QuestionHover from '@/components/question-list/QuestionHover.vue'
import BackToHome from '@/components/ReturnHome/ReturnHome.vue'
import historyList from '@/components/exercise/history-list.vue'
import { t } from '@/locales'
import PanelBox from '../panel-box/index.vue'
import monacoEditor from './components/monacoEditor.vue'
import { programDetails } from './api/program_detail'
import { questionCode } from './api/question_code'
import { recordList } from './api/record_list'
import { reviewQuestion } from './api/question_review'

interface KnowledgeConcept {
  knowledgeId: number
  knowledge: string
}

export interface ExerciseRecordList {
  difficulty: number
  exerciseId: number
  knowledgeConcept: KnowledgeConcept[]
  questionText: string
  recordId: number
  score: number
  submitTime: string
  type: number
}

export interface Program {
  difficulty: number
  id: number
  knowledgeConcept: KnowledgeConcept[]
  questionText: string
  review?: number
}

const message = useMessage()
const route = useRoute()
const questionId = computed(() => {
  const value = route.query.id
  if (typeof value !== 'string' || !/^\d+$/.test(value)) return null
  const id = Number(value)
  return Number.isSafeInteger(id) && id > 0 ? id : null
})
const language =
  localStorage.getItem('assistantType') === 'python' ? 'python' : 'java'
const bodyStyle = { width: '700px' }
const segmented = { content: 'soft' as const, footer: 'soft' as const }
const programDetail = ref<Program | null>(null)
const records = ref<ExerciseRecordList[]>([])
const activeTab = ref('content')
const code = ref('')
const score = ref(0)
const suggestion = ref('')
const correctAnswer = ref<string | null>(null)
const showModal = ref(false)
const isLoading = ref(false)
const isPageLoading = ref(false)
const loadError = ref('')
const historyReady = ref(false)
const submitNum = ref(0)
const isLiked = ref(false)
const isDisliked = ref(false)
const isReviewing = ref(false)
let loadVersion = 0

function errorMessage(error: unknown, fallback: string) {
  return error instanceof Error ? error.message : fallback
}

async function refreshRecords(id: number, version: number) {
  const { data: result } = await recordList(id)
  if (result.code !== 'SUCCESS' || !Array.isArray(result.data)) {
    throw new Error(
      result.code !== 'SUCCESS' && result.message
        ? result.message
        : '获取历史记录失败，请重试',
    )
  }
  if (version !== loadVersion) return
  records.value = result.data
  submitNum.value = Math.max(submitNum.value, result.data.length)
  historyReady.value = true
}

async function loadQuestion() {
  const version = ++loadVersion
  const id = questionId.value
  programDetail.value = null
  records.value = []
  submitNum.value = 0
  historyReady.value = false
  code.value = ''
  correctAnswer.value = null
  showModal.value = false
  activeTab.value = 'content'
  isLiked.value = false
  isDisliked.value = false
  isLoading.value = false
  isReviewing.value = false
  loadError.value = ''
  isPageLoading.value = false
  if (id === null) {
    loadError.value = '题目编号无效，请从题目列表重新进入'
    return
  }
  isPageLoading.value = true
  await Promise.all([
    (async () => {
      try {
        const { data: result } = await programDetails(id)
        if (result.code !== 'SUCCESS' || !result.data) {
          throw new Error(
            result.message && result.code !== 'SUCCESS'
              ? result.message
              : '题目不存在',
          )
        }
        if (version !== loadVersion) return
        programDetail.value = result.data
        isLiked.value = result.data.review === 1
        isDisliked.value = result.data.review === -1
      } catch (error) {
        if (version === loadVersion) {
          loadError.value = errorMessage(error, '获取题目失败，请重试')
        }
      }
    })(),
    refreshRecords(id, version).catch((error) => {
      if (version === loadVersion) {
        message.warning(errorMessage(error, '获取历史记录失败，请重试'))
      }
    }),
  ])
  if (version === loadVersion) isPageLoading.value = false
}

watch(questionId, loadQuestion, { immediate: true })

async function submitCode() {
  const id = questionId.value
  if (isLoading.value || isPageLoading.value) return
  if (id === null || !programDetail.value) {
    message.warning('请先加载有效题目')
    return
  }
  if (!code.value.trim()) {
    message.warning('请输入代码后再提交')
    return
  }
  const version = loadVersion
  isLoading.value = true
  try {
    // 历史记录加载失败时先重试，避免使用错误的提交次数。
    if (!historyReady.value) await refreshRecords(id, version)
    if (version !== loadVersion) return
    const nextSubmitNum = submitNum.value + 1
    const { data: result } = await questionCode({
      id,
      answer: code.value,
      submitNum: nextSubmitNum,
    })
    if (version !== loadVersion) return
    if (result.code !== 'SUCCESS' || !result.data) {
      throw new Error(
        result.code === 'LLM_ERROR'
          ? '评分服务暂不可用，请稍后重试'
          : result.code !== 'SUCCESS' && result.message
          ? result.message
          : '提交失败，请重试',
      )
    }
    if (typeof result.data.score !== 'number') {
      throw new Error('评分结果无效，请稍后重试')
    }
    submitNum.value = nextSubmitNum
    score.value = result.data.score
    suggestion.value = result.data.suggestion ?? ''
    correctAnswer.value = result.data.correctAnswer ?? null
    showModal.value = true
    try {
      await refreshRecords(id, version)
    } catch (error) {
      if (version === loadVersion) {
        message.warning('提交成功，但历史记录刷新失败，请稍后刷新页面')
      }
    }
  } catch (error) {
    if (version === loadVersion) {
      message.error(errorMessage(error, '提交失败，请检查网络后重试'))
    }
  } finally {
    if (version === loadVersion) isLoading.value = false
  }
}

function toggleLike() {
  return submitReview(isLiked.value ? 0 : 1)
}

function toggleDislike() {
  return submitReview(isDisliked.value ? 0 : -1)
}

async function submitReview(reviewType: number) {
  if (!programDetail.value || isReviewing.value) return
  const version = loadVersion
  isReviewing.value = true
  try {
    const { data: result } = await reviewQuestion(
      programDetail.value.id,
      reviewType,
    )
    if (version !== loadVersion) return
    if (result.code !== 'SUCCESS') {
      throw new Error(result.message || '评价失败，请重试')
    }
    isLiked.value = reviewType === 1
    isDisliked.value = reviewType === -1
  } catch (error) {
    if (version === loadVersion) {
      message.error(errorMessage(error, '评价失败，请重试'))
    }
  } finally {
    if (version === loadVersion) isReviewing.value = false
  }
}
</script>

<style scoped>
.full-height {
  height: 100vh; /* Ensure full viewport height */
  display: flex;
  justify-content: center; /* Center horizontally */
  align-items: center; /* Center vertically */
  flex-direction: column; /* Stack children vertically */
}

.container {
  flex: 1; /* Take remaining space */
  padding: 20px;
  width: 100vw;
  display: flex;
  justify-content: center; /* Center content horizontally */
  align-items: center; /* Center content vertically */
}
.program-detail-container {
  display: flex;
  padding: 12px;
  /* Add padding to container */
  height: 100%;
  width: 100%;
  /* Adjust the height to make the container full height */
  justify-content: space-between;
  overflow: hidden;
  /* Hide overflow to avoid scrollbars */
  gap: 12px;
  /* Add gap between sidebar and main */
}

.sidebar {
  flex: 2;
  border: 1px solid #f2f1f1;
  height: 100%;
  overflow: auto;
  border-radius: 10px;
  /* Enable scrolling for the sidebar */
  /* Add padding inside the sidebar */
}

.main {
  border-radius: 10px;
  flex: 1;
  height: 100%;
  background-color: #fff;
  justify-content: center;
  align-items: center;
  border: 1px solid #f2f1f1;
  /* Add padding inside the main content */
}

.editor {
  width: 100%;
  height: 90%;
  display: flex;
  justify-content: center;
  align-items: center;
  color: #d3d1d1;
}

.circle {
  width: 20px;
  height: 20px;
  background-color: #d6d3d3;
  border-radius: 100%;
}

.circle-flex {
  display: flex;
  justify-items: center;
  justify-content: center;
  align-items: center;
  line-height: 1.4;
  margin-left: 10px;
}

.tabs {
  height: 35px;
  display: flex;
  margin-bottom: 20px;
  justify-content: left;
  background-color: #f2f1f1;
}

.tabs div {
  padding: 5px;
  cursor: pointer;
}

.tabs-two {
  border-top-left-radius: 10px;
  border-top-right-radius: 10px;
  height: 35px;
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  background-color: #f2f1f1;
}

.tabs-two button {
  flex: 1;
  padding: 5px;
}

.tabs div {
  transition: background-color 0.3s;
  color: #aaa;
}

.tabs div.active {
  color: black;
}

.tabs div:not(:last-child) {
  border-right: none;
}

.code-block {
}

.sub-section {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-left: 20px;
  /* Adds space between the two elements */
}

.tag {
  background-color: #f5f5f5;
  color: rgb(214, 62, 62);
  padding: 5px 10px;
  border-radius: 15px;
  font-weight: bold;
}

.knowledge-point {
  display: flex;
  align-items: center;
  background-color: #f5f5f5;
  padding: 5px 10px;
  border-radius: 15px;
  color: #666;
  font-weight: bold;
}

.icon-tag {
  display: inline-block;
  width: 1em;
  height: 1em;
  margin-right: 5px;
  background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path fill="%23666" d="M21.41 11.58l-9-9A2 2 0 0010.34 2H4a2 2 0 00-2 2v6.34a2 2 0 00.58 1.42l9 9a2 2 0 002.83 0l6.34-6.34a2 2 0 000-2.83zM6.5 8.5A1.5 1.5 0 118 7a1.5 1.5 0 01-1.5 1.5z"/></svg>')
    no-repeat center center;
  background-size: contain;
}

.feedback-buttons {
  position: fixed;
  top: 20px;
  right: 20px;
  display: flex;
  flex-direction: column;
  gap: 10px;
  z-index: 1000;
}

.feedback-button {
  display: flex;
  align-items: center;
  padding: 10px 15px;
  border: 1px solid #ddd;
  border-radius: 5px;
  background-color: #fff;
  cursor: pointer;
  transition: background-color 0.3s, border-color 0.3s;
}

.feedback-button:hover {
  background-color: #f0f0f0;
  border-color: #ccc;
}

.feedback-button .icon {
  margin-right: 8px;
}

.liked {
  border-color: #4caf50;
  color: #4caf50;
}

.disliked {
  border-color: #f44336;
  color: #f44336;
}
.vuepress-markdown-body {
  word-wrap: break-word; /* Allow text to break and wrap */
  white-space: pre-wrap; /* Ensure white spaces are preserved */
  overflow-wrap: break-word; /* For handling long words without spaces */
  max-width: 100%; /* Prevent it from overflowing the container */
  word-break: break-word; /* Prevent overflow due to long words */
}

.v-md-editor-preview {
  width: 100%;
  overflow-wrap: break-word; /* Break long words */
}

pre {
  white-space: pre-wrap; /* Allow wrapping of preformatted text */
  word-wrap: break-word; /* Break words if necessary */
}

p,
h1,
h2,
h3,
h4,
h5,
h6 {
  word-wrap: break-word;
  overflow-wrap: break-word; /* Ensure text within headings also wraps properly */
}
</style>
