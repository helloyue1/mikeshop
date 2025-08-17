<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import apiService from '../services/apiService';

export interface LoginForm {
  username: string;
  password: string;
}

const form = ref<LoginForm>({
  username: '',
  password: '',
});

const errorMessage = ref('');
const loginType = ref('account'); // 'account' 或 'qr'
const router = useRouter();

const handleRegister = () => {
  router.push('/register');
};

const switchLoginType = (type: string) => {
  loginType.value = type;
  errorMessage.value = '';
};

const handleSubmit = async () => {
  // 简单的表单验证
  if (!form.value.username || !form.value.password) {
    errorMessage.value = '请输入账号和密码';
    return;
  }

  try {
    // 调用真实登录API
    const response = await apiService.login(form.value);

    // 保存用户信息（包含从API获取的ID）
    const userInfo = {
      isLoggedIn: true,
      id: response.data.id,
      username: response.data.username, // 使用username字段，与ChatRoom.vue保持一致
      name: response.data.username || '用户',
      avatar: '/images/登录前.png'
    };
    localStorage.setItem('token', response.data.token);
    localStorage.setItem('userInfo', JSON.stringify(userInfo));

    // 登录成功后根据后端返回的URL进行跳转
    alert('登录成功！');
    const redirectUrl = response.data.redirectUrl || '/mikeshop';
    router.push(redirectUrl);
  } catch (error) {
    errorMessage.value = error instanceof Error ? error.message : '登录失败';
  }
};
</script>

<template>
  <div class="login-container">
    <div class="login-box">
      <!-- 左侧登录表单 -->
      <div class="login-form">
        <div class="login-header">
          <h2>欢迎登录Mike购物商城</h2>
          <div class="login-tabs">
            <span 
              :class="['tab-item', loginType === 'account' ? 'active' : '']"
              @click="switchLoginType('account')"
            >账号登录</span>
            <span 
              :class="['tab-item', loginType === 'qr' ? 'active' : '']"
              @click="switchLoginType('qr')"
            >扫码登录</span>
          </div>
        </div>

        <!-- 账号登录 -->
        <div v-if="loginType === 'account'" class="account-login">
          <div class="form-group">
            <input
              type="text"
              v-model="form.username"
              placeholder="邮箱/用户名/手机号"
              class="form-input"
            />
          </div>
          <div class="form-group">
            <input
              type="password"
              v-model="form.password"
              placeholder="密码"
              class="form-input"
            />
          </div>
          <div class="form-options">
            <label class="remember-me">
              <input type="checkbox" /> 记住密码
            </label>
            <a href="#" class="forget-pwd">忘记密码</a>
          </div>
          <div v-if="errorMessage" class="error-message">{{ errorMessage }}</div>
          <button @click="handleSubmit" class="login-button">登 录</button>
        </div>

        <!-- 扫码登录 -->
        <div v-if="loginType === 'qr'" class="qr-login">
          <div class="qr-code">
            <div class="qr-placeholder">
              <div class="qr-icon">📱</div>
              <p>扫码登录</p>
              <p class="qr-desc">打开手机App扫描二维码</p>
            </div>
          </div>
          <div class="qr-tips">
            <p>打开 <span class="highlight">Mike购物商城App</span> 扫描二维码</p>
          </div>
        </div>

        <div class="login-footer">
          <div class="other-login">
            <span>其他登录方式：</span>
            <a href="#" class="qq-login">QQ</a>
            <a href="#" class="wechat-login">微信</a>
          </div>
          <div class="register-link">
            还没有账号？
            <a href="#" @click="handleRegister" class="register-btn">立即注册</a>
          </div>
        </div>
      </div>

      <!-- 右侧图片 -->
      <div class="login-image">
        <div class="image-content">
          <h3>欢迎来到Mike购物商城</h3>
          <p>登录享受更多优惠和专属服务</p>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f5f5 0%, #e8e8e8 100%);
  padding: 20px;
}

.login-box {
  display: flex;
  background: white;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  max-width: 900px;
  width: 100%;
  min-height: 500px;
}

.login-form {
  flex: 1;
  padding: 40px;
  display: flex;
  flex-direction: column;
}

.login-header {
  margin-bottom: 30px;
}

.login-header h2 {
  font-size: 24px;
  color: #333;
  margin-bottom: 20px;
  text-align: center;
  font-weight: 500;
}

.login-tabs {
  display: flex;
  border-bottom: 1px solid #e4e4e4;
  margin-bottom: 30px;
}

.tab-item {
  flex: 1;
  text-align: center;
  padding: 12px 0;
  cursor: pointer;
  color: #666;
  font-size: 16px;
  transition: all 0.3s ease;
  position: relative;
}

.tab-item.active {
  color: #e1251b;
  font-weight: 500;
}

.tab-item.active::after {
  content: '';
  position: absolute;
  bottom: -1px;
  left: 50%;
  transform: translateX(-50%);
  width: 60px;
  height: 2px;
  background: #e1251b;
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

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  font-size: 14px;
}

.remember-me {
  color: #666;
  display: flex;
  align-items: center;
}

.remember-me input {
  margin-right: 6px;
}

.forget-pwd {
  color: #666;
  text-decoration: none;
}

.forget-pwd:hover {
  color: #e1251b;
}

.error-message {
  color: #e1251b;
  margin-bottom: 15px;
  text-align: center;
  font-size: 14px;
}

.login-button {
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

.login-button:hover {
  background: #c81623;
}

.qr-login {
  text-align: center;
  padding: 20px 0;
}

.qr-code {
  width: 180px;
  height: 180px;
  margin: 0 auto 20px;
  border: 1px solid #e4e4e4;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fafafa;
}

.qr-placeholder {
  text-align: center;
}

.qr-icon {
  font-size: 48px;
  margin-bottom: 10px;
}

.qr-placeholder p {
  margin: 5px 0;
  color: #333;
  font-size: 14px;
}

.qr-desc {
  color: #999;
  font-size: 12px;
}

.qr-tips {
  font-size: 14px;
  color: #666;
  line-height: 1.5;
}

.highlight {
  color: #e1251b;
  font-weight: 500;
}

.login-footer {
  margin-top: auto;
  padding-top: 30px;
  border-top: 1px solid #e4e4e4;
}

.other-login {
  text-align: center;
  margin-bottom: 15px;
  font-size: 14px;
  color: #666;
}

.other-login a {
  color: #666;
  text-decoration: none;
  margin: 0 8px;
  transition: color 0.3s ease;
}

.qq-login:hover {
  color: #12b7f5;
}

.wechat-login:hover {
  color: #07c160;
}

.register-link {
  text-align: center;
  font-size: 14px;
  color: #666;
}

.register-btn {
  color: #e1251b;
  text-decoration: none;
  font-weight: 500;
}

.register-btn:hover {
  text-decoration: underline;
}

.login-image {
  flex: 1;
  background: linear-gradient(135deg, #e1251b 0%, #c81623 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  position: relative;
  overflow: hidden;
}

.login-image::before {
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
}

@media (max-width: 768px) {
  .login-box {
    flex-direction: column;
    max-width: 400px;
  }
  
  .login-image {
    display: none;
  }
  
  .login-form {
    padding: 30px 20px;
  }
}
</style>