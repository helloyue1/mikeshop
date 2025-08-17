<template>
  <div class="jd-edit-profile">
    <!-- 京东风格头部 -->
    <div class="jd-header">
      <div class="jd-header-content">
        <div class="jd-back-btn" @click="goBack">
          <i class="bi bi-arrow-left"></i>
        </div>
        <h1 class="jd-page-title">编辑资料</h1>
      </div>
    </div>

    <!-- 头像上传区域 -->
    <div class="jd-avatar-section">
      <div class="jd-avatar-wrapper">
        <img :src="user.avatar || '/images/登录前.png'" alt="用户头像" class="jd-avatar-img">
        <div class="jd-avatar-overlay">
          <input type="file" id="avatar-upload" class="jd-avatar-upload-input" @change="handleAvatarUpload">
          <label for="avatar-upload" class="jd-change-avatar-btn">更换头像</label>
        </div>
      </div>
    </div>

    <!-- 表单区域 -->
    <form class="jd-profile-form" @submit.prevent="saveProfile">
      <div class="jd-form-group">
        <label for="nickname" class="jd-form-label">
          <img src="/images/我的账户.png" alt="昵称图标" class="jd-form-label-icon">
          昵称
        </label>
        <input type="text" id="nickname" v-model="user.nickname" class="jd-form-input" placeholder="请输入昵称">
      </div>

      <div class="jd-form-group">
        <label for="email" class="jd-form-label">
          <img src="/images/地址管理.png" alt="邮箱图标" class="jd-form-label-icon">
          电子邮箱
        </label>
        <input type="email" id="email" v-model="user.email" class="jd-form-input" placeholder="请输入电子邮箱">
      </div>

      <div class="jd-form-group">
        <label for="phone" class="jd-form-label">
          <img src="/images/设置.png" alt="手机图标" class="jd-form-label-icon">
          手机号码
        </label>
        <input type="tel" id="phone" v-model="user.phone" class="jd-form-input" placeholder="请输入手机号码">
      </div>

      <div class="jd-form-group">
        <label for="gender" class="jd-form-label">
          <img src="/images/我的会员.png" alt="性别图标" class="jd-form-label-icon">
          性别
        </label>
        <div class="jd-gender-options">
          <label class="jd-gender-option">
            <input type="radio" name="gender" value="male" v-model="user.gender">
            <span>男</span>
          </label>
          <label class="jd-gender-option">
            <input type="radio" name="gender" value="female" v-model="user.gender">
            <span>女</span>
          </label>
          <label class="jd-gender-option">
            <input type="radio" name="gender" value="other" v-model="user.gender">
            <span>保密</span>
          </label>
        </div>
      </div>

      <div class="jd-form-group">
        <label for="birthdate" class="jd-form-label">
          <img src="/images/登录前.png" alt="生日图标" class="jd-form-label-icon">
          出生日期
        </label>
        <input type="date" id="birthdate" v-model="user.birthdate" class="jd-form-input">
      </div>

      <div class="jd-form-group">
        <label for="signature" class="jd-form-label">
          <img src="/images/配饰.png" alt="签名图标" class="jd-form-label-icon">
          个性签名
        </label>
        <textarea id="signature" v-model="user.signature" class="jd-form-textarea" placeholder="请输入个性签名" maxlength="100"></textarea>
      </div>

      <button type="submit" class="jd-save-btn">保存修改</button>
    </form>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import apiService from '../services/apiService';

const router = useRouter();

// 初始化用户状态
const user = ref<{
  isLoggedIn: boolean;
  id?: number;
  username?: string;
  nickname: string;
  avatar: string;
  email: string;
  phone: string;
  gender: 'male' | 'female' | 'other';
  birthdate: string;
  signature: string;
  level: string;
  registerDate: string;
  lastLogin: string;
}>({
  isLoggedIn: false,
  nickname: '用户',
  avatar: '',
  email: '',
  phone: '',
  gender: 'other',
  birthdate: '',
  signature: '',
  level: '普通会员',
  registerDate: '2023-01-01',
  lastLogin: '今天'
});

// 从localStorage获取用户信息作为初始值
const storedUserInfo = localStorage.getItem('userInfo');
if (storedUserInfo) {
  const parsedInfo = JSON.parse(storedUserInfo);
  // 兼容旧数据格式
  if (!parsedInfo.nickname && parsedInfo.name) {
    parsedInfo.nickname = parsedInfo.name;
  }
  // 转换gender格式（数据库中是数字，前端是字符串）
  if (parsedInfo.gender !== undefined) {
    parsedInfo.gender = parsedInfo.gender === 1 ? 'male' : parsedInfo.gender === 2 ? 'female' : 'other';
  }
  user.value = { ...user.value, ...parsedInfo };
}

// 从localStorage获取用户ID
const storedUserId = localStorage.getItem('id');
if (storedUserId) {
  user.value.id = parseInt(storedUserId, 10);
}



// 返回上一页
const goBack = () => {
  router.go(-1);
};

// 处理头像上传
const handleAvatarUpload = (e: Event) => {
  const target = e.target as HTMLInputElement;
  if (target.files && target.files[0]) {
    const reader = new FileReader();
    reader.onload = (event) => {
      if (event.target?.result) {
        user.value.avatar = event.target.result as string;
      }
    };
    reader.readAsDataURL(target.files[0]);
  }
};

