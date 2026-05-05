import request from '@/utils/request';

const sleep = (ms) => new Promise(resolve => setTimeout(resolve, ms));

const SCENIC_IMAGE_PROMPT = encodeURIComponent('beautiful scenic spot landscape, natural light, realistic travel photography, wide angle, ultra detailed');
const REVIEW_IMAGE_PROMPT = encodeURIComponent('travel photo of scenic spot, realistic photography, ultra detailed');

const buildImageUrl = (prompt, seed, imageSize) => {
  return `https://coresg-normal.trae.ai/api/ide/v1/text_to_image?prompt=${prompt}%20seed%20${encodeURIComponent(String(seed))}&image_size=${imageSize}`;
};

const DEFAULT_AVATAR_URL = '/src/assets/user.png';

const MOCK_SCENICS = [
  {
    id: 1001,
    name: '西湖风景名胜区',
    address: '浙江省杭州市西湖区龙井路1号',
    areaCode: '330106',
    longitude: 120.1551,
    latitude: 30.2741,
    desc: '西湖以其“山水相依、城湖相融”的独特格局闻名。四季皆景，晨昏各异，是城市里最浪漫的自然风景。',
    openTimeDesc: '全天开放（部分景点有独立开放时间）',
    rating: 4.7,
    hotValue: 4.9,
    needTicket: 0,
    price: 0,
    images: [
      buildImageUrl(SCENIC_IMAGE_PROMPT, 'scenic-1001-1', 'landscape_16_9'),
      buildImageUrl(SCENIC_IMAGE_PROMPT, 'scenic-1001-2', 'landscape_16_9'),
      buildImageUrl(SCENIC_IMAGE_PROMPT, 'scenic-1001-3', 'landscape_16_9'),
      buildImageUrl(SCENIC_IMAGE_PROMPT, 'scenic-1001-4', 'landscape_16_9'),
      buildImageUrl(SCENIC_IMAGE_PROMPT, 'scenic-1001-5', 'landscape_16_9'),
      buildImageUrl(SCENIC_IMAGE_PROMPT, 'scenic-1001-6', 'landscape_16_9'),
      buildImageUrl(SCENIC_IMAGE_PROMPT, 'scenic-1001-7', 'landscape_16_9')
    ]
  },
  {
    id: 1002,
    name: '故宫博物院',
    address: '北京市东城区景山前街4号',
    areaCode: '110101',
    longitude: 116.397026,
    latitude: 39.918058,
    desc: '世界文化遗产，明清两代皇家宫殿。中轴对称的宫殿群宏伟壮丽，是了解中华传统建筑与历史文化的必到之处。',
    openTimeDesc: '周二至周日 08:30-17:00（周一闭馆，法定节假日除外）',
    rating: 4.8,
    hotValue: 4.8,
    needTicket: 1,
    price: 60,
    images: [
      buildImageUrl(SCENIC_IMAGE_PROMPT, 'scenic-1002-1', 'landscape_16_9'),
      buildImageUrl(SCENIC_IMAGE_PROMPT, 'scenic-1002-2', 'landscape_16_9'),
      buildImageUrl(SCENIC_IMAGE_PROMPT, 'scenic-1002-3', 'landscape_16_9'),
      buildImageUrl(SCENIC_IMAGE_PROMPT, 'scenic-1002-4', 'landscape_16_9'),
      buildImageUrl(SCENIC_IMAGE_PROMPT, 'scenic-1002-5', 'landscape_16_9')
    ]
  },
  {
    id: 1003,
    name: '黄山风景区',
    address: '安徽省黄山市黄山区汤口镇',
    areaCode: '341003',
    longitude: 118.1706,
    latitude: 30.1327,
    desc: '以奇松、怪石、云海、温泉“四绝”著称。登山途中一步一景，云雾缥缈时尤为震撼。',
    openTimeDesc: '06:30-17:30',
    rating: 4.6,
    hotValue: 4.6,
    needTicket: 1,
    price: 190,
    images: [
      buildImageUrl(SCENIC_IMAGE_PROMPT, 'scenic-1003-1', 'landscape_16_9'),
      buildImageUrl(SCENIC_IMAGE_PROMPT, 'scenic-1003-2', 'landscape_16_9'),
      buildImageUrl(SCENIC_IMAGE_PROMPT, 'scenic-1003-3', 'landscape_16_9'),
      buildImageUrl(SCENIC_IMAGE_PROMPT, 'scenic-1003-4', 'landscape_16_9')
    ]
  },
  {
    id: 1004,
    name: '张家界国家森林公园',
    address: '湖南省张家界市武陵源区金鞭路',
    areaCode: '430811',
    longitude: 110.4792,
    latitude: 29.3443,
    desc: '峰林奇秀、峡谷幽深。电影取景地的立体景观令人过目难忘，适合徒步与观景。',
    openTimeDesc: '07:00-18:00',
    rating: 4.5,
    hotValue: 4.7,
    needTicket: 1,
    price: 225,
    images: [
      buildImageUrl(SCENIC_IMAGE_PROMPT, 'scenic-1004-1', 'landscape_16_9'),
      buildImageUrl(SCENIC_IMAGE_PROMPT, 'scenic-1004-2', 'landscape_16_9'),
      buildImageUrl(SCENIC_IMAGE_PROMPT, 'scenic-1004-3', 'landscape_16_9')
    ]
  },
  {
    id: 1005,
    name: '丽江古城',
    address: '云南省丽江市古城区大研街道',
    areaCode: '530702',
    longitude: 100.2330,
    latitude: 26.8721,
    desc: '世界文化遗产古城，石板路、水系与纳西文化交织。夜色下的灯火与小巷更显氛围。',
    openTimeDesc: '全天开放',
    rating: 4.3,
    hotValue: 4.4,
    needTicket: 0,
    price: 0,
    images: [
      buildImageUrl(SCENIC_IMAGE_PROMPT, 'scenic-1005-1', 'landscape_16_9'),
      buildImageUrl(SCENIC_IMAGE_PROMPT, 'scenic-1005-2', 'landscape_16_9'),
      buildImageUrl(SCENIC_IMAGE_PROMPT, 'scenic-1005-3', 'landscape_16_9')
    ]
  },
  {
    id: 1006,
    name: '鼓浪屿',
    address: '福建省厦门市思明区鼓浪屿',
    areaCode: '350203',
    longitude: 118.0713,
    latitude: 24.4459,
    desc: '海岛步行慢生活，万国建筑与音乐文化闻名。适合拍照、喝咖啡、看日落。',
    openTimeDesc: '全天开放（轮渡时间以实际为准）',
    rating: 4.4,
    hotValue: 4.5,
    needTicket: 0,
    price: 0,
    images: [
      buildImageUrl(SCENIC_IMAGE_PROMPT, 'scenic-1006-1', 'landscape_16_9'),
      buildImageUrl(SCENIC_IMAGE_PROMPT, 'scenic-1006-2', 'landscape_16_9'),
      buildImageUrl(SCENIC_IMAGE_PROMPT, 'scenic-1006-3', 'landscape_16_9')
    ]
  }
];

