<template>
  <div class="user-management">
    <div class="page-header">
      <h1>用户管理</h1>
      <div class="header-stats">
        <div class="stat-item">
          <span class="stat-label">总用户数</span>
          <span class="stat-value">{{ totalUsers }}</span>
        </div>
        <div class="stat-item">
          <span class="stat-label">活跃用户</span>
          <span class="stat-value">{{ activeUsers }}</span>
        </div>
        <div class="stat-item">
          <span class="stat-label">新增用户</span>
          <span class="stat-value">{{ newUsers }}</span>
        </div>
      </div>
    </div>
    
    <!-- 搜索和筛选区域 -->
    <div class="search-filter-section">
      <div class="search-box">
        <input
          type="text"
          v-model="searchQuery"
          placeholder="搜索用户名或手机号..."
          @input="filterUsers"
        />
        <span class="search-icon">🔍</span>
      </div>
      
      <div class="filter-controls">
        <select v-model="statusFilter" @change="filterUsers">
          <option value="">所有状态</option>
          <option value="1">正常</option>
          <option value="0">封禁</option>
        </select>
        
        <select v-model="rolenameFilter" @change="filterUsers" class="filter-select">
          <option value="">全部角色</option>
          <option value="user">普通用户</option>
          <option value="vip">VIP用户</option>
          <option value="admin">管理员</option>
        </select>
        
        <select v-model="dateFilter" @change="filterUsers">
          <option value="">所有时间</option>
          <option value="today">今天</option>
          <option value="week">本周</option>
          <option value="month">本月</option>
        </select>
        
        <button @click="exportUsers" class="export-btn">
          <span>📥</span>
          导出用户
        </button>
      </div>
    </div>
    
    <!-- 用户表格 -->
    <div class="users-table-container">
      <table class="users-table">
        <thead>
          <tr>
            <th>用户ID</th>
            <th>用户名</th>
            <th>手机号</th>
            <th>邮箱</th>
            <th>角色</th>
            <th>状态</th>
            <th>注册时间</th>
            <th>最后登录</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="user in filteredUsers" :key="user.id">
            <td>
              <span class="user-id">{{ user.id }}</span>
            </td>
            <td>
              <div class="user-info">
                <div class="user-name">{{ user.username }}</div>
                <div class="user-nickname">{{ user.nickname }}</div>
              </div>
            </td>
            <td>
              <span class="user-phone">{{ user.phone }}</span>
            </td>
            <td>
              <span class="user-email">{{ user.email }}</span>
            </td>
            <td>
              <span class="role-badge" :class="user.rolename">
                {{ getRoleText(user.rolename) }}
              </span>
            </td>
            <td>
              <span class="status-badge" :class="user.status === 1 ? 'active' : 'banned'">
                {{ getStatusText(user.status) }}
              </span>
            </td>
            <td>
              <div class="register-time">
                <div>{{ user.registerDate }}</div>
                <div class="time">{{ user.registerTime }}</div>
              </div>
            </td>
            <td>
              <div class="last-login">
                <div v-if="user.lastLoginDate">{{ user.lastLoginDate }}</div>
                <div v-else class="never-login">从未登录</div>
                <div v-if="user.lastLoginTime" class="time">{{ user.lastLoginTime }}</div>
              </div>
            </td>
            <td>
              <div class="action-buttons">
                <button @click="viewUserDetails(user)" class="view-btn">查看</button>
                <button @click="editUser(user)" class="edit-btn">编辑</button>
                <button 
                  v-if="user.status === 1" 
                  @click="toggleUserStatus(user, '0')"
                  class="ban-btn"
                >
                  封禁
                </button>
                <button 
                  v-if="user.status === 0" 
                  @click="toggleUserStatus(user, '1')"
                  class="unban-btn"
                >
                  解封
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    
    <!-- 用户详情模态框 -->
    <div v-if="showUserDetails" class="modal-overlay">
      <div class="modal user-details-modal">
        <div class="modal-header">
          <h2>用户详情</h2>
          <button @click="closeUserDetails" class="close-btn">✕</button>
        </div>
        
        <div v-if="selectedUser" class="user-details-content">
          <div class="details-section">
            <h3>基本信息</h3>
            <div class="details-grid">
              <div class="detail-item">
                <label>用户ID</label>
                <span>{{ selectedUser.id }}</span>
              </div>
              <div class="detail-item">
                <label>用户名</label>
                <span>{{ selectedUser.username }}</span>
              </div>
              <div class="detail-item">
                <label>昵称</label>
                <span>{{ selectedUser.nickname }}</span>
              </div>
              <div class="detail-item">
                <label>手机号</label>
                <span>{{ selectedUser.phone }}</span>
              </div>
              <div class="detail-item">
                <label>邮箱</label>
                <span>{{ selectedUser.email }}</span>
              </div>
              <div class="detail-item">
                <label>用户角色</label>
                <span class="role-badge" :class="selectedUser.rolename">
                  {{ getRoleText(selectedUser.rolename) }}
                </span>
              </div>
              <div class="detail-item">
                <label>用户状态</label>
                <span class="status-badge" :class="selectedUser.status === 1 ? 'active' : 'banned'">
                  {{ getStatusText(selectedUser.status) }}
                </span>
              </div>
              <div class="detail-item">
                <label>注册时间</label>
                <span>{{ selectedUser.registerDate }} {{ selectedUser.registerTime }}</span>
              </div>
            </div>
          </div>
          
          <div class="details-section">
            <h3>登录信息</h3>
            <div class="details-grid">
              <div class="detail-item">
                <label>最后登录时间</label>
                <span>{{ selectedUser.lastLoginDate ? selectedUser.lastLoginDate + ' ' + selectedUser.lastLoginTime : '从未登录' }}</span>
              </div>
              <div class="detail-item">
                <label>登录次数</label>
                <span>{{ selectedUser.loginCount }}</span>
              </div>
              <div class="detail-item">
                <label>登录IP</label>
                <span>{{ selectedUser.lastLoginIP || '无' }}</span>
              </div>
            </div>
          </div>
          
          <div class="details-section">
            <h3>订单统计</h3>
            <div class="order-stats">
              <div class="stat-card">
                <div class="stat-number">{{ selectedUser.totalOrders }}</div>
                <div class="stat-label">总订单数</div>
              </div>
              <div class="stat-card">
                <div class="stat-number">{{ selectedUser.completedOrders }}</div>
                <div class="stat-label">已完成订单</div>
              </div>
              <div class="stat-card">
                <div class="stat-number">¥{{ selectedUser.totalSpent }}</div>
                <div class="stat-label">总消费金额</div>
              </div>
            </div>
          </div>
          
          <div class="details-section">
            <h3>收货地址</h3>
            <div class="address-list">
              <div v-for="(address, index) in selectedUser.addresses" :key="index" class="address-item">
                <div class="address-info">
                  <div class="address-name">{{ address.name }}</div>
                  <div class="address-phone">{{ address.phone }}</div>
                  <div class="address-text">{{ address.address }}</div>
                  <div class="address-default" v-if="address.isDefault">默认地址</div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 编辑用户模态框 -->
    <div v-if="showEditModal" class="modal-overlay">
      <div class="modal edit-user-modal">
        <div class="modal-header">
          <h2>编辑用户</h2>
          <button @click="closeEditModal" class="close-btn">✕</button>
        </div>
        
        <form @submit.prevent="saveUser" class="user-form">
          <div class="form-row">
            <div class="form-group">
              <label>用户名</label>
              <input
                type="text"
                v-model="editForm.username"
                required
                disabled
              />
            </div>
            
            <div class="form-group">
              <label>昵称</label>
              <input
                type="text"
                v-model="editForm.nickname"
                required
              />
            </div>
          </div>
          
          <div class="form-row">
            <div class="form-group">
              <label>手机号</label>
              <input
                type="tel"
                v-model="editForm.phone"
                required
                pattern="^1[3-9]\d{9}$"
                title="请输入正确的手机号码格式"
              />
            </div>
            
            <div class="form-group">
              <label>邮箱</label>
              <input
                type="email"
                v-model="editForm.email"
                required
                title="请输入正确的邮箱格式"
              />
            </div>
          </div>
          
          <div class="form-row">
            <div class="form-group">
              <label>用户角色</label>
              <select v-model="editForm.rolename" required>
                <option value="user">普通用户</option>
                <option value="vip">VIP用户</option>
                <option value="admin">管理员</option>
              </select>
            </div>
            
            <div class="form-group">
              <label>用户状态</label>
              <select v-model="editForm.status" required>
                <option value="1">正常</option>
                <option value="0">封禁</option>
              </select>
            </div>
          </div>
          
          <div class="form-row">
            <div class="form-group">
              <label>密码</label>
              <input
                type="password"
                v-model="editForm.password"
                placeholder="留空则不修改密码"
                minlength="6"
                title="密码至少6位字符"
              />
            </div>
            
            <div class="form-group">
              <label>确认密码</label>
              <input
                type="password"
                v-model="editForm.confirmPassword"
                placeholder="再次输入密码"
                :required="editForm.password !== ''"
              />
            </div>
          </div>
          
          <div class="form-actions">
            <button type="button" @click="closeEditModal" class="cancel-btn">取消</button>
            <button type="submit" class="save-btn">保存</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import apiService from '@/services/apiService';

