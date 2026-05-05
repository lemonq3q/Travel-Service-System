<template>
  <div class="composer">
    <img class="avatar" :src="props.avatarUrl" alt="" />
    <div class="composer-main">
      <el-input
        :model-value="props.modelValue"
        type="textarea"
        :rows="3"
        maxlength="1000"
        show-word-limit
        placeholder="写下你对这篇攻略的看法（最多 1000 字）"
        @update:model-value="emit('update:modelValue', $event)"
      />
      <div class="composer-actions">
        <el-button type="primary" :disabled="!String(props.modelValue || '').trim()" :loading="props.loading" @click="emit('submit')">发表评论</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { defineEmits, defineProps } from 'vue';

const props = defineProps({
  modelValue: {
    type: String,
    default: ''
  },
  avatarUrl: {
    type: String,
    required: true
  },
  loading: {
    type: Boolean,
    default: false
  }
});

const emit = defineEmits(['update:modelValue', 'submit']);
</script>

<style scoped>
.avatar {
  width: 44px;
  height: 44px;
  border-radius: 999px;
  border: 1px solid #e5e7eb;
  background: #fff;
}

.composer {
  display: flex;
  gap: 12px;
  padding: 10px 0 12px;
  border-bottom: 1px solid #eef2f7;
}

.composer-main {
  flex: 1;
  min-width: 0;
}

.composer-actions {
  margin-top: 10px;
  display: flex;
  justify-content: flex-end;
}
</style>

