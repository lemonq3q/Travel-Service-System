<template>
  <div class="md" v-html="html"></div>
</template>

<script setup>
import { computed, defineProps } from 'vue';
import MarkdownIt from 'markdown-it';

const props = defineProps({
  source: {
    type: String,
    default: ''
  }
});

const md = new MarkdownIt({
  html: false,
  linkify: true,
  breaks: true
});

const html = computed(() => {
  try {
    return md.render(String(props.source || ''));
  } catch {
    return '';
  }
});
</script>

<style scoped>
.md {
  text-align: left;
  color: #111827;
  line-height: 1.8;
  font-size: 14px;
}

.md :deep(h1) {
  font-size: 22px;
  line-height: 1.3;
  margin: 18px 0 10px;
  font-weight: 900;
  color: #0f172a;
}

.md :deep(h2) {
  font-size: 18px;
  line-height: 1.4;
  margin: 16px 0 8px;
  font-weight: 900;
  color: #0f172a;
}

.md :deep(h3) {
  font-size: 16px;
  line-height: 1.5;
  margin: 14px 0 8px;
  font-weight: 900;
  color: #0f172a;
}

.md :deep(p) {
  margin: 10px 0;
  color: #334155;
}

.md :deep(ul),
.md :deep(ol) {
  padding-left: 18px;
  margin: 10px 0;
  color: #334155;
}

.md :deep(blockquote) {
  margin: 12px 0;
  padding: 10px 12px;
  border-left: 4px solid #e2e8f0;
  background: #f8fafc;
  color: #475569;
}

.md :deep(code) {
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, 'Liberation Mono', 'Courier New', monospace;
  font-size: 12px;
  background: #f1f5f9;
  padding: 2px 6px;
  border-radius: 6px;
}

.md :deep(pre) {
  margin: 12px 0;
  padding: 12px;
  background: #0b1220;
  color: #e2e8f0;
  border-radius: 12px;
  overflow: auto;
}

.md :deep(pre code) {
  background: transparent;
  padding: 0;
  color: inherit;
}

.md :deep(img) {
  max-width: 100%;
  border-radius: 12px;
  margin: 12px 0;
  border: 1px solid #e5e7eb;
}

.md :deep(a) {
  color: #1677ff;
  text-decoration: none;
}

.md :deep(a:hover) {
  text-decoration: underline;
}

.md :deep(hr) {
  border: 0;
  border-top: 1px solid #e5e7eb;
  margin: 18px 0;
}
</style>

