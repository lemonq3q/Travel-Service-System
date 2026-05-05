<template>
  <div class="tag-input">
    <div class="selected">
      <el-tag
        v-for="tag in selectedTags"
        :key="tag"
        class="tag"
        closable
        @close="removeTag(tag)"
      >
        {{ tag }}
      </el-tag>

      <el-popover
        placement="bottom-start"
        :width="520"
        trigger="click"
        v-model:visible="pickerVisible"
      >
        <template #reference>
          <el-button class="add-btn" type="primary" plain size="small">
            + 添加
          </el-button>
        </template>

        <div class="picker">
          <div class="picker-head">
            <div class="picker-title">选择预设标签</div>
            <el-button text @click="clearAll" :disabled="selectedTags.length === 0">清空</el-button>
          </div>

          <div class="picker-search" v-if="(presets || []).length">
            <el-input v-model="keyword" size="small" placeholder="搜索预设标签" clearable />
          </div>

          <div class="preset-list" v-if="filteredPresets.length">
            <el-tag
              v-for="p in filteredPresets"
              :key="p"
              class="preset"
              :type="isSelected(p) ? 'success' : 'primary'"
              :effect="isSelected(p) ? 'dark' : 'plain'"
              @click="togglePreset(p)"
            >
              {{ p }}
            </el-tag>
          </div>

          <div class="custom">
            <div class="custom-title">自定义标签</div>
            <div class="custom-row">
              <el-input v-model="customInput" size="small" :placeholder="placeholder" @keyup.enter="addCustom" />
              <el-button type="primary" size="small" @click="addCustom" :disabled="!customInput.trim()">添加</el-button>
            </div>
          </div>
        </div>
      </el-popover>
    </div>
  </div>
</template>

<script setup>
import { computed, ref, watch, defineProps, defineEmits } from 'vue';

const props = defineProps({
  modelValue: {
    type: String,
    default: '[]'
  },
  presets: {
    type: Array,
    default: () => []
  },
  placeholder: {
    type: String,
    default: '请选择或输入标签'
  }
});

const emit = defineEmits(['update:modelValue']);

const sanitize = (arr) => {
  const list = Array.isArray(arr) ? arr : [];
  const cleaned = list
    .map((x) => String(x ?? '').trim())
    .filter((x) => !!x);
  return Array.from(new Set(cleaned));
};

const selectedTags = ref([]);
try {
  selectedTags.value = sanitize(props.modelValue ? JSON.parse(props.modelValue) : []);
} catch {
  selectedTags.value = [];
}

watch(() => props.modelValue, (newVal) => {
  try {
    const parsed = newVal ? JSON.parse(newVal) : [];
    selectedTags.value = sanitize(parsed);
  } catch {
    selectedTags.value = [];
  }
});

const presets = computed(() => sanitize(props.presets || []));

const pickerVisible = ref(false);
const keyword = ref('');
const customInput = ref('');

const isSelected = (tag) => selectedTags.value.includes(String(tag));

const filteredPresets = computed(() => {
  const kw = String(keyword.value || '').trim();
  if (!kw) return presets.value;
  return presets.value.filter((x) => x.includes(kw));
});

const emitChange = () => {
  emit('update:modelValue', JSON.stringify(selectedTags.value));
};

const addTag = (tag) => {
  const t = String(tag ?? '').trim();
  if (!t) return;
  if (isSelected(t)) return;
  selectedTags.value = sanitize([...selectedTags.value, t]);
  emitChange();
};

const removeTag = (tag) => {
  const t = String(tag ?? '').trim();
  if (!t) return;
  selectedTags.value = selectedTags.value.filter((x) => x !== t);
  emitChange();
};

const togglePreset = (tag) => {
  if (isSelected(tag)) {
    removeTag(tag);
    return;
  }
  addTag(tag);
};

const addCustom = () => {
  const t = String(customInput.value || '').trim();
  if (!t) return;
  addTag(t);
  customInput.value = '';
};

const clearAll = () => {
  selectedTags.value = [];
  emitChange();
};
</script>

<style scoped>
.tag-input {
  width: 100%;
}

.selected {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  align-items: center;
}

.tag {
  border-radius: 10px;
}

.add-btn {
  border-radius: 10px;
}

.picker {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.picker-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.picker-title {
  font-weight: 900;
  color: #0f172a;
}

.preset-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  max-height: 220px;
  overflow: auto;
  padding-right: 4px;
}

.preset {
  cursor: pointer;
  border-radius: 10px;
  user-select: none;
}

.custom {
  border-top: 1px solid #eef2f7;
  padding-top: 10px;
}

.custom-title {
  font-weight: 900;
  color: #0f172a;
  margin-bottom: 8px;
}

.custom-row {
  display: grid;
  grid-template-columns: 1fr 84px;
  gap: 10px;
  align-items: center;
}
</style>
