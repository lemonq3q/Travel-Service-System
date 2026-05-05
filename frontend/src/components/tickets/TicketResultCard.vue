<template>
  <div class="card">
    <div class="top" @click="toggle">
      <div class="left">
        <div class="title-row">
          <el-tag class="type-tag" :type="typeTagType" effect="dark">
            {{ typeText }}
          </el-tag>
          <div class="code">{{ codeText }}</div>
        </div>
        <div class="route">
          <div class="time">
            <div class="t">{{ startTime }}</div>
            <div class="s">{{ startStation }}</div>
          </div>
          <div class="mid">
            <div class="dur">
              <el-icon><Clock /></el-icon>
              {{ durationText }}
            </div>
            <div class="line" />
          </div>
          <div class="time">
            <div class="t">{{ endTime }}</div>
            <div class="s">{{ endStation }}</div>
          </div>
        </div>
      </div>

      <div class="right">
        <div class="price">
          <span class="from">¥</span>
          <span class="num">{{ minPriceText }}</span>
          <span class="from">起</span>
        </div>
        <el-button class="expand" text type="primary">
          {{ expanded ? '收起' : '展开' }}
          <el-icon>
            <ArrowDown v-if="!expanded" />
            <ArrowUp v-else />
          </el-icon>
        </el-button>
      </div>
    </div>

    <div v-if="expanded" class="details">
      <div class="detail-title">可预订票种</div>

      <div v-if="transportType === 'train'" class="offers">
        <div v-for="t in trainTickets" :key="String(t.id)" class="offer">
          <div class="offer-left">
            <div class="offer-name">{{ t.type_name }}</div>
            <div class="offer-sub">余票 {{ Number(t.num) || 0 }}</div>
          </div>
          <div class="offer-mid">¥{{ formatMoney(t.price) }}</div>
          <div class="offer-right">
            <el-button type="primary" :disabled="Number(t.num) <= 0" @click.stop="reserveTrain(t)">
              预订
            </el-button>
          </div>
        </div>
      </div>

      <div v-else class="offers">
        <div class="offer">
          <div class="offer-left">
            <div class="offer-name">经济舱</div>
            <div class="offer-sub">余位 {{ Number(aircraftItem.economy_class_num) || 0 }}</div>
          </div>
          <div class="offer-mid">¥{{ formatMoney(aircraftItem.economy_class_price) }}</div>
          <div class="offer-right">
            <el-button
              type="primary"
              :disabled="Number(aircraftItem.economy_class_num) <= 0"
              @click.stop="reserveAircraft('经济舱')"
            >
              预订
            </el-button>
          </div>
        </div>

        <div class="offer">
          <div class="offer-left">
            <div class="offer-name">商务舱</div>
            <div class="offer-sub">余位 {{ Number(aircraftItem.business_class_num) || 0 }}</div>
          </div>
          <div class="offer-mid">¥{{ formatMoney(aircraftItem.business_class_price) }}</div>
          <div class="offer-right">
            <el-button
              type="primary"
              :disabled="Number(aircraftItem.business_class_num) <= 0"
              @click.stop="reserveAircraft('商务舱')"
            >
              预订
            </el-button>
          </div>
        </div>
      </div>

      <div class="rules">
        <el-icon><InfoFilled /></el-icon>
        <span>{{ rulesText }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue';
import { ArrowDown, ArrowUp, Clock, InfoFilled } from '@element-plus/icons-vue';

const props = defineProps({
  transportType: {
    type: String,
    required: true
  },
  item: {
    type: Object,
    required: true
  }
});

const emit = defineEmits(['reserve']);

const expanded = ref(false);

const toggle = () => {
  expanded.value = !expanded.value;
};

const formatMoney = (v) => {
  const n = Number(v);
  if (!Number.isFinite(n)) return '0.00';
  return n.toFixed(2);
};

const typeText = computed(() => (props.transportType === 'train' ? '火车' : '飞机'));
const typeTagType = computed(() => (props.transportType === 'train' ? 'success' : 'warning'));

const trainItem = computed(() => (props.transportType === 'train' ? props.item : null));
const aircraftItem = computed(() => (props.transportType === 'aircraft' ? props.item : null));

const codeText = computed(() => {
  if (props.transportType === 'train') return String(trainItem.value?.train_code || '-');
  const a = aircraftItem.value;
  const code = String(a?.aircraft_code || '-');
  const company = String(a?.as_company || '').trim();
  const model = String(a?.aircraft_type || '').trim();
  const extra = [company, model].filter(Boolean).join(' · ');
  return extra ? `${code}（${extra}）` : code;
});

const startTime = computed(() => {
  const v = props.item?.start_time;
  return String(v || '--:--');
});
const endTime = computed(() => {
  const v = props.item?.end_time;
  return String(v || '--:--');
});
const startStation = computed(() => String(props.item?.start_station_name || '-'));
const endStation = computed(() => String(props.item?.end_station_name || '-'));
const durationText = computed(() => String(props.item?.duration_text || ''));

const trainTickets = computed(() => {
  const list = trainItem.value?.tickets;
  return Array.isArray(list) ? list : [];
});

const minPriceText = computed(() => {
  if (props.transportType === 'train') {
    const prices = trainTickets.value.map((t) => Number(t?.price)).filter((n) => Number.isFinite(n));
    const min = prices.length ? Math.min(...prices) : 0;
    return formatMoney(min);
  }
  const a = aircraftItem.value;
  const prices = [a?.economy_class_price, a?.business_class_price].map((x) => Number(x)).filter((n) => Number.isFinite(n) && n > 0);
  const min = prices.length ? Math.min(...prices) : 0;
  return formatMoney(min);
});

const rulesText = computed(() => {
  if (props.transportType === 'train') return '开车前可退；改签以站点规则为准（演示）';
  return '起飞前可退；改签以航司规则为准（演示）';
});

const reserveTrain = (ticket) => {
  const t = ticket || {};
  emit('reserve', {
    transportType: 'train',
    segmentId: trainItem.value?.id,
    typeName: t.type_name,
    unitPrice: Number(t.price) || 0
  });
};

const reserveAircraft = (typeName) => {
  const a = aircraftItem.value;
  const isBiz = String(typeName) === '商务舱';
  const unitPrice = isBiz ? Number(a?.business_class_price) || 0 : Number(a?.economy_class_price) || 0;
  emit('reserve', {
    transportType: 'aircraft',
    segmentId: a?.id,
    typeName: String(typeName || ''),
    unitPrice
  });
};
</script>

<style scoped>
.card {
  border-radius: 16px;
  border: 1px solid #e5e7eb;
  box-shadow: 0 10px 25px rgba(2, 6, 23, 0.06);
  padding: 14px;
}

.top {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 14px;
  cursor: pointer;
}

.left {
  min-width: 0;
  flex: 1;
}

.title-row {
  display: inline-flex;
  align-items: center;
  gap: 10px;
}

.type-tag {
  border-radius: 999px;
  font-weight: 900;
}

.code {
  font-weight: 900;
  color: #0f172a;
  letter-spacing: 0.2px;
}

.route {
  margin-top: 10px;
  display: grid;
  grid-template-columns: 1fr 120px 1fr;
  gap: 12px;
  align-items: center;
}

.time .t {
  font-size: 22px;
  font-weight: 900;
  color: #0f172a;
}

.time .s {
  margin-top: 4px;
  font-size: 12px;
  color: #64748b;
}

.mid {
  text-align: center;
}

.dur {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: #64748b;
}

.line {
  height: 1px;
  background: linear-gradient(90deg, rgba(148, 163, 184, 0), rgba(148, 163, 184, 1), rgba(148, 163, 184, 0));
  margin-top: 10px;
}

.right {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 6px;
}

.price {
  display: inline-flex;
  align-items: baseline;
  gap: 2px;
}

.price .from {
  font-size: 12px;
  color: #64748b;
}

.price .num {
  font-size: 22px;
  font-weight: 900;
  color: #0f172a;
}

.expand {
  font-weight: 900;
  padding: 0;
}

.details {
  margin-top: 14px;
  border-top: 1px solid #eef2f7;
  padding-top: 14px;
}

.detail-title {
  font-weight: 900;
  color: #0f172a;
  margin-bottom: 10px;
}

.offers {
  display: grid;
  gap: 10px;
}

.offer {
  display: grid;
  grid-template-columns: 1fr auto auto;
  gap: 12px;
  align-items: center;
  padding: 10px 12px;
  border: 1px solid #eef2f7;
  border-radius: 12px;
  background: #fff;
}

.offer-name {
  font-weight: 900;
  color: #0f172a;
}

.offer-sub {
  margin-top: 4px;
  font-size: 12px;
  color: #64748b;
}

.offer-mid {
  font-size: 16px;
  font-weight: 900;
  color: #0f172a;
}

.rules {
  margin-top: 12px;
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
  color: #64748b;
}

@media (max-width: 960px) {
  .route {
    grid-template-columns: 1fr;
    gap: 10px;
  }

  .mid {
    display: none;
  }
}
</style>
