<template>
  <div class="back-to-home">
    <button @click="goHome" class="home-button">🏠 返回首页</button>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'

const router = useRouter()

const goHome = () => {
  router.push('/person/study') // Navigate to /person/study route
}
</script>

<style scoped lang="less">
.home-button {
  padding: 10px 20px;
  background-color: #52cc52;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s;
  font-size: 16px;
}

.home-button:hover {
  background-color: #45b845;
}
</style>
