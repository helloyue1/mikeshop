<template>
  <div class="admin-dashboard">
    <!-- 侧边栏 -->
    <div class="admin-sidebar">
      <div class="sidebar-header">
        <h2>管理系统</h2>
        <div class="admin-info">
          <span class="admin-name">{{ adminUser?.username }}</span>
          <span class="admin-role">管理员</span>
        </div>
      </div>
      
      <nav class="sidebar-nav">
        <router-link to="/admin" class="nav-item" active-class="active">
          <span class="nav-icon">📊</span>
          <span class="nav-text">仪表板</span>
        </router-link>
        <router-link to="/admin/product" class="nav-item" active-class="active">
          <span class="nav-icon">📦</span>
          <span class="nav-text">商品管理</span>
        </router-link>
        <router-link to="/admin/orders" class="nav-item" active-class="active">
          <span class="nav-icon">📋</span>
          <span class="nav-text">订单管理</span>
        </router-link>
        <router-link to="/admin/users" class="nav-item" active-class="active">
          <span class="nav-icon">👥</span>
          <span class="nav-text">用户管理</span>
        </router-link>
        <router-link to="/admin/categories" class="nav-item" active-class="active">
          <span class="nav-icon">🏷️</span>
          <span class="nav-text">分类管理</span>
        </router-link>
        <router-link to="/admin/images" class="nav-item" active-class="active">
          <span class="nav-icon">🖼️</span>
          <span class="nav-text">图片管理</span>
        </router-link>
        <router-link to="/admin/customer-service" class="nav-item" active-class="active">
          <span class="nav-icon">💬</span>
          <span class="nav-text">客服聊天</span>
        </router-link>
      </nav>
      
      <div class="sidebar-footer">
        <button @click="handleLogout" class="logout-btn">
          <span class="logout-icon">🚪</span>
          <span>退出登录</span>
        </button>
      </div>
    </div>
    
    <!-- 主内容区域 -->
    <div class="admin-main-content">
      <div class="admin-header">
        <h1>仪表板</h1>
        <div class="header-actions">
          <span class="current-time">加载中...</span>
        </div>
      </div>
      
      <router-view />

      <!-- 默认仪表板内容 - 仅在访问/admin时显示 -->
      <template v-if="$route.path === '/admin'">
        <div class="dashboard-content">
          <!-- 统计卡片 -->
          <div class="stats-grid">
            <div class="stat-card">
              <div class="stat-icon products">📦</div>
              <div class="stat-info">
                <h3>{{ stats.totalProducts }}</h3>
                <p>商品总数</p>
              </div>
            </div>
            
            <div class="stat-card">
              <div class="stat-icon orders">📋</div>
              <div class="stat-info">
                <h3>{{ stats.totalOrders }}</h3>
                <p>订单总数</p>
              </div>
            </div>
            
            <div class="stat-card">
              <div class="stat-icon users">👥</div>
              <div class="stat-info">
                <h3>{{ stats.totalUsers }}</h3>
                <p>用户总数</p>
              </div>
            </div>
            
            <div class="stat-card">
              <div class="stat-icon revenue">💰</div>
              <div class="stat-info">
                <h3>¥{{ stats.totalRevenue.toLocaleString() }}</h3>
                <p>总销售额</p>
              </div>
            </div>
          </div>
          
          <!-- 最近订单 -->
          <div class="dashboard-section">
            <h2>最近订单</h2>
            <div class="recent-orders">
              <table class="orders-table">
                <thead>
                  <tr>
                    <th>订单号</th>
                    <th>用户</th>
                    <th>金额</th>
                    <th>状态</th>
                    <th>时间</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="order in recentOrders" :key="order.id">
                    <td>{{ order.id }}</td>
                    <td>{{ order.user }}</td>
                    <td>¥{{ order.amount }}</td>
                    <td>
                      <span class="status-badge" :class="order.status">
                        {{ getStatusText(order.status) }}
                      </span>
                    </td>
                    <td>{{ order.time }}</td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
          
          <!-- 热门商品 -->
          <div class="dashboard-section">
            <h2>热门商品</h2>
            <div class="popular-products">
              <div v-for="product in popularProducts" :key="product.id" class="product-card">
                <div class="product-image">{{ product.image }}</div>
                <div class="product-info">
                  <h4>{{ product.name }}</h4>
                  <p>¥{{ product.price }}</p>
                  <span class="sales-count">销量: {{ product.sales }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </template>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onUnmounted, watch } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();
const adminUser = ref<any>(null);

// 统计数据
const stats = reactive({
  totalProducts: 156,
  totalOrders: 892,
  totalUsers: 1245,
  totalRevenue: 125680
});

// 最近订单
const recentOrders = ref([
  { id: 'ORD001', user: '张三', amount: 299, status: 'pending', time: '2024-01-15 14:30' },
  { id: 'ORD002', user: '李四', amount: 599, status: 'completed', time: '2024-01-15 13:45' },
  { id: 'ORD003', user: '王五', amount: 1299, status: 'shipped', time: '2024-01-15 12:20' },
  { id: 'ORD004', user: '赵六', amount: 899, status: 'pending', time: '2024-01-15 11:15' },
  { id: 'ORD005', user: '钱七', amount: 399, status: 'completed', time: '2024-01-15 10:30' }
]);

