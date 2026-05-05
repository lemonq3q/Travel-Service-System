<template>
  <div class="page">

    <div class="main" ref="mainScrollRef">
      <div class="container">
        <div class="heading">
          <div class="h1">发现你的下一次旅居</div>
          <div class="h2">按城市、价格、星级与评分筛选，向下滚动加载更多</div>
        </div>

        <div class="grid">
          <div class="filters">
            <el-card class="filter-card" shadow="never">
              <div class="filter-title">筛选</div>
              <el-form :model="filters" label-position="top">
                <el-form-item label="城市">
                  <AreaSelect v-model="filters.cityCode" level="area" placeholder="请选择城市" />
                </el-form-item>

                <el-form-item label="价格区间 (¥/晚)">
                  <div class="price-row">
                    <el-input-number v-model="filters.priceMin" :min="0" :step="50" controls-position="right" class="price" />
                    <div class="dash">-</div>
                    <el-input-number v-model="filters.priceMax" :min="0" :step="50" controls-position="right" class="price" />
                  </div>
                </el-form-item>

                <el-form-item label="星级">
                  <el-checkbox-group v-model="filters.starLevels">
                    <el-checkbox v-for="n in [3,4,5]" :key="n" :label="n">{{ n }}星</el-checkbox>
                  </el-checkbox-group>
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
            <div class="results-top">
              <div class="count">{{ totalHint }}</div>
              <el-select v-model="sort" class="sort" placeholder="排序" @change="apply">
                <el-option label="推荐" value="recommended" />
                <el-option label="价格从低到高" value="price_asc" />
                <el-option label="价格从高到低" value="price_desc" />
                <el-option label="评分从高到低" value="rating_desc" />
              </el-select>
            </div>

            <div
              class="list"
              v-infinite-scroll="loadMore"
              :infinite-scroll-disabled="loading || noMore"
              :infinite-scroll-distance="240"
              :infinite-scroll-container="mainScrollRef"
            >
              <HotelResultCard v-for="h in items" :key="h.id" :item="h" @open="openDetail" />

              <div v-if="loading && items.length === 0" class="skeletons">
                <div class="skeleton" v-for="i in 6" :key="i"></div>
              </div>

              <el-empty v-if="!loading && items.length === 0" description="没有找到符合条件的酒店" />

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
import UserHotelsHeader from '@/components/userHotels/UserHotelsHeader.vue';
import HotelResultCard from '@/components/userHotels/HotelResultCard.vue';
import Storage from '@/utils/storage';
import { searchHotels } from '@/api/publicHotel';

const router = useRouter();
const route = useRoute();

const STATE_KEY = 'user_hotels_search_state';

const mainScrollRef = ref(null);

const filters = reactive({
  cityCode: '',
  priceMin: null,
  priceMax: null,
  starLevels: [],
  ratingMin: 4.0
});

const sort = ref('recommended');
const items = ref([]);
const cursor = ref(null);
const loading = ref(false);
const noMore = ref(false);

const totalHint = ref('');

const persistState = () => {
  const scrollTop = mainScrollRef.value ? mainScrollRef.value.scrollTop : 0;
  Storage.set(
    STATE_KEY,
    {
      filters: {
        cityCode: filters.cityCode,
        priceMin: filters.priceMin,
        priceMax: filters.priceMax,
        starLevels: [...filters.starLevels],
        ratingMin: filters.ratingMin
      },
      sort: sort.value,
      items: items.value,
      cursor: cursor.value,
      noMore: noMore.value,
      totalHint: totalHint.value,
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
  filters.priceMin = f.priceMin ?? null;
  filters.priceMax = f.priceMax ?? null;
  filters.starLevels = Array.isArray(f.starLevels) ? f.starLevels : [];
  filters.ratingMin = Number.isFinite(Number(f.ratingMin)) ? Number(f.ratingMin) : 4.0;
  sort.value = state.sort || 'recommended';
  items.value = Array.isArray(state.items) ? state.items : [];
  cursor.value = state.cursor ?? null;
  noMore.value = !!state.noMore;
  totalHint.value = state.totalHint || '';

  setTimeout(() => {
    if (mainScrollRef.value) {
      mainScrollRef.value.scrollTop = Number(state.scrollTop || 0);
    }
  }, 0);
  return true;
};

const fetchPage = async ({ resetList } = {}) => {
  if (loading.value) return;
  if (!resetList && noMore.value) return;

  loading.value = true;
  try {
    const res = await searchHotels({
      cursor: resetList ? null : cursor.value,
      limit: 10,
      sort: sort.value,
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
    totalHint.value = items.value.length > 0 ? `已加载 ${items.value.length} 条` : '';
    persistState();
  } finally {
    loading.value = false;
  }
};

const apply = async () => {
  cursor.value = null;
  noMore.value = false;
  items.value = [];
  totalHint.value = '';
  if (mainScrollRef.value) mainScrollRef.value.scrollTop = 0;
  await fetchPage({ resetList: true });
};

const reset = async () => {
  filters.cityCode = '';
  filters.priceMin = null;
  filters.priceMax = null;
  filters.starLevels = [];
  filters.ratingMin = 4.0;
  sort.value = 'recommended';
  await apply();
};

const loadMore = async () => {
  await fetchPage({ resetList: false });
};

const openDetail = (hotelId) => {
  persistState();
  router.push(`/home/hotels/${hotelId}`);
};

onMounted(async () => {
  const shouldRestore = String(route.query.restore || '') === '1';
  if (shouldRestore && restoreState()) {
    const q = { ...route.query };
    delete q.restore;
    router.replace({ path: '/home/hotels', query: q });
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

.price-row {
  display: flex;
  align-items: center;
  gap: 8px;
}

.price {
  width: 100%;
}

.dash {
  color: #94a3b8;
}

.filter-actions {
  display: flex;
  gap: 10px;
}

.results-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 12px;
}

.count {
  font-size: 13px;
  color: #64748b;
  text-align: left;
}

.sort {
  width: 180px;
}

.list {
  display: grid;
  grid-template-columns: 1fr;
  gap: 14px;
}

.skeletons {
  display: grid;
  gap: 14px;
}

.skeleton {
  height: 180px;
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
  text-align: center;
  color: #94a3b8;
  padding: 14px 0;
}

@media (max-width: 920px) {
  .grid {
    grid-template-columns: 1fr;
  }
}
</style>
