<template>
  <transition name="fade">
    <div v-if="visible" class="loading-overlay">
      <div class="spinner"></div>
      <p class="loading-text">{{ text }}</p>
    </div>
  </transition>
</template>

<script setup lang="ts">
defineProps({
  visible: { type: Boolean, default: false },
  text: { type: String, default: 'Loading...' },
})
</script>

<style scoped>
.loading-overlay {
  position: fixed;
  inset: 0;
  background: rgba(255, 255, 255, 0.6);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  z-index: 2000;
  backdrop-filter: blur(4px);
}

/* 旋转动画 */
.spinner {
  width: 50px;
  height: 50px;
  border: 4px solid rgba(252, 104, 104, 0.3);
  border-top-color: #fc6868;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}
@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

/* 渐隐动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

.loading-text {
  margin-top: 16px;
  font-size: 15px;
  color: #444;
  letter-spacing: 0.5px;
}
</style>
