<template>
  <div class="page">

    <div class="main" ref="mainScrollRef">
      <div class="container" v-loading="loadingHotel">
        <div v-if="error" class="error">
          <el-result icon="error" title="酒店加载失败" :sub-title="error">
            <template #extra>
              <el-button type="primary" @click="goBack">返回</el-button>
            </template>
          </el-result>
        </div>

        <div v-else-if="hotel" class="content">
          <div class="topbar">
            <el-button text type="primary" @click="goBack">
              <el-icon><ArrowLeft /></el-icon>
              返回列表
            </el-button>
          </div>

          <div class="header">
            <div class="left">
              <div class="title">{{ hotel.name }}</div>
              <div class="subline">
                <div class="stars">
                  <el-icon v-for="n in hotel.starLevel" :key="n" class="star"><StarFilled /></el-icon>
                </div>
                <div class="addr">
                  <el-icon><Location /></el-icon>
                  <span>{{ hotel.address }}</span>
                </div>
              </div>
            </div>

            <div class="right">
              <div class="score">
                <div class="score-num">{{ hotel.rating.toFixed(1) }}</div>
                <div class="score-sub">评分 · {{ hotel.reviewCount }} 条评论</div>
              </div>
            </div>
          </div>

          <div class="gallery-wrap">
            <HotelGallery :images="hotel.images" />
          </div>

          <div class="info">
            <div class="info-left">
              <div class="section">
                <div class="section-title">特色</div>
                <div class="tags">
                  <el-tag v-for="t in featureTags" :key="t" effect="plain" round>{{ t }}</el-tag>
                </div>
              </div>
              <div class="section">
                <div class="section-title">设施</div>
                <div class="tags">
                  <el-tag v-for="t in facilityTags" :key="t" effect="plain" round type="success">{{ t }}</el-tag>
                </div>
              </div>
              <div class="section">
                <div class="section-title">服务</div>
                <div class="tags">
                  <el-tag v-for="t in serviceTags" :key="t" effect="plain" round type="info">{{ t }}</el-tag>
                </div>
              </div>

              <div class="section">
                <div class="section-title">简介</div>
                <div class="desc">{{ hotel.desc }}</div>
              </div>
            </div>

            <div class="info-right">
              <div class="sticky">
                <div class="side-card">
                  <div class="side-title">地址地图</div>
                  <MapViewer :longitude="hotel.longitude" :latitude="hotel.latitude" />
                </div>

                <div class="side-card">
                  <div class="side-title">热门评论</div>
                  <div v-if="top3Reviews.length" class="top3">
                    <div class="top3-item" v-for="r in top3Reviews" :key="r.id">
                      <div class="top3-head">
                        <span class="u">{{ r.user.name }}</span>
                        <span class="p">{{ Number(r.rating).toFixed(1) }}</span>
                      </div>
                      <div class="top3-content">{{ r.content }}</div>
                    </div>
                  </div>
                  <el-empty v-else description="暂无评论" />
                  <div class="jump">
                    <el-button type="primary" plain @click="jumpToReviews">查看全部评论</el-button>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div class="tabs">
            <el-tabs v-model="activeTab" class="tabbar" @tab-change="onTabChange">
              <el-tab-pane label="房间" name="rooms" />
              <el-tab-pane label="评论" name="reviews" />
            </el-tabs>

            <div v-show="activeTab === 'rooms'" class="panel">
              <div
                class="panel-list"
                v-infinite-scroll="loadMoreRooms"
                :infinite-scroll-disabled="roomsLoading || roomsNoMore || activeTab !== 'rooms'"
                :infinite-scroll-distance="240"
                :infinite-scroll-container="mainScrollRef"
              >
                <RoomCard v-for="r in roomsShown" :key="r.id" :room="r" @book="onBookRoom" />
                <div v-if="roomsLoading" class="panel-loading">加载中...</div>
                <div v-if="roomsNoMore && roomsShown.length" class="panel-end">没有更多房间了</div>
                <el-empty v-if="!roomsLoading && roomsShown.length === 0" description="暂无房间" />
              </div>
            </div>

            <div v-show="activeTab === 'reviews'" class="panel" ref="reviewsAnchorRef">
              <div
                class="panel-list"
                v-infinite-scroll="loadMoreReviews"
                :infinite-scroll-disabled="reviewsLoading || reviewsNoMore || activeTab !== 'reviews'"
                :infinite-scroll-distance="240"
                :infinite-scroll-container="mainScrollRef"
              >
                <ReviewRow v-for="r in reviews" :key="r.id" :review="r" />
                <div v-if="reviewsLoading" class="panel-loading">加载中...</div>
                <div v-if="reviewsNoMore && reviews.length" class="panel-end">没有更多评论了</div>
                <el-empty v-if="!reviewsLoading && reviews.length === 0" description="暂无评论" />
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ArrowLeft, Location, StarFilled } from '@element-plus/icons-vue';

