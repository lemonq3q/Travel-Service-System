<template>
  <div class="page">
    <div class="main" ref="mainScrollRef">
      <div class="container" v-loading="loadingScenic">
        <div v-if="error" class="error">
          <el-result icon="error" title="景区加载失败" :sub-title="error">
            <template #extra>
              <el-button type="primary" @click="goBack">返回</el-button>
            </template>
          </el-result>
        </div>

        <div v-else-if="scenic" class="content">
          <div class="topbar">
            <el-button text type="primary" @click="goBack">
              <el-icon><ArrowLeft /></el-icon>
              返回列表
            </el-button>
          </div>

          <div class="header">
            <div class="left">
              <div class="title">{{ scenic.name }}</div>
              <div class="subline">
                <div class="addr">
                  <el-icon><Location /></el-icon>
                  <span>{{ scenic.address }}</span>
                </div>
                <div class="open">
                  <el-icon><Clock /></el-icon>
                  <span>{{ scenic.openTimeDesc }}</span>
                </div>
              </div>
            </div>

            <div class="right">
              <div class="score">
                <div class="score-num">{{ Number(scenic.rating || 0).toFixed(1) }}</div>
                <div class="score-sub">评分 · 热度 {{ Number(scenic.hotValue || 0).toFixed(1) }}</div>
              </div>
            </div>
          </div>

          <div class="gallery-wrap">
            <HotelGallery :images="scenic.images" />
          </div>

          <div class="info">
            <div class="info-left">
              <div class="section">
                <div class="section-title">简介</div>
                <div class="desc">{{ scenic.desc }}</div>
              </div>

              <div class="section" v-if="Number(scenic.needTicket) === 1">
                <div class="section-title">门票</div>
                <div class="ticket">
                  <div class="ticket-price">
                    <span class="money">¥{{ Number(scenic.price || 0).toFixed(0) }}</span>
                    <span class="unit">/人</span>
                  </div>
                  <el-button type="primary" @click="goBuy">前往购买</el-button>
                </div>
              </div>

              <div class="section" ref="reviewsAnchorRef">
                <div class="section-head">
                  <div class="section-title">评论</div>
                  <el-button
                    v-if="Number(scenic.needTicket) === 0"
                    size="small"
                    type="primary"
                    plain
                    @click="goReview"
                  >
                    发表评论
                  </el-button>
                </div>
                <div
                  class="panel-list"
                  v-infinite-scroll="loadMoreReviews"
                  :infinite-scroll-disabled="reviewsLoading || reviewsNoMore"
                  :infinite-scroll-distance="260"
                  :infinite-scroll-container="mainScrollRef"
                >
                  <ScenicReviewRow v-for="r in reviews" :key="r.id" :review="r" />
                  <div v-if="reviewsLoading" class="panel-loading">加载中...</div>
                  <div v-if="reviewsNoMore && reviews.length" class="panel-end">没有更多评论了</div>
                  <el-empty v-if="!reviewsLoading && reviews.length === 0" description="暂无评论" />
                </div>
              </div>
            </div>

            <div class="info-right">
              <div class="sticky">
                <div class="side-card">
                  <div class="side-title">地址地图</div>
                  <MapViewer :longitude="Number(scenic.longitude)" :latitude="Number(scenic.latitude)" />
                  <div class="map-actions">
                    <el-button size="small" @click="copyText(scenic.address)">复制地址</el-button>
                    <el-button size="small" type="primary" plain @click="openNav">去导航</el-button>
                  </div>
                </div>

                <div class="side-card">
                  <div class="side-title">信息</div>
                  <div class="kv">
                    <div class="k">是否门票</div>
                    <div class="v">{{ Number(scenic.needTicket) === 1 ? '需要' : '不需要' }}</div>
                  </div>
                  <div class="kv" v-if="Number(scenic.needTicket) === 1">
                    <div class="k">票价</div>
                    <div class="v">¥{{ Number(scenic.price || 0).toFixed(0) }}</div>
                  </div>
                  <div class="kv">
                    <div class="k">热度</div>
                    <div class="v">{{ Number(scenic.hotValue || 0).toFixed(1) }}</div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div v-else class="empty">
          <el-result icon="warning" title="景区不存在或已下架">
            <template #extra>
              <el-button type="primary" @click="goBack">返回列表</el-button>
            </template>
          </el-result>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { ArrowLeft, Clock, Location } from '@element-plus/icons-vue';

import HotelGallery from '@/components/userHotels/HotelGallery.vue';
import MapViewer from '@/components/userHotels/MapViewer.vue';
import ScenicReviewRow from '@/components/userScenics/ScenicReviewRow.vue';
import { getScenicDetail, listScenicReviews } from '@/api/publicScenic';

const route = useRoute();
const router = useRouter();

const mainScrollRef = ref(null);
const reviewsAnchorRef = ref(null);

const loadingScenic = ref(false);
const error = ref('');
const scenic = ref(null);

const reviews = ref([]);
const reviewsCursor = ref(null);
const reviewsLoading = ref(false);
const reviewsNoMore = ref(false);

