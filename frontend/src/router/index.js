import EditSystemUser from '@/components/EditSystemUser.vue';
import PersonalCenter from '@/components/PersonalCenter.vue';
import UserManagement from '@/components/UserManagement.vue';
import HotelManagement from '@/components/HotelManagement.vue';
import EditHotel from '@/components/EditHotel.vue';
import ScenicManagement from '@/components/ScenicManagement.vue';
import EditScenic from '@/components/EditScenic.vue';
import HomePage from '@/page/HomePage.vue';
import LoginPage from '@/page/LoginPage.vue';
import Storage from '@/utils/storage';
import HotelsPage from '@/components/HotelsPage.vue';
import HotelDetailPage from '@/components/HotelDetailPage.vue';
import UserScenicsPage from '@/components/UserScenicsPage.vue';
import UserScenicDetailPage from '@/components/UserScenicDetailPage.vue';
import UserHotelOrderCreatePage from '@/components/orders/UserHotelOrderCreatePage.vue';
import UserScenicOrderCreatePage from '@/components/orders/UserScenicOrderCreatePage.vue';
import PaymentPlaceholderPage from '@/components/orders/PaymentPlaceholderPage.vue';
import UserOrdersPage from '@/components/orders/UserOrdersPage.vue';
import OrderReviewCreatePage from '@/components/orders/OrderReviewCreatePage.vue';

import UserTicketsPage from '@/components/tickets/UserTicketsPage.vue';
import UserTicketCheckoutPage from '@/components/tickets/UserTicketCheckoutPage.vue';

import GuideSearchPage from '@/components/userGuides/GuideSearchPage.vue';
import GuideDetailPage from '@/components/userGuides/GuideDetailPage.vue';
import GuidePublishPage from '@/components/userGuides/GuidePublishPage.vue';

import { createRouter, createWebHistory } from 'vue-router';

const routes = [
  {
    path: '/',
    redirect: '/login',
  },
  {
    path: '/hotels',
    redirect: '/home/hotels'
  },
  {
    path: '/hotels/:hotelId',
    redirect: (to) => `/home/hotels/${to.params.hotelId}`
  },
  {
    path: '/scenics',
    redirect: '/home/scenics'
  },
  {
    path: '/scenics/:scenicId',
    redirect: (to) => `/home/scenics/${to.params.scenicId}`
  },
  {
    path: '/guides',
    redirect: '/home/guides'
  },
  {
    path: '/guides/new',
    redirect: '/home/guides/new'
  },
  {
    path: '/guides/:guideId',
    redirect: (to) => `/home/guides/${to.params.guideId}`
  },
  {
    path: '/orders/hotel/create',
    redirect: '/home/orders/hotel/create'
  },
  {
    path: '/orders/scenic/create',
    redirect: '/home/orders/scenic/create'
  },
  {
    path: '/pay',
    redirect: '/home/pay'
  },
  {
    path: '/orders',
    redirect: '/home/orders'
  },
  {
    path: '/tickets',
    redirect: '/home/tickets'
  },
  {
    path: '/tickets/checkout',
    redirect: '/home/tickets/checkout'
  },
  {
    path: '/reviews/new',
    redirect: '/home/reviews/new'
  },
  {
    path: '/login',
    component: LoginPage,
    meta: {
      requiresAuth: false
    },
  },
  {
    path: '/home',
    component: HomePage,
    meta: {
      requiresAuth: true
    },
    children: [
      {
        path: '',
        redirect: '/home/personalCenter',
      },
      {
        path: 'hotels',
        component: HotelsPage
      },
      {
        path: 'hotels/:hotelId',
        component: HotelDetailPage
      },
      {
        path: 'scenics',
        component: UserScenicsPage
      },
      {
        path: 'scenics/:scenicId',
        component: UserScenicDetailPage
      },
      {
        path: 'guides',
        component: GuideSearchPage
      },
      {
        path: 'guides/new',
        component: GuidePublishPage
      },
      {
        path: 'guides/:guideId',
        component: GuideDetailPage
      },
      {
        path: 'orders/hotel/create',
        component: UserHotelOrderCreatePage
      },
      {
        path: 'orders/scenic/create',
        component: UserScenicOrderCreatePage
      },
      {
        path: 'orders',
        component: UserOrdersPage
      },
      {
        path: 'tickets',
        component: UserTicketsPage
      },
      {
        path: 'tickets/checkout',
        component: UserTicketCheckoutPage
      },
      {
        path: 'pay',
        component: PaymentPlaceholderPage
      },
      {
        path: 'reviews/new',
        component: OrderReviewCreatePage
      },
      {
        path: 'personalCenter',
        component: PersonalCenter
      },
      {
        path: 'userManagement',
        component: UserManagement
      },
      {
        path: 'editSystemUser',
        component: EditSystemUser
      },
      {
        path: 'hotelManagement',
        component: HotelManagement
      },
      {
        path: 'editHotel',
        component: EditHotel
      },
      {
        path: 'scenicManagement',
        component: ScenicManagement
      },
      {
        path: 'editScenic',
        component: EditScenic
      },
    ]
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

// 2. 添加全局前置路由守卫
router.beforeEach((to, from, next) => {
  const isLogin = Storage.get('token') !== null && Storage.get('token') !== '' && Storage.get('token') !== undefined;
  if (to.meta.requiresAuth){
    if (isLogin) {
      next();
    } else {
      next('/login');
    }
  }
  else{
    if (to.path === '/login' && isLogin) {
      next('/home');
    } else {
      next();
    }
  }
});

export default router;
