<template>
  <div class="page">
    <div class="main" ref="mainScrollRef">
      <div class="container">
        <div class="heading">
          <div class="h1">发现值得奔赴的风景</div>
          <div class="h2">按城市、门票与评分筛选，向下滚动加载更多</div>
        </div>

        <div class="grid">
          <div class="filters">
            <el-card class="filter-card" shadow="never">
              <div class="filter-title">筛选</div>
              <el-form :model="filters" label-position="top">
                <el-form-item label="城市">
                  <AreaSelect v-model="filters.cityCode" level="city" placeholder="请选择城市" />
                </el-form-item>

                <el-form-item label="是否需要门票">
                  <el-select v-model="filters.needTicket" placeholder="全部" clearable class="full">
                    <el-option label="不需要门票" :value="0" />
                    <el-option label="需要门票" :value="1" />
                  </el-select>
                </el-form-item>

                <el-form-item label="评分下限">
                  <el-slider v-model="filters.ratingMin" :min="0" :max="5" :step="0.1" show-input />
                </el-form-item>
              </el-form>

              <div class="filter-actions">
                <el-button type="primary" :loading="loading && items.length === 0" @click="apply">应用筛选</el-button>
                <el-button @click="reset">重置</el-button>
              </div>
            </el-card>
          </div>

          <div class="results">
            <el-card class="hot-card" shadow="never">
              <div class="hot-head">
                <div class="hot-title">热门推荐</div>
                <div class="hot-sub">按热度值精选</div>
              </div>
              <div class="hot-strip" v-if="hotItems.length">
                <div class="hot-item" v-for="h in hotItems" :key="h.id" @click="openDetail(h.id)">
                  <el-image class="hot-img" :src="h.coverImageUrl" fit="cover" />
                  <div class="hot-name">{{ h.name }}</div>
                </div>
              </div>
              <el-empty v-else description="暂无热门推荐" />
            </el-card>

            <div
              class="list"
              v-infinite-scroll="loadMore"
              :infinite-scroll-disabled="loading || noMore"
              :infinite-scroll-distance="260"
              :infinite-scroll-container="mainScrollRef"
            >
              <ScenicCard v-for="s in items" :key="s.id" :item="s" @open="openDetail" />

              <div v-if="loading && items.length === 0" class="skeletons">
                <div class="skeleton" v-for="i in 9" :key="i"></div>
              </div>

              <el-empty v-if="!loading && items.length === 0" description="没有找到符合条件的景区" />

              <div v-if="loading && items.length > 0" class="loading-more">
                <el-icon class="spin"><Loading /></el-icon>
                <span>加载中...</span>
              </div>
              <div v-if="noMore && items.length > 0" class="end">没有更多了</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { Loading } from '@element-plus/icons-vue';

import AreaSelect from '@/components/AreaSelect.vue';
import ScenicCard from '@/components/userScenics/ScenicCard.vue';
import Storage from '@/utils/storage';
import { listHotScenics, searchScenics } from '@/api/publicScenic';

const router = useRouter();
const route = useRoute();

const STATE_KEY = 'user_scenics_search_state';

const mainScrollRef = ref(null);

const filters = reactive({
  cityCode: '',
  needTicket: null,
  ratingMin: 4.0
});

const items = ref([]);
const hotItems = ref([]);
const cursor = ref(null);
const loading = ref(false);
const noMore = ref(false);

const persistState = () => {
  const scrollTop = mainScrollRef.value ? mainScrollRef.value.scrollTop : 0;
  Storage.set(
    STATE_KEY,
    {
      filters: {
        cityCode: filters.cityCode,
        needTicket: filters.needTicket,
        ratingMin: filters.ratingMin
      },
      items: items.value,
      cursor: cursor.value,
      noMore: noMore.value,
      scrollTop
    },
    60 * 60
  );
};

const restoreState = () => {
  const state = Storage.get(STATE_KEY);
  if (!state) return false;
  const f = state.filters || {};
  filters.cityCode = f.cityCode || '';
  filters.needTicket = f.needTicket ?? null;
  filters.ratingMin = Number.isFinite(Number(f.ratingMin)) ? Number(f.ratingMin) : 4.0;
  items.value = Array.isArray(state.items) ? state.items : [];
  cursor.value = state.cursor ?? null;
  noMore.value = !!state.noMore;
  setTimeout(() => {
    if (mainScrollRef.value) {
      mainScrollRef.value.scrollTop = Number(state.scrollTop || 0);
    }
  }, 0);
  return true;
};

