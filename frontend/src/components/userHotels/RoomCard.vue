<template>
  <div class="card">
    <div class="head">
      <div class="name">{{ room.name }}</div>
      <div class="sub">可住 {{ room.maxPeople }} 人</div>
    </div>

    <div class="body">
      <div class="left">
        <div class="media" v-if="images.length">
          <div class="media-big" @click="openImagesDialog">
            <el-image class="img" :src="images[0]" fit="cover" />
          </div>
          <div class="media-row">
            <div class="media-small-item" v-for="(src, idx) in images.slice(1, 3)" :key="src" @click="openImagesDialog">
              <el-image class="img" :src="src" fit="cover" />
              <div v-if="idx === 1 && images.length > 3" class="overlay">+{{ images.length - 3 }}</div>
            </div>
          </div>
        </div>

        <div class="features" v-if="featureTags.length">
          <div class="feature" v-for="t in featureTags" :key="t">
            <el-icon><CircleCheck /></el-icon>
            <span>{{ t }}</span>
          </div>
        </div>

        <div class="desc">{{ room.desc }}</div>
      </div>

      <div class="right">
        <div class="table">
          <div class="row head-row">
            <div class="col summary">房型</div>
            <div class="col num">人数</div>
            <div class="col price">价格</div>
            <div class="col action"></div>
          </div>
          <div class="row body-row" v-for="p in shownPackages" :key="p.id">
            <div class="col summary" style="white-space: pre-line;">{{ formatSummary(p.summaryJson) }}</div>
            <div class="col num">{{ room.maxPeople }}</div>
            <div class="col price">¥{{ p.price.amount.toFixed(0) }}</div>
            <div class="col action">
              <el-button type="primary" size="large" @click="book(p)">预定</el-button>
            </div>
          </div>
        </div>

        <div class="more" v-if="packages.length > 2">
          <el-button link type="primary" @click="toggleAll">
            {{ showAll ? '收起' : '显示全部房型' }}
          </el-button>
        </div>
      </div>
    </div>

    <el-dialog v-model="imagesDialogVisible" width="980px" align-center>
      <template #header>
        <div class="dialog-title">{{ room.name }} · 全部照片</div>
      </template>
      <div class="dialog-grid">
        <el-image
          v-for="(src, idx) in images"
          :key="src"
          class="dialog-img"
          :src="src"
          fit="cover"
          :preview-src-list="images"
          :initial-index="idx"
          preview-teleported
        />
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, defineEmits, defineProps, ref } from 'vue';
import { CircleCheck } from '@element-plus/icons-vue';

const props = defineProps({
  room: {
    type: Object,
    required: true
  }
});

const emit = defineEmits(['book']);

const showAll = ref(false);
const imagesDialogVisible = ref(false);

const room = computed(() => props.room);
const images = computed(() => (Array.isArray(room.value.images) ? room.value.images : []).filter(Boolean));
const packages = computed(() => (Array.isArray(room.value.roomTypePriceList) ? room.value.roomTypePriceList : []));

const shownPackages = computed(() => (showAll.value ? packages.value : packages.value.slice(0, 2)));

const featureTags = computed(() => {
  const raw = room.value.featureJson;
  if (!raw) return [];
  try {
    const arr = JSON.parse(raw);
    return Array.isArray(arr) ? arr.filter(Boolean) : [];
  } catch {
    return [];
  }
});

const formatSummary = (summaryJson) => {
  if (!summaryJson) return '';
  try {
    const arr = JSON.parse(summaryJson);
    return Array.isArray(arr) ? arr.filter(Boolean).join(' \n ') : String(summaryJson);
  } catch {
    return String(summaryJson);
  }
};

const toggleAll = () => {
  showAll.value = !showAll.value;
};

const openImagesDialog = () => {
  imagesDialogVisible.value = true;
};

const book = (pkg) => {
  emit('book', {
    room: room.value,
    pkg
  });
};
</script>

<style scoped>
.card {
  width: 100%;
  background: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 14px;
  overflow: hidden;
}

.head {
  padding: 14px 16px 8px;
  display: flex;
  align-items: baseline;
  justify-content: space-between;
}

.name {
  font-size: 16px;
  font-weight: 800;
  color: #0f172a;
  text-align: left;
}

.sub {
  font-size: 12px;
  color: #64748b;
}

.body {
  padding: 0 16px 16px;
  display: grid;
  grid-template-columns: 1fr 2fr;
  gap: 14px;
}

.media {
  display: grid;
  grid-template-rows: 180px 88px;
  gap: 10px;
}

.media-big {
  height: 180px;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
}

.media-row {
  height: 88px;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
}

.media-small-item {
  position: relative;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
}

.img {
  width: 100%;
  height: 100%;
}

.overlay {
  position: absolute;
  inset: 0;
  background: rgba(15, 23, 42, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-weight: 800;
}

.features {
  margin-top: 12px;
  display: grid;
  grid-template-columns: 1fr;
  gap: 6px;
}

.feature {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: #334155;
  text-align: left;
}

.desc {
  margin-top: 12px;
  font-size: 13px;
  color: #475569;
  text-align: left;
  line-height: 1.6;
}

.right {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.table {
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  overflow: hidden;
}

.row {
  display: grid;
  grid-template-columns: 5fr 1fr 2fr 2fr;
  gap: 10px;
  align-items: center;
  padding: 10px 12px;
  border-top: 1px solid #e2e8f0;
}

.head-row {
  border-top: none;
  background: #f8fafc;
  font-size: 12px;
  color: #64748b;
  font-weight: 700;
}

.body-row {
  min-height: 200px;
}

.col {
  text-align: left;
}

.price {
  text-align: right;
  color: #ef4444;
  font-weight: 800;
}

.action {
  text-align: right;
}

.more {
  margin-top: 8px;
  text-align: right;
}

.dialog-title {
  font-weight: 700;
  color: #0f172a;
}

.dialog-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
}

.dialog-img {
  width: 100%;
  height: 140px;
  border-radius: 12px;
  overflow: hidden;
}

@media (max-width: 920px) {
  .body {
    grid-template-columns: 1fr;
  }
  .media {
    grid-template-rows: 180px 88px;
  }
  .dialog-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
