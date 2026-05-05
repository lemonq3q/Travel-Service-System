<template>
  <div class="page">
    <div class="main">
      <div class="container">
        <div class="topbar">
          <el-button text type="primary" @click="goBack">
            <el-icon><ArrowLeft /></el-icon>
            返回
          </el-button>
        </div>

        <div class="header">
          <div class="title">我的攻略</div>
          <div class="sub">支持查看历史攻略、编辑/删除，以及新增发布</div>
        </div>

        <div class="grid">
          <div class="sidebar">
            <div class="sidebar-header">
              <div class="sidebar-title">历史攻略</div>
              <el-button type="primary" size="small" @click="newGuide">新增</el-button>
            </div>

            <div
              class="sidebar-list"
              v-infinite-scroll="loadMore"
              :infinite-scroll-disabled="loadingList || noMore"
              :infinite-scroll-distance="220"
            >
              <div
                v-for="g in items"
                :key="g.id"
                class="sidebar-item"
                :class="{ active: String(selectedId) === String(g.id) }"
                @click="selectGuide(g.id)"
              >
                <div class="sidebar-item-title">{{ g.title || '未命名攻略' }}</div>
                <div class="sidebar-item-sub">{{ formatTime(g.updateTime || g.createTime) }}</div>
              </div>

              <div v-if="loadingList" class="sidebar-loading">加载中...</div>
              <div v-if="noMore && items.length" class="sidebar-end">没有更多了</div>
              <el-empty v-if="!loadingList && items.length === 0" description="暂无历史攻略" />
            </div>
          </div>

          <div class="editor">
            <div class="editor-header">
              <div class="editor-title">{{ selectedId ? '编辑攻略' : '新建攻略' }}</div>
              <div class="editor-actions">
                <el-button v-if="selectedId" type="danger" plain :loading="deleting" @click="deleteCurrent">
                  删除
                </el-button>
                <el-button @click="newGuide">新建</el-button>
                <el-button type="primary" :loading="saving" :disabled="!canSave" @click="save">
                  {{ selectedId ? '保存' : '发布' }}
                </el-button>
              </div>
            </div>

            <div class="card">
              <el-form label-position="top">
                <el-form-item label="标题" required>
                  <el-input v-model="title" maxlength="100" show-word-limit placeholder="请输入攻略标题" />
                </el-form-item>
              </el-form>

              <GuideMarkdownEditor v-model="content" v-model:images="uploadedImages" />
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';
import { ArrowLeft } from '@element-plus/icons-vue';

import Message from '@/utils/message';
import GuideMarkdownEditor from '@/components/userGuides/GuideMarkdownEditor.vue';
import { createGuide, deleteMyGuide, getMyGuideDetail, listMyGuides, updateMyGuide } from '@/api/publicGuide';

const router = useRouter();

const items = ref([]);
const cursor = ref(null);
const loadingList = ref(false);
const noMore = ref(false);

const selectedId = ref('');
const title = ref('');
const content = ref('');
const uploadedImages = ref([]);

const saving = ref(false);
const deleting = ref(false);
const loadingDetail = ref(false);

const canSave = computed(() => {
  return title.value.trim().length > 0 && content.value.trim().length > 0 && !saving.value && !loadingDetail.value;
});

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

const fetchList = async ({ reset } = {}) => {
  if (loadingList.value) return;
  if (!reset && noMore.value) return;
  loadingList.value = true;
  try {
    const res = await listMyGuides({ cursor: reset ? null : cursor.value, limit: 10 });
    if (res?.data?.code !== 200) {
      Message.warning(res?.data?.msg || '加载失败');
      return;
    }
    const data = res?.data?.data;
    const nextItems = Array.isArray(data?.items) ? data.items : [];
    const nextCursor = data?.nextCursor ?? null;
    items.value = reset ? nextItems : [...items.value, ...nextItems];
    cursor.value = nextCursor;
    noMore.value = nextCursor === null;
  } finally {
    loadingList.value = false;
  }
};

const loadMore = async () => {
  await fetchList({ reset: false });
};

const newGuide = () => {
  selectedId.value = '';
  title.value = '';
  content.value = '';
  uploadedImages.value = [];
};

