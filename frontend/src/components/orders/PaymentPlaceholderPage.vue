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
        <div class="order-title">支付</div>
        <div class="order-sub">此页面为占位，后续接入真实支付</div>
      </div>

      <div class="order-grid">
        <div class="order-card">
          <div class="order-card-title">
            <el-icon><CircleCheck /></el-icon>
            支付信息
          </div>

          <div class="order-kv">
            <div class="k">订单类型</div>
            <div class="v">{{ orderTypeText }}</div>
          </div>
          <div class="order-kv">
            <div class="k">订单ID</div>
            <div class="v">{{ String(route.query.orderId || '-') }}</div>
          </div>
          <div class="order-kv" v-if="route.query.amount">
            <div class="k">金额</div>
            <div class="v order-money">¥{{ Number(route.query.amount).toFixed(2) }}</div>
          </div>

          <div class="order-divider" />

          <el-result
            icon="info"
            title="支付模块待实现"
            sub-title="已成功创建订单并跳转到此页面，后续可在此接入支付二维码/收银台等。"
          />

          <div class="actions">
            <el-button
              type="primary"
              size="large"
              class="order-submit"
              :loading="paying"
              @click="markPaid"
            >
              支付成功（模拟）
            </el-button>
            <el-button size="large" @click="goOrders">去订单中心</el-button>
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
import { payHotelOrder, payScenicOrder } from '@/api/publicOrder';
import { payAircraftOrder, payTrainOrder } from '@/api/publicTicket';

const route = useRoute();
const router = useRouter();
const paying = ref(false);

const orderTypeText = computed(() => {
  const t = String(route.query.orderType || '').toLowerCase();
  if (t === 'hotel') return '酒店订单';
  if (t === 'scenic') return '景区订单';
  if (t === 'train') return '火车票订单';
  if (t === 'aircraft') return '机票订单';
  return '订单';
});

const markPaid = async () => {
  const orderType = String(route.query.orderType || '').toLowerCase();
  const orderId = route.query.orderId;
  if (!orderId) {
    Message.warning('订单ID不存在');
    return;
  }
  paying.value = true;
  try {
    const res =
      orderType === 'scenic'
        ? await payScenicOrder({ id: orderId })
        : orderType === 'train'
          ? await payTrainOrder({ id: orderId })
          : orderType === 'aircraft'
            ? await payAircraftOrder({ id: orderId })
            : await payHotelOrder({ id: orderId });
    const data = res?.data;
    if (data?.code !== 200) {
      Message.warning(data?.msg || '支付失败');
      return;
    }
    Message.success('支付成功');
    const tab = String(route.query.tab || orderType || '').toLowerCase();
    router.push({ path: '/home/orders', query: tab ? { tab } : {} });
  } finally {
    paying.value = false;
  }
};

const goBack = () => {
  router.back();
};

const goOrders = () => {
  const tab = String(route.query.tab || route.query.orderType || '').toLowerCase();
  router.push({ path: '/home/orders', query: tab ? { tab } : {} });
};
</script>

<style scoped>
.actions {
  display: flex;
  gap: 12px;
  justify-content: center;
  margin-top: 14px;
}
</style>
