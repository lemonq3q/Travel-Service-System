<template>
  <div class="home">
    <!-- 左侧菜单 - 整列固定 -->
    <div class="left">
      <MenuComponent></MenuComponent>
    </div>

    <!-- 右侧整体区域 -->
    <div class="right-container">
      <!-- 右侧顶部 Header -->
      <div class="top">
        <HeaderComponent></HeaderComponent>
      </div>

      <!-- 右侧内容区域 -->
      <div class="right">
        <router-view></router-view>
      </div>
    </div>
  </div>
</template>

<script setup>
import HeaderComponent from '@/components/HeaderComponent.vue';
import MenuComponent from '@/components/MenuComponent.vue';
</script>

<style scoped>
.home {
  width: 100%;
  height: 100vh;
  display: flex; /* 左右布局 */
  overflow: hidden;
}

/* ========== 左侧菜单 ========== */
.left {
  width: 220px; /* 左侧菜单宽度，可自行修改 */
  height: 100vh;
  background: #fff;
  z-index: 500;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
}

/* ========== 右侧容器 ========== */
.right-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* ========== 右侧顶部 ========== */
.top {
  width: 100%;
  /* height: 60px;  */
  z-index: 1000;
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.2);
}

/* ========== 右侧内容区域 ========== */
.right {
  flex: 1;
  min-width: 700px;
  overflow: auto;
  background-color: #f5f7fa;
  padding: 10px;
}
</style>