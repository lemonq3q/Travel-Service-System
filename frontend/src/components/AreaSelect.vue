<template>
  <div class="area_select">
    <el-cascader
      v-model="innerSelectedArea" 
      :options="options" 
      clearable 
      :props="cascaderProps"
      filterable
      :placeholder="props.placeholder"
      @change="handleCascaderChange" 
    />
  </div>
</template>

<script setup>
import { computed, ref, watch, defineProps, defineEmits, onMounted } from 'vue';
import { getCascadeAreaCode, getSelectOption } from '@/utils/ChinaCitys';

// 1. 定义接收父组件的属性
const props = defineProps({
  modelValue: {
    type: [String, Array],
    default: ''
  },
  placeholder: {
    type: String,
    default: '请选择地区'
  },
  level: {
    type: String,
    default: 'district'
  },
  multiple: {
    type: Boolean,
    default: false
  },
  multi: {
    type: Boolean,
    default: undefined
  }
});


const emit = defineEmits(['update:modelValue']);

const innerSelectedArea = ref([]);
const options = ref([]); 

const isMultiple = computed(() => (props.multi === undefined ? props.multiple : props.multi));

const levelDepth = computed(() => {
  if (props.level === 'province') return 1;
  if (props.level === 'city') return 2;
  return 3;
});

const cascaderProps = computed(() => ({
  multiple: isMultiple.value,
  emitPath: true
}));

const normalizePath = (path) => {
  const p = Array.isArray(path) ? path : [];
  const depth = levelDepth.value;
  return p.slice(0, depth);
};

const normalizeInnerValueFromModel = (modelValue) => {
  const depth = levelDepth.value;
  if (isMultiple.value) {
    const codes = Array.isArray(modelValue) ? modelValue : [];
    return codes
      .map(code => getCascadeAreaCode(code))
      .map(path => path.slice(0, depth))
      .filter(path => path.length > 0);
  }

  const code = typeof modelValue === 'string' ? modelValue : '';
  if (!code) return [];
  return getCascadeAreaCode(code).slice(0, depth);
};

const extractModelValueFromInner = (innerValue) => {
  const depthIndex = levelDepth.value - 1;
  const pickLeaf = (path) => {
    if (!Array.isArray(path) || path.length === 0) return '';
    return path[depthIndex] ?? path[path.length - 1] ?? '';
  };

  if (isMultiple.value) {
    const paths = Array.isArray(innerValue) ? innerValue : [];
    return paths
      .map(p => pickLeaf(p))
      .filter(code => !!code);
  }
  return pickLeaf(innerValue);
};

const handleCascaderChange = (val) => {
  if (isMultiple.value) {
    const normalizedVal = Array.isArray(val) ? val.map(normalizePath) : [];
    innerSelectedArea.value = normalizedVal;
    emit('update:modelValue', extractModelValueFromInner(normalizedVal));
    return;
  }

  const normalizedVal = normalizePath(val);
  innerSelectedArea.value = normalizedVal;
  emit('update:modelValue', extractModelValueFromInner(normalizedVal));
};

// 仅监听父组件值变化
watch(
  () => [props.modelValue, props.level, isMultiple.value],
  () => {
    innerSelectedArea.value = normalizeInnerValueFromModel(props.modelValue);
  },
  { deep: true, immediate: true }
);

const truncateOptions = (opts, depth) => {
  if (!Array.isArray(opts)) return [];
  if (depth <= 0) return [];
  if (depth === 1) {
    return opts.map(item => ({
      ...item,
      children: undefined
    }));
  }
  return opts.map(item => ({
    ...item,
    children: truncateOptions(item.children || [], depth - 1)
  }));
};

onMounted(() => {
  const areaOptions = getSelectOption();
  if (areaOptions && areaOptions.length) {
    options.value = truncateOptions(areaOptions, levelDepth.value);
  }
});

watch(levelDepth, () => {
  const areaOptions = getSelectOption();
  if (areaOptions && areaOptions.length) {
    options.value = truncateOptions(areaOptions, levelDepth.value);
  }
});
</script>

<style scoped>
.area_select :deep(.el-cascader) {
  width: 100%;
}
</style>