// 保存用户资料
const saveProfile = async () => {
  // 简单验证
  if (!user.value.nickname.trim()) {
    alert('请输入昵称');
    return;
  }

  // 检查用户ID是否存在
  if (!user.value.id) {
    alert('用户未登录，请先登录');
    return;
  }

  if (user.value.email && !/^[\w-]+(\.[\w-]+)*@[\w-]+(\.[\w-]+)+$/.test(user.value.email)) {
    alert('请输入有效的电子邮箱');
    return;
  }

  if (user.value.phone && !/^1[3-9]\d{9}$/.test(user.value.phone)) {
    alert('请输入有效的手机号码');
    return;
  }

  try {
    // 准备要发送的数据，转换gender格式
    const userData = {
      id: user.value.id!, // 添加非空断言确保id为number类型
      nickname: user.value.nickname,
      email: user.value.email,
      phone: user.value.phone,
      gender: user.value.gender === 'male' ? 1 : user.value.gender === 'female' ? 2 : 0,
      birthdate: user.value.birthdate,
      signature: user.value.signature,
      avatar: user.value.avatar
    };

    // 调用API更新用户信息
    const response = await apiService.updateUserInfo(userData);

    if (response.code === 200) {
      // 更新localStorage
      localStorage.setItem('userInfo', JSON.stringify(user.value));

      // 显示成功消息
      alert('资料保存成功');

      // 返回个人中心
      router.go(-1);
    } else {
      alert('保存失败: ' + (response.message || '未知错误'));
    }
  } catch (error) {
    console.error('更新用户信息失败:', error);
    alert('保存失败，请重试');
  }
};

// 页面加载时执行
onMounted(() => {
  // 可以在这里添加初始化逻辑
});
</script>

<style scoped>
/* 京东风格全局样式 */
.jd-edit-profile {
  width: 100%;
  margin: 0;
  padding: 0;
  font-family: -apple-system, blinkmacsystemfont, 'segoe ui', 'pingfang sc', 'hiragino sans gb', 'microsoft yahei', 'helvetica neue', helvetica, arial, sans-serif;
  background: #f5f5f5;
  min-height: 100vh;
}

/* 京东风格头部 */
.jd-header {
  background: linear-gradient(135deg, #e1251b 0%, #c81623 100%);
  color: white;
  padding: 12px;
  position: sticky;
  top: 0;
  z-index: 100;
}

.jd-header-content {
  display: flex;
  align-items: center;
  width: 100%;
  margin: 0;
  padding: 0 16px;
}

.jd-back-btn {
  font-size: 20px;
  cursor: pointer;
  margin-right: 16px;
}

.jd-page-title {
  font-size: 18px;
  font-weight: 500;
  margin: 0;
}



/* 京东风格头像上传区域 */
.jd-avatar-section {
  background: white;
  padding: 24px;
  margin-bottom: 16px;
  display: flex;
  justify-content: center;
}

.jd-avatar-wrapper {
  position: relative;
  width: 120px;
  height: 120px;
}

.jd-avatar-img {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  object-fit: cover;
  border: 3px solid white;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.jd-avatar-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: rgba(225, 37, 27, 0.8);
  color: white;
  text-align: center;
  border-bottom-left-radius: 60px;
  border-bottom-right-radius: 60px;
  padding: 8px 0;
  cursor: pointer;
  opacity: 0;
  transition: opacity 0.3s;
}

.jd-avatar-wrapper:hover .jd-avatar-overlay {
  opacity: 1;
}

.jd-avatar-upload-input {
  display: none;
}

.jd-change-avatar-btn {
  font-size: 12px;
  cursor: pointer;
  font-weight: 500;
}

/* 京东风格表单样式 */
.jd-profile-form {
  background: white;
  border-radius: 12px;
  padding: 24px;
  margin: 0 16px 16px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
}

.jd-form-group {
  margin-bottom: 20px;
}

.jd-form-label {
  display: block;
  margin-bottom: 8px;
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

.jd-form-label-icon {
  width: 16px;
  height: 16px;
  margin-right: 6px;
  vertical-align: middle;
}

.jd-form-input {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #e0e0e0;
  border-radius: 6px;
  font-size: 14px;
  transition: all 0.3s ease;
}

.jd-form-input:focus {
  outline: none;
  border-color: #e1251b;
  box-shadow: 0 0 0 2px rgba(225, 37, 27, 0.1);
}

.jd-gender-options {
  display: flex;
  gap: 20px;
}

.jd-gender-option {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 8px 12px;
  border-radius: 6px;
  transition: background 0.3s ease;
}

.jd-gender-option:hover {
  background: #f8f8f8;
}

.jd-gender-option input {
  margin-right: 6px;
}

.jd-form-textarea {
  width: 100%;
  height: 80px;
  padding: 10px 12px;
  border: 1px solid #e0e0e0;
  border-radius: 6px;
  font-size: 14px;
  resize: none;
  transition: all 0.3s ease;
}

.jd-form-textarea:focus {
  outline: none;
  border-color: #e1251b;
  box-shadow: 0 0 0 2px rgba(225, 37, 27, 0.1);
}

.jd-save-btn {
  width: 100%;
  background: linear-gradient(135deg, #e1251b 0%, #c81623 100%);
  color: white;
  border: none;
  border-radius: 20px;
  padding: 12px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.jd-save-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(225, 37, 27, 0.3);
}

/* 京东风格响应式调整 */
@media (max-width: 768px) {
  .jd-profile-form {
    margin: 0 12px 12px;
    padding: 20px;
  }
  
  .jd-avatar-section {
    padding: 20px;
  }
}
</style>