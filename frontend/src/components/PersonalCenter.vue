<template>
  <div class="pc-wrap">
    <el-card class="pc-card pc-profile" shadow="never">
      <div class="pc-profile-top">
        <div class="pc-avatar">
            <img src="../assets/user.png" alt="用户头像" style="width: 90px; height: 90px;">
        </div>

        <div class="pc-meta">
          <div class="pc-name">{{ userInfo.name || '未设置姓名' }}</div>
          <div class="pc-sub">账号：{{ userInfo.phone || '-' }}</div>
          <div class="pc-tags">
            <el-tag :type="userInfo.status === '正常' ? 'success' : 'danger'" effect="light">{{ userInfo.status || '-' }}</el-tag>
            <el-tag type="info" effect="plain">{{ userInfo.role || '未知角色' }}</el-tag>
          </div>
        </div>

        <div class="pc-actions">
          <el-button type="primary" @click="showInfoDialog = true">
            <el-icon><Edit /></el-icon>
            修改基本信息
          </el-button>
        </div>
      </div>

      <div class="pc-grid">
        <div class="pc-field">
          <div class="pc-label">邮箱</div>
          <div class="pc-value">{{ userInfo.email || '-' }}</div>
        </div>
        <div class="pc-field">
          <div class="pc-label">创建时间</div>
          <div class="pc-value">{{ userInfo.createTime || '-' }}</div>
        </div>
      </div>
      <!-- <div class="pc-tip">系统默认头像，暂不支持上传</div> -->
    </el-card>

    <!-- 修改密码卡片 -->
    <el-card class="pc-card pc-pwd" shadow="hover">
      <div class="pc-pwd-row">
        <div class="pc-pwd-left">
          <div class="pc-pwd-title">
            <el-icon><Lock /></el-icon>
            密码管理
          </div>
          <div class="pc-pwd-desc">建议定期修改密码，密码长度 6–16 位</div>
        </div>
        <div class="pc-pwd-right">
          <el-button type="primary" plain @click="showPwdDialog = true">修改密码</el-button>
        </div>
      </div>
    </el-card>

    <!-- 修改基本信息弹窗 -->
    <el-dialog
      title="修改基本信息"
      v-model="showInfoDialog"
      width="800px"
      @close="resetInfoForm"
    >
      <el-form
        ref="infoFormRef"
        :model="editInfoForm"
        label-width="100px"
        :rules="infoRules"
        class="info-form"
      >
        <el-row>
          <el-col :span="12">
            <el-form-item label="用户姓名" prop="name">
              <el-input v-model="editInfoForm.name" placeholder="请输入姓名"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="手机号码" prop="username">
              <el-input v-model="editInfoForm.username" placeholder="请输入手机号"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row>
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="editInfoForm.email" placeholder="请输入邮箱"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="用户角色">
              <el-input v-model="editInfoForm.role" disabled placeholder="系统分配，不可修改"></el-input>
            </el-form-item>
          </el-col>
        </el-row>

      </el-form>
      <template #footer>
        <el-button @click="showInfoDialog = false">取消</el-button>
        <el-button type="primary" @click="saveInfo(infoFormRef)">保存</el-button>
      </template>
    </el-dialog>

    <!-- 修改密码弹窗 -->
    <el-dialog
      title="修改密码"
      v-model="showPwdDialog"
      width="600px"
      @close="resetPwdForm"
    >
      <el-form
        ref="pwdFormRef"
        :model="pwdForm"
        label-width="100px"
        :rules="pwdRules"
        class="pwd-form"
      >
        <!-- 骗浏览器把用户密码填到这里来 -->
        <div style="position:absolute;left:-9999px;top:-9999px;z-index:-1;">
          <input type="text" name="fake-username" autocomplete="username">
          <input type="password" name="fake-password" autocomplete="password">
        </div>
        <el-form-item label="新密码" prop="password">
          <el-input v-model="pwdForm.password" type="password" show-password placeholder="请输入新密码"></el-input>
        </el-form-item>

        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="pwdForm.confirmPassword" type="password" show-password placeholder="请再次输入新密码"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showPwdDialog = false">取消</el-button>
        <el-button type="primary" @click="changePassword(pwdFormRef)">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { Edit, Lock } from '@element-plus/icons-vue';
import { selectPersonalUser, updatePassword, updateUser} from '@/api/user';
import { formatSecondTimestamp } from '@/utils/time';
import Message from '@/utils/message';
import { validatePhoneNumber, vaildateEmail } from '@/utils/validate';
import Loading from '@/utils/loading';

// 弹窗控制
const showInfoDialog = ref(false);
const showPwdDialog = ref(false);

// 表单引用
const infoFormRef = ref(null);
const pwdFormRef = ref(null);

// 原始用户信息（展示用）
const userInfo = reactive({
  name: '',
  // gender: undefined,
  // age: undefined,
  // idCard: '',
  // createTime: null,
  phone: '',
  status: '',
  role: '', // 不可修改的字段
});

// 编辑用表单数据
const editInfoForm = reactive({});

const oriInfo = ref({});

// 修改密码表单
const pwdForm = reactive({
  password: '',
  confirmPassword: ''
});

// 信息校验规则
const infoRules = reactive({
  name: [
    { required: true, message: '请输入用户姓名', trigger: 'blur' },
    { min: 1, max: 45, message: '输入内容过长', trigger: 'blur' }
  ],
  email: [
    { min: 0, max: 95, message: '输入内容过长', trigger: 'blur' },
    { validator: vaildateEmail, trigger: 'blur'}
  ],
  username: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { validator: validatePhoneNumber, trigger: 'blur' }
  ],
});

