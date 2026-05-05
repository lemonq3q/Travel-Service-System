<template>
  <div class="order-module">
    <div class="order-container">
      <div class="order-header">
        <div class="order-title">火车票/机票查询</div>
        <div class="order-sub">日期仅用于展示（本系统为演示数据），可按时间段与城市查询并预订</div>
      </div>

      <div class="layout">
        <div class="panel">
          <div class="panel-title">
            <el-icon><Search /></el-icon>
            查询条件
          </div>

          <el-form class="form" label-width="72px">
            <el-form-item label="交通方式">
              <el-radio-group v-model="transportType" size="large">
                <el-radio-button label="train">火车票</el-radio-button>
                <el-radio-button label="aircraft">机票</el-radio-button>
              </el-radio-group>
            </el-form-item>

            <el-form-item label="出发地">
              <AreaSelect v-model="startAreaCode" level="city" placeholder="选择出发城市" />
            </el-form-item>
            <el-form-item label="目的地">
              <AreaSelect v-model="endAreaCode" level="city" placeholder="选择到达城市" />
            </el-form-item>

            <el-form-item label="出行日期">
              <el-date-picker v-model="date" type="date" value-format="YYYY-MM-DD" placeholder="选择日期" />
            </el-form-item>

            <el-form-item label="时间段">
              <el-time-picker
                v-model="timeRange"
                is-range
                value-format="HH:mm"
                format="HH:mm"
                range-separator="至"
                start-placeholder="开始时间"
                end-placeholder="结束时间"
              />
            </el-form-item>

            <el-form-item label="人数">
              <el-input-number v-model="passengers" :min="1" :max="9" />
            </el-form-item>

            <el-form-item>
              <el-button type="primary" size="large" class="search" :loading="loading" @click="search">
                查询
              </el-button>
              <el-button size="large" class="reset" plain @click="reset">重置</el-button>
            </el-form-item>
          </el-form>
        </div>

        <div class="results">
          <div class="results-top">
            <div class="summary">
              <el-icon><Location /></el-icon>
              <span>{{ summaryText }}</span>
            </div>
            <div class="sort">
              <el-select v-model="sortBy" size="default" class="sort-select">
                <el-option label="按最低价" value="price" />
                <el-option label="按出发时间" value="time" />
              </el-select>
              <el-radio-group v-model="sortOrder" size="default">
                <el-radio-button label="asc">升序</el-radio-button>
                <el-radio-button label="desc">降序</el-radio-button>
              </el-radio-group>
            </div>
          </div>

          <el-skeleton v-if="loading" :rows="8" animated />

          <template v-else>
            <el-empty v-if="sortedList.length === 0" description="暂无可售班次/航班" />
            <div v-else class="list">
              <TicketResultCard
                v-for="item in sortedList"
                :key="String(item.id)"
                :transport-type="transportType"
                :item="item"
                @reserve="onReserve"
              />
            </div>
          </template>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';
import { Location, Search } from '@element-plus/icons-vue';
import AreaSelect from '@/components/AreaSelect.vue';
import TicketResultCard from '@/components/tickets/TicketResultCard.vue';
import Message from '@/utils/message';
import { getCascadeArea } from '@/utils/ChinaCitys';
import { searchAircraftTickets, searchTrainTickets } from '@/api/publicTicket';

const router = useRouter();

const transportType = ref('train');
const startAreaCode = ref('');
const endAreaCode = ref('');
const date = ref('');
const timeRange = ref(['08:00', '20:00']);
const passengers = ref(1);

const sortBy = ref('price');
const sortOrder = ref('asc');

const loading = ref(false);
const list = ref([]);

const cityNameFromCode = (code) => {
  if (!code) return '';
  const full = String(getCascadeArea(code) || '');
  const parts = full.split(' / ').map((s) => String(s).trim()).filter(Boolean);
  return parts.length ? parts[parts.length - 1] : '';
};

const startCityName = computed(() => cityNameFromCode(startAreaCode.value));
const endCityName = computed(() => cityNameFromCode(endAreaCode.value));

const summaryText = computed(() => {
  const a = startCityName.value || '出发地';
  const b = endCityName.value || '目的地';
  const d = date.value || '选择日期';
  const p = Number(passengers.value) || 1;
  return `${a} → ${b} · ${d} · ${p}人`;
});

const minPriceOfTrain = (item) => {
  const tickets = Array.isArray(item?.tickets) ? item.tickets : [];
  const prices = tickets.map((t) => Number(t?.price)).filter((n) => Number.isFinite(n) && n > 0);
  return prices.length ? Math.min(...prices) : 0;
};

