import request from '@/utils/request';

const USE_MOCK_SEARCH = true;
const USE_MOCK_ORDER = false;

const nowIso = () => new Date().toISOString();

const hashString = (str) => {
  const s = String(str || '');
  let h = 2166136261;
  for (let i = 0; i < s.length; i += 1) {
    h ^= s.charCodeAt(i);
    h = Math.imul(h, 16777619);
  }
  return h >>> 0;
};

const mulberry32 = (a) => {
  return function () {
    let t = (a += 0x6d2b79f5);
    t = Math.imul(t ^ (t >>> 15), t | 1);
    t ^= t + Math.imul(t ^ (t >>> 7), t | 61);
    return ((t ^ (t >>> 14)) >>> 0) / 4294967296;
  };
};

const pad2 = (n) => String(n).padStart(2, '0');

const hhmmToMinutes = (hhmm) => {
  const s = String(hhmm || '');
  const m = s.match(/^(\d{1,2}):(\d{2})$/);
  if (!m) return null;
  const h = Number(m[1]);
  const mm = Number(m[2]);
  if (!Number.isFinite(h) || !Number.isFinite(mm)) return null;
  if (h < 0 || h > 23 || mm < 0 || mm > 59) return null;
  return h * 60 + mm;
};

const minutesToHhmm = (minutes) => {
  const m = Number(minutes);
  if (!Number.isFinite(m) || m < 0) return '00:00';
  const h = Math.floor((m % 1440) / 60);
  const mm = Math.floor(m % 60);
  return `${pad2(h)}:${pad2(mm)}`;
};

const formatDuration = (startHhmm, endHhmm) => {
  const a = hhmmToMinutes(startHhmm);
  const b = hhmmToMinutes(endHhmm);
  if (a === null || b === null) return '';
  let diff = b - a;
  if (diff < 0) diff += 1440;
  const h = Math.floor(diff / 60);
  const m = diff % 60;
  if (h <= 0) return `${m}分钟`;
  if (m <= 0) return `${h}小时`;
  return `${h}小时${m}分钟`;
};

const loadMockOrders = () => {
  try {
    const raw = localStorage.getItem('mock_ticket_orders_v1');
    const arr = raw ? JSON.parse(raw) : [];
    return Array.isArray(arr) ? arr : [];
  } catch {
    return [];
  }
};

const saveMockOrders = (orders) => {
  try {
    localStorage.setItem('mock_ticket_orders_v1', JSON.stringify(Array.isArray(orders) ? orders : []));
  } catch {
    return;
  }
};

const nextOrderId = () => {
  const base = Date.now();
  const extra = Math.floor(Math.random() * 1000);
  return Number(String(base).slice(-10)) * 1000 + extra;
};

const mockTrainList = ({ fromAreaCode, toAreaCode, fromCityName, toCityName, timeStart, timeEnd } = {}) => {
  const seed = hashString(['train', fromAreaCode, toAreaCode].join('|'));
  const rnd = mulberry32(seed);
  const baseStart = 6 * 60;
  const baseEnd = 21 * 60;
  const trains = [];
  const cnt = 6;
  const startMin = hhmmToMinutes(timeStart);
  const endMin = hhmmToMinutes(timeEnd);
  const inRange = (m) => {
    if (startMin === null || endMin === null) return true;
    if (startMin <= endMin) return m >= startMin && m <= endMin;
    return m >= startMin || m <= endMin;
  };

  for (let i = 0; i < cnt; i += 1) {
    const depart = Math.floor(baseStart + (baseEnd - baseStart) * (i / (cnt - 1)) + rnd() * 14);
    if (!inRange(depart)) continue;
    const dur = 120 + Math.floor(rnd() * 220);
    const arrive = depart + dur;
    const id = 100000 + ((seed % 9000) + 1) * 10 + i;
    const trainCodePrefix = rnd() > 0.65 ? 'G' : rnd() > 0.35 ? 'D' : 'K';
    const trainNo = 100 + Math.floor(rnd() * 800);
    const train_code = `${trainCodePrefix}${trainNo}`;
    const start_time = minutesToHhmm(depart);
    const end_time = minutesToHhmm(arrive);
    const tickets = [];
    const basePrice = 120 + Math.floor(rnd() * 520);
    const remainBase = 5 + Math.floor(rnd() * 80);
    const seatDefs = [
      { name: '二等座', mult: 1.0 },
      { name: '一等座', mult: 1.35 },
      { name: '商务座', mult: 2.25 }
    ];
    seatDefs.forEach((s, idx) => {
      const ticketId = id * 10 + idx;
      const price = Math.max(1, Math.round(basePrice * s.mult));
      const num = Math.max(0, Math.floor(remainBase * (1 - idx * 0.18)));
      tickets.push({
        id: ticketId,
        as_train: id,
        type_name: s.name,
        price: Number(price.toFixed(2)),
        num
      });
    });

    trains.push({
      id,
      train_code,
      start_area_code: String(fromAreaCode || ''),
      end_area_code: String(toAreaCode || ''),
      start_time,
      end_time,
      start_station_name: `${fromCityName || '出发地'}站`,
      end_station_name: `${toCityName || '目的地'}站`,
      duration_text: formatDuration(start_time, end_time),
      tickets
    });
  }

  return trains;
};

