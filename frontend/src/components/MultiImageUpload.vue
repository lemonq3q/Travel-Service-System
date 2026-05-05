<template>
  <div class="multi-image-upload">
    <el-upload
      v-model:file-list="fileList"
      action="#"
      list-type="picture-card"
      :http-request="handleUpload"
      :on-preview="handlePictureCardPreview"
      :on-remove="handleRemove"
      multiple
    >
      <el-icon><Plus /></el-icon>
    </el-upload>

    <el-dialog v-model="dialogVisible">
      <img w-full :src="dialogImageUrl" alt="Preview Image" style="width: 100%" />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, watch, computed } from 'vue';
import { Plus } from '@element-plus/icons-vue';
import { upload } from '@/api/file';
import { ElMessage } from 'element-plus';

const props = defineProps({
  modelValue: {
    type: Array,
    default: () => []
  }
});

const emit = defineEmits(['update:modelValue', 'upload-state-change']);

const fileList = ref([]);
const dialogImageUrl = ref('');
const dialogVisible = ref(false);
const uploadingCount = ref(0);

// Initialize fileList from modelValue (IDs)
// Note: In a real app, you might need to fetch image URLs for existing IDs
watch(() => props.modelValue, (newVal) => {
  if (newVal && newVal.length > 0 && fileList.value.length === 0) {
    fileList.value = newVal.map(file => ({
      id: file.id,
      url: file.url, // Adjust based on your actual file download URL pattern
      status: 'success'
    }));
  }
}, { immediate: true });

const isUploading = computed(() => uploadingCount.value > 0);

watch(isUploading, (newVal) => {
  emit('upload-state-change', newVal);
});

const handleUpload = async (options) => {
  const { file, onSuccess, onError } = options;
  uploadingCount.value++;
  
  try {
    const res = await upload(file);
    if (res.data && res.data.code === 200) {
      const imageId = res.data.data.id;
      const imageUrl = res.data.data.url;
      onSuccess(res.data);
      
      // Find the file in fileList and update its ID
      const fileItem = fileList.value.find(item => item.uid === file.uid);
      if (fileItem) {
        fileItem.id = imageId;
        if (imageUrl) {
          fileItem.url = imageUrl;
        }
      }
      updateModelValue();
    } else {
      onError(new Error('上传失败'));
      ElMessage.error('图片上传失败');
    }
  } catch (err) {
    onError(err);
    ElMessage.error('图片上传出错');
  } finally {
    uploadingCount.value--;
  }
};

const handleRemove = (file) => {
  updateModelValue();
};

const handlePictureCardPreview = (uploadFile) => {
  dialogImageUrl.value = uploadFile.url;
  dialogVisible.value = true;
};

const updateModelValue = () => {
  // 把ids改成[{id: xxx}]
  const ids = fileList.value
    .filter(item => item.status === 'success' && item.id)
    .map(item => ({id: item.id}));
  emit('update:modelValue', ids);
};

defineExpose({
  isUploading
});
</script>

<style scoped>
.multi-image-upload :deep(.el-upload--picture-card) {
  width: 100px;
  height: 100px;
}
.multi-image-upload :deep(.el-upload-list--picture-card .el-upload-list__item) {
  width: 100px;
  height: 100px;
}
</style>
