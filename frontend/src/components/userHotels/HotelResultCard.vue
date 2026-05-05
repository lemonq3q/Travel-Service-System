<template>
  <div class="card" @click="emit('open', props.item.id)">
    <div class="media">
      <el-image class="img" :src="props.item.coverImageUrl" fit="cover">
        <template #placeholder>
          <div class="img-skeleton"></div>
        </template>
      </el-image>
    </div>

    <div class="content">
      <div class="top">
        <div class="name">{{ props.item.name }}</div>
        <div class="meta">
          <div class="address">
            <el-icon><Location /></el-icon>
            <span class="address-text">{{ props.item.address }}</span>
          </div>
          <div class="rating">
            <el-rate :model-value="props.item.rating" disabled allow-half />
            <div class="rating-num">{{ props.item.rating.toFixed(1) }}</div>
            <div class="stars">{{ props.item.starLevel }}星</div>
          </div>
        </div>
      </div>

      <div class="bottom">
        <div class="cheapest">
          <div class="room">{{ props.item.cheapest.roomName }} · 可住{{ props.item.cheapest.maxPeople }}人</div>
          <div class="price">
            <span class="money">¥{{ props.item.cheapest.price.amount.toFixed(0) }}</span>
            <span class="unit">起</span>
          </div>
        </div>
        <div class="fake-btn">查看详情</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { defineEmits, defineProps } from 'vue';
import { Location } from '@element-plus/icons-vue';

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
  display: grid;
  grid-template-columns: 260px 1fr;
  cursor: pointer;
  transition: box-shadow 0.15s ease, transform 0.15s ease;
}

.card:hover {
  box-shadow: 0 12px 30px rgba(15, 23, 42, 0.12);
  transform: translateY(-1px);
}

.media {
  height: 180px;
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

.content {
  padding: 16px 16px 14px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.name {
  font-size: 18px;
  font-weight: 700;
  color: #0f172a;
  text-align: left;
}

.meta {
  margin-top: 8px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.address {
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

.rating {
  display: flex;
  align-items: center;
  gap: 10px;
}

.rating-num {
  font-weight: 700;
  color: #0f172a;
}

.stars {
  font-size: 12px;
  color: #64748b;
  border: 1px solid #e2e8f0;
  border-radius: 999px;
  padding: 2px 8px;
}

.bottom {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  margin-top: 12px;
}

.cheapest {
  text-align: left;
}

.room {
  font-size: 13px;
  color: #334155;
}

.price {
  margin-top: 6px;
  display: flex;
  align-items: baseline;
  gap: 6px;
}

.money {
  font-size: 22px;
  font-weight: 800;
  color: #ef4444;
}

.unit {
  font-size: 12px;
  color: #64748b;
}

.fake-btn {
  font-size: 13px;
  color: #1677ff;
  padding: 8px 12px;
  border-radius: 10px;
  border: 1px solid rgba(22, 119, 255, 0.28);
  background: rgba(22, 119, 255, 0.06);
  user-select: none;
}

@media (max-width: 920px) {
  .card {
    grid-template-columns: 1fr;
  }
  .media {
    height: 200px;
  }
}
</style>