const mockAircraftList = ({ fromAreaCode, toAreaCode, fromCityName, toCityName, timeStart, timeEnd } = {}) => {
  const seed = hashString(['aircraft', fromAreaCode, toAreaCode].join('|'));
  const rnd = mulberry32(seed);
  const baseStart = 7 * 60;
  const baseEnd = 22 * 60;
  const flights = [];
  const cnt = 5;
  const startMin = hhmmToMinutes(timeStart);
  const endMin = hhmmToMinutes(timeEnd);
  const inRange = (m) => {
    if (startMin === null || endMin === null) return true;
    if (startMin <= endMin) return m >= startMin && m <= endMin;
    return m >= startMin || m <= endMin;
  };
  const companies = [
    { name: '中国国航', code: 'CA' },
    { name: '东方航空', code: 'MU' },
    { name: '南方航空', code: 'CZ' },
    { name: '海南航空', code: 'HU' }
  ];
  const types = ['A320', 'B737', 'A321', 'A330'];

  for (let i = 0; i < cnt; i += 1) {
    const depart = Math.floor(baseStart + (baseEnd - baseStart) * (i / (cnt - 1)) + rnd() * 18);
    if (!inRange(depart)) continue;
    const dur = 75 + Math.floor(rnd() * 165);
    const arrive = depart + dur;
    const id = 200000 + ((seed % 9000) + 1) * 10 + i;
    const c = companies[Math.floor(rnd() * companies.length)];
    const aircraft_code = `${c.code}${1000 + Math.floor(rnd() * 8000)}`;
    const aircraft_type = types[Math.floor(rnd() * types.length)];
    const start_time = minutesToHhmm(depart);
    const end_time = minutesToHhmm(arrive);
    const economyPrice = 260 + Math.floor(rnd() * 900);
    const businessPrice = Math.round(economyPrice * (1.85 + rnd() * 0.35));
    const economyNum = 10 + Math.floor(rnd() * 120);
    const businessNum = 0 + Math.floor(rnd() * 18);

    flights.push({
      id,
      aircraft_code,
      aircraft_type,
      as_company: c.name,
      start_area_code: String(fromAreaCode || ''),
      end_area_code: String(toAreaCode || ''),
      start_time,
      end_time,
      start_station_name: `${fromCityName || '出发地'}机场`,
      end_station_name: `${toCityName || '目的地'}机场`,
      duration_text: formatDuration(start_time, end_time),
      economy_class_price: Number(economyPrice.toFixed(2)),
      economy_class_num: economyNum,
      business_class_price: Number(businessPrice.toFixed(2)),
      business_class_num: businessNum
    });
  }

  return flights;
};

