<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import apiService from '../services/apiService';

interface RegisterForm {
  username: string;
  password: string;
  email: string;
}

const form = ref<RegisterForm>({
  username: '',
  password: '',
  email: '',
});

const errorMessage = ref('');
const successMessage = ref('');
const router = useRouter();

const handleLogin = () => {
  router.push('/login');
};

const handleSubmit = async () => {
  // 简单的表单验证
  if (!form.value.username || !form.value.password || !form.value.email) {
    errorMessage.value = '请输入账号、密码和邮箱';
    successMessage.value = '';
    return;
  }

  try {
    // 使用apiService调用注册API
    const response = await apiService.register(form.value);

    // 注册成功后显示消息并跳转到登录页面
    successMessage.value = '注册成功，请登录';
    errorMessage.value = '';

    // 3秒后跳转到登录页面
    setTimeout(() => {
      router.push('/login');
    }, 3000);
  } catch (error) {
    errorMessage.value = error instanceof Error ? error.message : '注册失败';
    successMessage.value = '';
  }
};
</script>

<template>
  <div class="register-container">
    <div class="register-box">
      <!-- 左侧注册表单 -->
      <div class="register-form">
        <div class="register-header">
          <h2>欢迎注册Mike购物商城</h2>
          <p>加入我们，享受优质购物体验</p>
        </div>

        <div class="form-group">
          <input
            type="text"
            v-model="form.username"
            placeholder="请设置用户名"
            class="form-input"
          />
        </div>
        <div class="form-group">
          <input
            type="email"
            v-model="form.email"
            placeholder="请输入邮箱地址"
            class="form-input"
          />
        </div>
        <div class="form-group">
          <input
            type="password"
            v-model="form.password"
            placeholder="请设置密码（6-20位）"
            class="form-input"
          />
        </div>
        
        <div v-if="errorMessage" class="error-message">{{ errorMessage }}</div>
        <div v-if="successMessage" class="success-message">{{ successMessage }}</div>
        
        <button @click="handleSubmit" class="register-button">立即注册</button>
        
        <div class="register-footer">
          <div class="login-link">
            已有账号？
            <a href="#" @click="handleLogin" class="login-btn">立即登录</a>
          </div>
          <div class="agreement">
            注册即表示您同意
            <a href="#" class="agreement-link">《用户协议》</a>
            和
            <a href="#" class="agreement-link">《隐私政策》</a>
          </div>
        </div>
      </div>

      <!-- 右侧图片 -->
      <div class="register-image">
        <div class="image-content">
          <h3>欢迎加入Mike购物商城</h3>
          <p>注册成为会员，享受专属优惠和贴心服务</p>
          <div class="benefits">
            <div class="benefit-item">
              <span class="benefit-icon">🎁</span>
              <span>新用户专享优惠</span>
            </div>
            <div class="benefit-item">
              <span class="benefit-icon">🚚</span>
              <span>免费配送服务</span>
            </div>
            <div class="benefit-item">
              <span class="benefit-icon">💎</span>
              <span>会员积分奖励</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f5f5 0%, #e8e8e8 100%);
  padding: 20px;
}

.register-box {
  display: flex;
  background: white;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  max-width: 900px;
  width: 100%;
  min-height: 500px;
}

.register-form {
  flex: 1;
  padding: 40px;
  display: flex;
  flex-direction: column;
}

.register-header {
  margin-bottom: 30px;
  text-align: center;
}

.register-header h2 {
  font-size: 24px;
  color: #333;
  margin-bottom: 10px;
  font-weight: 500;
}

.register-header p {
  color: #666;
  font-size: 14px;
}

.form-group {
  margin-bottom: 20px;
}

.form-input {
  width: 100%;
  padding: 12px 16px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  transition: border-color 0.3s ease;
  box-sizing: border-box;
}

.form-input:focus {
  outline: none;
  border-color: #e1251b;
  box-shadow: 0 0 0 2px rgba(225, 37, 27, 0.1);
}

.error-message {
  color: #e1251b;
  margin-bottom: 15px;
  text-align: center;
  font-size: 14px;
}

.success-message {
  color: #07c160;
  margin-bottom: 15px;
  text-align: center;
  font-size: 14px;
}

.register-button {
  width: 100%;
  padding: 14px;
  background: #e1251b;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  cursor: pointer;
  transition: background-color 0.3s ease;
  font-weight: 500;
  margin-top: 10px;
}

.register-button:hover {
  background: #c81623;
}

.register-footer {
  margin-top: auto;
  padding-top: 30px;
  border-top: 1px solid #e4e4e4;
}

.login-link {
  text-align: center;
  margin-bottom: 15px;
  font-size: 14px;
  color: #666;
}

.login-btn {
  color: #e1251b;
  text-decoration: none;
  font-weight: 500;
}

.login-btn:hover {
  text-decoration: underline;
}

.agreement {
  text-align: center;
  font-size: 12px;
  color: #999;
  line-height: 1.5;
}

.agreement-link {
  color: #666;
  text-decoration: none;
}

.agreement-link:hover {
  color: #e1251b;
  text-decoration: underline;
}

.register-image {
  flex: 1;
  background: linear-gradient(135deg, #e1251b 0%, #c81623 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  position: relative;
  overflow: hidden;
}

.register-image::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><circle cx="20" cy="20" r="2" fill="rgba(255,255,255,0.1)"/><circle cx="80" cy="30" r="3" fill="rgba(255,255,255,0.1)"/><circle cx="40" cy="70" r="1" fill="rgba(255,255,255,0.1)"/><circle cx="90" cy="80" r="2" fill="rgba(255,255,255,0.1)"/><circle cx="10" cy="90" r="1" fill="rgba(255,255,255,0.1)"/></svg>') repeat;
  opacity: 0.3;
}

.image-content {
  text-align: center;
  z-index: 1;
  padding: 40px;
}

.image-content h3 {
  font-size: 28px;
  margin-bottom: 15px;
  font-weight: 500;
}

.image-content p {
  font-size: 16px;
  opacity: 0.9;
  line-height: 1.6;
  margin-bottom: 30px;
}

.benefits {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.benefit-item {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  font-size: 14px;
  opacity: 0.9;
}

.benefit-icon {
  font-size: 20px;
}

@media (max-width: 768px) {
  .register-box {
    flex-direction: column;
    max-width: 400px;
  }
  
  .register-image {
    display: none;
  }
  
  .register-form {
    padding: 30px 20px;
  }
}
</style>