const minPriceOfAircraft = (item) => {
  const prices = [item?.economy_class_price, item?.business_class_price].map((x) => Number(x)).filter((n) => Number.isFinite(n) && n > 0);
  return prices.length ? Math.min(...prices) : 0;
};

const timeOfItem = (item) => {
  const s = String(item?.start_time || '');
  const m = s.match(/^(\d{1,2}):(\d{2})$/);
  if (!m) return 0;
  return Number(m[1]) * 60 + Number(m[2]);
};

const sortedList = computed(() => {
  const arr = Array.isArray(list.value) ? [...list.value] : [];
  const dir = sortOrder.value === 'desc' ? -1 : 1;
  arr.sort((a, b) => {
    if (sortBy.value === 'time') return (timeOfItem(a) - timeOfItem(b)) * dir;
    const pa = transportType.value === 'train' ? minPriceOfTrain(a) : minPriceOfAircraft(a);
    const pb = transportType.value === 'train' ? minPriceOfTrain(b) : minPriceOfAircraft(b);
    return (pa - pb) * dir;
  });
  return arr;
});

const validate = () => {
  if (!startAreaCode.value || !endAreaCode.value) {
    Message.warning('请选择出发地与目的地');
    return false;
  }
  if (String(startAreaCode.value) === String(endAreaCode.value)) {
    Message.warning('出发地与目的地不能相同');
    return false;
  }
  return true;
};

const search = async () => {
  if (!validate()) return;
  loading.value = true;
  try {
    const timeStart = Array.isArray(timeRange.value) ? timeRange.value[0] : '';
    const timeEnd = Array.isArray(timeRange.value) ? timeRange.value[1] : '';
    const payload = {
      startAreaCode: startAreaCode.value,
      endAreaCode: endAreaCode.value,
      startCityName: startCityName.value,
      endCityName: endCityName.value,
      date: date.value,
      timeStart,
      timeEnd
    };

    const res = transportType.value === 'aircraft' ? await searchAircraftTickets(payload) : await searchTrainTickets(payload);
    const data = res?.data;
    if (data?.code !== 200) {
      Message.warning(data?.msg || '查询失败');
      list.value = [];
      return;
    }
    list.value = Array.isArray(data?.data?.list) ? data.data.list : [];
  } finally {
    loading.value = false;
  }
};

const reset = () => {
  startAreaCode.value = '';
  endAreaCode.value = '';
  date.value = '';
  timeRange.value = ['08:00', '20:00'];
  passengers.value = 1;
  list.value = [];
};

const onReserve = (payload) => {
  const p = payload || {};
  const pick = {
    transportType: p.transportType,
    segmentId: p.segmentId,
    typeName: p.typeName,
    unitPrice: Number(p.unitPrice) || 0,
    passengers: Number(passengers.value) || 1,
    date: date.value,
    startAreaCode: startAreaCode.value,
    endAreaCode: endAreaCode.value,
    startCityName: startCityName.value,
    endCityName: endCityName.value,
    timeStart: Array.isArray(timeRange.value) ? timeRange.value[0] : '',
    timeEnd: Array.isArray(timeRange.value) ? timeRange.value[1] : ''
  };
  sessionStorage.setItem('ticket_checkout_draft_v1', JSON.stringify(pick));
  router.push({ path: '/home/tickets/checkout', query: { t: pick.transportType } });
};

onMounted(() => {
  const d = new Date();
  const pad = (n) => String(n).padStart(2, '0');
  date.value = `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}`;
});
</script>

<style scoped>
.layout {
  display: grid;
  grid-template-columns: 360px 1fr;
  gap: 14px;
}

.panel {
  background: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 14px;
  padding: 14px;
}

.panel-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 900;
  color: #0f172a;
  margin-bottom: 12px;
}

.form :deep(.el-date-editor),
.form :deep(.el-time-editor) {
  width: 100%;
}

.search {
  width: 140px;
  border-radius: 12px;
}

.reset {
  border-radius: 12px;
}

.results {
  min-width: 0;
}

.results-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 12px;
}

.summary {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  font-weight: 900;
  color: #0f172a;
}

.sort {
  display: inline-flex;
  align-items: center;
  gap: 10px;
}

.sort-select {
  width: 140px;
}

.list {
  display: grid;
  gap: 12px;
}

@media (max-width: 960px) {
  .layout {
    grid-template-columns: 1fr;
  }

  .results-top {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>

