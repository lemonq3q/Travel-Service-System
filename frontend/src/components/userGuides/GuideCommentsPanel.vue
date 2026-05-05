<template>
  <div class="section">
    <div class="section-title">评论</div>

    <GuideCommentComposer v-model="newComment" :avatar-url="currentUser.avatarUrl" :loading="posting" @submit="postComment" />

    <div
      class="panel-list"
      v-infinite-scroll="loadMoreReviews"
      :infinite-scroll-disabled="reviewsLoading || reviewsNoMore"
      :infinite-scroll-distance="260"
      :infinite-scroll-container="props.scrollContainer"
    >
      <GuideCommentItem
        v-for="c in reviews"
        :key="c.id"
        :comment="c"
        @reply="openReplyForComment(c)"
        @reply-reply="openReplyForReply(c, $event)"
        @vote="onVoteComment"
        @vote-reply="onVoteReply"
      >
        <GuideInlineReplyEditor
          v-if="replyTarget.reviewId === c.id"
          v-model="replyBody"
          :mention="replyTarget.mention"
          :loading="posting"
          @cancel="cancelReply"
          @submit="postReply"
        />
      </GuideCommentItem>

      <div v-if="reviewsLoading" class="panel-loading">加载中...</div>
      <div v-if="reviewsNoMore && reviews.length" class="panel-end">没有更多评论了</div>
      <el-empty v-if="!reviewsLoading && reviews.length === 0" description="暂无评论" />
    </div>
  </div>
</template>

<script setup>
import { computed, defineProps, onMounted, reactive, ref, watch } from 'vue';

import Message from '@/utils/message';
import defaultAvatarUrl from '@/assets/user.png';
import { jsonStrToObj } from '@/utils/convert';
import GuideCommentItem from '@/components/userGuides/GuideCommentItem.vue';
import GuideCommentComposer from '@/components/userGuides/GuideCommentComposer.vue';
import GuideInlineReplyEditor from '@/components/userGuides/GuideInlineReplyEditor.vue';
import {
  createGuideReview,
  createGuideReviewReply,
  listGuideReviews,
  toggleGuideReviewReplyVote,
  toggleGuideReviewVote
} from '@/api/publicGuide';

const props = defineProps({
  guideId: {
    type: [String, Number],
    required: true
  },
  scrollContainer: {
    type: Object,
    default: null
  }
});

const reviews = ref([]);
const reviewsCursor = ref(null);
const reviewsLoading = ref(false);
const reviewsNoMore = ref(false);

const newComment = ref('');
const posting = ref(false);

const replyTarget = reactive({
  reviewId: '',
  replyUser: null,
  mention: ''
});
const replyBody = ref('');

const currentUser = computed(() => {
  try {
    const user = jsonStrToObj(localStorage.getItem('userInfo')) || {};
    return {
      id: user.id ?? user.userId ?? 0,
      name: user.name || user.username || '我',
      avatarUrl: defaultAvatarUrl
    };
  } catch {
    return { id: 0, name: '我', avatarUrl: defaultAvatarUrl };
  }
});

const loadMoreReviews = async () => {
  if (reviewsLoading.value) return;
  if (reviewsNoMore.value) return;
  reviewsLoading.value = true;
  try {
    const res = await listGuideReviews({
      guideId: props.guideId,
      cursor: reviewsCursor.value,
      limit: 10
    });
    const data = res?.data?.data;
    const nextItems = Array.isArray(data?.items) ? data.items : [];
    const nextCursor = data?.nextCursor ?? null;
    reviews.value = [...reviews.value, ...nextItems];
    reviewsCursor.value = nextCursor;
    reviewsNoMore.value = nextCursor === null;
  } finally {
    reviewsLoading.value = false;
  }
};

const resetAndLoad = async () => {
  reviews.value = [];
  reviewsCursor.value = null;
  reviewsNoMore.value = false;
  cancelReply();
  await loadMoreReviews();
};

