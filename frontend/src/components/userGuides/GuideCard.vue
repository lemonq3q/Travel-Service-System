<template>
  <div class="card" @click="emit('open', props.item.id)">
    <div class="media">
      <el-image class="img" :src="props.item.coverUrl" fit="cover">
        <template #placeholder>
          <div class="img-skeleton"></div>
        </template>
      </el-image>
    </div>

    <div class="content">
      <div class="title">{{ props.item.title }}</div>
      <div class="meta">
        <div class="author">
          <img class="avatar" :src="props.item.author?.avatarUrl || defaultAvatarUrl" alt="" />
          <span class="name">{{ props.item.author?.name || '匿名' }}</span>
        </div>
        <div class="time">{{ formatTime(props.item.createTime) }}</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { defineEmits, defineProps } from 'vue';
import defaultAvatarUrl from '@/assets/user.png';

const props = defineProps({
  item: {
    type: Object,
    required: true
  }
});

const emit = defineEmits(['open']);

const pad2 = (n) => String(n).padStart(2, '0');

const formatTime = (iso) => {
  if (!iso) return '';
  const d = new Date(iso);
  if (d.toString() === 'Invalid Date') return String(iso);
  return `${d.getFullYear()}-${pad2(d.getMonth() + 1)}-${pad2(d.getDate())} ${pad2(d.getHours())}:${pad2(d.getMinutes())}`;
};
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
  0% {
    background-position: 200% 0;
  }
  100% {
    background-position: -200% 0;
  }
}

.content {
  padding: 12px 12px 14px;
  text-align: left;
}

.title {
  font-size: 15px;
  font-weight: 900;
  color: #0f172a;
  line-height: 1.4;
  height: 42px;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.meta {
  margin-top: 10px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
}

.author {
  display: flex;
  align-items: center;
  gap: 8px;
  min-width: 0;
}

.avatar {
  width: 24px;
  height: 24px;
  border-radius: 999px;
  border: 1px solid #e5e7eb;
  background: #fff;
}

.name {
  font-size: 12px;
  font-weight: 800;
  color: #334155;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.time {
  font-size: 12px;
  color: #94a3b8;
  white-space: nowrap;
}
</style>