// 热门商品
const popularProducts = ref([
  { id: 1, name: 'iPhone 15 Pro', price: 8999, sales: 156, image: '📱' },
  { id: 2, name: 'MacBook Pro', price: 12999, sales: 89, image: '💻' },
  { id: 3, name: 'AirPods Pro', price: 1999, sales: 234, image: '🎧' },
  { id: 4, name: 'iPad Air', price: 4599, sales: 67, image: '📱' }
]);

// 更新时间 - 直接操作DOM避免Vue重新渲染
const updateTime = () => {
  const now = new Date();
  const timeElement = document.querySelector('.current-time');
  if (timeElement) {
    timeElement.textContent = now.toLocaleString('zh-CN');
  }
};

// 获取状态文本
const getStatusText = (status: string) => {
  const statusMap: Record<string, string> = {
    pending: '待处理',
    completed: '已完成',
    shipped: '已发货',
    cancelled: '已取消'
  };
  return statusMap[status] || status;
};

// 退出登录
const handleLogout = () => {
  localStorage.removeItem('adminToken');
  localStorage.removeItem('adminUser');
  router.push('/admin/login');
};

// 初始化
onMounted(() => {
  const adminUserData = localStorage.getItem('adminUser');
  if (adminUserData) {
    adminUser.value = JSON.parse(adminUserData);
  }
  
  updateTime();
  const timer = setInterval(updateTime, 1000);
  
  // 在组件卸载时清除定时器
  onUnmounted(() => {
    clearInterval(timer);
  });
});
</script>

<style scoped>
.admin-dashboard {
  display: flex;
  min-height: 100vh;
  background-color: #f5f5f5;
}

/* 侧边栏样式 */
.admin-sidebar {
  width: 250px;
  background: linear-gradient(180deg, #2c3e50 0%, #34495e 100%);
  color: white;
  display: flex;
  flex-direction: column;
}

.sidebar-header {
  padding: 20px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.sidebar-header h2 {
  margin: 0 0 10px 0;
  font-size: 20px;
}

.admin-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.admin-name {
  font-weight: 600;
}

.admin-role {
  font-size: 12px;
  opacity: 0.8;
}

.sidebar-nav {
  flex: 1;
  padding: 20px 0;
}

.nav-item {
  display: flex;
  align-items: center;
  padding: 12px 20px;
  color: rgba(255, 255, 255, 0.8);
  text-decoration: none;
  transition: all 0.3s;
}

.nav-item:hover {
  background-color: rgba(255, 255, 255, 0.1);
  color: white;
}

.nav-item.active {
  background-color: rgba(255, 255, 255, 0.2);
  color: white;
  border-right: 3px solid #3498db;
}

.nav-icon {
  margin-right: 10px;
  font-size: 16px;
}

.sidebar-footer {
  padding: 20px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.logout-btn {
  width: 100%;
  padding: 10px;
  background-color: rgba(231, 76, 60, 0.8);
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  transition: background-color 0.3s;
}

.logout-btn:hover {
  background-color: #e74c3c;
}

/* 主内容区域样式 */
.admin-main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.admin-header {
  background: white;
  padding: 20px 30px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.admin-header h1 {
  margin: 0;
  color: #2c3e50;
}

.current-time {
  color: #7f8c8d;
  font-size: 14px;
}

.dashboard-content {
  padding: 30px;
  flex: 1;
}

/* 统计卡片样式 */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.stat-card {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  gap: 15px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}

.stat-icon.products {
  background-color: #3498db;
}

.stat-icon.orders {
  background-color: #2ecc71;
}

.stat-icon.users {
  background-color: #f39c12;
}

.stat-icon.revenue {
  background-color: #e74c3c;
}

.stat-info h3 {
  margin: 0 0 5px 0;
  color: #2c3e50;
  font-size: 24px;
}

.stat-info p {
  margin: 0;
  color: #7f8c8d;
  font-size: 14px;
}

/* 仪表板区域样式 */
.dashboard-section {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
}

.dashboard-section h2 {
  margin: 0 0 20px 0;
  color: #2c3e50;
}

/* 订单表格样式 */
.orders-table {
  width: 100%;
  border-collapse: collapse;
}

.orders-table th,
.orders-table td {
  padding: 12px;
  text-align: left;
  border-bottom: 1px solid #ecf0f1;
}

.orders-table th {
  background-color: #f8f9fa;
  font-weight: 600;
  color: #2c3e50;
}

.status-badge {
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
}

.status-badge.pending {
  background-color: #fff3cd;
  color: #856404;
}

.status-badge.completed {
  background-color: #d4edda;
  color: #155724;
}

.status-badge.shipped {
  background-color: #cce5ff;
  color: #004085;
}

.status-badge.cancelled {
  background-color: #f8d7da;
  color: #721c24;
}

/* 热门商品样式 */
.popular-products {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 15px;
}

.product-card {
  border: 1px solid #ecf0f1;
  border-radius: 8px;
  padding: 15px;
  text-align: center;
}

.product-image {
  font-size: 40px;
  margin-bottom: 10px;
}

.product-info h4 {
  margin: 0 0 5px 0;
  color: #2c3e50;
  font-size: 14px;
}

.product-info p {
  margin: 0 0 8px 0;
  color: #e74c3c;
  font-weight: 600;
}

.sales-count {
  font-size: 12px;
  color: #7f8c8d;
}
</style>