<template>
  <div class="order-module">
    <div class="order-container">
      <div class="order-header">
        <div class="order-title">我的订单</div>
        <div class="order-sub">查看酒店/景区/火车票/机票订单，未支付可去支付或取消</div>
      </div>

      <div class="toolbar">
        <el-tabs v-model="activeTab" class="tabs" stretch>
          <el-tab-pane name="hotel">
            <template #label>
              <span class="tab-label">
                <el-icon><OfficeBuilding /></el-icon>
                酒店订单
              </span>
            </template>
          </el-tab-pane>
          <el-tab-pane name="scenic">
            <template #label>
              <span class="tab-label">
                <el-icon><Location /></el-icon>
                景区订单
              </span>
            </template>
          </el-tab-pane>

          <el-tab-pane name="train">
            <template #label>
              <span class="tab-label">
                <el-icon><Van /></el-icon>
                火车票
              </span>
            </template>
          </el-tab-pane>

          <el-tab-pane name="aircraft">
            <template #label>
              <span class="tab-label">
                <el-icon><Promotion /></el-icon>
                机票
              </span>
            </template>
          </el-tab-pane>
        </el-tabs>
        <el-button class="refresh" @click="refresh" :loading="loading" plain>
          <el-icon><Refresh /></el-icon>
          刷新
        </el-button>
      </div>

      <div class="order-grid">
        <el-skeleton v-if="loading" :rows="6" animated />

        <template v-else>
          <el-empty v-if="currentList.length === 0" description="暂无订单" />

          <div v-else class="list">
            <div v-for="item in currentList" :key="String(item.id)" class="item">
              <div class="item-top">
                <div class="left">
                  <div class="no">订单 #{{ item.id }}</div>
                  <div class="meta">{{ item.created_at ? formatCreatedAt(item.created_at) : '' }}</div>
                </div>
                <div class="right">
                  <el-tag v-if="item.status === 'CANCELLED'" type="info" effect="plain">已取消</el-tag>
                  <el-tag v-else-if="Number(item.is_pay) === 1" type="success" effect="dark">已支付</el-tag>
                  <el-tag v-else type="warning" effect="dark">待支付</el-tag>
                </div>
              </div>

              <div class="item-body">
                <template v-if="activeTab === 'hotel'">
                  <div class="kv">
                    <div class="k">酒店</div>
                    <div class="v">{{ item.hotel_name || ('酒店ID ' + item.as_hotel) }}</div>
                  </div>
                  <div class="kv">
                    <div class="k">房型</div>
                    <div class="v">{{ item.room_name || ('房型ID ' + item.as_room) }}</div>
                  </div>
                  <div class="kv">
                    <div class="k">日期</div>
                    <div class="v">{{ item.check_in_date }} 至 {{ item.check_out_date }}</div>
                  </div>
                  <div class="kv">
                    <div class="k">房间数</div>
                    <div class="v">{{ item.room_num }} 间</div>
                  </div>
                </template>

                <template v-else-if="activeTab === 'scenic'">
                  <div class="kv">
                    <div class="k">景区</div>
                    <div class="v">{{ item.scenic_name || ('景区ID ' + item.as_scenic) }}</div>
                  </div>
                  <div class="kv">
                    <div class="k">数量</div>
                    <div class="v">1</div>
                  </div>
                </template>

                <template v-else-if="activeTab === 'train'">
                  <div class="kv">
                    <div class="k">车次</div>
                    <div class="v">{{ item.as_train || '-' }}</div>
                  </div>
                  <div class="kv">
                    <div class="k">票种</div>
                    <div class="v">{{ item.type_name || '-' }}</div>
                  </div>
                  <div class="kv">
                    <div class="k">起终点</div>
                    <div class="v">{{ (item.start_station_name || '-') + ' → ' + (item.end_station_name || '-') }}</div>
                  </div>
                </template>

                <template v-else>
                  <div class="kv">
                    <div class="k">航班</div>
                    <div class="v">{{ item.as_aircraft || '-' }}</div>
                  </div>
                  <div class="kv">
                    <div class="k">舱位</div>
                    <div class="v">{{ item.type_name || '-' }}</div>
                  </div>
                  <div class="kv">
                    <div class="k">起终点</div>
                    <div class="v">{{ (item.start_station_name || '-') + ' → ' + (item.end_station_name || '-') }}</div>
                  </div>
                </template>
              </div>

              <div class="item-bottom">
                <div class="money">
                  <span class="label">应付</span>
                  <span class="value">¥{{ formatMoney(item.pay_amount ?? item.pay_amount) }}</span>
                </div>

                <div class="actions">
                  <el-button
                    v-if="canPay(item)"
                    type="primary"
                    @click="goPay(item)"
                  >
                    去支付
                  </el-button>
                  <el-button
                    v-if="canReview(item)"
                    type="success"
                    plain
                    @click="goReview(item)"
                  >
                    去评论
                  </el-button>
                  <el-button
                    v-if="canCancel(item)"
                    type="danger"
                    plain
                    @click="cancel(item)"
                  >
                    取消
                  </el-button>
                </div>
              </div>
            </div>
          </div>
        </template>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessageBox } from 'element-plus';