const MOCK_REVIEWS = [
  {
    id: 'sr-1',
    scenicId: 1001,
    user: { id: 1, name: '小雨', avatarUrl: DEFAULT_AVATAR_URL },
    rating: 5.0,
    content: '雨后的湖面特别温柔，空气也很清新。傍晚沿湖走一圈非常舒服。',
    images: [
      buildImageUrl(REVIEW_IMAGE_PROMPT, 'scenic-review-1-1', 'square'),
      buildImageUrl(REVIEW_IMAGE_PROMPT, 'scenic-review-1-2', 'square')
    ],
    createTime: '2026-04-16T09:20:00.000Z'
  },
  {
    id: 'sr-2',
    scenicId: 1001,
    user: { id: 2, name: '旅行家阿峰', avatarUrl: DEFAULT_AVATAR_URL },
    rating: 4.6,
    content: '游客不少，但景色确实漂亮。建议早点出门，避开高峰。',
    images: [],
    createTime: '2026-04-12T15:05:00.000Z'
  },
  {
    id: 'sr-3',
    scenicId: 1002,
    user: { id: 3, name: '小北', avatarUrl: DEFAULT_AVATAR_URL },
    rating: 4.9,
    content: '第一次来故宫震撼到，建筑细节太美了。记得提前预约门票。',
    images: [buildImageUrl(REVIEW_IMAGE_PROMPT, 'scenic-review-2-1', 'square')],
    createTime: '2026-04-18T11:10:00.000Z'
  }
];

