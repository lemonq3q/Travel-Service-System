import request from '@/utils/request';

// 模拟延迟函数
const sleep = (ms) => new Promise(resolve => setTimeout(resolve, ms));

const STORAGE_KEY = 'mock_hotel_management_v1';

const pad2 = (n) => String(n).padStart(2, '0');
const formatDateTime = (d) => {
  const dt = d instanceof Date ? d : new Date(d);
  return `${dt.getFullYear()}-${pad2(dt.getMonth() + 1)}-${pad2(dt.getDate())} ${pad2(dt.getHours())}:${pad2(dt.getMinutes())}:${pad2(dt.getSeconds())}`;
};

const genId = () => Math.floor(Date.now() / 10) + Math.floor(Math.random() * 1000);

const normalizeText = (s) => String(s || '').trim().toLowerCase();

const normalizeAreaFilter = (code) => {
  if (code === null || code === undefined) return null;
  const c = String(code).trim();
  if (!c) return null;
  if (c.length === 12) {
    if (c.endsWith('00000000')) return { mode: 'prefix', key: c.slice(0, 4) };
    if (c.endsWith('000000')) return { mode: 'exact', key: c };
    return { mode: 'exact', key: c };
  }
  if (c.length === 6) {
    return { mode: 'prefix', key: c.slice(0, 2) };
  }
  return { mode: 'prefix', key: c };
};

const ensureInitialHotels = () => {
  const raw = localStorage.getItem(STORAGE_KEY);
  if (raw) {
    try {
      const parsed = JSON.parse(raw);
      if (Array.isArray(parsed)) return parsed;
    } catch {
      // ignore
    }
  }

  const now = new Date();
  const seed = [
    {
      id: 10001,
      name: '示例大酒店',
      address: '北京市朝阳区建国路 88 号',
      areaCode: '110105000000',
      longitude: 116.471,
      latitude: 39.909,
      desc: '位于核心商圈的高端酒店，设施完善，适合商务与家庭出行。',
      featureJson: '["免费WiFi","免费停车","健身房"]',
      facilityJson: '["游泳池","洗衣房","自助早餐"]',
      serviceJson: '["叫醒服务","行李寄存","接机服务"]',
      rating: 4.6,
      checkInTime: '14:00:00',
      checkOutTime: '12:00:00',
      starLevel: 5,
      hotelImgList: [101, 102],
      hotelRoomList: [
        {
          id: 20001,
          name: '豪华大床房',
          desc: '约 35㎡，1.8m 大床，采光好，适合 1-2 人。',
          featureJson: '["高层","安静","落地窗"]',
          tagJson: '["无烟","推荐"]',
          bedCount: 1,
          bedSize: 1.8,
          maxPeople: 2,
          totalRoom: 20,
          roomImgList: [201],
          roomTypePriceList: [
            { id: 30001, summaryJson: '["含双早","不可取消"]', price: 588.0 },
            { id: 30002, summaryJson: '["无早","可免费取消"]', price: 520.0 }
          ]
        }
      ],
      createTime: formatDateTime(new Date(now.getTime() - 2 * 24 * 3600 * 1000)),
      updateTime: formatDateTime(new Date(now.getTime() - 2 * 24 * 3600 * 1000)),
    },
    {
      id: 10002,
      name: '测试精品酒店',
      address: '上海市黄浦区南京东路 1 号',
      areaCode: '310101000000',
      longitude: 121.490,
      latitude: 31.240,
      desc: '设计感十足的精品酒店，地理位置优越，适合城市漫游。',
      featureJson: '["地铁近","拍照出片"]',
      facilityJson: '["咖啡吧","自助入住"]',
      serviceJson: '["旅游咨询","行李寄存"]',
      rating: 4.2,
      checkInTime: '14:00:00',
      checkOutTime: '12:00:00',
      starLevel: 4,
      hotelImgList: [103],
      hotelRoomList: [],
      createTime: formatDateTime(new Date(now.getTime() - 5 * 24 * 3600 * 1000)),
      updateTime: formatDateTime(new Date(now.getTime() - 1 * 24 * 3600 * 1000)),
    },
    {
      id: 10003,
      name: '广州天河商务酒店',
      address: '广州市天河区体育西路 66 号',
      areaCode: '440106000000',
      longitude: 113.327,
      latitude: 23.132,
      desc: '商务出差友好，配套齐全，交通便捷。',
      featureJson: '["会议室","商务中心"]',
      facilityJson: '["打印复印","高速WiFi"]',
      serviceJson: '["发票服务","24小时前台"]',
      rating: 4.0,
      checkInTime: '14:00:00',
      checkOutTime: '12:00:00',
      starLevel: 4,
      hotelImgList: [],
      hotelRoomList: [],
      createTime: formatDateTime(new Date(now.getTime() - 10 * 24 * 3600 * 1000)),
      updateTime: formatDateTime(new Date(now.getTime() - 10 * 24 * 3600 * 1000)),
    }
  ];

  localStorage.setItem(STORAGE_KEY, JSON.stringify(seed));
  return seed;
};

