<template>
  <div class="grid" v-if="images.length">
    <div class="big" @click="openDialog(0)">
      <el-image class="img" :src="images[0]" fit="cover" />
    </div>
    <div class="small">
      <div
        v-for="(src, idx) in smallImages"
        :key="src"
        class="small-item"
        @click="openDialog(idx + 1)"
      >
        <el-image class="img" :src="src" fit="cover" />
        <div v-if="idx === smallImages.length - 1" class="overlay">
          查看全部 ({{ images.length }})
        </div>
      </div>
    </div>
  </div>

  <el-dialog v-model="dialogVisible" width="980px" align-center>
    <template #header>
      <div class="dialog-title">全部照片</div>
    </template>
    <div class="dialog-grid">
      <el-image
        v-for="(src, idx) in images"
        :key="src"
        class="dialog-img"
        :src="src"
        fit="cover"
        :preview-src-list="images"
        :initial-index="idx"
        preview-teleported
      />
    </div>
  </el-dialog>
</template>

<script setup>
import { computed, defineProps, ref } from 'vue';

const props = defineProps({
  images: {
    type: Array,
    default: () => []
  }
});

const dialogVisible = ref(false);

const images = computed(() => (Array.isArray(props.images) ? props.images : []).filter(Boolean));
const shownImages = computed(() => images.value.slice(0, 7));
const smallImages = computed(() => shownImages.value.slice(1, 7));

const openDialog = () => {
  dialogVisible.value = true;
};
</script>

<style scoped>
.grid {
  width: 100%;
  display: grid;
  grid-template-columns: 3fr 2fr;
  gap: 12px;
}

.big {
  height: 320px;
  border-radius: 14px;
  overflow: hidden;
  cursor: pointer;
}

.small {
  height: 320px;
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  grid-template-rows: repeat(2, 1fr);
  gap: 12px;
}

.small-item {
  position: relative;
  border-radius: 14px;
  overflow: hidden;
  cursor: pointer;
}

.img {
  width: 100%;
  height: 100%;
}

.overlay {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(15, 23, 42, 0.58);
  color: #fff;
  font-weight: 700;
  font-size: 14px;
}

.dialog-title {
  font-weight: 700;
  color: #0f172a;
}

.dialog-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
}

.dialog-img {
  width: 100%;
  height: 140px;
  border-radius: 12px;
  overflow: hidden;
}

@media (max-width: 920px) {
  .grid {
    grid-template-columns: 1fr;
  }
  .small {
    height: auto;
    grid-template-columns: 1fr 1fr;
  }
  .small-item {
    height: 120px;
  }
  .dialog-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