const selectGuide = async (id) => {
  if (!id) return;
  loadingDetail.value = true;
  try {
    const res = await getMyGuideDetail(id);
    if (res?.data?.code !== 200) {
      Message.warning(res?.data?.msg || '加载失败');
      return;
    }
    const g = res?.data?.data;
    if (!g) {
      Message.warning('攻略不存在');
      return;
    }
    selectedId.value = String(g.id);
    title.value = g.title || '';
    content.value = g.content || '';
    const imgs = Array.isArray(g.images) ? g.images : [];
    const sorted = [...imgs].sort((a, b) => Number(a.sortIndex || 0) - Number(b.sortIndex || 0));
    uploadedImages.value = sorted
      .filter(x => x?.fileId && x?.url)
      .map(x => ({ id: x.fileId, url: x.url }));
  } finally {
    loadingDetail.value = false;
  }
};

const save = async () => {
  if (!canSave.value) return;
  saving.value = true;
  try {
    if (!selectedId.value) {
      const res = await createGuide({
        title: title.value.trim(),
        content: content.value,
        images: uploadedImages.value
      });
      if (res?.data?.code !== 200) {
        Message.warning(res?.data?.msg || '发布失败');
        return;
      }
      const id = res?.data?.data?.id;
      Message.success('发布成功');
      await fetchList({ reset: true });
      if (id) await selectGuide(id);
      return;
    }

    const res = await updateMyGuide({
      guideId: selectedId.value,
      title: title.value.trim(),
      content: content.value,
      images: uploadedImages.value
    });
    if (res?.data?.code !== 200) {
      Message.warning(res?.data?.msg || '保存失败');
      return;
    }
    Message.success('已保存');
    await fetchList({ reset: true });
  } finally {
    saving.value = false;
  }
};

const deleteCurrent = async () => {
  if (!selectedId.value) return;
  deleting.value = true;
  try {
    const res = await deleteMyGuide(selectedId.value);
    if (res?.data?.code !== 200) {
      Message.warning(res?.data?.msg || '删除失败');
      return;
    }
    Message.success('已删除');
    await fetchList({ reset: true });
    newGuide();
  } finally {
    deleting.value = false;
  }
};

onMounted(async () => {
  await fetchList({ reset: true });
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
  padding: 10px 0 16px;
  text-align: left;
}

.title {
  font-size: 24px;
  font-weight: 900;
  color: #0f172a;
}

.sub {
  margin-top: 8px;
  font-size: 13px;
  color: #64748b;
}

.grid {
  display: grid;
  grid-template-columns: 320px 1fr;
  gap: 14px;
}

.sidebar {
  background: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 14px;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  min-height: 620px;
}

.sidebar-header {
  padding: 12px;
  border-bottom: 1px solid #eef2f7;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
}

.sidebar-title {
  font-weight: 900;
  color: #0f172a;
}

.sidebar-list {
  padding: 10px;
}

.sidebar-item {
  padding: 10px;
  border-radius: 12px;
  border: 1px solid #eef2f7;
  cursor: pointer;
  transition: all 0.15s ease;
}

.sidebar-item + .sidebar-item {
  margin-top: 10px;
}

.sidebar-item.active {
  border-color: #c7d2fe;
  background: #eef2ff;
}

.sidebar-item-title {
  font-weight: 900;
  color: #0f172a;
  text-align: left;
  line-height: 1.4;
}

.sidebar-item-sub {
  margin-top: 6px;
  font-size: 12px;
  color: #64748b;
  text-align: left;
}

.sidebar-loading,
.sidebar-end {
  color: #94a3b8;
  text-align: center;
  padding: 12px 0;
}

.editor {
  min-width: 0;
}

.editor-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 10px;
}

.editor-title {
  font-size: 18px;
  font-weight: 900;
  color: #0f172a;
  text-align: left;
}

.editor-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.card {
  background: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 14px;
  padding: 14px;
}

@media (max-width: 960px) {
  .grid {
    grid-template-columns: 1fr;
  }
  .sidebar {
    min-height: auto;
  }
}
</style>

