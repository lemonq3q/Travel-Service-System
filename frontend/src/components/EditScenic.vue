<template>
  <div class="container_body card">
    <div class="page_header">
      <h2>{{ isEdit ? '修改景点信息' : '新增景点' }}</h2>
      <el-button @click="goBack">返回列表</el-button>
    </div>

    <el-form ref="formRef" :model="formData" :rules="rules" label-width="120px" class="scenic-form">
      <el-form-item label="景点名称" prop="name">
        <el-input v-model="formData.name" placeholder="请输入景点名称" />
      </el-form-item>

      <el-form-item label="所在地区" prop="areaCode">
        <AreaSelect v-model="formData.areaCode" level="district" :multiple="false" placeholder="请选择景点所在地区" />
      </el-form-item>

      <el-form-item label="详细地址" prop="address">
        <el-input v-model="formData.address" placeholder="请输入景点地址" />
      </el-form-item>

      <el-form-item label="经纬度选择" required>
        <MapSelector
          :longitude="formData.longitude"
          :latitude="formData.latitude"
          @update:coordinates="handleCoordinatesUpdate"
        />
      </el-form-item>

      <el-row :gutter="20" style="margin-top: 10px;">
        <el-col :span="12">
          <el-form-item label="经度" prop="longitude">
            <el-input-number v-model="formData.longitude" :precision="6" :step="0.000001" style="width: 100%;" disabled />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="纬度" prop="latitude">
            <el-input-number v-model="formData.latitude" :precision="6" :step="0.000001" style="width: 100%;" disabled />
          </el-form-item>
        </el-col>
      </el-row>

      <el-form-item label="景点简介" prop="scenicDesc">
        <el-input type="textarea" v-model="formData.scenicDesc" :rows="3" placeholder="请输入景点简介" />
      </el-form-item>

      <el-form-item label="开放时间" prop="openTimeDesc">
        <el-input v-model="formData.openTimeDesc" placeholder="例如：09:00-17:30 / 全天开放" />
      </el-form-item>

      <el-form-item label="是否需要门票" prop="needTicket">
        <el-radio-group v-model="formData.needTicket" @change="handleNeedTicketChange">
          <el-radio :value="0">不需要</el-radio>
          <el-radio :value="1">需要</el-radio>
        </el-radio-group>
      </el-form-item>

      <el-form-item label="门票价格" prop="price">
        <el-input-number v-model="formData.price" :precision="2" :step="10" :min="0" :disabled="formData.needTicket !== 1" />
      </el-form-item>

      <el-form-item label="景点图片">
        <MultiImageUpload v-model="formData.scenicImgList" @upload-state-change="handleUploadStateChange" />
      </el-form-item>

      <div class="form-footer">
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">保 存</el-button>
        <el-button @click="goBack">取 消</el-button>
      </div>
    </el-form>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import AreaSelect from './AreaSelect.vue';
import MapSelector from './MapSelector.vue';
import MultiImageUpload from './MultiImageUpload.vue';
import Loading from '@/utils/loading';
import Message from '@/utils/message';
import { addScenic, getScenicDetail, updateScenic } from '@/api/scenic';

const route = useRoute();
const router = useRouter();

const isEdit = ref(false);
const submitLoading = ref(false);
const formRef = ref(null);
const uploadingCounter = ref(0);

const formData = reactive({
  id: undefined,
  name: '',
  address: '',
  areaCode: '',
  longitude: 116.397428,
  latitude: 39.90923,
  scenicDesc: '',
  openTimeDesc: '',
  needTicket: 0,
  price: 0,
  scenicImgList: []
});

const validatePrice = (rule, value, callback) => {
  if (formData.needTicket === 1) {
    if (value === null || value === undefined || value === '') {
      callback(new Error('请输入门票价格'));
      return;
    }
    if (Number(value) <= 0) {
      callback(new Error('门票价格需大于0'));
      return;
    }
  }
  callback();
};

const rules = {
  name: [{ required: true, message: '请输入景点名称', trigger: 'blur' }],
  address: [{ required: true, message: '请输入景点地址', trigger: 'blur' }],
  areaCode: [{ required: true, message: '请选择景点所在地区', trigger: 'change' }],
  openTimeDesc: [{ required: true, message: '请输入开放时间描述', trigger: 'blur' }],
  needTicket: [{ required: true, message: '请选择是否需要门票', trigger: 'change' }],
  price: [{ validator: validatePrice, trigger: 'change' }]
};

const goBack = () => {
  router.push('/home/scenicManagement');
};

const handleCoordinatesUpdate = (coords) => {
  formData.longitude = coords.lng;
  formData.latitude = coords.lat;
};

const handleUploadStateChange = (state) => {
  if (state) {
    uploadingCounter.value += 1;
  } else {
    uploadingCounter.value -= 1;
    if (uploadingCounter.value < 0) uploadingCounter.value = 0;
  }
};

const handleNeedTicketChange = (val) => {
  if (val !== 1) {
    formData.price = 0;
  }
};

const waitForUploads = async () => {
  if (uploadingCounter.value <= 0) return;
  Loading.open();
  await new Promise(resolve => {
    const timer = setInterval(() => {
      if (uploadingCounter.value <= 0) {
        clearInterval(timer);
        resolve();
      }
    }, 200);
  });
  Loading.close();
};

const loadDetail = async (id) => {
  Loading.open();
  try {
    const res = await getScenicDetail(id);
    const body = res.data;
    if (body.code === 200) {
      Object.assign(formData, body.data);
      if (formData.needTicket !== 1) {
        formData.price = 0;
      }
      console.log(formData);
    }
  } finally {
    Loading.close();
  }
};

const handleSubmit = async () => {
  if (!formRef.value) return;
  await waitForUploads();

  formRef.value.validate(async (valid) => {
    if (!valid) return;
    submitLoading.value = true;
    Loading.open();
    try {
      const payload = JSON.parse(JSON.stringify(formData));
      if (payload.needTicket !== 1) {
        payload.price = 0;
      }

      let res;
      if (isEdit.value) {
        console.log(payload);
        res = await updateScenic(payload);
      } else {
        res = await addScenic(payload);
      }
      const body = res.data;
      if (body.code === 200) {
        Message.success('保存成功');
        goBack();
      }
    } finally {
      submitLoading.value = false;
      Loading.close();
    }
  });
};

onMounted(() => {
  if (route.query.type === 'update' && route.query.id) {
    isEdit.value = true;
    loadDetail(route.query.id);
  }
});
</script>

<style scoped>
.page_header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  border-bottom: 1px solid #ebeef5;
  padding-bottom: 10px;
}
.page_header h2 {
  margin: 0;
  font-size: 18px;
}
.scenic-form {
  max-width: 1000px;
  margin: 0 auto;
}
.form-footer {
  margin-top: 30px;
  text-align: center;
}
</style>