const router = useRouter();

// 搜索和筛选状态
const searchQuery = ref('');
const statusFilter = ref('');
const rolenameFilter = ref('');
const dateFilter = ref('');

// 模态框状态
const showUserDetails = ref(false);
const showEditModal = ref(false);
const selectedUser = ref<any>(null);
const editingUserId = ref<number | null>(null);

// 编辑表单
const editForm = reactive({
  username: '',
  nickname: '',
  phone: '',
  email: '',
  rolename: 'user',
  status: 1,
  password: '',
  confirmPassword: ''
});

// 用户数据
const users = ref<any[]>([]);

// 统计数据
const totalUsers = computed(() => users.value.length);
const activeUsers = computed(() => users.value.filter(user => user.status === 1).length);
const newUsers = computed(() => {
  const weekAgo = new Date();
  weekAgo.setDate(weekAgo.getDate() - 7);
  return users.value.filter(user => new Date(user.registerDate) >= weekAgo).length;
});

// 过滤后的用户
const filteredUsers = computed(() => {
  return users.value.filter(user => {
    const matchesSearch = user.username.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
                         user.nickname.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
                         user.phone.includes(searchQuery.value);
    const matchesStatus = !statusFilter.value || user.status.toString() === statusFilter.value;
    const matchesRole = !rolenameFilter.value || user.rolename === rolenameFilter.value;
    
    let matchesDate = true;
    if (dateFilter.value === 'today') {
      const today = new Date().toISOString().split('T')[0];
      matchesDate = user.registerDate === today;
    } else if (dateFilter.value === 'week') {
      const weekAgo = new Date();
      weekAgo.setDate(weekAgo.getDate() - 7);
      matchesDate = new Date(user.registerDate) >= weekAgo;
    } else if (dateFilter.value === 'month') {
      const monthAgo = new Date();
      monthAgo.setMonth(monthAgo.getMonth() - 1);
      matchesDate = new Date(user.registerDate) >= monthAgo;
    }
    
    return matchesSearch && matchesStatus && matchesRole && matchesDate;
  });
});

