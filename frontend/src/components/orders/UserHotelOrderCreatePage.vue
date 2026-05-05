<template>
  <div class="order-module">
    <div class="order-container">
      <div class="order-topbar">
        <el-button text type="primary" @click="goBack">
          <el-icon><ArrowLeft /></el-icon>
          返回
        </el-button>
      </div>

      <div class="order-header">
        <div class="order-title">酒店下单</div>
        <div class="order-sub">确认日期与房间数量后创建订单</div>
      </div>

      <div class="order-grid two">
        <div>
          <div class="order-card">
            <div class="order-card-title">
              <el-icon><CircleCheck /></el-icon>
              预订信息
            </div>

            <div class="order-kv">
              <div class="k">酒店</div>
              <div class="v">{{ hotelName || '-' }}</div>
            </div>
            <div class="order-kv">
              <div class="k">房间</div>
              <div class="v">{{ roomName || '-' }}</div>
            </div>
            <div class="order-kv">
              <div class="k">套餐</div>
              <div class="v">{{ packageSummary || '-' }}</div>
            </div>
            <div class="order-kv">
              <div class="k">单价</div>
              <div class="v order-money">¥{{ unitPrice.toFixed(0) }}/晚</div>
            </div>

            <div class="order-divider" />

            <el-form label-width="96px" class="form">
              <el-form-item label="入住/离店">
                <el-date-picker
                  v-model="dateRange"
                  type="daterange"
                  range-separator="至"
                  start-placeholder="入住日期"
                  end-placeholder="离店日期"
                  value-format="YYYY-MM-DD"
                  @change="onDateChange"
                />
              </el-form-item>

              <el-form-item label="余房">
                <div class="order-remain">
                  <el-tag v-if="remainRooms === null" effect="plain">请选择日期查询</el-tag>
                  <el-tag v-else-if="remainRooms <= 0" type="danger" effect="dark">无房</el-tag>
                  <el-tag v-else type="success" effect="dark">剩余 {{ remainRooms }} 间</el-tag>
                  <el-button
                    class="remain-btn"
                    size="small"
                    :loading="checking"
                    @click="checkRemain"
                    :disabled="!canCheck"
                  >
                    重新查询
                  </el-button>
                </div>
              </el-form-item>

              <el-form-item label="房间数量">
                <el-input-number
                  v-model="roomNum"
                  :min="1"
                  :max="maxRoomNum"
                  :disabled="remainRooms === null || remainRooms <= 0"
                />
              </el-form-item>
            </el-form>
          </div>
        </div>

        <div>
          <div class="order-card">
            <div class="order-card-title">
              <el-icon><CircleCheck /></el-icon>
              费用明细
            </div>

            <div class="order-price-row">
              <div class="label">入住晚数</div>
              <div class="value">{{ nights }} 晚</div>
            </div>
            <div class="order-price-row">
              <div class="label">房间数量</div>
              <div class="value">{{ roomNum }} 间</div>
            </div>
            <div class="order-price-row">
              <div class="label">商品金额</div>
              <div class="value">¥{{ totalPrice.toFixed(2) }}</div>
            </div>
            <div class="order-divider" />
            <div class="order-total">
              <div class="label">应付</div>
              <div class="value">¥{{ totalPrice.toFixed(2) }}</div>
            </div>

            <el-button
              type="primary"
              size="large"
              class="submit"
              :loading="submitting"
              :disabled="!canSubmit"
              @click="submitOrder"
            >
              去支付
            </el-button>

            <div class="order-tip">支付页面暂未实现，将跳转到占位页</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ArrowLeft, CircleCheck } from '@element-plus/icons-vue';
import Message from '@/utils/message';
import Storage from '@/utils/storage';
import { checkHotelRoomRemain, createHotelOrder } from '@/api/publicOrder';

const route = useRoute();
const router = useRouter();

const hotelId = computed(() => String(route.query.hotelId || ''));
const hotelName = computed(() => String(route.query.hotelName || ''));
const roomId = computed(() => String(route.query.roomId || ''));
const roomName = computed(() => String(route.query.roomName || ''));
const packageId = computed(() => String(route.query.packageId || ''));
const packageSummary = computed(() => String(route.query.packageSummary || ''));
const unitPrice = computed(() => {
  const p = Number(route.query.price);
  return Number.isFinite(p) && p > 0 ? p : 0;
});

