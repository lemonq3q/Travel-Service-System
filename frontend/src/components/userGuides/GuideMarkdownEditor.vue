<template>
  <div class="editor-grid">
    <div class="editor">
      <div class="toolbar">
        <div class="tool-left">
          <el-button size="small" @click="insertHeading">标题</el-button>
          <el-button size="small" @click="insertList">列表</el-button>
          <el-button size="small" @click="insertBold">加粗</el-button>
        </div>

        <div class="tool-right">
          <el-upload :show-file-list="false" accept="image/*" :http-request="handleUpload">
            <el-button size="small" type="primary" plain :loading="uploading">
              <el-icon><Picture /></el-icon>
              插入图片
            </el-button>
          </el-upload>
        </div>
      </div>

      <textarea
        ref="textareaRef"
        class="textarea"
        :value="props.modelValue"
        placeholder="支持 Markdown。建议包含：行程安排 / 交通 / 住宿 / 注意事项"
        @input="onInput"
      ></textarea>
    </div>

    <div class="preview">
      <div class="preview-title">预览</div>
      <div class="preview-body">
        <MarkdownRenderer :source="props.modelValue" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { defineEmits, defineProps, ref } from 'vue';
import { Picture } from '@element-plus/icons-vue';

import Message from '@/utils/message';
import { upload } from '@/api/file';
import MarkdownRenderer from '@/components/userGuides/MarkdownRenderer.vue';

const props = defineProps({
  modelValue: {
    type: String,
    default: ''
  },
  images: {
    type: Array,
    default: () => []
  }
});

const emit = defineEmits(['update:modelValue', 'update:images']);

const uploading = ref(false);
const textareaRef = ref(null);

const updateValue = (next) => {
  emit('update:modelValue', next);
};

const onInput = (e) => {
  updateValue(e?.target?.value ?? '');
};

const insertAtCursor = (text) => {
  const el = textareaRef.value;
  if (!el) {
    updateValue(`${props.modelValue}${text}`);
    return;
  }
  const start = el.selectionStart || 0;
  const end = el.selectionEnd || 0;
  const before = String(props.modelValue || '').slice(0, start);
  const after = String(props.modelValue || '').slice(end);
  const next = `${before}${text}${after}`;
  updateValue(next);
  const nextPos = start + text.length;
  requestAnimationFrame(() => {
    el.focus();
    el.setSelectionRange(nextPos, nextPos);
  });
};

const insertHeading = () => {
  insertAtCursor('\n## 小节标题\n');
};

const insertList = () => {
  insertAtCursor('\n- 条目 1\n- 条目 2\n');
};

const insertBold = () => {
  insertAtCursor('**加粗文字**');
};

const handleUpload = async ({ file }) => {
  if (!file) return;
  uploading.value = true;
  try {
    const res = await upload(file);
    const payload = res?.data?.data;
    if (!payload?.url) {
      Message.warning('上传失败');
      return;
    }
    const nextImages = [...(Array.isArray(props.images) ? props.images : []), { id: payload.id, url: payload.url }];
    emit('update:images', nextImages);
    insertAtCursor(`\n![](${payload.url})\n`);
    Message.success('图片已插入');
  } finally {
    uploading.value = false;
  }
};
</script>

<style scoped>
.editor-grid {
  margin-top: 12px;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 14px;
}

.editor {
  border: 1px solid #eef2f7;
  border-radius: 12px;
  overflow: hidden;
}

.toolbar {
  padding: 10px;
  border-bottom: 1px solid #eef2f7;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
  background: #fafafa;
}

.tool-left,
.tool-right {
  display: flex;
  align-items: center;
  gap: 8px;
}

.textarea {
  width: 100%;
  min-height: 520px;
  resize: vertical;
  border: 0;
  padding: 12px;
  font-size: 13px;
  line-height: 1.75;
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, 'Liberation Mono', 'Courier New', monospace;
  outline: none;
}

.preview {
  border: 1px solid #eef2f7;
  border-radius: 12px;
  overflow: hidden;
  background: #fff;
}

.preview-title {
  padding: 10px 12px;
  border-bottom: 1px solid #eef2f7;
  background: #fafafa;
  text-align: left;
  font-weight: 900;
  color: #0f172a;
}

.preview-body {
  padding: 12px;
  max-height: 560px;
  overflow: auto;
}

@media (max-width: 960px) {
  .editor-grid {
    grid-template-columns: 1fr;
  }
  .preview-body {
    max-height: none;
  }
}
</style>

