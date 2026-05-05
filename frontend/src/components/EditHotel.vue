<template>
  <div class="container_body card">
    <div class="page_header">
      <h2>{{ isEdit ? '修改酒店信息' : '新增酒店' }}</h2>
      <el-button @click="goBack">返回列表</el-button>
    </div>

    <el-form ref="formRef" :model="formData" :rules="rules" label-width="120px" class="hotel-form">
      <el-tabs v-model="activeTab" type="border-card">
        <!-- 酒店基本信息 -->
        <el-tab-pane label="基本信息" name="basic">
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="酒店名称" prop="name">
                <el-input v-model="formData.name" placeholder="请输入酒店名称" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="所在地区" prop="areaCode">
                <AreaSelect v-model="formData.areaCode" level="district" :multiple="false" placeholder="请选择酒店所在地区" />
              </el-form-item>
            </el-col>
          </el-row>
          
          <el-form-item label="详细地址" prop="address">
            <el-input v-model="formData.address" placeholder="请输入详细地址" />
          </el-form-item>

          <el-form-item label="经纬度选择" required>
            <MapSelector ref="mapSelectorRef"
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

          <el-form-item label="酒店简介" prop="hotelDesc">
            <el-input type="textarea" v-model="formData.hotelDesc" :rows="3" placeholder="请输入酒店简介" />
          </el-form-item>

          <el-form-item label="酒店特色(Tag)" prop="featureJson">
            <TagInput v-model="formData.featureJson" :presets="HOTEL_FEATURE_TAGS" placeholder="选择或输入酒店特色" />
          </el-form-item>

          <el-form-item label="酒店设施(Tag)" prop="facilityJson">
            <TagInput v-model="formData.facilityJson" :presets="HOTEL_FACILITY_TAGS" placeholder="选择或输入酒店设施" />
          </el-form-item>

          <el-form-item label="酒店服务(Tag)" prop="serviceJson">
            <TagInput v-model="formData.serviceJson" :presets="HOTEL_SERVICE_TAGS" placeholder="选择或输入酒店服务" />
          </el-form-item>

          <el-form-item label="酒店图片">
            <MultiImageUpload v-model="formData.hotelImgList" @upload-state-change="handleUploadStateChange" />
          </el-form-item>

          <el-row :gutter="20">
            <el-col :span="8">
              <el-form-item label="星级" prop="starLevel">
                <el-rate v-model="formData.starLevel" />
              </el-form-item>
            </el-col>
            <!-- <el-col :span="8">
              <el-form-item label="评分" prop="rating">
                <el-input-number v-model="formData.rating" :precision="1" :step="0.1" :max="5" :min="0" />
              </el-form-item>
            </el-col> -->
          </el-row>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="入住时间" prop="checkInTime">
                <el-time-picker v-model="formData.checkInTime" format="HH:mm:ss" value-format="HH:mm:ss" placeholder="请选择入住时间" style="width: 100%;" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="退房时间" prop="checkOutTime">
                <el-time-picker v-model="formData.checkOutTime" format="HH:mm:ss" value-format="HH:mm:ss" placeholder="请选择退房时间" style="width: 100%;" />
              </el-form-item>
            </el-col>
          </el-row>
        </el-tab-pane>

        <!-- 房间与套餐信息 -->
        <el-tab-pane label="房间与套餐设置" name="rooms">
          <div v-for="(room, roomIndex) in formData.hotelRoomList" :key="roomIndex" class="room-card">
            <div class="room-header">
              <h4>房间 {{ roomIndex + 1 }}</h4>
              <el-button type="danger" size="small" @click="removeRoom(roomIndex)">删除房间</el-button>
            </div>
            
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item :label="'房间名称'" :prop="'hotelRoomList.' + roomIndex + '.name'" :rules="{ required: true, message: '请输入房间名称', trigger: 'blur' }">
                  <el-input v-model="room.name" placeholder="例如：豪华大床房" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="房间总数">
                  <el-input-number v-model="room.totalRoom" :min="0" />
                </el-form-item>
              </el-col>
            </el-row>

            <el-row :gutter="20">
              <el-col :span="8">
                <el-form-item label="床铺数量">
                  <el-input-number v-model="room.bedCount" :min="1" />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="床铺尺寸(m)">
                  <el-input-number v-model="room.bedSize" :precision="2" :step="0.1" :min="1.0" />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="最多入住人数">
                  <el-input-number v-model="room.maxPeople" :min="1" />
                </el-form-item>
              </el-col>
            </el-row>

            <el-form-item label="房间特色(Tag)">
              <TagInput v-model="room.featureJson" :presets="ROOM_FEATURE_TAGS" placeholder="选择或输入房间特色" />
            </el-form-item>

            <el-form-item label="房间简介">
              <el-input type="textarea" v-model="room.roomDesc" :rows="2" placeholder="请输入房间简介" />
            </el-form-item>

            <el-form-item label="房间图片">
              <MultiImageUpload v-model="room.roomImgList" @upload-state-change="handleUploadStateChange" />
            </el-form-item>

            <!-- 房型套餐 -->
            <div class="package-section">
              <h5>房型套餐 (Room Type Price)</h5>
              <el-table :data="room.roomTypePriceList" border size="small">
                <el-table-column label="套餐内容(Tag)" min-width="300">
                  <template #default="scope">
                    <TagInput v-model="scope.row.summaryJson" :presets="PACKAGE_CONTENT_TAGS" placeholder="选择或输入套餐内容" />
                  </template>
                </el-table-column>
                <el-table-column label="价格(元)" width="150">
                  <template #default="scope">
                    <el-input-number v-model="scope.row.price" :precision="2" :step="10" :min="0" style="width: 100%;" />
                  </template>
                </el-table-column>
                <el-table-column label="操作" width="100" align="center">
                  <template #default="scope">
                    <el-button type="danger" size="small" @click="removePackage(roomIndex, scope.$index)">删除</el-button>
                  </template>
                </el-table-column>
              </el-table>
              <div style="margin-top: 10px;">
                <el-button type="primary" size="small" plain @click="addPackage(roomIndex)">+ 新增套餐</el-button>
              </div>
            </div>
          </div>

          <div style="margin-top: 20px; text-align: center;">
            <el-button type="success" @click="addRoom">+ 新增房间</el-button>
          </div>
        </el-tab-pane>
      </el-tabs>

      <div class="form-footer">
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">保 存</el-button>
        <el-button @click="goBack">取 消</el-button>
      </div>
    </el-form>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import TagInput from './TagInput.vue';