const mapScenicToListItem = (s) => {
  return {
    id: s.id,
    name: s.name,
    address: s.address,
    areaCode: s.areaCode,
    longitude: s.longitude,
    latitude: s.latitude,
    rating: Number(s.rating) || 0,
    hotValue: Number(s.hotValue) || 0,
    needTicket: Number(s.needTicket) || 0,
    price: Number(s.price) || 0,
    coverImageUrl: Array.isArray(s.images) && s.images.length ? s.images[0] : '',
    images: Array.isArray(s.images) ? s.images : []
  };
};

// ------------------------------
// 真实接口
// ------------------------------
export function searchScenics({ cursor, limit = 9, filters = {} } = {}) {
  return request({
    url: '/scenic/list/public',
    method: 'get',
    params: {
      cursor,
      limit,
      cityCode: filters?.cityCode || '',
      needTicket: filters?.needTicket ?? null,
      ratingMin: filters?.ratingMin ?? null
    }
  });
}

export function listHotScenics({ limit = 6 } = {}) {
  return request({
    url: '/scenic/list/hot',
    method: 'get',
    params: { limit }
  });
}

export function getScenicDetail(scenicId) {
  return request({
    url: `/scenic/${scenicId}/public`,
    method: 'get'
  });
}

export function listScenicReviews({ scenicId, cursor, limit = 10 } = {}) {
  return request({
    url: `/comment/scenic/${scenicId}`,
    method: 'get',
    params: { cursor, limit }
  });
}

// ------------------------------
// 真实接口（联调阶段：取消注释并对齐后端格式）
// ------------------------------
/*
后端建议返回格式（示例）

1) 景区列表查询
GET /api/scenics
Query: { area_code?, need_ticket?, rating_min?, page?, page_size? }

Resp:
{
  code: 200,
  data: {
    items: [
      {
        id: 1,
        name: '...',
        address: '...',
        area_code: '110101',
        longitude: 116.397428,
        latitude: 39.90923,
        rating: 4.6,
        hot_value: 4.8,
        need_ticket: 1,
        price: 80.0,
        images: ['https://...']
      }
    ],
    nextCursor: null
  }
}

2) 热门景区
GET /api/scenics/hot
Resp: { code: 200, data: { items: ScenicListItem[] } }

3) 景区详情
GET /api/scenics/{id}
Resp:
{
  code: 200,
  data: {
    id,
    name,
    address,
    area_code,
    longitude,
    latitude,
    desc,
    open_time_desc,
    rating,
    hot_value,
    need_ticket,
    price,
    images: ['https://...']
  }
}

4) 景区评论
GET /api/scenics/{id}/reviews
Query: { page?, page_size?, sort? }
Resp:
{
  code: 200,
  data: {
    items: [
      {
        id: 1,
        as_scenic: 1,
        as_user: 10,
        rating: 4.5,
        content: '...',
        create_time: '2026-04-21T10:00:00.000Z',
        user: { id: 10, name: '昵称' },
        images: ['https://...']
      }
    ],
    nextCursor: null
  }
}

export function searchScenics({ cursor, limit = 9, filters = {} } = {}) {
  return request({
    url: '/scenic/list/public',
    method: 'get',
    params: { cursor, limit, ...filters }
  });
}

export function listHotScenics({ limit = 6 } = {}) {
  return request({
    url: '/scenic/list/hot',
    method: 'get',
    params: { limit }
  });
}

export function getScenicDetail(scenicId) {
  return request({
    url: `/scenic/${scenicId}/public`,
    method: 'get'
  });
}

export function listScenicReviews({ scenicId, cursor, limit = 10, sort = 'latest' } = {}) {
  return request({
    url: `/comment/${scenicId}`,
    method: 'get',
    params: { cursor, limit, sort }
  });
}
*/
