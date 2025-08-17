<template>
  <div class="admin-login-container">
    <div class="admin-login-card">
      <div class="admin-login-header">
        <h1>管理员登录</h1>
        <p>购物商城管理系统</p>
      </div>
      
      <form @submit.prevent="handleLogin" class="admin-login-form">
        <div class="form-group">
          <label for="username">用户名</label>
          <input
            type="text"
            id="username"
            v-model="loginForm.username"
            placeholder="请输入管理员用户名"
            required
          />
        </div>
        
        <div class="form-group">
          <label for="password">密码</label>
          <input
            type="password"
            id="password"
            v-model="loginForm.password"
            placeholder="请输入密码"
            required
          />
        </div>
        
        <button type="submit" class="login-btn" :disabled="isLoading">
          {{ isLoading ? '登录中...' : '登录' }}
        </button>
      </form>
      
      <div v-if="errorMessage" class="error-message">
        {{ errorMessage }}
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue';
import { useRouter } from 'vue-router';
import ApiService from '@/services/apiService';

// 使用已创建的API服务实例

const router = useRouter();
const isLoading = ref(false);
const errorMessage = ref('');

const loginForm = reactive({
  username: '',
  password: ''
});

const handleLogin = async () => {
  isLoading.value = true;
  errorMessage.value = '';
  
  try {
    // 调用管理员登录API
    const response = await ApiService.adminLogin({
      username: loginForm.username,
      password: loginForm.password
    });
    
    // 登录成功后，根据后端返回的redirectUrl或用户角色进行跳转
    if (response.data.redirectUrl) {
      router.push(response.data.redirectUrl);
    } else if (loginForm.username === 'aaa') {
      router.push('/admin/customer-service');
    } else {
      router.push('/admin');
    }
  } catch (error: any) {
    console.error('管理员登录失败:', error);
    errorMessage.value = error.message || '用户名或密码错误';
  } finally {
    isLoading.value = false;
  }
};
</script>

<style scoped>
.admin-login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.admin-login-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  padding: 40px;
  width: 100%;
  max-width: 400px;
}

.admin-login-header {
  text-align: center;
  margin-bottom: 30px;
}

.admin-login-header h1 {
  color: #333;
  margin-bottom: 8px;
  font-size: 28px;
}

.admin-login-header p {
  color: #666;
  font-size: 14px;
}

.admin-login-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-group label {
  color: #333;
  font-weight: 500;
  font-size: 14px;
}

.form-group input {
  padding: 12px 16px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
  transition: border-color 0.3s;
}

.form-group input:focus {
  outline: none;
  border-color: #667eea;
}

.login-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 6px;
  padding: 14px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
}

.login-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
}

.login-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.error-message {
  color: #e74c3c;
  font-size: 14px;
  text-align: center;
  margin-top: 10px;
}
</style>