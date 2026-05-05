<template>
  <div class="row">
    <img class="avatar" :src="comment.asUser?.avatarUrl || defaultAvatarUrl" alt="" />

    <div class="main">
      <div class="top">
        <div class="name">{{ comment.asUser?.name || '匿名' }}</div>
        <div class="time">{{ formatTime(comment.createTime) }}</div>
      </div>

      <div class="content">{{ comment.content }}</div>

      <div class="actions">
        <button class="action" :class="comment.voted === 'like' ? 'active' : ''" @click="emit('vote', { id: comment.id, voteType: 'like' })">
          <el-icon><CaretTop /></el-icon>
          <span>{{ Number(comment.likeCount || 0) }}</span>
        </button>
        <button class="action" :class="comment.voted === 'dislike' ? 'active' : ''" @click="emit('vote', { id: comment.id, voteType: 'dislike' })">
          <el-icon><CaretBottom /></el-icon>
          <span>{{ Number(comment.dislikeCount || 0) }}</span>
        </button>
        <button class="action link" @click="emit('reply')">
          <el-icon><ChatDotRound /></el-icon>
          <span>回复</span>
        </button>
      </div>

      <div v-if="Array.isArray(comment.replies) && comment.replies.length" class="replies">
        <div class="reply" v-for="r in visibleReplies" :key="r.id">
          <img class="avatar sm" :src="r.asUser?.avatarUrl || defaultAvatarUrl" alt="" />
          <div class="reply-body">
            <div class="top">
              <div class="name">{{ r.asUser?.name || '匿名' }}</div>
              <div class="time">{{ formatTime(r.createTime) }}</div>
            </div>
            <div class="content">{{ r.content }}</div>
            <div class="actions">
              <button class="action" :class="r.voted === 'like' ? 'active' : ''" @click="emit('vote-reply', { id: r.id, voteType: 'like' })">
                <el-icon><CaretTop /></el-icon>
                <span>{{ Number(r.likeCount || 0) }}</span>
              </button>
              <button class="action" :class="r.voted === 'dislike' ? 'active' : ''" @click="emit('vote-reply', { id: r.id, voteType: 'dislike' })">
                <el-icon><CaretBottom /></el-icon>
                <span>{{ Number(r.dislikeCount || 0) }}</span>
              </button>
              <button class="action link" @click="emit('reply-reply', r)">
                <el-icon><ChatDotRound /></el-icon>
                <span>回复</span>
              </button>
            </div>
          </div>
        </div>

        <div v-if="comment.replies.length > 2" class="more">
          <span v-if="!expanded" class="more-link" @click="expanded = true">共 {{ comment.replies.length }} 条回复，点击查看</span>
          <span v-else class="more-link" @click="expanded = false">收起回复</span>
        </div>
      </div>

      <slot />
    </div>
  </div>
</template>

<script setup>
import { computed, defineEmits, defineProps, ref } from 'vue';
import { CaretBottom, CaretTop, ChatDotRound } from '@element-plus/icons-vue';
import defaultAvatarUrl from '@/assets/user.png';

const props = defineProps({
  comment: {
    type: Object,
    required: true
  }
});

const emit = defineEmits(['reply', 'reply-reply', 'vote', 'vote-reply']);

const expanded = ref(false);

const visibleReplies = computed(() => {
  const replies = Array.isArray(props.comment.replies) ? props.comment.replies : [];
  if (expanded.value) return replies;
  return replies.slice(0, 2);
});

const pad2 = (n) => String(n).padStart(2, '0');

const formatTime = (iso) => {
  if (!iso) return '';
  const d = new Date(iso);
  if (d.toString() === 'Invalid Date') return String(iso);
  return `${d.getFullYear()}-${pad2(d.getMonth() + 1)}-${pad2(d.getDate())} ${pad2(d.getHours())}:${pad2(d.getMinutes())}`;
};
</script>

<style scoped>
.row {
  display: flex;
  gap: 12px;
  padding: 12px 0;
  border-bottom: 1px solid #eef2f7;
}

.avatar {
  width: 38px;
  height: 38px;
  border-radius: 999px;
  border: 1px solid #e5e7eb;
  background: #fff;
}

.avatar.sm {
  width: 30px;
  height: 30px;
}

.main {
  flex: 1;
  min-width: 0;
  text-align: left;
}

.top {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  gap: 10px;
}

.name {
  font-weight: 900;
  color: #0f172a;
  font-size: 13px;
}

.time {
  font-size: 12px;
  color: #94a3b8;
  white-space: nowrap;
}

.content {
  margin-top: 8px;
  color: #334155;
  font-size: 13px;
  line-height: 1.75;
  word-break: break-word;
}

.actions {
  margin-top: 8px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.action {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 6px 10px;
  border-radius: 999px;
  border: 1px solid #e5e7eb;
  background: #fff;
  cursor: pointer;
  color: #64748b;
  font-size: 12px;
}

.action:hover {
  background: #f8fafc;
}

.action.active {
  color: #1677ff;
  border-color: rgba(22, 119, 255, 0.35);
  background: rgba(22, 119, 255, 0.08);
}

.action.link {
  border-color: transparent;
  background: transparent;
  padding-left: 6px;
}

.replies {
  margin-top: 10px;
  padding: 10px 12px;
  border-radius: 12px;
  background: #f8fafc;
  border: 1px solid #eef2f7;
}

.reply {
  display: flex;
  gap: 10px;
  padding: 10px 0;
}

.reply + .reply {
  border-top: 1px dashed #e5e7eb;
}

.reply-body {
  flex: 1;
  min-width: 0;
}

.more {
  margin-top: 8px;
}

.more-link {
  font-size: 12px;
  color: #1677ff;
  cursor: pointer;
}

.more-link:hover {
  text-decoration: underline;
}
</style>

