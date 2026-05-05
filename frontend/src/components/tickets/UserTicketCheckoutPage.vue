<template>
  <div class="order-module">
    <div class="order-container">
      <div class="order-topbar">
        <el-button text type="primary" @click="goBack">
          <el-icon><ArrowLeft /></el-icon>
          返回
        </el-button>
      </div>

      <div class="order-header">
        <div class="order-title">{{ pageTitle }}</div>
        <div class="order-sub">确认行程信息，填写乘客与联系人信息后去付款（演示）</div>
      </div>

      <div class="order-grid two">
        <div>
          <div class="order-card">
            <div class="order-card-title">
              <el-icon><CircleCheck /></el-icon>
              行程信息
            </div>

            <div class="order-kv">
              <div class="k">出发</div>
              <div class="v">{{ draft.startCityName || '-' }}</div>
            </div>
            <div class="order-kv">
              <div class="k">到达</div>
              <div class="v">{{ draft.endCityName || '-' }}</div>
            </div>
            <div class="order-kv">
              <div class="k">日期</div>
              <div class="v">{{ draft.date || '-' }}</div>
            </div>
            <div class="order-kv">
              <div class="k">票种</div>
              <div class="v">{{ draft.typeName || '-' }}</div>
            </div>
            <div class="order-kv">
              <div class="k">人数</div>
              <div class="v">{{ Number(draft.passengers) || 1 }} 人</div>
            </div>

            <div class="order-divider" />

            <div class="order-card-title">
              <el-icon><User /></el-icon>
              乘客信息
            </div>

            <div class="passengers">
              <div v-for="(p, idx) in passengerList" :key="idx" class="passenger">
                <div class="p-title">乘客 {{ idx + 1 }}</div>
                <div class="p-form">
                  <el-input v-model="p.name" placeholder="姓名" />
                  <el-select v-model="p.idType" placeholder="证件类型" style="width: 120px">
                    <el-option label="身份证" value="id_card" />
                    <el-option label="护照" value="passport" />
                  </el-select>
                  <el-input v-model="p.idNo" placeholder="证件号" />
                </div>
              </div>
            </div>

            <div class="order-divider" />

            <div class="order-card-title">
              <el-icon><Phone /></el-icon>
              联系信息
            </div>
            <div class="contact">
              <el-input v-model="contactName" placeholder="联系人姓名" />
              <el-input v-model="contactPhone" placeholder="手机号" />
            </div>
          </div>
        </div>

        <div>
          <div class="order-card">
            <div class="order-card-title">
              <el-icon><CircleCheck /></el-icon>
              费用明细
            </div>

            <div class="order-price-row">
              <div class="label">单价</div>
              <div class="value">¥{{ unitPrice.toFixed(2) }}</div>
            </div>
            <div class="order-price-row">
              <div class="label">人数</div>
              <div class="value">{{ passengerList.length }} 人</div>
            </div>
            <div class="order-divider" />
            <div class="order-total">
              <div class="label">应付</div>
              <div class="value">¥{{ totalPrice.toFixed(2) }}</div>
            </div>

            <el-button
              type="primary"
              size="large"
              class="order-submit"
              :loading="submitting"
              :disabled="!canSubmit"
              @click="submitAndPay"
            >
              去付款
            </el-button>
            <div class="order-tip">支付为模拟流程：创建订单后跳转占位支付页</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue';
import { useRouter } from 'vue-router';
import { ArrowLeft, CircleCheck, Phone, User } from '@element-plus/icons-vue';
import Message from '@/utils/message';
import Storage from '@/utils/storage';
import { createAircraftOrder, createTrainOrder } from '@/api/publicTicket';

const router = useRouter();
const submitting = ref(false);

const draft = reactive({
  transportType: 'train',
  segmentId: null,
  typeName: '',
  unitPrice: 0,
  passengers: 1,
  date: '',
  startAreaCode: '',
  endAreaCode: '',
  startCityName: '',
  endCityName: '',
  timeStart: '',
  timeEnd: ''
});

const passengerList = ref([]);
const contactName = ref('');
const contactPhone = ref('');

const pageTitle = computed(() => (draft.transportType === 'aircraft' ? '机票下单' : '火车票下单'));

const unitPrice = computed(() => {
  const n = Number(draft.unitPrice);
  return Number.isFinite(n) && n > 0 ? n : 0;
});

