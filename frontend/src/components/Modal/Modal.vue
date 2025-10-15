<template>
  <div v-show="isVisible" class="overlay" @click="cancel"></div>
  <div v-show="isVisible" class="assistant-modal">
    <div class="assistant-modal-header">
      <span>Select Assistant Type</span>
      <button @click="cancel" class="close-btn">X</button>
    </div>
    <div class="assistant-modal-body">
      <button @click="selectAssistant('java')">Java 小助手</button>
      <button @click="selectAssistant('python')">Python 小助手</button>
    </div>
  </div>
</template>

<script setup>
import { ref, defineExpose } from 'vue'

// 控制模态框显示与隐藏
const isVisible = ref(false)

// 选择助手类型并保存选择
const selectAssistant = (type) => {
  localStorage.setItem('assistantType', type) // 将选择保存在 localStorage 中
  isVisible.value = false // 关闭模态框
  emit('assistant-selected', type) // 向父组件发送事件，告知选择了哪种助手
}

// 控制模态框是否显示
const showModal = () => {
  isVisible.value = true
}

// 关闭模态框
const cancel = () => {
  isVisible.value = false
}

// 暴露 showModal 方法，供父组件调用
defineExpose({
  showModal,
})
</script>

<style scoped>
/* 模态框样式 */
.assistant-modal {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background-color: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
  z-index: 1001;
}

.assistant-modal-header {
  font-size: 18px;
  margin-bottom: 15px;
  display: flex;
  justify-content: space-between;
}

.assistant-modal-body button {
  padding: 10px 20px;
  margin: 10px;
  font-size: 16px;
  cursor: pointer;
  border: none;
  border-radius: 5px;
}

.assistant-modal-body button:hover {
  background-color: #f0f0f0;
}

.overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.3);
  z-index: 1000;
}
</style>
