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
        <div class="order-title">发表评论</div>
        <div class="order-sub">{{ titleSub }}</div>
      </div>

      <div class="order-grid">
        <div class="order-card">
          <el-form label-width="84px" class="form">
            <el-form-item label="评分">
              <el-rate v-model="rating" allow-half show-score />
            </el-form-item>
            <el-form-item label="内容">
              <el-input v-model="content" type="textarea" :rows="6" placeholder="写下你的真实体验吧" />
            </el-form-item>
            <el-form-item label="照片">
              <MultiImageUpload v-model="images" @upload-state-change="onUploadStateChange" />
            </el-form-item>
          </el-form>

          <div class="actions">
            <el-button type="primary" size="large" :loading="submitting" :disabled="uploading" @click="submit">提交评论</el-button>
            <el-button size="large" @click="goOrders">{{ backButtonText }}</el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ArrowLeft } from '@element-plus/icons-vue';
import Message from '@/utils/message';
import { createHotelReview, createScenicReview } from '@/api/publicComment';
import MultiImageUpload from '@/components/MultiImageUpload.vue';

const route = useRoute();
const router = useRouter();

const submitting = ref(false);
const rating = ref(5);
const content = ref('');
const images = ref([]);
const uploading = ref(false);

const reviewType = computed(() => String(route.query.type || '').toLowerCase());
const hotelId = computed(() => String(route.query.hotelId || ''));
const roomId = computed(() => String(route.query.roomId || ''));
const scenicId = computed(() => String(route.query.scenicId || ''));
const redirect = computed(() => String(route.query.redirect || ''));

const backButtonText = computed(() => {
  if (redirect.value) return '返回';
  return '回到订单';
});

const titleSub = computed(() => {
  if (reviewType.value === 'hotel') return '从订单入口对酒店进行评论';
  if (reviewType.value === 'scenic') return '对景区进行评论';
  return '请选择评论对象';
});

const goBack = () => {
  router.back();
};

const goOrders = () => {
  if (redirect.value) {
    router.push(redirect.value);
    return;
  }
  router.push({
    path: '/home/orders',
    query: { tab: reviewType.value === 'scenic' ? 'scenic' : 'hotel' }
  });
};

const onUploadStateChange = (isUploading) => {
  uploading.value = !!isUploading;
};

const submit = async () => {
  if (submitting.value) return;
  if (uploading.value) {
    Message.warning('图片上传中，请稍候');
    return;
  }
  const r = Number(rating.value);
  if (!Number.isFinite(r) || r <= 0) {
    Message.warning('请先选择评分');
    return;
  }
  const c = String(content.value || '').trim();
  if (!c) {
    Message.warning('请填写评论内容');
    return;
  }

  submitting.value = true;
  try {
    const imageFileIds = Array.isArray(images.value) ? images.value.map((x) => x?.id).filter(Boolean) : [];
    let res;
    if (reviewType.value === 'hotel') {
      if (!hotelId.value) {
        Message.warning('酒店ID不存在');
        return;
      }
      res = await createHotelReview({
        hotelId: hotelId.value,
        roomId: roomId.value || undefined,
        content: c,
        rating: r,
        imageFileIds
      });
    } else if (reviewType.value === 'scenic') {
      if (!scenicId.value) {
        Message.warning('景区ID不存在');
        return;
      }
      res = await createScenicReview({
        scenicId: scenicId.value,
        content: c,
        rating: r,
        imageFileIds
      });
    } else {
      Message.warning('评论类型不正确');
      return;
    }
    const data = res?.data;
    if (data?.code !== 200) {
      Message.warning(data?.msg || '提交失败');
      return;
    }
    Message.success('提交成功');
    await goOrders();
  } finally {
    submitting.value = false;
  }
};
</script>

<style scoped>
.actions {
  display: flex;
  justify-content: center;
  gap: 12px;
  margin-top: 14px;
}
</style>