// 筛选用户
const filterUsers = () => {
  // 计算属性会自动更新
};

// 获取角色文本
const getRoleText = (role: string) => {
  const roleMap: Record<string, string> = {
    user: '普通用户',
    vip: 'VIP用户',
    admin: '管理员'
  };
  return roleMap[role] || role;
};

// 获取状态文本
const getStatusText = (status: string | number) => {
  // 处理数字状态：1=正常，0=封禁
  if (typeof status === 'number') {
    return status === 1 ? '正常' : '封禁';
  }
  // 处理字符串状态
  const statusMap: Record<string, string> = {
    '1': '正常',
    '0': '封禁',
    active: '正常',
    banned: '封禁'
  };
  return statusMap[status.toString()] || status;
};

// 查看用户详情
const viewUserDetails = (user: any) => {
  selectedUser.value = user;
  showUserDetails.value = true;
};

// 关闭用户详情
const closeUserDetails = () => {
  showUserDetails.value = false;
  selectedUser.value = null;
};

// 编辑用户
const editUser = (user: any) => {
  console.log('编辑用户数据:', user);
  console.log('用户角色字段:', user.rolename, user.role);
  editingUserId.value = user.id;
  editForm.username = user.username;
  editForm.nickname = user.nickname;
  editForm.phone = user.phone;
  editForm.email = user.email;
  // 确保角色字段正确赋值，优先使用rolename，如果没有则使用role
  editForm.rolename = user.rolename || user.role || 'user';
  editForm.status = user.status;
  editForm.password = ''; // 重置密码字段
  editForm.confirmPassword = ''; // 重置确认密码字段
  console.log('编辑表单角色值:', editForm.rolename);
  showEditModal.value = true;
};

// 关闭编辑模态框
const closeEditModal = () => {
  showEditModal.value = false;
  editingUserId.value = null;
};