const readHotels = () => ensureInitialHotels();
const writeHotels = (hotels) => localStorage.setItem(STORAGE_KEY, JSON.stringify(hotels));

// --- 模拟接口 (Mock) ---

// export async function getHotelList(params) {
//   console.log('Mock: getHotelList', params);
//   await sleep(500);
//   const pageNum = Number(params?.pageNum || 1);
//   const pageSize = Number(params?.pageSize || 10);
//   const name = normalizeText(params?.name);
//   const starLevel = params?.starLevel === '' || params?.starLevel === null || params?.starLevel === undefined
//     ? undefined
//     : Number(params.starLevel);
//   const areaFilter = normalizeAreaFilter(params?.areaCode);

//   const all = readHotels();
//   const filtered = all.filter(h => {
//     if (name && !normalizeText(h.name).includes(name)) return false;
//     if (Number.isFinite(starLevel) && Number(h.starLevel) !== starLevel) return false;
//     if (areaFilter) {
//       if (areaFilter.mode === 'exact') {
//         if (String(h.areaCode) !== areaFilter.key) return false;
//       } else {
//         if (!String(h.areaCode || '').startsWith(areaFilter.key)) return false;
//       }
//     }
//     return true;
//   });

//   const total = filtered.length;
//   const start = Math.max(0, (pageNum - 1) * pageSize);
//   const records = filtered.slice(start, start + pageSize).map(h => ({
//     id: h.id,
//     name: h.name,
//     address: h.address,
//     areaCode: h.areaCode,
//     rating: h.rating,
//     starLevel: h.starLevel,
//     checkInTime: (h.checkInTime || '').slice(0, 5),
//     checkOutTime: (h.checkOutTime || '').slice(0, 5),
//     createTime: h.createTime,
//   }));
//   return {
//     data: {
//       code: 200,
//       data: {
//         table: records,
//         total
//       }
//     }
//   };
// }

export function getHotelList(params) {
  return request({
    url: '/hotel/list',
    method: 'get',
    params
  });
}

// export async function getHotelDetail(id) {
//   console.log('Mock: getHotelDetail', id);
//   await sleep(300);
//   const hotels = readHotels();
//   const found = hotels.find(h => String(h.id) === String(id)) || null;
//   return {
//     data: {
//       code: 200,
//       data: found
//     }
//   };
// }

export function getHotelDetail(id) {
  return request({
    url: `/hotel/${id}`,
    method: 'get'
  });
}

// export async function addHotel(data) {
//   await sleep(800);
//   const hotels = readHotels();
//   const id = data?.id ?? genId();
//   const now = formatDateTime(new Date());
//   const record = {
//     ...JSON.parse(JSON.stringify(data || {})),
//     id,
//     createTime: data?.createTime || now,
//     updateTime: now,
//   };
//   hotels.unshift(record);
//   writeHotels(hotels);
//   return {
//     data: {
//       code: 200,
//       data: { id },
//       message: '添加成功'
//     }
//   };
// }

export function addHotel(data) {
  return request({
    url: '/hotel',
    method: 'post',
    data
  });
}

