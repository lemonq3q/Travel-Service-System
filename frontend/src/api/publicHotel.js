import request from '@/utils/request';

const sleep = (ms) => new Promise(resolve => setTimeout(resolve, ms));
const HOTEL_IMAGE_PROMPT = encodeURIComponent('modern luxury hotel exterior, warm lighting, realistic photography, wide angle, ultra detailed');
const ROOM_IMAGE_PROMPT = encodeURIComponent('cozy modern hotel room interior, natural light, realistic photography, wide angle, ultra detailed');
const REVIEW_IMAGE_PROMPT = encodeURIComponent('travel photo, city landmark nearby hotel, realistic photography, ultra detailed');

const buildImageUrl = (prompt, seed, imageSize) => {
  return `https://coresg-normal.trae.ai/api/ide/v1/text_to_image?prompt=${prompt}%20seed%20${encodeURIComponent(String(seed))}&image_size=${imageSize}`;
};

const formatMoney = (amount) => ({ currency: 'CNY', amount: Number(amount) });

// ------------------------------
// 固定 Mock 数据（已补全 cheapest 字段）
// ------------------------------
const MOCK_HOTELS = [
  {
    id: 'h-1',
    name: '北京豪华大酒店',
    address: '北京市朝阳区建国门外大街1号',
    areaCode: '110100',
    longitude: 116.4074,
    latitude: 39.9042,
    starLevel: 5,
    rating: 4.8,
    reviewCount: 1286,
    desc: '位于北京核心商圈，五星级豪华酒店，设施一流，服务贴心。',
    featureJson: JSON.stringify(['免费停车', '24小时前台', '健身房', '游泳池']),
    facilityJson: JSON.stringify(['中央空调', '智能门锁', '迷你吧']),
    serviceJson: JSON.stringify(['叫醒服务', '行李寄存', '送餐服务']),
    images: [
      buildImageUrl(HOTEL_IMAGE_PROMPT, 'hotel-1-1', 'landscape_16_9'),
      buildImageUrl(HOTEL_IMAGE_PROMPT, 'hotel-1-2', 'landscape_16_9'),
    ],
    // 必须保留，前端要读取 roomName
    cheapest: {
      roomName: '高级大床房',
      maxPeople: 2,
      price: 588
    }
  },
  {
    id: 'h-2',
    name: '上海外滩精品酒店',
    address: '上海市黄浦区外滩南京路88号',
    areaCode: '310100',
    longitude: 121.4737,
    latitude: 31.2304,
    starLevel: 4,
    rating: 4.5,
    reviewCount: 856,
    desc: '外滩江景精品酒店，现代简约风格，交通便利。',
    featureJson: JSON.stringify(['江景房', '免费早餐', '洗衣服务']),
    facilityJson: JSON.stringify(['高清电视', '高速WiFi', '独立卫浴']),
    serviceJson: JSON.stringify(['旅游咨询', '票务服务', '接送机']),
    images: [
      buildImageUrl(HOTEL_IMAGE_PROMPT, 'hotel-2-1', 'landscape_16_9'),
      buildImageUrl(HOTEL_IMAGE_PROMPT, 'hotel-2-2', 'landscape_16_9'),
    ],
    cheapest: {
      roomName: '豪华双床房',
      maxPeople: 2,
      price: 628
    }
  },
  {
    id: 'h-3',
    name: '广州天河商务酒店',
    address: '广州市天河区天河路385号',
    areaCode: '440100',
    longitude: 113.2644,
    latitude: 23.1291,
    starLevel: 4,
    rating: 4.2,
    reviewCount: 623,
    desc: '天河CBD核心地段，适合商务出差，配套完善。',
    featureJson: JSON.stringify(['商务中心', '会议室', '免费咖啡']),
    facilityJson: JSON.stringify(['办公桌椅', '保险箱', '吹风机']),
    serviceJson: JSON.stringify(['打印复印', '快递服务', '24小时热水']),
    images: [
      buildImageUrl(HOTEL_IMAGE_PROMPT, 'hotel-3-1', 'landscape_16_9'),
      buildImageUrl(HOTEL_IMAGE_PROMPT, 'hotel-3-2', 'landscape_16_9'),
    ],
    cheapest: {
      roomName: '高级大床房',
      maxPeople: 2,
      price: 468
    }
  },
  {
    id: 'h-4',
    name: '深圳前海度假酒店',
    address: '深圳市南山区前海路100号',
    areaCode: '440300',
    longitude: 114.0579,
    latitude: 22.5431,
    starLevel: 5,
    rating: 4.7,
    reviewCount: 945,
    desc: '前海度假区海景酒店，休闲度假首选，环境优美。',
    featureJson: JSON.stringify(['海景阳台', '温泉泡池', '私人沙滩']),
    facilityJson: JSON.stringify(['智能马桶', '冰箱', '音响系统']),
    serviceJson: JSON.stringify(['SPA服务', '私人管家', '儿童乐园']),
    images: [
      buildImageUrl(HOTEL_IMAGE_PROMPT, 'hotel-4-1', 'landscape_16_9'),
      buildImageUrl(HOTEL_IMAGE_PROMPT, 'hotel-4-2', 'landscape_16_9'),
    ],
    cheapest: {
      roomName: '海景大床房',
      maxPeople: 2,
      price: 788
    }
  },
];