export async function searchTrainTickets({
  startAreaCode,
  endAreaCode,
  startCityName,
  endCityName,
  date,
  timeStart,
  timeEnd
} = {}) {
  if (USE_MOCK_SEARCH) {
    const list = mockTrainList({
      fromAreaCode: startAreaCode,
      toAreaCode: endAreaCode,
      fromCityName: startCityName,
      toCityName: endCityName,
      timeStart,
      timeEnd
    });

    return Promise.resolve({
      data: {
        code: 200,
        msg: 'ok',
        data: {
          query: {
            start_area_code: startAreaCode,
            end_area_code: endAreaCode,
            date,
            time_start: timeStart,
            time_end: timeEnd
          },
          list
        }
      }
    });
  }

  /*
  真接口（示例，按后端实际调整）：
  GET /ticket/train/search
  Query:
    start_area_code: string  城市编码（市级）
    end_area_code: string
    date: string            YYYY-MM-DD（本项目演示可不使用，仅用于展示）
    time_start: string      HH:mm
    time_end: string        HH:mm
  Response:
    { code:200, msg:'ok', data:{ list: Array<{
        id: bigint,
        train_code: string,
        start_area_code: string,
        end_area_code: string,
        start_time: string,  // HH:mm 或 time
        end_time: string,
        start_station_name: string,
        end_station_name: string,
        train_ticket_list: Array<{
          id: bigint,
          as_train: bigint,
          type_name: string,
          price: decimal,
          num: int
        }>
      }> } }
  */
  return request({
    url: '/ticket/train/search',
    method: 'get',
    params: {
      start_area_code: startAreaCode,
      end_area_code: endAreaCode,
      date,
      time_start: timeStart,
      time_end: timeEnd
    }
  });
}

export async function searchAircraftTickets({
  startAreaCode,
  endAreaCode,
  startCityName,
  endCityName,
  date,
  timeStart,
  timeEnd
} = {}) {
  if (USE_MOCK_SEARCH) {
    const list = mockAircraftList({
      fromAreaCode: startAreaCode,
      toAreaCode: endAreaCode,
      fromCityName: startCityName,
      toCityName: endCityName,
      timeStart,
      timeEnd
    });

    return Promise.resolve({
      data: {
        code: 200,
        msg: 'ok',
        data: {
          query: {
            start_area_code: startAreaCode,
            end_area_code: endAreaCode,
            date,
            time_start: timeStart,
            time_end: timeEnd
          },
          list
        }
      }
    });
  }

  /*
  真接口（示例，按后端实际调整）：
  GET /ticket/aircraft/search
  Query:
    start_area_code: string
    end_area_code: string
    date: string            YYYY-MM-DD（演示用）
    time_start: string      HH:mm
    time_end: string        HH:mm
  Response:
    { code:200, msg:'ok', data:{ list: Array<{
        id: bigint,
        aircraft_code: string,
        aircraft_type: string,
        as_company: string,
        start_area_code: string,
        end_area_code: string,
        start_time: string,
        end_time: string,
        start_station_name: string,
        end_station_name: string,
        economy_class_price: decimal,
        economy_class_num: int,
        business_class_price: decimal,
        business_class_num: int
      }> } }
  */
  return request({
    url: '/ticket/aircraft/search',
    method: 'get',
    params: {
      start_area_code: startAreaCode,
      end_area_code: endAreaCode,
      date,
      time_start: timeStart,
      time_end: timeEnd
    }
  });
}