// export async function updateHotel(data) {
//   console.log('Mock: updateHotel', data);
//   await sleep(800);
//   const hotels = readHotels();
//   const id = data?.id;
//   const idx = hotels.findIndex(h => String(h.id) === String(id));
//   if (idx >= 0) {
//     const now = formatDateTime(new Date());
//     hotels[idx] = {
//       ...hotels[idx],
//       ...JSON.parse(JSON.stringify(data || {})),
//       id: hotels[idx].id,
//       updateTime: now,
//     };
//     writeHotels(hotels);
//   }
//   return {
//     data: {
//       code: 200,
//       message: '修改成功'
//     }
//   };
// }

export function updateHotel(data) {
  return request({
    url: '/hotel',
    method: 'put',
    data
  });
}

// export async function deleteHotel(id) {
//   // console.log('Mock: deleteHotel', id);
//   await sleep(300);
//   const hotels = readHotels();
//   const next = hotels.filter(h => String(h.id) !== String(id));
//   writeHotels(next);
//   return {
//     data: {
//       code: 200,
//       message: '删除成功'
//     }
//   };
// }

export function deleteHotel(id) {
  return request({
    url: `/hotel/${id}`,
    method: 'delete'
  });
}

// --- 实际接口 (Real) ---
/*

后端建议字段（示例，字段名可按你后端规范调整）

Hotel（酒店）
{
  id: number,
  name: string,
  address: string,
  areaCode: string,        // 省/市/区编码（与 AreaSelect 输出一致，如 110105000000）
  longitude: number,
  latitude: number,
  desc: string,
  featureJson: string,     // JSON 字符串数组：['免费WiFi', ...]
  facilityJson: string,
  serviceJson: string,
  rating: number,
  starLevel: number,
  checkInTime: string,     // HH:mm:ss
  checkOutTime: string,    // HH:mm:ss
  hotelImgList: number[],  // 图片文件 id 列表
  hotelRoomList: HotelRoom[],
  createTime: string,
  updateTime: string
}

HotelRoom（房间）
{
  id: number,
  name: string,
  desc: string,
  featureJson: string,
  tagJson: string,
  bedCount: number,
  bedSize: number,
  maxPeople: number,
  totalRoom: number,
  roomImgList: number[],
  roomTypePriceList: RoomTypePrice[]
}

RoomTypePrice（房型套餐）
{
  id: number,
  summaryJson: string,     // JSON 字符串数组：['含双早', ...]
  price: number
}

1) 酒店分页查询
GET /hotel/list
Query（发送给服务器的数据格式）
{
  pageNum: number,
  pageSize: number,
  name?: string,
  starLevel?: number,
  areaCode?: string
}
Resp（需要的数据格式）
{
  code: 200,
  data: {
    records: Array<{
      id: number,
      name: string,
      address: string,
      areaCode: string,
      rating: number,
      starLevel: number,
      checkInTime: string,
      checkOutTime: string,
      createTime: string
    }>,
    total: number
  }
}

2) 酒店详情
GET /hotel/{id}
Resp（需要的数据格式）
{ code: 200, data: Hotel }

3) 新增酒店
POST /hotel
Body（发送给服务器的数据格式）: Hotel（可不带 id/createTime/updateTime，后端生成）
Resp（需要的数据格式）
{ code: 200, data: { id: number }, message?: string }

4) 修改酒店
PUT /hotel
Body（发送给服务器的数据格式）: Hotel（必须包含 id）
Resp（需要的数据格式）
{ code: 200, message?: string }

5) 删除酒店
DELETE /hotel/{id}
Resp（需要的数据格式）
{ code: 200, message?: string }

export function getHotelList(params) {
  return request({
    url: '/hotel/list',
    method: 'get',
    params
  });
}

export function getHotelDetail(id) {
  return request({
    url: `/hotel/${id}`,
    method: 'get'
  });
}

export function addHotel(data) {
  return request({
    url: '/hotel',
    method: 'post',
    data
  });
}

export function updateHotel(data) {
  return request({
    url: '/hotel',
    method: 'put',
    data
  });
}

export function deleteHotel(id) {
  return request({
    url: `/hotel/${id}`,
    method: 'delete'
  });
}
*/
