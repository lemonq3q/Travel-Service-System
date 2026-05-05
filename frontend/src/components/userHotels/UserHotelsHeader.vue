<template>
  <div class="wrap">
    <div class="inner">
      <div class="brand" @click="goHotels">
        <img class="logo" src="/src/assets/logo.png" alt="logo" />
        <div class="title">找酒店</div>
      </div>

      <div class="actions">
        <el-button v-if="isLogin" type="primary" plain @click="goAdmin">后台管理</el-button>
        <el-button v-else type="primary" @click="goLogin">登录 / 注册</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue';
import { useRouter } from 'vue-router';
import Storage from '@/utils/storage';

const router = useRouter();

const isLogin = computed(() => {
  const token = Storage.get('token');
  return token !== null && token !== '' && token !== undefined;
});

const goHotels = () => router.push('/home/hotels');
const goLogin = () => router.push('/login');
const goAdmin = () => router.push('/home');
</script>

<style scoped>
.wrap {
  width: 100%;
  background: rgba(255, 255, 255, 0.92);
  border-bottom: 1px solid #e5e7eb;
  backdrop-filter: blur(8px);
}

.inner {
  max-width: 1200px;
  margin: 0 auto;
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 16px;
}

.brand {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  user-select: none;
}

.logo {
  width: 28px;
  height: 28px;
  border-radius: 6px;
}

.title {
  font-size: 16px;
  font-weight: 700;
  color: #0f172a;
}

.actions {
  display: flex;
  align-items: center;
  gap: 8px;
}
</style>