const goBack = () => {
  router.push('/home/scenics?restore=1');
};

const loadScenic = async () => {
  loadingScenic.value = true;
  error.value = '';
  try {
    const res = await getScenicDetail(route.params.scenicId);
    if (res?.data?.code !== 200) {
      error.value = res?.data?.msg || '未知错误';
      scenic.value = null;
      return;
    }
    scenic.value = res?.data?.data || null;
  } finally {
    loadingScenic.value = false;
  }
};

const loadMoreReviews = async () => {
  if (reviewsLoading.value) return;
  if (reviewsNoMore.value) return;
  reviewsLoading.value = true;
  try {
    const res = await listScenicReviews({
      scenicId: route.params.scenicId,
      cursor: reviewsCursor.value,
      limit: 10
    });
    const data = res?.data?.data;
    const nextItems = Array.isArray(data?.items) ? data.items : [];
    const nextCursor = data?.nextCursor ?? null;
    reviews.value = [...reviews.value, ...nextItems];
    reviewsCursor.value = nextCursor;
    reviewsNoMore.value = nextCursor === null;
  } finally {
    reviewsLoading.value = false;
  }
};

const goBuy = () => {
  if (!scenic.value) {
    ElMessage.warning('景区信息缺失');
    return;
  }
  router.push({
    path: '/home/orders/scenic/create',
    query: {
      scenicId: scenic.value.id,
      scenicName: scenic.value.name,
      price: scenic.value.price
    }
  });
};

const goReview = () => {
  const sid = scenic.value?.id || route.params.scenicId;
  router.push({
    path: '/home/reviews/new',
    query: {
      type: 'scenic',
      scenicId: sid,
      redirect: `/home/scenics/${sid}`
    }
  });
};

const copyText = async (text) => {
  try {
    await navigator.clipboard.writeText(String(text || ''));
    ElMessage.success('已复制');
  } catch {
    ElMessage.warning('复制失败');
  }
};

const openNav = () => {
  const lng = Number(scenic.value?.longitude);
  const lat = Number(scenic.value?.latitude);
  const name = encodeURIComponent(String(scenic.value?.name || '目的地'));
  if (!Number.isFinite(lng) || !Number.isFinite(lat)) {
    ElMessage.warning('坐标缺失');
    return;
  }
  const url = `https://uri.amap.com/marker?position=${lng},${lat}&name=${name}`;
  window.open(url, '_blank');
};

onMounted(async () => {
  await loadScenic();
  reviews.value = [];
  reviewsCursor.value = null;
  reviewsNoMore.value = false;
  await loadMoreReviews();
});
</script>

<style scoped>
.section-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
}

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

.topbar {
  margin-bottom: 10px;
  text-align: left;
}

.header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 14px;
}

.title {
  font-size: 24px;
  font-weight: 900;
  color: #0f172a;
  text-align: left;
}

.subline {
  margin-top: 10px;
  display: flex;
  align-items: center;
  gap: 14px;
  flex-wrap: wrap;
}

.addr,
.open {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #475569;
}

.score {
  background: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 14px;
  padding: 12px 14px;
  text-align: right;
  min-width: 180px;
}

.score-num {
  font-size: 26px;
  font-weight: 900;
  color: #0f172a;
}

.score-sub {
  margin-top: 4px;
  font-size: 12px;
  color: #64748b;
}

.gallery-wrap {
  margin-bottom: 14px;
}

.info {
  display: grid;
  grid-template-columns: 1fr 340px;
  gap: 16px;
}

.section {
  background: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 14px;
  padding: 14px;
  margin-bottom: 14px;
}

.section-title {
  font-weight: 900;
  color: #0f172a;
  text-align: left;
  margin-bottom: 10px;
}

.desc {
  color: #334155;
  line-height: 1.75;
  font-size: 13px;
  text-align: left;
}

.ticket {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.ticket-price {
  display: flex;
  align-items: baseline;
  gap: 6px;
}

.money {
  font-size: 26px;
  font-weight: 900;
  color: #ef4444;
}

.unit {
  font-size: 12px;
  color: #64748b;
}

.panel-list {
  min-height: 120px;
}

.panel-loading {
  color: #64748b;
  text-align: center;
  padding: 12px 0;
}

.panel-end {
  color: #94a3b8;
  text-align: center;
  padding: 12px 0;
}

.sticky {
  position: sticky;
  top: 10px;
  display: grid;
  gap: 14px;
}

.side-card {
  background: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 14px;
  padding: 14px;
}

.side-title {
  font-weight: 900;
  color: #0f172a;
  text-align: left;
  margin-bottom: 10px;
}

.map-actions {
  margin-top: 10px;
  display: flex;
  gap: 10px;
}

.kv {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 0;
  border-bottom: 1px dashed #e5e7eb;
}

.kv:last-child {
  border-bottom: 0;
}

.k {
  color: #64748b;
  font-size: 12px;
}

.v {
  color: #0f172a;
  font-weight: 800;
  font-size: 13px;
}

@media (max-width: 1024px) {
  .info {
    grid-template-columns: 1fr;
  }
  .score {
    min-width: auto;
  }
}
</style>
