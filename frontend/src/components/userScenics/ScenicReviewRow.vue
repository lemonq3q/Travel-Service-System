<template>
  <div class="row">
    <div class="avatar">
      <el-avatar :size="40" :src="review.user.avatarUrl" />
    </div>
    <div class="main">
      <div class="top">
        <div class="name">{{ review.user.name }}</div>
        <div class="rate">
          <el-rate :model-value="review.rating" disabled allow-half />
          <span class="num">{{ Number(review.rating).toFixed(1) }}</span>
        </div>
        <div class="time">{{ formatTime(review.createTime) }}</div>
      </div>

      <div class="content">{{ review.content }}</div>

      <div class="images" v-if="review.images && review.images.length">
        <el-image
          v-for="(src, idx) in review.images"
          :key="src"
          class="img"
          :src="src"
          fit="cover"
          :preview-src-list="review.images"
          :initial-index="idx"
          preview-teleported
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { defineProps } from 'vue';

defineProps({
  review: {
    type: Object,
    required: true
  }
});

const formatTime = (iso) => {
  const d = new Date(iso);
  if (Number.isNaN(d.getTime())) return '';
  const y = d.getFullYear();
  const m = String(d.getMonth() + 1).padStart(2, '0');
  const day = String(d.getDate()).padStart(2, '0');
  return `${y}-${m}-${day}`;
};
</script>

<style scoped>
.row {
  display: grid;
  grid-template-columns: 44px 1fr;
  gap: 12px;
  padding: 14px 0;
  border-bottom: 1px solid #e5e7eb;
}

.main {
  text-align: left;
}

.top {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.name {
  font-weight: 800;
  color: #0f172a;
}

.rate {
  display: flex;
  align-items: center;
  gap: 6px;
}

.num {
  font-weight: 700;
  color: #0f172a;
}

.time {
  margin-left: auto;
  font-size: 12px;
  color: #94a3b8;
}

.content {
  margin-top: 8px;
  font-size: 13px;
  color: #334155;
  line-height: 1.65;
}

.images {
  margin-top: 10px;
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: 8px;
}

.img {
  width: 100%;
  height: 76px;
  border-radius: 12px;
  overflow: hidden;
}

@media (max-width: 920px) {
  .images {
    grid-template-columns: repeat(3, 1fr);
  }
}
</style>