import MapSelector from './MapSelector.vue';
import MultiImageUpload from './MultiImageUpload.vue';
import AreaSelect from './AreaSelect.vue';
import { addHotel, updateHotel, getHotelDetail } from '@/api/hotel';
import {
  HOTEL_FACILITY_TAGS,
  HOTEL_FEATURE_TAGS,
  HOTEL_SERVICE_TAGS,
  PACKAGE_CONTENT_TAGS,
  ROOM_FEATURE_TAGS
} from '@/constants/presetTags';
const mapSelectorRef = ref(null);

const route = useRoute();
const router = useRouter();

const isEdit = ref(false);
const activeTab = ref('basic');
const submitLoading = ref(false);
const isUploadingAny = ref(0); // 改为计数器
const formRef = ref(null);

const formData = reactive({
  id: undefined,
  name: '',
  address: '',
  areaCode: '',
  longitude: 116.397428,
  latitude: 39.90923,
  hotelDesc: '',
  featureJson: '[]',
  facilityJson: '[]',
  serviceJson: '[]',
  checkInTime: '14:00:00',
  checkOutTime: '12:00:00',
  starLevel: 5,
  coverImg: null,
  hotelImgList: [], // 酒店图片列表
  hotelRoomList: []
});

const handleUploadStateChange = (state) => {
  if (state) {
    isUploadingAny.value++;
  } else {
    isUploadingAny.value--;
  }
};

const rules = {
  name: [{ required: true, message: '请输入酒店名称', trigger: 'blur' }],
  address: [{ required: true, message: '请输入详细地址', trigger: 'blur' }],
  areaCode: [{ required: true, message: '请选择酒店所在地区', trigger: 'change' }],
  checkInTime: [{ required: true, message: '请选择入住时间', trigger: 'change' }],
  checkOutTime: [{ required: true, message: '请选择退房时间', trigger: 'change' }]
};