import HotelGallery from '@/components/userHotels/HotelGallery.vue';
import MapViewer from '@/components/userHotels/MapViewer.vue';
import RoomCard from '@/components/userHotels/RoomCard.vue';
import ReviewRow from '@/components/userHotels/ReviewRow.vue';
import { getHotelBundle, listHotelReviews } from '@/api/publicHotel';
import Message from '@/utils/message';

const route = useRoute();
const router = useRouter();

const mainScrollRef = ref(null);
const reviewsAnchorRef = ref(null);

const loadingHotel = ref(false);
const error = ref('');

const hotel = ref(null);
const roomsAll = ref([]);
const roomsShown = ref([]);
const roomsCursor = ref(0);
const roomsLoading = ref(false);
const roomsNoMore = ref(false);

const activeTab = ref('rooms');

const reviews = ref([]);
const reviewsCursor = ref(null);
const reviewsLoading = ref(false);
const reviewsNoMore = ref(false);

const parseTagJson = (raw) => {
  if (!raw) return [];
  try {
    const arr = JSON.parse(raw);
    return Array.isArray(arr) ? arr.filter(Boolean) : [];
  } catch {
    return [];
  }
};

const featureTags = computed(() => parseTagJson(hotel.value?.featureJson));
const facilityTags = computed(() => parseTagJson(hotel.value?.facilityJson));
const serviceTags = computed(() => parseTagJson(hotel.value?.serviceJson));

const top3Reviews = computed(() => reviews.value.slice(0, 1));

const goBack = () => {
  router.push('/home/hotels?restore=1');
};

const onBookRoom = ({ room, pkg }) => {
  if (!hotel.value || !room || !pkg) {
    Message.warning('房间信息缺失');
    return;
  }
  router.push({
    path: '/home/orders/hotel/create',
    query: {
      hotelId: hotel.value.id,
      hotelName: hotel.value.name,
      roomId: room.id,
      roomName: room.name,
      packageId: pkg.id,
      packageSummary: (() => {
        try {
          const arr = JSON.parse(pkg.summaryJson);
          return Array.isArray(arr) ? arr.filter(Boolean).join(' / ') : String(pkg.summaryJson || '');
        } catch {
          return String(pkg.summaryJson || '');
        }
      })(),
      price: pkg?.price?.amount
    }
  });
};

const loadHotel = async () => {
  loadingHotel.value = true;
  error.value = '';
  try {
    const res = await getHotelBundle(route.params.hotelId);
    if (res?.data?.code !== 200) {
      error.value = res?.data?.msg || '未知错误';
      hotel.value = null;
      roomsAll.value = [];
      return;
    }

    const data = res.data.data;
    hotel.value = data?.hotel || null;
    roomsAll.value = Array.isArray(data?.rooms) ? data.rooms : [];

    roomsShown.value = [];
    roomsCursor.value = 0;
    roomsNoMore.value = false;
    await loadMoreRooms();
  } finally {
    loadingHotel.value = false;
  }
};

