<template>
  <div class="container_body card" style="margin-bottom: 10px;">
    <div class="search_title">查询筛选</div>
    <div class="search" style="border: 0;">
      <div class="params_container">
        <div class="param_item">
          <span class="form_title">景点名称</span>
          <div class="form_content">
            <el-input v-model="selectParams.name" placeholder="请输入景点名称" />
          </div>
        </div>
        <div class="param_item">
          <span class="form_title">所在地区</span>
          <div class="form_content">
            <AreaSelect v-model="selectParams.areaCode" level="district" :multiple="false" placeholder="请选择地区" />
          </div>
        </div>
        <div class="radio_param_item">
          <span class="radio_form_title">门票</span>
          <div class="radio_form_content">
            <el-radio-group v-model="selectParams.needTicket">
              <el-radio value="">全部</el-radio>
              <el-radio :value="1">需要</el-radio>
              <el-radio :value="0">不需要</el-radio>
            </el-radio-group>
          </div>
        </div>
      </div>
      <div class="operation_container">
        <el-button type="primary" @click="handleAdd">新增景点</el-button>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
      </div>
    </div>
  </div>

  <div class="container_body card">
    <div class="table_header">
      <div class="search_title">
        <span>查询结果</span>
      </div>
    </div>
    <div class="table_container">
      <el-table
        v-loading="tableLoading"
        :data="tableData"
        stripe
        border
        style="width: 100%;"
        table-layout="auto"
        :header-cell-style="{ textAlign: 'center' }"
        :cell-style="{ textAlign: 'center' }"
      >
        <el-table-column prop="id" label="ID" width="90" />
        <el-table-column prop="name" label="景点名称" min-width="160" />
        <el-table-column prop="address" label="地址" min-width="220" />
        <el-table-column prop="areaName" label="地区" min-width="180" />
        <el-table-column prop="rating" label="评分" width="90" />
        <el-table-column prop="hotValue" label="热度" width="90" />
        <el-table-column label="门票" width="90">
          <template #default="scope">
            <el-tag :type="scope.row.needTicket === 1 ? 'warning' : 'success'">
              {{ scope.row.needTicket === 1 ? '需要' : '不需要' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="票价" width="110">
          <template #default="scope">
            {{ scope.row.needTicket === 1 ? (scope.row.price ?? 0) : '-' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <div class="table_button_container">
              <el-button size="small" type="primary" @click="handleEdit(scope.$index, scope.row)">编辑</el-button>
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
import { ElMessageBox } from 'element-plus';
import Message from '@/utils/message';
import { getCascadeArea } from '@/utils/ChinaCitys';
import AreaSelect from './AreaSelect.vue';
import { deleteScenic, getScenicList } from '@/api/scenic';

const router = useRouter();
const tableLoading = ref(false);

const page = reactive({
  pageSize: 10,
  pageNum: 1,
  total: 0
});

const tableData = ref([]);

const selectParams = ref({
  name: '',
  areaCode: '',
  needTicket: ''
});

const handlePaginationChange = () => {
  getData();
};

const handleEdit = (index, row) => {
  router.push({
    path: '/home/editScenic',
    query: {
      type: 'update',
      id: row.id
    }
  });
};

const handleAdd = () => {
  router.push({
    path: '/home/editScenic',
    query: {
      type: 'add'
    }
  });
};

const handleReset = () => {
  selectParams.value = {
    name: '',
    areaCode: '',
    needTicket: ''
  };
  handleSearch();
};

const handleSearch = () => {
  page.pageNum = 1;
  getData();
};

const buildTableData = (records) => {
  tableData.value = (records || []).map(item => ({
    ...item,
    areaName: item.areaCode ? getCascadeArea(item.areaCode) : ''
  }));
};

const getData = async () => {
  try {
    tableLoading.value = true;
    const params = {
      ...selectParams.value,
      pageNum: page.pageNum,
      pageSize: page.pageSize
    };
    // 如果needTicket是空字符串，说明不查询门票状态
    if (params.needTicket === '') {
      delete params.needTicket;
    }
    console.log(params);
    const res = await getScenicList(params);
    const body = res.data;
    if (body.code === 200) {
      buildTableData(body.data.table);
      page.total = body.data.total;
    }
  } finally {
    tableLoading.value = false;
  }
};

const handleDelete = (index, row) => {
  ElMessageBox.confirm('确认要删除此数据?', '警告', {
    confirmButtonText: '确认',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(async () => {
      const res = await deleteScenic(row.id);
      const body = res.data;
      if (body.code === 200) {
        Message.success('删除成功');
        getData();
      }
    })
    .catch(() => {});
};

onMounted(() => {
  getData();
});
</script>

<style scoped>
</style>

