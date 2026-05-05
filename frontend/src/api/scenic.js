import request from '@/utils/request';

const sleep = (ms) => new Promise(resolve => setTimeout(resolve, ms));

// export async function getScenicList(params) {
//   await sleep(300);
//   return {
//     data: {
//       code: 200,
//       data: {
//         table: [
//           {
//             id: 1,
//             name: '测试景点A',
//             address: '测试市测试区测试路 1 号',
//             areaCode: '110101',
//             longitude: 116.397428,
//             latitude: 39.90923,
//             desc: '这是一个用于演示的景点',
//             openTimeDesc: '09:00-17:30',
//             rating: 4.6,
//             hotValue: 4.8,
//             needTicket: 1,
//             price: 80.0,
//             scenicImgList: [10101, 10102]
//           },
//           {
//             id: 2,
//             name: '测试景点B',
//             address: '测试市测试区测试路 2 号',
//             areaCode: '310101',
//             longitude: 121.473701,
//             latitude: 31.230416,
//             desc: '另一个用于演示的景点',
//             openTimeDesc: '全天开放',
//             rating: 4.2,
//             hotValue: 3.9,
//             needTicket: 0,
//             price: 0,
//             scenicImgList: [10201]
//           }
//         ],
//         total: 2
//       }
//     }
//   };
// }

export function getScenicList(params) {
  return request({
    url: '/scenic/list',
    method: 'get',
    params
  });
}

// export async function getScenicDetail(id) {
//   await sleep(200);
//   return {
//     data: {
//       code: 200,
//       data: {
//         id,
//         name: '测试景点A',
//         address: '测试市测试区测试路 1 号',
//         areaCode: '110101',
//         longitude: 116.397428,
//         latitude: 39.90923,
//         desc: '这是一个用于演示的景点',
//         openTimeDesc: '09:00-17:30',
//         rating: 4.6,
//         hotValue: 4.8,
//         needTicket: 1,
//         price: 80.0,
//         scenicImgList: [10101, 10102]
//       }
//     }
//   };
// }

export function getScenicDetail(id) {
  return request({
    url: `/scenic/${id}`,
    method: 'get'
  });
}

// export async function addScenic(data) {
//   await sleep(500);
//   return {
//     data: {
//       code: 200,
//       data: {
//         id: Math.floor(Math.random() * 1000000),
//         payload: data
//       }
//     }
//   };
// }

export function addScenic(data) {
  return request({
    url: '/scenic',
    method: 'post',
    data
  });
}

// export async function updateScenic(data) {
//   await sleep(500);
//   return {
//     data: {
//       code: 200,
//       data: {
//         payload: data
//       }
//     }
//   };
// }

export function updateScenic(data) {
  return request({
    url: '/scenic',
    method: 'put',
    data
  });
}

// export async function deleteScenic(id) {
//   await sleep(200);
//   return {
//     data: {
//       code: 200,
//       data: {
//         id
//       }
//     }
//   };
// }

export function deleteScenic(id) {
  return request({
    url: `/scenic/${id}`,
    method: 'delete'
  });
}