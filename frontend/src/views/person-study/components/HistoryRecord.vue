<template>
  <div class="container">
    <div v-if="isLoading" class="loading-placeholder">
      Loading data...
      <!-- You can replace this with a spinner or any loading animation -->
    </div>
    <div v-else>
      <div class="hot-question">
        <div class="chapter-container">
          <div class="chapter-items">
            <div class="column">
              <li
                v-for="chapter in chapters.slice(0, 7)"
                :key="chapter.label"
                class="chapter-item"
                @click="goChapter(chapter)"
              >
                {{ chapter.label }}
              </li>
            </div>
            <div class="column">
              <li
                v-for="chapter in chapters.slice(7)"
                :key="chapter.label"
                class="chapter-item"
                @click="goChapter(chapter)"
              >
                {{ chapter.label }}
              </li>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { knowledgePoint } from '@/components/exercise/api/knowledge_point'

interface ChapterItem {
  label: string
  chapter?: string
  knowledgeId?: number
}

interface KnowledgeItem {
  knowledgeId: number
  knowledge: string
}

const javaChapters: ChapterItem[] = [
  'Java概述',
  'Java环境搭建和程序初体验',
  'java初体验',
  'Java类基础知识',
  'Java常用类',
  'Java异常和异常处理',
  'Java数据结构',
  'Java文件读写',
  'Java案例实践和总结',
  'package、import和classpath',
  'static、final和常量设计',
  '其他',
].map((chapter) => ({ label: chapter, chapter }))

const pythonFallbackChapters: ChapterItem[] = [
  'Python概述',
  'Python环境搭建和程序初体验',
  'Python基础语法',
  'Python函数与模块',
  'Python常用标准库',
  'Python异常和异常处理',
  'Python数据结构',
  'Python文件读写',
  'Python案例实践和总结',
  '模块、包和导入机制',
  '作用域与编码规范',
  'Python面向对象编程',
].map((label) => ({ label, chapter: 'Python知识点' }))

const router = useRouter()
const isPython = localStorage.getItem('assistantType') === 'python'
const chapters = ref<ChapterItem[]>(
  isPython ? pythonFallbackChapters : javaChapters,
)
const isLoading = ref(false)

onMounted(async () => {
  if (!isPython) return

  try {
    const knowledgeList = await knowledgePoint('')
    const pythonChapters = knowledgeList
      .filter(
        (item: KnowledgeItem) => item.knowledgeId && item.knowledge?.trim(),
      )
      .slice(0, 12)
      .map((item: KnowledgeItem) => ({
        label: item.knowledge.trim(),
        knowledgeId: item.knowledgeId,
      }))

    const existingLabels = new Set(
      pythonChapters.map((chapter) => chapter.label),
    )
    const fallbackChapters = pythonFallbackChapters.filter(
      (chapter) => !existingLabels.has(chapter.label),
    )

    chapters.value = [...pythonChapters, ...fallbackChapters].slice(0, 12)
  } catch (error) {
    console.error('获取 Python 知识点失败，将使用默认分类:', error)
  }
})

function goChapter(chapter: ChapterItem) {
  router.push({
    path: '/chapter/exercise',
    query: {
      title: chapter.label,
      chapter: chapter.chapter,
      knowledgeId: chapter.knowledgeId,
    },
  })
}
</script>

<style lang="scss" scoped>
.container {
  width: 100%;
  height: 100%;
  padding: 10px;
  /* Ensure container stretches to full viewport height */
}

.loading-placeholder {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 200px;
  /* Adjust height based on your design */
  font-size: 1.5rem;
  color: #555;
  /* Placeholder text color */
}

.hot-question {
  display: flex;
  justify-content: center;
  align-content: center;
  padding: 2px;
}

.hot-title {
  color: #ffffff;
  align-content: center;
  font-size: 24px;
  padding-left: 8px;
}

.hot-icon {
  align-content: center;
}

.chapter-container {
  display: flex;
  /* 使用 Flexbox 创建两列 */
  justify-content: center;
  align-items: center;
  align-content: center;
  justify-items: center;
}

.column {
  margin: 30px 15px;
  /* 调整列间距 */
}

.chapter-items {
  display: flex;
  justify-content: center;
  align-content: center;
  justify-items: center;
  align-items: center;
}

.chapter-item {
  background-color: rgba(255, 255, 255, 0.1);
  color: #ffffff;
  /* 半透明背景 */
  border-radius: 5px;
  /* 圆角 */
  padding: 10px 10px 10px 10px;
  /* 内边距 */
  margin-bottom: 10px;
  /* 列间间距 */
  list-style-type: none;
  /* 去掉点 */
}
</style>
