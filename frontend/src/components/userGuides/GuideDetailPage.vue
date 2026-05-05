<template>
  <div class="page">
    <div class="main" ref="mainScrollRef">
      <div class="container" v-loading="loadingGuide">
        <div v-if="error" class="error">
          <el-result icon="error" title="攻略加载失败" :sub-title="error">
            <template #extra>
              <el-button type="primary" @click="goBack">返回</el-button>
            </template>
          </el-result>
        </div>

        <div v-else-if="guide" class="content">
          <div class="topbar">
            <el-button text type="primary" @click="goBack">
              <el-icon><ArrowLeft /></el-icon>
              返回列表
            </el-button>
          </div>

          <div class="header">
            <div class="author">
              <img class="avatar" :src="guide.asUser?.avatarUrl || defaultAvatarUrl" alt="" />
              <div class="author-meta">
                <div class="author-name">{{ guide.asUser?.name || '匿名' }}</div>
                <div class="author-sub">发布于 {{ formatTime(guide.createTime) }}</div>
              </div>
            </div>
            <div class="title">{{ guide.title }}</div>
          </div>

          <div class="article">
            <MarkdownRenderer :source="guide.content" />
          </div>

          <div class="footnote">更新时间：{{ formatTime(guide.updateTime || guide.createTime) }}</div>

          <GuideCommentsPanel :guide-id="route.params.guideId" :scroll-container="mainScrollRef" />
        </div>

        <div v-else class="empty">
          <el-result icon="warning" title="攻略不存在或已下架">
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
import { ArrowLeft } from '@element-plus/icons-vue';

import defaultAvatarUrl from '@/assets/user.png';
import GuideCommentsPanel from '@/components/userGuides/GuideCommentsPanel.vue';
import MarkdownRenderer from '@/components/userGuides/MarkdownRenderer.vue';
import { getGuideDetail } from '@/api/publicGuide';

const route = useRoute();
const router = useRouter();

const mainScrollRef = ref(null);

const loadingGuide = ref(false);
const error = ref('');
const guide = ref(null);

const pad2 = (n) => String(n).padStart(2, '0');

const formatTime = (iso) => {
  if (!iso) return '';
  const d = new Date(iso);
  if (d.toString() === 'Invalid Date') return String(iso);
  return `${d.getFullYear()}-${pad2(d.getMonth() + 1)}-${pad2(d.getDate())} ${pad2(d.getHours())}:${pad2(d.getMinutes())}`;
};

const goBack = () => {
  router.push('/home/guides?restore=1');
};

const loadGuide = async () => {
  loadingGuide.value = true;
  error.value = '';
  try {
    const res = await getGuideDetail(route.params.guideId);
    if (res?.data?.code !== 200) {
      error.value = res?.data?.msg || '未知错误';
      guide.value = null;
      return;
    }
    guide.value = res?.data?.data || null;
  } finally {
    loadingGuide.value = false;
  }
};

onMounted(async () => {
  await loadGuide();
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

.topbar {
  margin-bottom: 10px;
  text-align: left;
}

.header {
  background: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 14px;
  padding: 16px;
}

.author {
  display: flex;
  align-items: center;
  gap: 12px;
}

.avatar {
  width: 44px;
  height: 44px;
  border-radius: 999px;
  border: 1px solid #e5e7eb;
  background: #fff;
}

.author-meta {
  text-align: left;
}

.author-name {
  font-weight: 900;
  color: #0f172a;
}

.author-sub {
  margin-top: 2px;
  font-size: 12px;
  color: #64748b;
}

.title {
  margin-top: 14px;
  font-size: 24px;
  font-weight: 900;
  color: #0f172a;
  text-align: left;
  line-height: 1.35;
}

.article {
  margin-top: 14px;
  background: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 14px;
  padding: 16px;
}

.footnote {
  margin-top: 10px;
  font-size: 12px;
  color: #94a3b8;
  text-align: left;
}

</style>