const loadMoreRooms = async () => {
  if (roomsLoading.value) return;
  if (roomsNoMore.value) return;
  roomsLoading.value = true;
  try {
    const limit = 6;
    const start = roomsCursor.value;
    const next = roomsAll.value.slice(start, start + limit);
    roomsShown.value = [...roomsShown.value, ...next];
    roomsCursor.value = start + next.length;
    roomsNoMore.value = roomsCursor.value >= roomsAll.value.length;
  } finally {
    roomsLoading.value = false;
  }
};

const loadMoreReviews = async () => {
  if (reviewsLoading.value) return;
  if (reviewsNoMore.value) return;
  reviewsLoading.value = true;
  try {
    const res = await listHotelReviews({
      hotelId: route.params.hotelId,
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

const jumpToReviews = async () => {
  activeTab.value = 'reviews';
  await loadMoreReviews();
  setTimeout(() => {
    if (reviewsAnchorRef.value) {
      reviewsAnchorRef.value.scrollIntoView({ behavior: 'smooth', block: 'start' });
    }
  }, 0);
};

const onTabChange = async (name) => {
  if (name === 'reviews' && reviews.value.length === 0) {
    await loadMoreReviews();
  }
};

onMounted(async () => {
  await loadHotel();
  await loadMoreReviews();
});
</script>

<style scoped>
.page {
  width: 100%;
  /* height: 100vh; */
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
  text-align: left;
  margin-bottom: 10px;
}

.header {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 14px;
}

.title {
  font-size: 22px;
  font-weight: 900;
  color: #0f172a;
  text-align: left;
}

.subline {
  margin-top: 8px;
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.stars {
  display: flex;
  gap: 3px;
  color: #f59e0b;
}

.star {
  font-size: 16px;
}

.addr {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #475569;
  font-size: 13px;
}

.score {
  background: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 14px;
  padding: 12px 14px;
  text-align: left;
  min-width: 220px;
}

.score-num {
  font-size: 24px;
  font-weight: 900;
  color: #0f172a;
}

.score-sub {
  margin: 4px 0 8px;
  font-size: 12px;
  color: #64748b;
}

.gallery-wrap {
  margin-bottom: 16px;
}

.info {
  display: grid;
  grid-template-columns: 1.2fr 0.8fr;
  gap: 16px;
  margin-bottom: 14px;
}

.section {
  background: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 14px;
  padding: 14px;
  margin-bottom: 12px;
}

.section-title {
  font-weight: 900;
  color: #0f172a;
  text-align: left;
  margin-bottom: 10px;
}

.tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  justify-content: flex-start;
}

.desc {
  text-align: left;
  color: #475569;
  line-height: 1.7;
  font-size: 13px;
}

.sticky {
  display: grid;
  gap: 12px;
  position: sticky;
  top: 12px;
}

.side-card {
  background: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 14px;
  padding: 14px;
  text-align: left;
}

.side-title {
  font-weight: 900;
  color: #0f172a;
  margin-bottom: 10px;
}

.top3 {
  display: grid;
  gap: 10px;
}

.top3-item {
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  padding: 10px;
}

.top3-head {
  display: flex;
  justify-content: space-between;
  color: #0f172a;
  font-weight: 800;
  font-size: 13px;
}

.top3-content {
  margin-top: 6px;
  color: #475569;
  font-size: 12px;
  line-height: 1.6;
}

.jump {
  margin-top: 12px;
  text-align: right;
}

.tabs {
  background: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 14px;
  padding: 8px 14px 14px;
}

.panel {
  padding-top: 8px;
}

.panel-list {
  display: grid;
  gap: 14px;
}

.panel-loading {
  text-align: center;
  color: #64748b;
  padding: 10px 0;
}

.panel-end {
  text-align: center;
  color: #94a3b8;
  padding: 10px 0;
}

@media (max-width: 920px) {
  .header {
    flex-direction: column;
  }
  .info {
    grid-template-columns: 1fr;
  }
  .score {
    width: 100%;
  }
}
</style>