export async function createTrainOrder({
  asUser,
  asTrain,
  startStationName,
  endStationName,
  typeName,
  payAmount,
  passengers,
  contactName,
  contactPhone,
  extra
} = {}) {
  if (USE_MOCK_ORDER) {
    const orders = loadMockOrders();
    const id = nextOrderId();
    orders.unshift({
      id,
      order_type: 'train',
      as_user: Number(asUser) || 0,
      as_train: Number(asTrain) || 0,
      type_name: String(typeName || ''),
      pay_amount: Number(payAmount) || 0,
      is_pay: 0,
      status: 'NORMAL',
      created_at: nowIso(),
      passengers: Array.isArray(passengers) ? passengers : [],
      contact_name: String(contactName || ''),
      contact_phone: String(contactPhone || ''),
      extra: extra && typeof extra === 'object' ? extra : null
    });
    saveMockOrders(orders);
    return Promise.resolve({
      data: {
        code: 200,
        msg: 'ok',
        data: { id }
      }
    });
  }

  /*
  真接口（示例，按后端实际调整）：
  POST /order/train/create
  Body:
    as_user: bigint
    as_train: bigint
    type_name: string
    pay_amount: decimal(10,2)  (数据库字段名为 pay_amount；如后端已修正可用 pay_amount)
    is_pay: tinyint             0/1
  Response:
    { code:200, msg:'ok', data:{ id: bigint } }
  */
  return request({
    url: '/order/train/create',
    method: 'post',
    data: {
      as_user: asUser,
      as_train: asTrain,
      start_station_name: startStationName,
      end_station_name: endStationName,
      type_name: typeName,
      pay_amount: payAmount,
      is_pay: 0
    }
  });
}

export async function createAircraftOrder({
  asUser,
  asAircraft,
  startStationName,
  endStationName,
  typeName,
  payAmount,
  passengers,
  contactName,
  contactPhone,
  extra
} = {}) {
  if (USE_MOCK_ORDER) {
    const orders = loadMockOrders();
    const id = nextOrderId();
    orders.unshift({
      id,
      order_type: 'aircraft',
      as_user: Number(asUser) || 0,
      as_aircraft: Number(asAircraft) || 0,
      type_name: String(typeName || ''),
      pay_amount: Number(payAmount) || 0,
      is_pay: 0,
      status: 'NORMAL',
      created_at: nowIso(),
      passengers: Array.isArray(passengers) ? passengers : [],
      contact_name: String(contactName || ''),
      contact_phone: String(contactPhone || ''),
      extra: extra && typeof extra === 'object' ? extra : null
    });
    saveMockOrders(orders);
    return Promise.resolve({
      data: {
        code: 200,
        msg: 'ok',
        data: { id }
      }
    });
  }

  /*
  真接口（示例，按后端实际调整）：
  POST /order/aircraft/create
  Body:
    as_user: bigint
    as_aircraft: bigint
    type_name: string
    pay_amount: decimal(10,2)
    is_pay: tinyint
  Response:
    { code:200, msg:'ok', data:{ id: bigint } }
  */
  return request({
    url: '/order/aircraft/create',
    method: 'post',
    data: {
      as_user: asUser,
      as_aircraft: asAircraft,
      start_station_name: startStationName,
      end_station_name: endStationName,
      type_name: typeName,
      pay_amount: payAmount,
      is_pay: 0
    }
  });
}

export async function listMyTrainOrders({ asUser } = {}) {
  if (USE_MOCK_ORDER) {
    const uid = Number(asUser) || 0;
    const orders = loadMockOrders().filter((o) => o.order_type === 'train' && Number(o.as_user) === uid);
    return Promise.resolve({
      data: {
        code: 200,
        msg: 'ok',
        data: { list: orders }
      }
    });
  }

  /*
  真接口（示例，按后端实际调整）：
  GET /order/train/my
  Query:
    as_user: bigint
  Response:
    { code:200, msg:'ok', data:{ list: Array<{
        id: bigint,
        as_user: bigint,
        as_train: bigint,
        type_name: string,
        pay_amount: decimal(10,2) | pay_amount: decimal(10,2),
        is_pay: tinyint,
        create_time: bigint,
        update_time: bigint,
        update_by: bigint,
        is_delete: tinyint
      }> } }
  */
  return request({
    url: '/order/train/my',
    method: 'get',
    params: { as_user: asUser }
  });
}