const totalPrice = computed(() => {
  const count = passengerList.value.length || 0;
  return unitPrice.value * count;
});

const goBack = () => {
  router.back();
};

const initPassengers = (count) => {
  const n = Math.max(1, Math.min(9, Number(count) || 1));
  const next = [];
  for (let i = 0; i < n; i += 1) {
    next.push({ name: '', idType: 'id_card', idNo: '' });
  }
  passengerList.value = next;
};

const canSubmit = computed(() => {
  if (submitting.value) return false;
  if (!draft.segmentId || !draft.typeName) return false;
  if (unitPrice.value <= 0) return false;
  if (!contactName.value || !contactPhone.value) return false;
  if (!/^(\d{11})$/.test(String(contactPhone.value))) return false;
  const ps = passengerList.value;
  if (!Array.isArray(ps) || ps.length <= 0) return false;
  for (let i = 0; i < ps.length; i += 1) {
    const p = ps[i];
    if (!p?.name || !p?.idNo || !p?.idType) return false;
  }
  return true;
});

const getUserId = () => {
  try {
    const raw = localStorage.getItem('userInfo');
    const uid = raw ? Number(JSON.parse(raw)?.id) : 0;
    return Number.isFinite(uid) && uid > 0 ? uid : 0;
  } catch {
    return 0;
  }
};

const submitAndPay = async () => {
  if (!canSubmit.value) {
    Message.warning('请完善订单信息');
    return;
  }

  const uid = getUserId();
  if (!uid) {
    const token = Storage.get('token');
    if (!token) {
      Message.warning('请先登录');
      router.push('/login');
      return;
    }
  }

  submitting.value = true;
  try {
    const payAmount = Number(totalPrice.value).toFixed(2);
    const extra = {
      start_city_name: draft.startCityName,
      end_city_name: draft.endCityName,
      date: draft.date,
      time_start: draft.timeStart,
      time_end: draft.timeEnd,
      unit_price: unitPrice.value,
      passengers: passengerList.value
    };

    const res =
      draft.transportType === 'aircraft'
        ? await createAircraftOrder({
            asUser: uid,
            asAircraft: draft.segmentId,
            startStationName: draft.startCityName,
            endStationName: draft.endCityName,
            typeName: draft.typeName,
            payAmount,
            passengers: passengerList.value,
            contactName: contactName.value,
            contactPhone: contactPhone.value,
            extra
          })
        : await createTrainOrder({
            asUser: uid,
            asTrain: draft.segmentId,
            startStationName: draft.startCityName,
            endStationName: draft.endCityName,
            typeName: draft.typeName,
            payAmount,
            passengers: passengerList.value,
            contactName: contactName.value,
            contactPhone: contactPhone.value,
            extra
          });
    const data = res?.data;
    if (data?.code !== 200) {
      Message.warning(data?.msg || '创建订单失败');
      return;
    }
    const id = data?.data?.id;
    Message.success('订单已创建');
    router.push({
      path: '/home/pay',
      query: {
        orderType: draft.transportType,
        orderId: id,
        amount: payAmount,
        tab: draft.transportType
      }
    });
  } finally {
    submitting.value = false;
  }
};

onMounted(() => {
  try {
    const raw = sessionStorage.getItem('ticket_checkout_draft_v1');
    const obj = raw ? JSON.parse(raw) : null;
    if (!obj || typeof obj !== 'object') {
      Message.warning('请先选择要预订的票');
      router.push('/home/tickets');
      return;
    }
    Object.assign(draft, obj);
    initPassengers(obj.passengers);
  } catch {
    Message.warning('请先选择要预订的票');
    router.push('/home/tickets');
  }
});
</script>

<style scoped>
.passengers {
  margin-top: 12px;
  display: grid;
  gap: 12px;
}

.passenger {
  border: 1px solid #eef2f7;
  border-radius: 12px;
  padding: 12px;
}

.p-title {
  font-weight: 900;
  color: #0f172a;
  margin-bottom: 10px;
}

.p-form {
  display: grid;
  grid-template-columns: 1fr 120px 1.2fr;
  gap: 10px;
}

.contact {
  margin-top: 12px;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
}

@media (max-width: 960px) {
  .p-form {
    grid-template-columns: 1fr;
  }

  .contact {
    grid-template-columns: 1fr;
  }
}
</style>
