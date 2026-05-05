<template>
  <div class="page">
    <div class="main" ref="mainScrollRef">
      <div class="container">
        <div class="heading">
          <div class="h1">发现一篇适合你的旅行攻略</div>
          <div class="h2">支持关键词搜索，向下滚动加载更多</div>
        </div>

        <div class="searchbar">
          <el-input
            v-model="keyword"
            size="large"
            placeholder="搜索目的地 / 关键词"
            clearable
            @keyup.enter="apply"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
          <el-button type="primary" size="large" :loading="loading && items.length === 0" @click="apply">
            搜索
          </el-button>
          <el-button size="large" @click="goPublish">
            我的攻略
          </el-button>
        </div>

        <div
          class="list"
          v-infinite-scroll="loadMore"
          :infinite-scroll-disabled="loading || noMore"
          :infinite-scroll-distance="260"
          :infinite-scroll-container="mainScrollRef"
        >
          <GuideCard v-for="g in items" :key="g.id" :item="g" @open="openDetail" />

          <div v-if="loading && items.length === 0" class="skeletons">
            <div class="skeleton" v-for="i in 9" :key="i"></div>
          </div>

          <el-empty v-if="!loading && items.length === 0" description="没有找到相关攻略" />

          <div v-if="loading && items.length > 0" class="loading-more">
            <el-icon class="spin"><Loading /></el-icon>
            <span>加载中...</span>
          </div>
          <div v-if="noMore && items.length > 0" class="end">没有更多了</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { Loading, Search } from '@element-plus/icons-vue';

import Storage from '@/utils/storage';
import GuideCard from '@/components/userGuides/GuideCard.vue';
import { searchGuides } from '@/api/publicGuide';

const router = useRouter();
const route = useRoute();

const STATE_KEY = 'user_guides_search_state';

const mainScrollRef = ref(null);
const keyword = ref('');

const items = ref([]);
const cursor = ref(null);
const loading = ref(false);
const noMore = ref(false);

const persistState = () => {
  const scrollTop = mainScrollRef.value ? mainScrollRef.value.scrollTop : 0;
  Storage.set(
    STATE_KEY,
    {
      keyword: keyword.value,
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
  keyword.value = String(state.keyword || '');
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

const fetchPage = async ({ resetList } = {}) => {
  if (loading.value) return;
  if (!resetList && noMore.value) return;
  loading.value = true;
  try {
    const res = await searchGuides({
      keyword: keyword.value,
      cursor: resetList ? null : cursor.value,
      limit: 9
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

const loadMore = async () => {
  await fetchPage({ resetList: false });
};

const openDetail = (guideId) => {
  persistState();
  router.push(`/home/guides/${guideId}`);
};

const goPublish = () => {
  persistState();
  router.push('/home/guides/new');
};

onMounted(async () => {
  const shouldRestore = String(route.query.restore || '') === '1';
  if (shouldRestore && restoreState()) {
    const q = { ...route.query };
    delete q.restore;
    router.replace({ path: '/home/guides', query: q });
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
  margin-bottom: 14px;
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

.searchbar {
  display: flex;
  gap: 10px;
  align-items: center;
  margin-bottom: 14px;
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
  0% {
    background-position: 200% 0;
  }
  100% {
    background-position: -200% 0;
  }
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
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

.end {
  grid-column: 1 / -1;
  text-align: center;
  color: #94a3b8;
  padding: 14px 0;
}

@media (max-width: 1024px) {
  .list,
  .skeletons {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 640px) {
  .searchbar {
    flex-direction: column;
    align-items: stretch;
  }
  .list,
  .skeletons {
    grid-template-columns: 1fr;
  }
}
</style>