export async function listMyAircraftOrders({ asUser } = {}) {
  if (USE_MOCK_ORDER) {
    const uid = Number(asUser) || 0;
    const orders = loadMockOrders().filter((o) => o.order_type === 'aircraft' && Number(o.as_user) === uid);
    return Promise.resolve({
      data: {
        code: 200,
        msg: 'ok',
        data: { list: orders }
      }
    });
  }

  /*
  真接口（示例，按后端实际调整）：
  GET /order/aircraft/my
  Query:
    as_user: bigint
  Response:
    { code:200, msg:'ok', data:{ list: Array<{
        id: bigint,
        as_user: bigint,
        as_aircraft: bigint,
        type_name: string,
        pay_amount: decimal(10,2) | pay_amount: decimal(10,2),
        is_pay: tinyint,
        create_time: bigint,
        update_time: bigint,
        update_by: bigint,
        is_delete: tinyint
      }> } }
  */
  return request({
    url: '/order/aircraft/my',
    method: 'get',
    params: { as_user: asUser }
  });
}

const updateOrder = (orderId, updater) => {
  const idNum = Number(orderId);
  const orders = loadMockOrders();
  const idx = orders.findIndex((o) => Number(o.id) === idNum);
  if (idx < 0) return { ok: false, orders };
  const next = typeof updater === 'function' ? updater(orders[idx]) : orders[idx];
  orders[idx] = { ...orders[idx], ...(next || {}) };
  saveMockOrders(orders);
  return { ok: true, orders };
};

export async function payTrainOrder({ id } = {}) {
  if (USE_MOCK_ORDER) {
    const res = updateOrder(id, (o) => {
      if (o.status === 'CANCELLED') return o;
      return { is_pay: 1 };
    });
    return Promise.resolve({
      data: {
        code: res.ok ? 200 : 404,
        msg: res.ok ? 'ok' : '订单不存在',
        data: {}
      }
    });
  }

  /*
  真接口（示例，按后端实际调整）：
  POST /order/train/pay
  Body:
    id: bigint
  Response:
    { code:200, msg:'ok', data:{} }
  */
  return request({
    url: '/order/train/pay',
    method: 'post',
    data: { id }
  });
}

export async function payAircraftOrder({ id } = {}) {
  if (USE_MOCK_ORDER) {
    const res = updateOrder(id, (o) => {
      if (o.status === 'CANCELLED') return o;
      return { is_pay: 1 };
    });
    return Promise.resolve({
      data: {
        code: res.ok ? 200 : 404,
        msg: res.ok ? 'ok' : '订单不存在',
        data: {}
      }
    });
  }

  /*
  真接口（示例，按后端实际调整）：
  POST /order/aircraft/pay
  Body:
    id: bigint
  Response:
    { code:200, msg:'ok', data:{} }
  */
  return request({
    url: '/order/aircraft/pay',
    method: 'post',
    data: { id }
  });
}

export async function cancelTrainOrder({ id } = {}) {
  if (USE_MOCK_ORDER) {
    const res = updateOrder(id, (o) => {
      if (Number(o.is_pay) === 1) return o;
      return { status: 'CANCELLED' };
    });
    return Promise.resolve({
      data: {
        code: res.ok ? 200 : 404,
        msg: res.ok ? 'ok' : '订单不存在',
        data: {}
      }
    });
  }

  /*
  真接口（示例，按后端实际调整）：
  POST /order/train/cancel
  Body:
    id: bigint
  Response:
    { code:200, msg:'ok', data:{} }
  */
  return request({
    url: '/order/train/cancel',
    method: 'post',
    data: { id }
  });
}

export async function cancelAircraftOrder({ id } = {}) {
  if (USE_MOCK_ORDER) {
    const res = updateOrder(id, (o) => {
      if (Number(o.is_pay) === 1) return o;
      return { status: 'CANCELLED' };
    });
    return Promise.resolve({
      data: {
        code: res.ok ? 200 : 404,
        msg: res.ok ? 'ok' : '订单不存在',
        data: {}
      }
    });
  }

  /*
  真接口（示例，按后端实际调整）：
  POST /order/aircraft/cancel
  Body:
    id: bigint
  Response:
    { code:200, msg:'ok', data:{} }
  */
  return request({
    url: '/order/aircraft/cancel',
    method: 'post',
    data: { id }
  });
}
