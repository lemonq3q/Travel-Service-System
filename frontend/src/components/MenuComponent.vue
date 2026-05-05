<template>
  <div class="menu_container">
    <el-menu
      :default-active="activeMenu"
      class="menu"
      :default-openeds="['5','7']"
      router
    > 

      <router-link to="/home/userManagement" v-if="isHasPerm('all')">
        <el-menu-item index="/home/userManagement">
          <el-icon><User /></el-icon>
          <span>用户管理</span>
        </el-menu-item>
      </router-link>

      <router-link to="/home/hotelManagement" v-if="isHasPerm('all')">
        <el-menu-item index="/home/hotelManagement">
          <el-icon><OfficeBuilding /></el-icon>
          <span>酒店管理</span>
        </el-menu-item>
      </router-link>

      <router-link to="/home/scenicManagement" v-if="isHasPerm('all')">
        <el-menu-item index="/home/scenicManagement">
          <el-icon><Location /></el-icon>
          <span>景点管理</span>
        </el-menu-item>
      </router-link>

      <router-link to="/home/hotels">
        <el-menu-item index="/home/hotels">
          <el-icon><Search /></el-icon>
          <span>酒店浏览</span>
        </el-menu-item>
      </router-link>

      <router-link to="/home/scenics">
        <el-menu-item index="/home/scenics">
          <el-icon><Location /></el-icon>
          <span>景区浏览</span>
        </el-menu-item>
      </router-link>

      <router-link to="/home/tickets">
        <el-menu-item index="/home/tickets">
          <el-icon><Promotion /></el-icon>
          <span>车票机票</span>
        </el-menu-item>
      </router-link>

      <router-link to="/home/guides">
        <el-menu-item index="/home/guides">
          <el-icon><Reading /></el-icon>
          <span>攻略浏览</span>
        </el-menu-item>
      </router-link>

      <router-link to="/home/guides/new">
        <el-menu-item index="/home/guides/new">
          <el-icon><EditPen /></el-icon>
          <span>我的攻略</span>
        </el-menu-item>
      </router-link>

      <router-link to="/home/orders">
        <el-menu-item index="/home/orders">
          <el-icon><Tickets /></el-icon>
          <span>我的订单</span>
        </el-menu-item>
      </router-link>

      <router-link to="/home/personalCenter">
        <el-menu-item index="/home/personalCenter">
          <el-icon><HomeFilled /></el-icon>
          <span>个人中心</span>
        </el-menu-item>
      </router-link>


    </el-menu>
  </div>
</template>

<script setup>
import { onMounted, watch, ref } from 'vue';
import { useRoute } from 'vue-router';
import { 
 User, HomeFilled, OfficeBuilding, Location, Search, Tickets, Reading, EditPen, Promotion
} from '@element-plus/icons-vue';
import { isHasPerm } from '@/utils/authenticate';

// 获取当前路由实例
const route = useRoute();
// 定义响应式的激活菜单索引
const activeMenu = ref('');

// 初始化/更新激活菜单
const updateActiveMenu = () => {
  const path = route.path;
  if (path.startsWith('/home/scenics/')) {
    activeMenu.value = '/home/scenics';
    return;
  }
  if (path.startsWith('/home/hotels/')) {
    activeMenu.value = '/home/hotels';
    return;
  }
  if (path.startsWith('/home/guides/new')) {
    activeMenu.value = '/home/guides/new';
    return;
  }
  if (path.startsWith('/home/guides/')) {
    activeMenu.value = '/home/guides';
    return;
  }
  if (path.startsWith('/home/tickets')) {
    activeMenu.value = '/home/tickets';
    return;
  }
  activeMenu.value = path;
};

// 页面挂载时初始化
onMounted(() => {
  updateActiveMenu();
});

// 监听路由变化，实时更新激活状态
watch(
  () => route.path,
  () => {
    updateActiveMenu();
  },
  { immediate: true }
);
</script>

<style scoped>
.menu_container {
  height: 100%;
  overflow: auto
}
.menu {
  height: 100%;
  display: flex;
  flex-direction: column;
  /* gap: 5px;  */
  padding: 10px 0; /* 上下内边距，让顶部底部也有留白 */
  box-sizing: border-box;
}

:deep(.el-menu-item) {
  border-radius: 6px !important;
  transition: all 0.2s ease;
  margin: 0 6px; 
  margin-top: 10px;
}

:deep(.el-sub-menu__title) {
  border-radius: 6px !important;
  margin: 0 6px;
}

/* hover 效果 */
:deep(.el-menu-item:hover),
:deep(.el-sub-menu__title:hover) {
  border-radius: 20px !important;
  background-color: #f0f7ff !important;
}

/* 选中项 蓝色背景+白色文字+圆角 */
:deep(.el-menu-item.is-active) {
  background-color: #409eff !important;
  color: #fff !important;
  border-radius: 20px !important;
}
:deep(.el-menu-item.is-active svg) {
  color: #fff !important;
}

</style>