const goBack = () => {
  router.push('/home/hotelManagement');
};

const handleCoordinatesUpdate = (coords) => {
  formData.longitude = coords.lng;
  formData.latitude = coords.lat;
};

const addRoom = () => {
  formData.hotelRoomList.push({
    name: '',
    roomDesc: '',
    featureJson: '[]',
    bedCount: 1,
    bedSize: 1.5,
    maxPeople: 2,
    totalRoom: 10,
    roomImgList: [], // 房间图片列表
    roomTypePriceList: [
      { summaryJson: '[]', price: 100.00 }
    ]
  });
};

const removeRoom = (index) => {
  formData.hotelRoomList.splice(index, 1);
};

const addPackage = (roomIndex) => {
  formData.hotelRoomList[roomIndex].roomTypePriceList.push({
    summaryJson: '[]',
    price: 100.00
  });
};

const removePackage = (roomIndex, packageIndex) => {
  formData.hotelRoomList[roomIndex].roomTypePriceList.splice(packageIndex, 1);
};

const loadDetail = async (id) => {
  try {
    const res = await getHotelDetail(id);
    if (res?.data?.code === 200) {
      const detail = res.data.data;
      if (detail) {
        Object.assign(formData, detail);
        formData.hotelImgList = formData.hotelImgList ?? [];
        formData.hotelRoomList = formData.hotelRoomList ?? [];
        formData.hotelRoomList.forEach(room => {
          room.roomImgList = room.roomImgList ?? [];
        });
        console.log(formData);
        // 初始化地图选择器
        mapSelectorRef.value.setCoordinates(formData.longitude, formData.latitude);
      }
    }
  } catch (e) {
    console.log(e);
    ElMessage.error('获取酒店详情失败');
  }
};

onMounted(() => {
  if (route.query.type === 'update' && route.query.id) {
    isEdit.value = true;
    loadDetail(route.query.id);
  } else {
    // 默认给一个房间
    addRoom();
  }
});

const handleSubmit = async () => {
  if (!formRef.value) return;
  
  // 等待图片上传完毕
  if (isUploadingAny.value > 0) {
    ElMessage.info('图片正在上传中，请稍候...');
    // 使用简单的等待机制或在提交前再次检查
    const checkUpload = setInterval(async () => {
      if (isUploadingAny.value <= 0) {
        clearInterval(checkUpload);
        await performSubmit();
      }
    }, 500);
    return;
  }
  
  await performSubmit();
};

const performSubmit = async () => {
  await formRef.value.validate(async (valid, fields) => {
    if (valid) {
      submitLoading.value = true;
      try {
        // 验证房间是否为空
        if (formData.hotelRoomList.length === 0) {
          ElMessage.warning('请至少添加一个房间信息');
          submitLoading.value = false;
          return;
        }

        const payload = JSON.parse(JSON.stringify(formData));
        // 若hotelImgList不为空，取第一张图的id为封面
        if (payload.hotelImgList.length > 0) {
          payload.coverImg = payload.hotelImgList[0].id;
        }
        console.log(payload);
        const res = isEdit.value ? await updateHotel(payload) : await addHotel(payload);
        if (res?.data?.code === 200) {
          ElMessage.success('保存成功');
          goBack();
          return;
        }
        ElMessage.error(res?.data?.message || '保存失败');

      } catch (error) {
        ElMessage.error('保存失败');
      } finally {
        submitLoading.value = false;
      }
    } else {
      ElMessage.warning('请完善表单信息');
    }
  });
};
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
.hotel-form {
  max-width: 1000px;
  margin: 0 auto;
}
.room-card {
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  padding: 15px;
  margin-bottom: 15px;
  background-color: #fafafa;
}
.room-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  border-bottom: 1px dashed #dcdfe6;
  padding-bottom: 10px;
}
.room-header h4 {
  margin: 0;
  color: #409eff;
}
.package-section {
  margin-top: 15px;
  padding: 10px;
  background-color: #fff;
  border-radius: 4px;
}
.package-section h5 {
  margin-top: 0;
  margin-bottom: 10px;
}
.form-footer {
  margin-top: 30px;
  text-align: center;
}
</style>