const postComment = async () => {
  const content = newComment.value.trim();
  if (!content) return;
  posting.value = true;
  try {
    const res = await createGuideReview({ guideId: props.guideId, content });
    if (res?.data?.code !== 200) {
      Message.warning(res?.data?.msg || '发表评论失败');
      return;
    }
    newComment.value = '';
    cancelReply();
    reviews.value = [res.data.data, ...reviews.value];
    Message.success('已发表');
  } finally {
    posting.value = false;
  }
};

const openReplyForComment = (comment) => {
  replyTarget.reviewId = String(comment.id);
  replyTarget.replyUser = comment.asUser || null;
  replyTarget.mention = `@${comment.asUser?.name || '匿名'}：`;
  replyBody.value = '';
};

const openReplyForReply = (comment, reply) => {
  replyTarget.reviewId = String(comment.id);
  replyTarget.replyUser = reply.asUser || null;
  replyTarget.mention = `@${reply.asUser?.name || '匿名'}：`;
  replyBody.value = '';
};

const cancelReply = () => {
  replyTarget.reviewId = '';
  replyTarget.replyUser = null;
  replyTarget.mention = '';
  replyBody.value = '';
};

const postReply = async () => {
  if (!replyTarget.reviewId) return;
  const body = replyBody.value.trim();
  if (!body) return;
  const content = `${replyTarget.mention}${body}`;
  posting.value = true;
  try {
    const res = await createGuideReviewReply({
      guideReviewId: replyTarget.reviewId,
      replyUser: replyTarget.replyUser ? { id: replyTarget.replyUser.id, name: replyTarget.replyUser.name } : null,
      content
    });
    if (res?.data?.code !== 200) {
      Message.warning(res?.data?.msg || '回复失败');
      return;
    }
    const reply = res.data.data;
    const idx = reviews.value.findIndex(x => String(x.id) === String(replyTarget.reviewId));
    if (idx >= 0) {
      const target = reviews.value[idx];
      const next = { ...target, replies: Array.isArray(target.replies) ? [...target.replies, reply] : [reply] };
      const copy = [...reviews.value];
      copy[idx] = next;
      reviews.value = copy;
    }
    cancelReply();
    Message.success('已发送');
  } finally {
    posting.value = false;
  }
};

const onVoteComment = async ({ id, voteType }) => {
  const res = await toggleGuideReviewVote({ guideReviewId: id, voteType });
  if (res?.data?.code !== 200) {
    Message.warning(res?.data?.msg || '操作失败');
    return;
  }
  const updated = res.data.data;
  const idx = reviews.value.findIndex(x => String(x.id) === String(id));
  if (idx >= 0) {
    const copy = [...reviews.value];
    copy[idx] = { ...copy[idx], ...updated };
    reviews.value = copy;
  }
};

const onVoteReply = async ({ id, voteType }) => {
  const res = await toggleGuideReviewReplyVote({ replyId: id, voteType });
  if (res?.data?.code !== 200) {
    Message.warning(res?.data?.msg || '操作失败');
    return;
  }
  const updated = res.data.data;
  const copy = reviews.value.map(r => {
    const replies = Array.isArray(r.replies) ? r.replies : [];
    const idx = replies.findIndex(x => String(x.id) === String(id));
    if (idx < 0) return r;
    const nextReplies = [...replies];
    nextReplies[idx] = { ...nextReplies[idx], ...updated };
    return { ...r, replies: nextReplies };
  });
  reviews.value = copy;
};

watch(
  () => props.guideId,
  async () => {
    await resetAndLoad();
  }
);

onMounted(async () => {
  await resetAndLoad();
});
</script>

<style scoped>
.section {
  margin-top: 14px;
  background: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 14px;
  padding: 14px;
}

.section-title {
  font-weight: 900;
  color: #0f172a;
  text-align: left;
  margin-bottom: 10px;
}

.panel-list {
  min-height: 120px;
}

.panel-loading {
  color: #64748b;
  text-align: center;
  padding: 12px 0;
}

.panel-end {
  color: #94a3b8;
  text-align: center;
  padding: 12px 0;
}

</style>