import { Location, OfficeBuilding, Promotion, Refresh, Van } from '@element-plus/icons-vue';
import Message from '@/utils/message';
import { cancelHotelOrder, cancelScenicOrder, listMyHotelOrders, listMyScenicOrders } from '@/api/publicOrder';
import { cancelAircraftOrder, cancelTrainOrder, listMyAircraftOrders, listMyTrainOrders } from '@/api/publicTicket';

const route = useRoute();
const router = useRouter();

const normalizeTab = (t) => {
  const v = String(t || '').toLowerCase();
  if (v === 'scenic') return 'scenic';
  if (v === 'train') return 'train';
  if (v === 'aircraft') return 'aircraft';
  return 'hotel';
};
const activeTab = ref(normalizeTab(route.query.tab));
const loading = ref(false);

const hotelOrders = ref([]);
const scenicOrders = ref([]);
const trainOrders = ref([]);
const aircraftOrders = ref([]);

const getUserId = () => {
  try {
    const raw = localStorage.getItem('userInfo');
    const uid = raw ? Number(JSON.parse(raw)?.id) : 0;
    return Number.isFinite(uid) && uid > 0 ? uid : 0;
  } catch {
    return 0;
  }
};

const currentList = computed(() => {
  if (activeTab.value === 'scenic') return scenicOrders.value;
  if (activeTab.value === 'train') return trainOrders.value;
  if (activeTab.value === 'aircraft') return aircraftOrders.value;
  return hotelOrders.value;
});

const formatMoney = (v) => {
  const n = Number(v);
  if (!Number.isFinite(n)) return '0.00';
  return n.toFixed(2);
};