// 保存用户
const saveUser = async () => {
  if (!editingUserId.value) return;
  
  // 验证密码
  if (editForm.password !== editForm.confirmPassword) {
    alert('两次输入的密码不一致');
    return;
  }
  
  if (editForm.password && editForm.password.length < 6) {
    alert('密码长度不能少于6位');
    return;
  }
  
  try {
    // 准备要发送的用户数据
    const userData = {
      id: editingUserId.value,
      username: editForm.username,
      nickname: editForm.nickname,
      phone: editForm.phone,
      email: editForm.email,
      rolename: editForm.rolename,
      status: parseInt(editForm.status.toString())
    };

    // 如果有新密码，添加到用户数据中
    if (editForm.password) {
      userData.password = editForm.password;
    }

    console.log('准备更新用户数据:', userData);

    // 调用后端API更新用户信息
    const response = await apiService.updateUserInfo(userData);
    
    if (response.code === 200 || response.success) {
      // 更新成功后刷新用户列表
      await loadUsers();
      alert('用户更新成功');
      closeEditModal();
    } else {
      alert('更新失败: ' + (response.message || '未知错误'));
    }
  } catch (error) {
    console.error('更新用户失败:', error);
    alert('更新用户失败，请重试');
  }
};

// 加载用户列表
const loadUsers = async () => {
  try {
    const adminToken = localStorage.getItem('adminToken');
    if (!adminToken) {
      router.push('/admin/login');
      return;
    }
    
    // 调用后端接口获取所有用户信息
    const response = await apiService.getAllUsers();
    users.value = response.data || [];
  } catch (error) {
    console.error('获取用户数据失败:', error);
    alert('获取用户数据失败，请稍后重试');
  }
};

// 切换用户状态
const toggleUserStatus = async (user: any, newStatus: string) => {
  try {
    const userData = {
      id: user.id,
      username: user.username,
      nickname: user.nickname,
      phone: user.phone,
      email: user.email,
      rolename: user.rolename,
      status: parseInt(newStatus)
    };

    console.log('准备更新用户状态:', userData);

    // 调用后端API更新用户状态
    const response = await apiService.updateUserInfo(userData);
    
    if (response.code === 200 || response.success) {
      // 更新成功后刷新用户列表
      await loadUsers();
      alert(`用户${newStatus === '1' ? '解封' : '封禁'}成功`);
    } else {
      alert('更新失败: ' + (response.message || '未知错误'));
    }
  } catch (error) {
    console.error('更新用户状态失败:', error);
    alert('更新用户状态失败，请重试');
  }
};

// 导出用户
const exportUsers = () => {
  alert('用户导出功能正在开发中...');
};

// 检查管理员登录状态并获取用户数据
onMounted(async () => {
  await loadUsers();
});
</script>

