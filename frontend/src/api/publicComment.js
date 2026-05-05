import request from '@/utils/request';

export function createHotelReview({ hotelId, roomId, content, rating, imageFileIds } = {}) {
  return request({
    url: `/comment/hotel/${hotelId}`,
    method: 'post',
    data: {
      roomId,
      content,
      rating,
      imageFileIds: Array.isArray(imageFileIds) ? imageFileIds : []
    }
  });
}

export function createScenicReview({ scenicId, content, rating, imageFileIds } = {}) {
  return request({
    url: `/comment/scenic/${scenicId}`,
    method: 'post',
    data: {
      content,
      rating,
      imageFileIds: Array.isArray(imageFileIds) ? imageFileIds : []
    }
  });
}
