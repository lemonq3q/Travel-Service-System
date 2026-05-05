import request from '@/utils/request';

const GUIDE_IMG_PLACEHOLDER_PREFIX = '__GUIDE_IMG__';

const buildGuideImgPlaceholder = (fileId) => `${GUIDE_IMG_PLACEHOLDER_PREFIX}${String(fileId)}__`;

const replaceAll = (text, search, replacement) => {
  if (!search) return text;
  return String(text || '').split(String(search)).join(String(replacement));
};

const applyGuideImageUrls = (content, images) => {
  let next = String(content || '');
  const list = Array.isArray(images) ? images : [];
  for (const img of list) {
    if (!img?.fileId || !img?.url) continue;
    next = replaceAll(next, buildGuideImgPlaceholder(img.fileId), img.url);
  }
  return next;
};

const replaceGuideImageUrlsWithPlaceholders = (content, images) => {
  let next = String(content || '');
  const list = Array.isArray(images) ? images : [];
  for (const img of list) {
    if (!img?.id || !img?.url) continue;
    next = replaceAll(next, img.url, buildGuideImgPlaceholder(img.id));
  }
  return next;
};

const extractUsedImages = (content, images) => {
  const text = String(content || '');
  const list = Array.isArray(images) ? images : [];
  const hits = [];

  for (const img of list) {
    if (!img?.id) continue;
    const placeholder = buildGuideImgPlaceholder(img.id);
    const posUrl = img.url ? text.indexOf(img.url) : -1;
    const posPlaceholder = text.indexOf(placeholder);
    const pos = posUrl >= 0 ? posUrl : posPlaceholder;
    if (pos >= 0) hits.push({ img, pos });
  }

  hits.sort((a, b) => a.pos - b.pos);
  const out = [];
  const seen = new Set();
  for (const h of hits) {
    const id = h.img.id;
    if (seen.has(id)) continue;
    seen.add(id);
    out.push(h.img);
  }
  return out;
};

export function searchGuides({ keyword = '', cursor, limit = 9 } = {}) {
  return request({
    url: '/guide/list/public',
    method: 'get',
    params: { keyword, cursor, limit }
  });
}

export async function getGuideDetail(guideId) {
  const res = await request({
    url: `/guide/${guideId}/public`,
    method: 'get'
  });
  const data = res?.data?.data;
  if (res?.data?.code === 200 && data) {
    const images = Array.isArray(data.images) ? data.images : [];
    data.content = applyGuideImageUrls(data.content, images);
  }
  return res;
}

export function listGuideReviews({ guideId, cursor, limit = 10 } = {}) {
  return request({
    url: `/comment/guide/${guideId}`,
    method: 'get',
    params: { cursor, limit }
  });
}

export function createGuideReview({ guideId, content }) {
  return request({
    url: `/comment/guide/${guideId}`,
    method: 'post',
    data: { content }
  });
}

export function createGuideReviewReply({ guideReviewId, replyUser, content }) {
  return request({
    url: `/comment/guide/review/${guideReviewId}/reply`,
    method: 'post',
    data: { replyUser, content }
  });
}

export function toggleGuideReviewVote({ guideReviewId, voteType }) {
  return request({
    url: `/comment/guide/review/${guideReviewId}/vote`,
    method: 'post',
    data: { voteType }
  });
}

export function toggleGuideReviewReplyVote({ replyId, voteType }) {
  return request({
    url: `/comment/guide/reply/${replyId}/vote`,
    method: 'post',
    data: { voteType }
  });
}

export function listMyGuides({ cursor, limit = 10 } = {}) {
  return request({
    url: '/guide/my/list',
    method: 'get',
    params: { cursor, limit }
  });
}

export async function getMyGuideDetail(guideId) {
  const res = await request({
    url: `/guide/my/${guideId}`,
    method: 'get'
  });
  const data = res?.data?.data;
  if (res?.data?.code === 200 && data) {
    const images = Array.isArray(data.images) ? data.images : [];
    data.content = applyGuideImageUrls(data.content, images);
  }
  return res;
}

export async function createGuide({ title, content, images = [], imageFileIds = [] } = {}) {
  const used = extractUsedImages(content, images);
  const normalizedImages = used.map((img, idx) => ({
    fileId: img.id,
    sortIndex: idx,
    url: buildGuideImgPlaceholder(img.id)
  }));
  const normalizedContent = replaceGuideImageUrlsWithPlaceholders(content, used);
  return request({
    url: '/guide/my',
    method: 'post',
    data: {
      title,
      content: normalizedContent,
      images: normalizedImages,
      imageFileIds
    }
  });
}

export async function updateMyGuide({ guideId, title, content, images = [], imageFileIds = [] } = {}) {
  const used = extractUsedImages(content, images);
  const normalizedImages = used.map((img, idx) => ({
    fileId: img.id,
    sortIndex: idx,
    url: buildGuideImgPlaceholder(img.id)
  }));
  const normalizedContent = replaceGuideImageUrlsWithPlaceholders(content, used);
  return request({
    url: `/guide/my/${guideId}`,
    method: 'put',
    data: {
      title,
      content: normalizedContent,
      images: normalizedImages,
      imageFileIds
    }
  });
}

export function deleteMyGuide(guideId) {
  return request({
    url: `/guide/my/${guideId}`,
    method: 'delete'
  });
}

