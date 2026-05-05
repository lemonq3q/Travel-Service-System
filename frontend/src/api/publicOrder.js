import request from '@/utils/request';

export async function checkHotelRoomRemain({
  asHotel,
  asRoom,
  checkInDate,
  checkOutDate
} = {}) {
  return request({
    url: '/order/hotel/remain',
    method: 'get',
    params: {
      as_hotel: asHotel,
      as_room: asRoom,
      check_in_date: checkInDate,
      check_out_date: checkOutDate
    }
  });
}

export async function listMyHotelOrders({ asUser } = {}) {
  return request({
    url: '/order/hotel/my',
    method: 'get',
    params: { as_user: asUser }
  });
}

export async function listMyScenicOrders({ asUser } = {}) {
  return request({
    url: '/order/scenic/my',
    method: 'get',
    params: { as_user: asUser }
  });
}

export async function cancelHotelOrder({ id, asUser } = {}) {
  return request({
    url: '/order/hotel/cancel',
    method: 'post',
    data: { id, as_user: asUser }
  });
}

export async function cancelScenicOrder({ id, asUser } = {}) {
  return request({
    url: '/order/scenic/cancel',
    method: 'post',
    data: { id, as_user: asUser }
  });
}

export async function createHotelOrder({
  asUser,
  asHotel,
  asRoom,
  roomNum,
  checkInDate,
  checkOutDate,
  payAmount,
  isPay
} = {}) {
  return request({
    url: '/order/hotel/create',
    method: 'post',
    data: {
      as_user: asUser,
      as_hotel: asHotel,
      as_room: asRoom,
      room_num: roomNum,
      check_in_date: checkInDate,
      check_out_date: checkOutDate,
      pay_amount: payAmount,
      is_pay: isPay
    }
  });
}

export async function createScenicOrder({ asUser, asScenic, payAmount, isPay } = {}) {
  return request({
    url: '/order/scenic/create',
    method: 'post',
    data: {
      as_user: asUser,
      as_scenic: asScenic,
      pay_amount: payAmount,
      is_pay: isPay
    }
  });
}

export async function payHotelOrder({ id, asUser } = {}) {
  return request({
    url: '/order/hotel/pay',
    method: 'post',
    data: { id, as_user: asUser }
  });
}

export async function payScenicOrder({ id, asUser } = {}) {
  return request({
    url: '/order/scenic/pay',
    method: 'post',
    data: { id, as_user: asUser }
  });
}
