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
              <div class="program-content">
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
              class="feedback-button"
            >
              <span class="icon">👍</span>
              <span>{{ t('likeButton') }}</span>
            </button>
            <button
              @click="toggleDislike"
              :class="{ disliked: isDisliked }"
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
              @editor-mounted="editorMounted"
            ></monacoEditor>
          </div>
          <NButton
            v-if="!isLoading"
            style="width: 100%; margin-top: 5px"
            type="primary"
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
import QuestionHover from '@/components/question-list/QuestionHover.vue'
import PanelBox from '../panel-box/index.vue'
import { ref, onMounted } from 'vue'
import * as monaco from 'monaco-editor'
import monacoEditor from './components/monacoEditor.vue'
import DragBall from './components/DragBall.vue'
import BackToHome from '@/components/ReturnHome/ReturnHome.vue'
import { useRoute } from 'vue-router'
import { programDetails } from './api/program_detail'
import { NButton, NModal } from 'naive-ui'
import { questionCode } from './api/question_code'
import ModalDialog from './components/ModalDialog.vue'
import historyList from '@/components/exercise/history-list.vue'
import { recordList } from './api/record_list'
import { reviewQuestion } from './api/question_review'
import { codeRecordDetail } from '@/components/exercise/api/code_record_detail'
import { t as globalT } from '@/locales'

const t = globalT

const bodyStyle = ref({
  width: '700px',
})
const segmented = ref({
  content: 'soft',
  footer: 'soft',
})
const showModal = ref(false)

const route = useRoute()
const questionId = ref(route.query.id)
console.log(questionId.value)

const language = ref('java')
const editorMounted = (editor: monaco.editor.IStandaloneCodeEditor) => {
  console.log('editor实例加载完成', editor)
}

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

const records = ref<ExerciseRecordList[]>([])
const correctAnswer = ref('')
const correct = ref('')
// const response = await recordList(Number(questionId.value));
// console.log(response)
// records.value = response.data.data
// console.log(records.value)

onMounted(async () => {
  try {
    const res = await recordList(questionId.value)
    console.log(res)
    records.value = res.data.data
  } catch (error) {
    console.error('获取记录失败:', error)
  }

  try {
    const response = await codeRecordDetail(questionId.value)
    console.log(response)
    correct.value = response.data.data.correctAnswer
  } catch (error) {
    console.error('获取正确答案失败:', error)
  }

  try {
    const programRes = await programDetails(questionId.value)
    if (programRes.status === 200) {
      programDetail.value = programRes.data.data
      exerciseId.value = programDetail.value.id // 只有在数据加载完成后才赋值
    }
  } catch (err) {
    console.error('获取程序详情失败:', err)
  }
})

export interface Program {
  difficulty: number
  /**
   * 题目id
   */
  id: number
  knowledgeConcept: KnowledgeConcept[]
  /**
   * 题目内容
   */
  questionText: string
}

export interface KnowledgeConcept {
  /**
   * 知识点
   */
  knowledge: string
  /**
   * 知识点id
   */
  knowledgeId: number
}

const getProgramDetail = async () => {
  // localStorage.removeItem('chatStorage')

  try {
    const res = await programDetails(questionId.value) // 假设 chatList 是一个异步请求函数
    if (res.status === 200) {
      programDetail.value = res.data.data
    } else {
      // 更新失败
    }
  } catch (err) {
    console.error('获取推荐列表失败:', err)
  }
}
getProgramDetail()

const programDetail = ref<Program>({})

const activeTab = ref('content')

const code = ref('')

const score = ref(0)

const suggestion = ref('')

const isLoading = ref(false)

const submitNum = ref(0)

function submitCode(choice: string) {
  isLoading.value = true
  submitNum.value += 1
  const request = {
    id: questionId.value,
    answer: code.value,
    submitNum: submitNum.value,
  }
  console.log(request)

  questionCode(request)
    .then((response) => {
      console.log('提交成功:', response.data)
      score.value = response.data.data.score
      // suggestion.value = response.data.data.suggestion
      console.log(response.data.data)
      suggestion.value = response.data.data.suggestion
      correctAnswer.value = response.data.data.correctAnswer
      showModal.value = true
      isLoading.value = false
    })
    .catch((error) => {
      console.error('提交失败:', error)
      // Handle error
    })
}

const isLiked = ref(false)
const isDisliked = ref(false)
async function toggleLike() {
  if (isDisliked.value) {
    await submitReview(0) // 先取消踩
    isDisliked.value = false
  }
  await submitReview(isLiked.value ? 0 : 1) // 点赞或取消点赞
}

async function toggleDislike() {
  if (isLiked.value) {
    await submitReview(0) // 先取消点赞
    isLiked.value = false
  }
  await submitReview(isDisliked.value ? 0 : -1) // 踩或取消踩
}

const exerciseId = ref<number>(programDetail.value.id) // 从题目中获取ID

// 提交评价
async function submitReview(reviewType: number) {
  // 检查 exerciseId 是否定义
  if (typeof exerciseId.value === 'undefined' || exerciseId.value === null) {
    console.error('exerciseId is undefined')
    return
  }
  if (typeof reviewType !== 'number') {
    console.error('Invalid reviewType', reviewType)
    return
  }

  try {
    const response = await reviewQuestion(
      exerciseId.value.toString(),
      reviewType,
    )
    if (response && response.status === 200) {
      console.log('评价成功:', response.data)
      if (reviewType === 1) {
        isLiked.value = true
        isDisliked.value = false
      } else if (reviewType === -1) {
        isLiked.value = false
        isDisliked.value = true
      } else {
        isLiked.value = false
        isDisliked.value = false
      }
    } else {
      console.error('评价失败:', response?.data || '未知错误')
    }
  } catch (error) {
    console.error('提交评价失败:', error.message)
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