const dateRange = ref([]);
const roomNum = ref(1);

const checking = ref(false);
const remainRooms = ref(null);
const submitting = ref(false);

const canCheck = computed(() => {
  if (!hotelId.value || !roomId.value) return false;
  return Array.isArray(dateRange.value) && dateRange.value.length === 2 && !!dateRange.value[0] && !!dateRange.value[1];
});

const nights = computed(() => {
  if (!Array.isArray(dateRange.value) || dateRange.value.length !== 2) return 0;
  const start = new Date(dateRange.value[0]);
  const end = new Date(dateRange.value[1]);
  const diff = end.getTime() - start.getTime();
  if (!Number.isFinite(diff) || diff <= 0) return 0;
  return Math.round(diff / 86400000);
});

const maxRoomNum = computed(() => {
  if (remainRooms.value === null) return 1;
  if (remainRooms.value <= 0) return 1;
  return Math.max(1, Number(remainRooms.value));
});

const totalPrice = computed(() => {
  const n = nights.value;
  const num = Number(roomNum.value) || 0;
  const price = unitPrice.value;
  if (n <= 0 || num <= 0 || price <= 0) return 0;
  return n * num * price;
});

const canSubmit = computed(() => {
  if (!canCheck.value) return false;
  if (checking.value || submitting.value) return false;
  if (!unitPrice.value) return false;
  if (nights.value <= 0) return false;
  if (remainRooms.value === null || remainRooms.value <= 0) return false;
  if (roomNum.value < 1 || roomNum.value > maxRoomNum.value) return false;
  return true;
});

const goBack = () => {
  router.back();
};

const checkRemain = async () => {
  if (!canCheck.value) {
    Message.warning('请先选择入住与离店日期');
    return;
  }
  checking.value = true;
  try {
    const res = await checkHotelRoomRemain({
      asHotel: hotelId.value,
      asRoom: roomId.value,
      checkInDate: dateRange.value[0],
      checkOutDate: dateRange.value[1]
    });
    const data = res?.data;
    if (data?.code !== 200) {
      remainRooms.value = 0;
      Message.warning(data?.msg || '查询失败');
      return;
    }
    const remain = Number(data?.data?.remainRooms);
    remainRooms.value = Number.isFinite(remain) ? remain : 0;
    if (remainRooms.value <= 0) {
      Message.warning('当前日期没有剩余房间');
    } else {
      Message.success('已查询到可预订房间');
      if (roomNum.value > remainRooms.value) roomNum.value = remainRooms.value;
    }
  } finally {
    checking.value = false;
  }
};

const onDateChange = async () => {
  remainRooms.value = null;
  if (roomNum.value < 1) roomNum.value = 1;
  if (canCheck.value) {
    await checkRemain();
  }
};

const submitOrder = async () => {
  if (!canSubmit.value) {
    Message.warning('请完善订单信息');
    return;
  }

  const userInfoRaw = localStorage.getItem('userInfo');
  let userId = null;
  try {
    userId = userInfoRaw ? JSON.parse(userInfoRaw)?.id : null;
  } catch {
    userId = null;
  }
  if (!userId) {
    const token = Storage.get('token');
    if (!token) {
      Message.warning('请先登录');
      router.push('/login');
      return;
    }
  }

  submitting.value = true;
  try {
    const res = await createHotelOrder({
      asUser: userId,
      asHotel: hotelId.value,
      asRoom: roomId.value,
      roomNum: Number(roomNum.value),
      checkInDate: dateRange.value[0],
      checkOutDate: dateRange.value[1],
      payAmount: Number(totalPrice.value).toFixed(2),
      isPay: 0,
      hotelName: hotelName.value,
      roomName: roomName.value
    });
    const data = res?.data;
    if (data?.code !== 200) {
      Message.warning(data?.msg || '创建订单失败');
      return;
    }
    const id = data?.data?.id;
    Message.success('订单已创建');
    router.push({
      path: '/home/pay',
      query: {
        orderType: 'hotel',
        orderId: id,
        hotelName: hotelName.value,
        roomName: roomName.value,
        amount: Number(totalPrice.value).toFixed(2),
        checkInDate: dateRange.value[0],
        checkOutDate: dateRange.value[1],
        roomNum: String(roomNum.value),
        packageId: packageId.value
      }
    });
  } finally {
    submitting.value = false;
  }
};
</script>
