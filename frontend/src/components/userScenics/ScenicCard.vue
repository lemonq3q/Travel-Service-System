<template>
  <div class="card" @click="emit('open', props.item.id)">
    <div class="media">
      <el-image class="img" :src="props.item.coverImageUrl" fit="cover">
        <template #placeholder>
          <div class="img-skeleton"></div>
        </template>
      </el-image>
      <div class="badge" :class="props.item.needTicket ? 'ticket' : 'free'">
        <span v-if="props.item.needTicket">¥{{ Number(props.item.price || 0).toFixed(0) }}</span>
        <span v-else>免票</span>
      </div>
    </div>

    <div class="content">
      <div class="name">{{ props.item.name }}</div>
      <div class="meta">
        <div class="rating">
          <el-rate :model-value="props.item.rating" disabled allow-half />
          <span class="num">{{ Number(props.item.rating || 0).toFixed(1) }}</span>
        </div>
        <div class="hot">
          <el-icon><TrendCharts /></el-icon>
          <span>{{ Number(props.item.hotValue || 0).toFixed(1) }}</span>
        </div>
      </div>
      <div class="address">
        <el-icon><Location /></el-icon>
        <span class="address-text">{{ props.item.address }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { defineEmits, defineProps } from 'vue';
import { Location, TrendCharts } from '@element-plus/icons-vue';

const props = defineProps({
  item: {
    type: Object,
    required: true
  }
});

const emit = defineEmits(['open']);
</script>

<style scoped>
.card {
  width: 100%;
  background: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 14px;
  overflow: hidden;
  cursor: pointer;
  transition: box-shadow 0.15s ease, transform 0.15s ease;
}

.card:hover {
  box-shadow: 0 12px 30px rgba(15, 23, 42, 0.12);
  transform: translateY(-1px);
}

.media {
  position: relative;
  height: 160px;
}

.img {
  width: 100%;
  height: 100%;
}

.img-skeleton {
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, #f1f5f9, #e2e8f0, #f1f5f9);
  background-size: 200% 100%;
  animation: shimmer 1.2s infinite;
}

@keyframes shimmer {
  0% { background-position: 200% 0; }
  100% { background-position: -200% 0; }
}

.badge {
  position: absolute;
  left: 10px;
  top: 10px;
  padding: 6px 10px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 800;
  backdrop-filter: blur(6px);
}

.badge.free {
  color: #0f766e;
  background: rgba(13, 148, 136, 0.12);
  border: 1px solid rgba(13, 148, 136, 0.25);
}

.badge.ticket {
  color: #b45309;
  background: rgba(245, 158, 11, 0.14);
  border: 1px solid rgba(245, 158, 11, 0.28);
}

.content {
  padding: 12px 12px 14px;
  text-align: left;
}

.name {
  font-size: 16px;
  font-weight: 800;
  color: #0f172a;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.meta {
  margin-top: 8px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
}

.rating {
  display: flex;
  align-items: center;
  gap: 8px;
}

.num {
  font-weight: 800;
  color: #0f172a;
}

.hot {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: #64748b;
  padding: 2px 8px;
  border-radius: 999px;
  border: 1px solid #e2e8f0;
}

.address {
  margin-top: 8px;
  display: flex;
  align-items: center;
  gap: 6px;
  color: #475569;
  font-size: 13px;
  min-width: 0;
}

.address-text {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>