const MOCK_HOTEL_BUNDLE = {
  hotel: MOCK_HOTELS[0],
  rooms: [
    {
      id: 'r-1-1',
      hotelId: 'h-1',
      name: '高级大床房',
      maxPeople: 2,
      desc: '35㎡，1.8米大床，城市景观，独立卫浴。',
      featureJson: JSON.stringify(['安静', '采光好', '落地窗']),
      images: [
        buildImageUrl(ROOM_IMAGE_PROMPT, 'room-1-1a', 'landscape_16_9'),
        buildImageUrl(ROOM_IMAGE_PROMPT, 'room-1-1b', 'landscape_16_9'),
      ],
      roomTypePriceList: [
        { id: 'p-1-1-1', summaryJson: JSON.stringify(['不含早', '不可取消']), price: formatMoney(588) },
        { id: 'p-1-1-2', summaryJson: JSON.stringify(['含双早', '免费取消']), price: formatMoney(668) },
      ],
    },
    {
      id: 'r-1-2',
      hotelId: 'h-1',
      name: '豪华双床房',
      maxPeople: 2,
      desc: '40㎡，两张1.2米单人床，空间宽敞。',
      featureJson: JSON.stringify(['隔音好', '书桌', '沙发']),
      images: [
        buildImageUrl(ROOM_IMAGE_PROMPT, 'room-1-2a', 'landscape_16_9'),
        buildImageUrl(ROOM_IMAGE_PROMPT, 'room-1-2b', 'landscape_16_9'),
      ],
      roomTypePriceList: [
        { id: 'p-1-2-1', summaryJson: JSON.stringify(['不含早', '限时优惠']), price: formatMoney(628) },
        { id: 'p-1-2-2', summaryJson: JSON.stringify(['含双早', '延迟退房']), price: formatMoney(698) },
      ],
    },
  ],
};

const MOCK_REVIEWS = [
  {
    id: 'rev-h-1-1',
    hotelId: 'h-1',
    user: { id: 'u-1', name: '用户001', avatarUrl: '/src/assets/user.png' },
    room: { id: 'r-1-1', name: '高级大床房' },
    rating: 5.0,
    content: '位置绝佳，就在市中心，出行超方便。房间干净整洁，床很舒服，前台服务热情，强烈推荐！',
    images: [
      buildImageUrl(REVIEW_IMAGE_PROMPT, 'review-1-1', 'square'),
      buildImageUrl(REVIEW_IMAGE_PROMPT, 'review-1-2', 'square'),
    ],
    createTime: '2026-04-15T08:30:00.000Z',
  },
  {
    id: 'rev-h-1-2',
    hotelId: 'h-1',
    user: { id: 'u-2', name: '用户002', avatarUrl: '/src/assets/user.png' },
    room: { id: 'r-1-2', name: '豪华双床房' },
    rating: 4.5,
    content: '整体非常满意，房间很大，设施很新。早餐种类丰富，味道不错。唯一缺点是高峰电梯有点慢。',
    images: [],
    createTime: '2026-04-10T14:15:00.000Z',
  },
];

// ------------------------------
// Mock 接口
// ------------------------------
// export async function searchHotels({ cursor, limit = 10, sort = 'recommended', filters = {} } = {}) {
//   await sleep(300);
//   return {
//     data: {
//       code: 200,
//       data: {
//         items: MOCK_HOTELS.map(h => ({
//           id: h.id,
//           name: h.name,
//           address: h.address,
//           cityCode: h.areaCode,
//           coverImageUrl: h.images[0],
//           rating: h.rating,
//           reviewCount: h.reviewCount,
//           starLevel: h.starLevel,
//           cheapest: {
//             roomName: h.cheapest.roomName,
//             maxPeople: h.cheapest.maxPeople,
//             price: formatMoney(h.cheapest.price)
//           }
//         })),
//         nextCursor: null,
//       },
//     },
//   };
// }
export function searchHotels({ cursor, limit = 10, sort = 'recommended', filters = {} } = {}) {
  return request({
    url: '/hotel/public/list',
    method: 'get',
    params: { cursor, limit, sort, filters: JSON.stringify(filters) }
  });
}
export function getHotelBundle(hotelId) {
  return request({
    url: `/hotel/public/${hotelId}`,
    method: 'get'
  });
}


export function listHotelReviews({ hotelId, cursor, limit = 10 } = {}) {
  return request({
    url: `/comment/hotel/${hotelId}`,
    method: 'get',
    params: { cursor, limit }
  });
}

// --- 真实接口 ---
/*
export function searchHotels({ cursor, limit = 10, sort = 'recommended', filters = {} } = {}) {
  return request({
    url: '/hotel/public/list',
    method: 'get',
    params: { cursor, limit, sort, filters: JSON.stringify(filters) }
  });
}

export function getHotelBundle(hotelId) {
  return request({
    url: `/hotel/public/${hotelId}`,
    method: 'get'
  });
}

export function listHotelReviews({ hotelId, cursor, limit = 10 } = {}) {
  return request({
    url: `/comment/hotel/${hotelId}`,
    method: 'get',
    params: { cursor, limit }
  });
}
*/
