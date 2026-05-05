<template>
  <div class="container_body card" style="margin-bottom: 10px;">
    <div class="search_title">查询筛选</div>
    <div class="search" style="border: 0;">
      <div class="params_container">
        <div class="param_item">
          <span class="form_title">酒店名称</span>
          <div class="form_content">
            <el-input v-model="selectParams.name" placeholder="请输入酒店名称"/>
          </div>
        </div>
        <div class="param_item">
          <span class="form_title">所在地区</span>
          <div class="form_content" style="min-width: 260px;">
            <AreaSelect v-model="selectParams.areaCode" level="area" :multiple="false" placeholder="请选择所在地区" />
          </div>
        </div>
        <div class="param_item">
          <span class="form_title">星级</span>
          <div class="form_content">
            <el-select
              style="width: 100%;"
              clearable
              v-model="selectParams.starLevel"
              placeholder="请选择酒店星级"
            >
              <el-option label="五星级" :value="5" />
              <el-option label="四星级" :value="4" />
              <el-option label="三星级" :value="3" />
              <el-option label="二星级" :value="2" />
              <el-option label="一星级" :value="1" />
            </el-select>
          </div>
        </div>
      </div>
      <div class="operation_container">
        <el-button type="primary" @click="handleAdd">添加酒店</el-button>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
      </div>
    </div>
  </div>

  <div class="container_body card">
    <div class="table_header">
      <div class="search_title">
        <span>酒店列表</span>
      </div>
    </div>
    <div class="table_container">
      <el-table
      v-loading="tableLoading" 
      :data="tableData" 
      stripe border 
      style="width: 100%;" 
      table-layout="auto"
      :header-cell-style="{ textAlign: 'center' }"
      :cell-style="{ textAlign: 'center' }">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="酒店名称" />
        <el-table-column prop="areaCode" label="所在地区" min-width="160">
          <template #default="scope">
            {{ getCascadeArea(scope.row.areaCode) || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="address" label="详细地址" />
        <el-table-column prop="rating" label="评分" width="100" />
        <el-table-column prop="starLevel" label="星级" width="100">
          <template #default="scope">
            {{ scope.row.starLevel ? scope.row.starLevel + '星' : '无' }}
          </template>
        </el-table-column>
        <el-table-column prop="checkInTime" label="入住时间" width="120" />
        <el-table-column prop="checkOutTime" label="退房时间" width="120" />
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <div class="table_button_container">
              <el-button size="small" type="primary" @click="handleEdit(scope.$index, scope.row)">修改</el-button>
              <el-button size="small" type="danger" @click="handleDelete(scope.$index, scope.row)">删除</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination_container">
        <el-pagination
          size="small"
          v-model:current-page="page.pageNum"
          v-model:page-size="page.pageSize"
          :page-sizes="[5, 10, 25, 50, 100]"
          :background="true"
          layout="total, prev, pager, next, jumper, sizes"
          :total="page.total"
          @change="handlePaginationChange"
        />
      </div>
    </div>
  </div>

</template>

<script setup>
import { reactive, ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { getHotelList, deleteHotel } from '@/api/hotel';
import { ElMessage, ElMessageBox } from 'element-plus';
import AreaSelect from './AreaSelect.vue';
import { getCascadeArea } from '@/utils/ChinaCitys';

const tableLoading = ref(false);
const router = useRouter();

const page = reactive({
  pageSize: 10,
  pageNum: 1,
  total: 0
});

const tableData = ref([]);

const selectParams = ref({
  name: '',
  areaCode: '',
  starLevel: undefined
});

const handlePaginationChange = () => {
  getData();
};

const handleEdit = (index, row) => {
  router.push({
    path: '/home/editHotel',
    query: {
      type: 'update',
      id: row.id
    }
  });
};

const handleAdd = () => {
  router.push({
    path: '/home/editHotel',
    query: {
      type: 'add',
    }
  });
}

const handleReset = () => {
  selectParams.value = {
    name: '',
    areaCode: '',
    starLevel: undefined
  };
  handleSearch();
}

const handleSearch = () => {
  page.pageNum = 1;
  getData();
}

const getData = async () => {
  tableLoading.value = true;
  try {
    const res = await getHotelList({
      ...selectParams.value,
      pageNum: page.pageNum,
      pageSize: page.pageSize
    });
    const body = res.data;
    if (body?.code === 200) {
      console.log(body);
      tableData.value = body.data.table || [];
      page.total = body.data.total || 0;
    }
  } catch (error) {
    ElMessage.error('获取酒店列表失败');
  } finally {
    tableLoading.value = false;
  }
}

const handleDelete = (index, row) => {
  console.log(index, row);
  ElMessageBox.confirm(
    '确认要删除此酒店数据?',
    '警告',
    {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning',
    }
  )
  .then(async () => {
    try {
      const res = await deleteHotel(row.id);
      if (res?.data?.code === 200) {
        ElMessage.success('删除成功');
        getData();
        return;
      }
      ElMessage.error(res?.data?.message || '删除失败');
    } catch (e) {
      ElMessage.error('删除失败');
    }
  })
  .catch(() => {});
};

onMounted(()=>{
  getData();
});
</script>

<style scoped>
.search_title {
  font-weight: bold;
  margin-bottom: 10px;
}
.params_container {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  margin-bottom: 20px;
}
.param_item {
  display: flex;
  align-items: center;
}
.form_title {
  margin-right: 10px;
  white-space: nowrap;
}
.table_header {
  margin-bottom: 10px;
}
.table_button_container {
  display: flex;
  justify-content: center;
  gap: 10px;
}
.pagination_container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