<style scoped>
.user-management {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h1 {
  margin: 0;
  color: #2c3e50;
}

.header-stats {
  display: flex;
  gap: 20px;
}

.stat-item {
  text-align: center;
  padding: 10px 15px;
  background: white;
  border-radius: 6px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.stat-label {
  display: block;
  font-size: 12px;
  color: #7f8c8d;
  margin-bottom: 4px;
}

.stat-value {
  display: block;
  font-size: 18px;
  font-weight: 600;
  color: #2c3e50;
}

/* 搜索和筛选区域 */
.search-filter-section {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
  align-items: center;
}

.search-box {
  position: relative;
  flex: 1;
  max-width: 300px;
}

.search-box input {
  width: 100%;
  padding: 10px 40px 10px 15px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
}

.search-icon {
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  color: #999;
}

.filter-controls {
  display: flex;
  gap: 10px;
  align-items: center;
}

.filter-controls select {
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
  background: white;
}

.export-btn {
  background: linear-gradient(135deg, #27ae60, #229954);
  color: white;
  border: none;
  border-radius: 6px;
  padding: 10px 15px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 14px;
  transition: transform 0.2s;
}

.export-btn:hover {
  transform: translateY(-2px);
}

/* 用户表格 */
.users-table-container {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.users-table {
  width: 100%;
  border-collapse: collapse;
}

.users-table th,
.users-table td {
  padding: 12px;
  text-align: left;
  border-bottom: 1px solid #ecf0f1;
}

.users-table th {
  background-color: #f8f9fa;
  font-weight: 600;
  color: #2c3e50;
}

.user-id {
  font-family: monospace;
  font-weight: 600;
  color: #3498db;
}

.user-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.user-name {
  font-weight: 500;
  color: #2c3e50;
}

.user-nickname {
  font-size: 12px;
  color: #7f8c8d;
}

.user-phone,
.user-email {
  font-size: 14px;
  color: #2c3e50;
}

.role-badge {
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
}

.role-badge.user {
  background-color: #e3f2fd;
  color: #1976d2;
}

.role-badge.vip {
  background-color: #fff3e0;
  color: #f57c00;
}

.role-badge.admin {
  background-color: #f3e5f5;
  color: #7b1fa2;
}

.status-badge {
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
}

.status-badge.active {
  background-color: #d4edda;
  color: #155724;
}



.status-badge.banned {
  background-color: #f8d7da;
  color: #721c24;
}

.register-time,
.last-login {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.time {
  font-size: 12px;
  color: #7f8c8d;
}

.never-login {
  font-size: 12px;
  color: #999;
  font-style: italic;
}

.action-buttons {
  display: flex;
  gap: 5px;
  flex-wrap: wrap;
}

.action-buttons button {
  padding: 4px 8px;
  border: none;
  border-radius: 4px;
  font-size: 12px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.view-btn {
  background-color: #3498db;
  color: white;
}

.view-btn:hover {
  background-color: #2980b9;
}

.edit-btn {
  background-color: #f39c12;
  color: white;
}

.edit-btn:hover {
  background-color: #e67e22;
}

.deactivate-btn {
  background-color: #95a5a6;
  color: white;
}

.deactivate-btn:hover {
  background-color: #7f8c8d;
}

.activate-btn {
  background-color: #27ae60;
  color: white;
}

.activate-btn:hover {
  background-color: #229954;
}

.ban-btn {
  background-color: #e74c3c;
  color: white;
}

.ban-btn:hover {
  background-color: #c0392b;
}

.unban-btn {
  background-color: #9b59b6;
  color: white;
}

.unban-btn:hover {
  background-color: #8e44ad;
}

/* 模态框样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal {
  background: white;
  border-radius: 8px;
  width: 90%;
  max-width: 700px;
  max-height: 90vh;
  overflow-y: auto;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #ecf0f1;
}

.modal-header h2 {
  margin: 0;
  color: #2c3e50;
}

.close-btn {
  background: none;
  border: none;
  font-size: 20px;
  cursor: pointer;
  color: #999;
}

.close-btn:hover {
  color: #333;
}

.user-details-content,
.user-form {
  padding: 20px;
}

.details-section {
  margin-bottom: 25px;
}

.details-section h3 {
  margin: 0 0 15px 0;
  color: #2c3e50;
  font-size: 16px;
  border-bottom: 1px solid #ecf0f1;
  padding-bottom: 8px;
}

.details-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 15px;
}

.detail-item {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.detail-item label {
  font-size: 12px;
  color: #7f8c8d;
  font-weight: 500;
}

.detail-item span {
  font-size: 14px;
  color: #2c3e50;
}

.order-stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 15px;
}

.stat-card {
  background: #f8f9fa;
  border-radius: 6px;
  padding: 15px;
  text-align: center;
}

.stat-number {
  font-size: 20px;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 12px;
  color: #7f8c8d;
}

.address-list {
  border: 1px solid #ecf0f1;
  border-radius: 6px;
  padding: 15px;
}

.address-item {
  padding: 10px 0;
  border-bottom: 1px solid #ecf0f1;
}

.address-item:last-child {
  border-bottom: none;
}

.address-info {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.address-name {
  font-weight: 500;
  color: #2c3e50;
}

.address-phone {
  font-size: 14px;
  color: #7f8c8d;
}

.address-text {
  font-size: 14px;
  color: #2c3e50;
}

.address-default {
  display: inline-block;
  background: #e3f2fd;
  color: #1976d2;
  padding: 2px 6px;
  border-radius: 3px;
  font-size: 11px;
  margin-top: 5px;
}

/* 编辑用户表单 */
.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 15px;
}

.form-group {
  margin-bottom: 15px;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
  color: #2c3e50;
  font-weight: 500;
  font-size: 14px;
}

.form-group input,
.form-group select {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
}

.form-group input:disabled {
  background-color: #f8f9fa;
  color: #6c757d;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 20px;
}

.form-actions button {
  padding: 10px 20px;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  cursor: pointer;
}

.cancel-btn {
  background-color: #95a5a6;
  color: white;
}

.cancel-btn:hover {
  background-color: #7f8c8d;
}

.save-btn {
  background: linear-gradient(135deg, #3498db, #2980b9);
  color: white;
}

.save-btn:hover {
  background: linear-gradient(135deg, #2980b9, #21618c);
}

/* 密码输入框样式 */
.form-group input[type="password"] {
  font-family: monospace;
}

.form-group input[type="password"]:focus {
  border-color: #3498db;
  box-shadow: 0 0 0 2px rgba(52, 152, 219, 0.2);
}
</style>