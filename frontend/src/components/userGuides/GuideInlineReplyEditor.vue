<template>
  <div class="reply-editor">
    <div class="reply-tip">回复</div>
    <div class="reply-line">
      <span class="mention">{{ props.mention }}</span>
      <el-input
        :model-value="props.modelValue"
        type="textarea"
        :rows="2"
        maxlength="1000"
        show-word-limit
        placeholder="输入回复内容"
        @update:model-value="emit('update:modelValue', $event)"
      />
    </div>
    <div class="reply-actions">
      <el-button size="small" @click="emit('cancel')">取消</el-button>
      <el-button size="small" type="primary" :disabled="!String(props.modelValue || '').trim()" :loading="props.loading" @click="emit('submit')">发送</el-button>
    </div>
  </div>
</template>

<script setup>
import { defineEmits, defineProps } from 'vue';

const props = defineProps({
  mention: {
    type: String,
    required: true
  },
  modelValue: {
    type: String,
    default: ''
  },
  loading: {
    type: Boolean,
    default: false
  }
});

const emit = defineEmits(['update:modelValue', 'cancel', 'submit']);
</script>

<style scoped>
.reply-editor {
  margin-top: 10px;
  padding: 10px 12px;
  border-radius: 12px;
  background: #ffffff;
  border: 1px solid #eef2f7;
}

.reply-tip {
  font-size: 12px;
  color: #64748b;
  margin-bottom: 8px;
  text-align: left;
}

.reply-line {
  display: grid;
  grid-template-columns: auto 1fr;
  gap: 10px;
  align-items: start;
}

.mention {
  display: inline-flex;
  align-items: center;
  padding: 6px 10px;
  border-radius: 999px;
  background: rgba(22, 119, 255, 0.08);
  border: 1px solid rgba(22, 119, 255, 0.25);
  color: #1677ff;
  font-size: 12px;
  font-weight: 800;
  height: 32px;
  white-space: nowrap;
}

.reply-actions {
  margin-top: 10px;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

@media (max-width: 640px) {
  .reply-line {
    grid-template-columns: 1fr;
  }
  .mention {
    justify-self: start;
  }
}
</style>