const formatCreatedAt = (iso) => {
  try {
    const d = new Date(iso);
    if (Number.isNaN(d.getTime())) return String(iso || '');
    const pad = (n) => String(n).padStart(2, '0');
    return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`;
  } catch {
    return String(iso || '');
  }
};

const canPay = (item) => item?.status !== 'CANCELLED' && Number(item?.is_pay) !== 1;
const canCancel = (item) => item?.status !== 'CANCELLED' && Number(item?.is_pay) !== 1;
const canReview = (item) => {
  if (activeTab.value !== 'hotel' && activeTab.value !== 'scenic') return false;
  return item?.status !== 'CANCELLED' && Number(item?.is_pay) === 1;
};

const loadHotel = async () => {
  const res = await listMyHotelOrders({ asUser: getUserId() });
  const data = res?.data;
  if (data?.code !== 200) throw new Error(data?.msg || '加载失败');
  hotelOrders.value = Array.isArray(data?.data?.list) ? data.data.list : [];
};

const loadScenic = async () => {
  const res = await listMyScenicOrders({ asUser: getUserId() });
  const data = res?.data;
  if (data?.code !== 200) throw new Error(data?.msg || '加载失败');
  scenicOrders.value = Array.isArray(data?.data?.list) ? data.data.list : [];
};

const loadTrain = async () => {
  const res = await listMyTrainOrders({ asUser: getUserId() });
  const data = res?.data;
  if (data?.code !== 200) throw new Error(data?.msg || '加载失败');
  trainOrders.value = Array.isArray(data?.data?.list) ? data.data.list : [];
};

const loadAircraft = async () => {
  const res = await listMyAircraftOrders({ asUser: getUserId() });
  const data = res?.data;
  if (data?.code !== 200) throw new Error(data?.msg || '加载失败');
  aircraftOrders.value = Array.isArray(data?.data?.list) ? data.data.list : [];
};

const refresh = async () => {
  loading.value = true;
  try {
    await Promise.all([loadHotel(), loadScenic(), loadTrain(), loadAircraft()]);
  } catch (e) {
    Message.warning(e?.message || '加载订单失败');
  } finally {
    loading.value = false;
  }
};

const goPay = (item) => {
  if (activeTab.value === 'hotel') {
    router.push({
      path: '/home/pay',
      query: {
        orderType: 'hotel',
        orderId: item.id,
        hotelName: item.hotel_name,
        roomName: item.room_name,
        amount: formatMoney(item.pay_amount ?? item.pay_amount),
        checkInDate: item.check_in_date,
        checkOutDate: item.check_out_date,
        roomNum: String(item.room_num || 1)
      }
    });
    return;
  }

  if (activeTab.value === 'scenic') {
    router.push({
      path: '/home/pay',
      query: {
        orderType: 'scenic',
        orderId: item.id,
        scenicName: item.scenic_name,
        amount: formatMoney(item.pay_amount ?? item.pay_amount),
        tab: 'scenic'
      }
    });
    return;
  }

  if (activeTab.value === 'train') {
    router.push({
      path: '/home/pay',
      query: {
        orderType: 'train',
        orderId: item.id,
        amount: formatMoney(item.pay_amount ?? item.pay_amount),
        tab: 'train'
      }
    });
    return;
  }

  router.push({
    path: '/home/pay',
    query: {
      orderType: 'aircraft',
      orderId: item.id,
      amount: formatMoney(item.pay_amount ?? item.pay_amount),
      tab: 'aircraft'
    }
  });
};

const goReview = (item) => {
  if (!canReview(item)) return;
  if (activeTab.value === 'hotel') {
    router.push({
      path: '/home/reviews/new',
      query: {
        type: 'hotel',
        orderId: item.id,
        hotelId: item.as_hotel,
        roomId: item.as_room
      }
    });
    return;
  }
  router.push({
    path: '/home/reviews/new',
    query: {
      type: 'scenic',
      orderId: item.id,
      scenicId: item.as_scenic
    }
  });
};

const cancel = async (item) => {
  if (!canCancel(item)) return;
  try {
    await ElMessageBox.confirm('确认取消该订单？取消后不可恢复。', '取消订单', {
      confirmButtonText: '确认取消',
      cancelButtonText: '再想想',
      type: 'warning'
    });
  } catch {
    return;
  }

  loading.value = true;
  try {
    const uid = getUserId();
    const res =
      activeTab.value === 'hotel'
        ? await cancelHotelOrder({ id: item.id, asUser: uid })
        : activeTab.value === 'scenic'
          ? await cancelScenicOrder({ id: item.id, asUser: uid })
          : activeTab.value === 'train'
            ? await cancelTrainOrder({ id: item.id, asUser: uid })
            : await cancelAircraftOrder({ id: item.id, asUser: uid });
    const data = res?.data;
    if (data?.code !== 200) {
      Message.warning(data?.msg || '取消失败');
      return;
    }
    Message.success('已取消');
    await refresh();
  } finally {
    loading.value = false;
  }
};

onMounted(async () => {
  activeTab.value = normalizeTab(route.query.tab);
  await refresh();
});

watch(
  () => route.query.tab,
  (v) => {
    activeTab.value = normalizeTab(v);
  }
);

watch(
  () => activeTab.value,
  () => {
    if (!loading.value && currentList.value.length === 0) refresh();
  }
);
</script>

<style scoped>
.toolbar {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 12px;
}

.tabs {
  flex: 1;
}

.tab-label {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-weight: 900;
}

.refresh {
  border-radius: 10px;
}

.list {
  display: grid;
  grid-template-columns: 1fr;
  gap: 12px;
}

.item {
  background: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 14px;
  padding: 14px;
}

.item-top {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
}

.item-top .no {
  font-weight: 900;
  color: #0f172a;
}

.item-top .meta {
  margin-top: 4px;
  font-size: 12px;
  color: #64748b;
}

.item-body {
  margin-top: 12px;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px 14px;
}

.kv {
  display: grid;
  grid-template-columns: 64px 1fr;
  gap: 10px;
}

.kv .k {
  font-size: 12px;
  color: #64748b;
}

.kv .v {
  font-size: 14px;
  color: #111827;
  font-weight: 700;
}

.item-bottom {
  margin-top: 14px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding-top: 12px;
  border-top: 1px solid #eef2f7;
}

.money .label {
  font-size: 12px;
  color: #64748b;
  margin-right: 8px;
}

.money .value {
  font-size: 18px;
  font-weight: 900;
  color: #0f172a;
}

.actions {
  display: flex;
  gap: 10px;
}

@media (max-width: 960px) {
  .item-body {
    grid-template-columns: 1fr;
  }
}
</style>