const fetchHot = async () => {
  const res = await listHotScenics({ limit: 6 });
  const data = res?.data?.data;
  hotItems.value = Array.isArray(data?.items) ? data.items : [];
};

const fetchPage = async ({ resetList } = {}) => {
  if (loading.value) return;
  if (!resetList && noMore.value) return;
  loading.value = true;
  try {
    const res = await searchScenics({
      cursor: resetList ? null : cursor.value,
      limit: 9,
      filters
    });
    const data = res?.data?.data;
    const nextItems = Array.isArray(data?.items) ? data.items : [];
    const nextCursor = data?.nextCursor ?? null;
    if (resetList) {
      items.value = nextItems;
    } else {
      items.value = [...items.value, ...nextItems];
    }
    cursor.value = nextCursor;
    noMore.value = nextCursor === null;
    persistState();
  } finally {
    loading.value = false;
  }
};

const apply = async () => {
  cursor.value = null;
  noMore.value = false;
  items.value = [];
  if (mainScrollRef.value) mainScrollRef.value.scrollTop = 0;
  await fetchPage({ resetList: true });
};

const reset = async () => {
  filters.cityCode = '';
  filters.needTicket = null;
  filters.ratingMin = 4.0;
  await apply();
};

const loadMore = async () => {
  await fetchPage({ resetList: false });
};

const openDetail = (scenicId) => {
  persistState();
  router.push(`/home/scenics/${scenicId}`);
};

onMounted(async () => {
  await fetchHot();
  const shouldRestore = String(route.query.restore || '') === '1';
  if (shouldRestore && restoreState()) {
    const q = { ...route.query };
    delete q.restore;
    router.replace({ path: '/home/scenics', query: q });
    return;
  }
  await fetchPage({ resetList: true });
});
</script>

<style scoped>
.page {
  width: 100%;
  background: #f7f8fa;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.main {
  flex: 1;
  overflow: auto;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 18px 16px 30px;
}

.heading {
  text-align: left;
  margin-bottom: 16px;
}

.h1 {
  font-size: 22px;
  font-weight: 900;
  color: #0f172a;
}

.h2 {
  margin-top: 6px;
  font-size: 13px;
  color: #64748b;
}

.grid {
  display: grid;
  grid-template-columns: 320px 1fr;
  gap: 16px;
}

.filter-card {
  border-radius: 14px;
  border: 1px solid #e5e7eb;
}

.filter-title {
  font-weight: 800;
  text-align: left;
  margin-bottom: 10px;
  color: #0f172a;
}

.filter-actions {
  display: flex;
  gap: 10px;
}

.full {
  width: 100%;
}

.hot-card {
  border-radius: 14px;
  border: 1px solid #e5e7eb;
  margin-bottom: 14px;
}

.hot-head {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 12px;
}

.hot-title {
  font-weight: 900;
  color: #0f172a;
}

.hot-sub {
  font-size: 12px;
  color: #64748b;
}

.hot-strip {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: 10px;
}

.hot-item {
  cursor: pointer;
}

.hot-img {
  width: 100%;
  height: 76px;
  border-radius: 12px;
  overflow: hidden;
  border: 1px solid #e5e7eb;
}

.hot-name {
  margin-top: 6px;
  font-size: 12px;
  font-weight: 800;
  color: #0f172a;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.list {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 14px;
}

.skeletons {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 14px;
}

.skeleton {
  height: 230px;
  border-radius: 14px;
  background: linear-gradient(90deg, #f1f5f9, #e2e8f0, #f1f5f9);
  background-size: 200% 100%;
  animation: shimmer 1.2s infinite;
}

@keyframes shimmer {
  0% { background-position: 200% 0; }
  100% { background-position: -200% 0; }
}

.loading-more {
  grid-column: 1 / -1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: #64748b;
  padding: 12px 0;
}

.spin {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.end {
  grid-column: 1 / -1;
  text-align: center;
  color: #94a3b8;
  padding: 14px 0;
}

@media (max-width: 1024px) {
  .hot-strip {
    grid-template-columns: repeat(3, 1fr);
  }
  .list,
  .skeletons {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 920px) {
  .grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 640px) {
  .list,
  .skeletons {
    grid-template-columns: 1fr;
  }
}
</style>

