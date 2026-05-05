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
        <div class="order-title">景区下单</div>
        <div class="order-sub">确认信息后创建订单</div>
      </div>

      <div class="order-grid">
        <div class="order-card">
          <div class="order-card-title">
            <el-icon><CircleCheck /></el-icon>
            订单信息
          </div>

          <div class="order-kv">
            <div class="k">景区</div>
            <div class="v">{{ scenicName || '-' }}</div>
          </div>
          <div class="order-kv">
            <div class="k">门票单价</div>
            <div class="v order-money">¥{{ price.toFixed(0) }}/人</div>
          </div>

          <div class="order-divider" />

          <div class="order-total">
            <div class="label">应付</div>
            <div class="value">¥{{ price.toFixed(2) }}</div>
          </div>

          <el-button
            type="primary"
            size="large"
            class="order-submit"
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
</template>

<script setup>
import { computed, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ArrowLeft, CircleCheck } from '@element-plus/icons-vue';
import Message from '@/utils/message';
import Storage from '@/utils/storage';
import { createScenicOrder } from '@/api/publicOrder';

const route = useRoute();
const router = useRouter();

const scenicId = computed(() => String(route.query.scenicId || ''));
const scenicName = computed(() => String(route.query.scenicName || ''));
const price = computed(() => {
  const p = Number(route.query.price);
  return Number.isFinite(p) && p >= 0 ? p : 0;
});

const submitting = ref(false);

const canSubmit = computed(() => {
  if (submitting.value) return false;
  if (!scenicId.value) return false;
  if (price.value <= 0) return false;
  return true;
});

const goBack = () => {
  router.back();
};

const submitOrder = async () => {
  if (!canSubmit.value) {
    Message.warning('当前景区无法创建门票订单');
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
    const res = await createScenicOrder({
      asUser: userId,
      asScenic: scenicId.value,
      payAmount: Number(price.value).toFixed(2),
      isPay: 0,
      scenicName: scenicName.value
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
        orderType: 'scenic',
        orderId: id,
        scenicName: scenicName.value,
        amount: Number(price.value).toFixed(2)
      }
    });
  } finally {
    submitting.value = false;
  }
};
</script>