// 密码校验规则
const pwdRules = reactive({
  password: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 16, message: '密码长度需要在6~16位之间', trigger: 'blur' }
  ],
  confirmPassword: [
    {
      validator: (rule, value, callback) => {
        if (value !== pwdForm.password) {
          callback(new Error('两次输入的密码不一致'));
        } else {
          callback();
        }
      },
      trigger: 'blur'
    }
  ]
});

// 生命周期 - 挂载时初始化编辑表单
onMounted(() => {
  getPersonalUser();
});


const getPersonalUser = async () => {
  try{
    Loading.open();
    let res = await selectPersonalUser();
    res = res.data;
    if (res.code === 200) {
      oriInfo.value = res.data;
      buildInfo();
      resetInfoForm();
    }
  }
  finally{
    Loading.close();
  }
};

const buildInfo = () => {
  userInfo.name = oriInfo.value.name;
  // userInfo.gender = oriInfo.value.gender == 1 ? '男' : '女';
  // userInfo.age = oriInfo.value.age;
  userInfo.phone = oriInfo.value.username;
  // userInfo.idCard = oriInfo.value.idNum;
  userInfo.status = oriInfo.value.status == 1 ? '正常' : '禁用';
  userInfo.role = oriInfo.value.roleName;
  userInfo.email = oriInfo.value.email;
  userInfo.createTime = formatSecondTimestamp(oriInfo.value.createTime);
}

// 重置编辑表单（同步原始数据）
const resetInfoForm = () => {
  if (infoFormRef.value) {
    infoFormRef.value.resetFields();
  }
  editInfoForm.name = oriInfo.value.name;
  // editInfoForm.gender = String(oriInfo.value.gender);
  // editInfoForm.age = oriInfo.value.age;
  editInfoForm.username = oriInfo.value.username;
  editInfoForm.email = oriInfo.value.email;
  // editInfoForm.idNum = oriInfo.value.idNum;
  editInfoForm.status = oriInfo.value.status == 1 ? '正常' : '禁用';
  editInfoForm.role = oriInfo.value.roleName;
};

const buildInsertData = () => {
  return {
    id: oriInfo.value.id,
    name: editInfoForm.name,
    username: editInfoForm.username,
    email: editInfoForm.email,
  };
};

// 保存个人信息
const saveInfo = async (formEl) => {
  if (!formEl) return;
  formEl.validate(async (valid) => {
    if(valid){
      try{
        Loading.open();
        let data = buildInsertData();
        await updateUser(data).then(res => {
          res = res.data;
          if (res.code == 200){
            Message.success("更新成功");
            getPersonalUser();
          }
        });
      }
      finally{
        Loading.close();
      }
    }
  });
};

// 重置密码表单
const resetPwdForm = () => {
  if (pwdFormRef.value) {
    pwdFormRef.value.resetFields();
  }
  pwdForm.password = '';
  pwdForm.confirmPassword = '';
};

// 修改密码
const changePassword = async (formEl) => {
  if (!formEl) return;
  formEl.validate(async (valid) => {
    if(valid){
      try{
        Loading.open();
        let user = {
          id: oriInfo.value.id,
          password: pwdForm.password
        }
        await updatePassword(user).then(res => {
          res = res.data;
          if (res.code == 200){
            Message.success('密码修改成功');
          }
        });
      }
      finally{
        Loading.close();
      }
    }
  });
};
</script>

<style scoped>
.pc-wrap {
  padding: 18px;
  background: radial-gradient(1200px 220px at 20% 0%, rgba(59, 130, 246, 0.18) 0%, rgba(255, 255, 255, 0) 70%),
    radial-gradient(900px 220px at 80% 20%, rgba(34, 197, 94, 0.12) 0%, rgba(255, 255, 255, 0) 65%);
  border-radius: 14px;
}

.pc-card {
  border: 1px solid #eef2f7;
  border-radius: 14px;
}

.pc-profile {
  margin-bottom: 16px;
}

.pc-profile-top {
  display: flex;
  align-items: center;
  gap: 16px;
}

.pc-avatar-img {
  border: 3px solid rgba(59, 130, 246, 0.25);
}

.pc-meta {
  flex: 1;
  min-width: 0;
}

.pc-name {
  font-size: 18px;
  font-weight: 700;
  color: #0f172a;
  line-height: 1.2;
}

.pc-sub {
  margin-top: 6px;
  font-size: 13px;
  color: #64748b;
}

.pc-tags {
  margin-top: 10px;
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.pc-actions {
  flex: none;
}

.pc-grid {
  margin-top: 16px;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.pc-field {
  padding: 12px 14px;
  background: #f8fafc;
  border: 1px solid #eef2f7;
  border-radius: 12px;
}

.pc-label {
  font-size: 12px;
  color: #64748b;
}

.pc-value {
  margin-top: 6px;
  font-size: 14px;
  font-weight: 600;
  color: #0f172a;
  word-break: break-all;
}

.pc-tip {
  margin-top: 12px;
  font-size: 12px;
  color: #94a3b8;
}

.pc-pwd-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

.pc-pwd-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 700;
  color: #0f172a;
}

.pc-pwd-desc {
  margin-top: 6px;
  font-size: 13px;
  color: #64748b;
}

.info-form, .pwd-form {
  width: 100%;
  margin-top: 10px;
}
</style>
