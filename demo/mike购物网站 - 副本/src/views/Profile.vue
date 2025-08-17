<template>
  <div class="profile-container min-h-screen bg-gray-50 flex flex-col">
    <!-- 顶部用户信息 -->
    <header class="mike-header">
      <div class="logo">mikeShop</div>
      <nav class="main-nav">
        <a href="http://localhost:5173/mikeshop">首页</a>
        <a href="#/categories">分类</a>
        <a href="#/cart">购物车</a>
        <a href="#/profile">我的账户</a>
      </nav>
      <div class="search-bar">
        <input type="text" placeholder="搜索商品...">
        <button class="search-btn">搜索</button>
      </div>
    </header>

    <div class="user-info-header">
      <div class="user-basic-info">
        <div class="avatar-wrapper">
          <img :src="user.avatar || '/images/登录前.png'" alt="用户头像" class="avatar-img">
        </div>
        <div class="nickname">{{ user.nickname || '昵称' }}</div>
        <p class="membership-level">mikeShop会员等级: {{ user.level || '普通会员' }}</p>
        <div class="info-row">
          <span>注册时间: {{ user.registerDate || '2023-01-01' }}</span>
        </div>
        <button class="edit-profile-btn" @click="navigateToEditProfile">编辑资料</button>
      </div>
      
    </div>
    
    <div class="profile-content">
      <div class="sidebar">

        
        <div class="nav-menu">
          <div class="nav-item" @click="goToOrders">我的订单</div>
          <div class="nav-item" @click="goToWallet">我的钱包</div>
          <div class="nav-item" @click="goToFavorites">我的收藏</div>
          <div class="nav-item" @click="goToAddress">我的地址</div>
          <div class="nav-item" @click="goToPaymentMethods">支付方式</div>
          <div class="nav-item" @click="goToOrders">我的评价</div>
          <div class="nav-item" @click="goToOrders">我的足迹</div>
          <div class="nav-item" @click="goToOrders">账户设置</div>
          <div class="nav-item" @click="goToOrders">我的交易</div>
          <div class="nav-item" @click="goToOrders">客户服务</div>
          <div class="nav-item" @click="goToOrders">设置</div>
        </div>
      </div>

      <div class="main-content">
        <!-- 订单管理区域 -->
        <div class="order-management">
          <div class="section-header">
            <h3>我的订单</h3>
            <div class="view-all" @click="goToOrders">
              查看全部订单
              <span class="arrow">›</span>
            </div>
          </div>
          
          <div class="status-tabs">
            <div class="status-tab" @click="filterOrders('all')">
              <img src="/mikeShop/待付款.png" alt="待付款" class="status-icon-img">
              <span class="status-text">待付款</span>
            </div>
            <div class="status-tab" @click="filterOrders('pending')">
              <img src="/mikeShop/待收货.png" alt="待收货" class="status-icon-img">
              <span class="status-text">待收货</span>
            </div>
            <div class="status-tab" @click="filterOrders('shipped')">
              <img src="/mikeShop/待评价.png" alt="待评价" class="status-icon-img">
              <span class="status-text">待评价</span>
            </div>
            <div class="status-tab" @click="filterOrders('completed')">
              <img src="/mikeShop/售后.png" alt="售后" class="status-icon-img">
              <span class="status-text">退换/售后</span>
            </div>
            <div class="status-tab" @click="filterOrders('all')">
              <img src="/mikeShop/全部订单.png" alt="全部订单" class="status-icon-img">
              <span class="status-text">全部订单</span>
            </div>
          </div>
          
          <div class="order-list">
            <div v-for="order in filteredOrders" :key="order.id" class="order-item">
              <div class="order-header">
                <span class="order-date">{{ order.date }}</span>
                <span class="order-number">订单号: {{ order.id }}</span>
                <span class="order-status">{{ order.status }}</span>
              </div>
              <div class="order-content">
                <div class="order-image">
                  <img :src="order.productImage" alt="商品图片" />
                </div>
                <div class="order-details">
                  <h4 class="product-name">{{ order.productName }}</h4>
                  <p class="product-desc">{{ order.description }}</p>
                  <div class="order-footer">
                    <span class="product-price">¥{{ order.price }}</span>
                    <button class="action-btn" @click="viewOrder(order.id)">查看详情</button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import apiService from '../services/apiService';

const router = useRouter();

// 返回商品首页
const goBack = () => {
  router.push({ name: 'home' });
};

// 导航到编辑资料页面
const navigateToEditProfile = () => {
  router.push('/profile/edit');
};

// 处理地址管理点击事件
const handleAddressClick = () => {
  if (user.value.isLoggedIn) {
    router.push({ name: 'addressManagement' });
  } else {
    router.push('/login');
  }
};

const handleAccountClick = () => {
  if (user.value.isLoggedIn) {
    router.push({ name: 'account' });
  } else {
    router.push('/login');
  }
};

const handleMemberCenterClick = () => {
  if (user.value.isLoggedIn) {
    router.push({ name: 'member-center' });
  } else {
    router.push('/login');
  }
};

// 跳转到订单页面
const goToOrders = () => {
  if (user.value.isLoggedIn) {
    router.push({ name: 'orders' });
  } else {
    router.push('/login');
  }
};

// 跳转到收藏页面
const goToFavorites = () => {
  if (user.value.isLoggedIn) {
    router.push('/favorites');
  } else {
    router.push('/login');
  }
};

// 跳转到地址管理页面
const goToAddress = () => {
  if (user.value.isLoggedIn) {
    router.push('/addresses');
  } else {
    router.push('/login');
  }
};

// 跳转到支付方式页面
const goToPaymentMethods = () => {
  if (user.value.isLoggedIn) {
    router.push('/payment-methods');
  } else {
    router.push('/login');
  }
};

// 跳转到钱包页面
const goToWallet = () => {
  if (user.value.isLoggedIn) {
    router.push('/wallet');
  } else {
    router.push('/login');
  }
};

// 修复用户对象初始化，确保在script setup内并提供完整默认值
const user = ref(JSON.parse(localStorage.getItem('userInfo') || JSON.stringify({
  isLoggedIn: false,
  id: null,
  username: '',
  nickname: '用户',
  avatar: '',
  level: '普通会员',
  registerDate: '2023-01-01',
  lastLogin: '今天'
})));

// 从localStorage获取用户信息（修改部分）
const storedUserInfo = localStorage.getItem('userInfo');
if (storedUserInfo) {
  const parsedInfo = JSON.parse(storedUserInfo);
  user.value = { ...user.value, ...parsedInfo };
}





// 订单筛选状态
const currentFilter = ref('all');

// 获取用户订单数据
const fetchUserOrders = async () => {
  try {
    if (!user.value.id) {
      console.warn('用户未登录，无法获取订单数据');
      return;
    }
    
    const response = await apiService.getUserOrders(user.value.id);
    console.log('获取到的订单数据:', response);
    
    // 将API返回的数据转换为前端需要的格式
    if (response && response.data) {
      orders.value = response.data.map((order: any) => ({
        id: order.order_id || order.id,
        date: order.create_time || order.date || new Date().toISOString().split('T')[0],
        status: order.status || '待付款',
        productName: order.product_name || '商品名称',
        description: order.description || '商品描述',
        price: order.total_amount || order.price || '0',
        productImage: order.product_image || '/images/配饰.png'
      }));
    } else {
      orders.value = [];
    }
  } catch (error) {
    console.error('获取用户订单失败:', error);
    // 如果API调用失败，使用空数组
    orders.value = [];
  }
};

// 订单数据
const orders = ref([]);

// 筛选订单
const filterOrders = (filter: string) => {
  currentFilter.value = filter;
};

// 计算筛选后的订单
const filteredOrders = computed(() => {
  if (currentFilter.value === 'all') {
    return orders.value;
  }
  // 这里可以根据不同的筛选条件进行过滤
  return orders.value.filter(order => {
    if (currentFilter.value === 'pending') return order.status === '待收货';
    if (currentFilter.value === 'shipped') return order.status === '待评价';
    if (currentFilter.value === 'completed') return order.status === '已完成';
    return true;
  });
});

// 查看订单详情
const viewOrder = (orderId: string) => {
  console.log('查看订单详情:', orderId);
  // 跳转到订单详情页面
  router.push(`/order-detail/${orderId}`);
};

// 页面加载时获取订单数据
onMounted(() => {
  fetchUserOrders();
});
</script>

<style scoped>
/* 全局样式 - 京东风格 */
.profile-container {
  width: 100%;
  margin: 0;
  padding: 0;
  font-family: 'Microsoft YaHei', Arial, sans-serif;
  color: #333;
  background-color: #f5f5f5;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

/* 返回按钮样式 */
.back-button {
  display: none;
}

.back-button:hover {
  background-color: #f0f0f0;
}

.back-button svg {
  margin-right: 6px;
}

/* 用户信息头部 - 京东风格 */
.mike-header {
  background-color: #e1251b;
  color: white;
  padding: 12px 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 2px 8px rgba(225, 37, 27, 0.3);
  height: 60px;
}

.logo {
  font-size: 26px;
  font-weight: bold;
  color: white;
  text-shadow: 1px 1px 2px rgba(0,0,0,0.2);
}

.main-nav a {
  color: white;
  text-decoration: none;
  margin: 0 18px;
  font-size: 15px;
  font-weight: 500;
  transition: all 0.3s ease;
  position: relative;
}

.main-nav a:hover {
  color: #ffe4e1;
  text-shadow: 0 0 5px rgba(255,255,255,0.5);
}

.main-nav a::after {
  content: '';
  position: absolute;
  bottom: -2px;
  left: 0;
  width: 0;
  height: 2px;
  background-color: white;
  transition: width 0.3s ease;
}

.main-nav a:hover::after {
  width: 100%;
}

.search-bar {
  display: flex;
}

.search-bar input {
  padding: 5px;
  border: none;
  border-radius: 4px 0 0 4px;
  width: 200px;
}

.search-btn {
  background-color: #c81623;
  color: white;
  border: none;
  padding: 6px 12px;
  border-radius: 0 4px 4px 0;
  cursor: pointer;
  font-weight: 500;
  transition: background-color 0.3s ease;
}

.search-btn:hover {
  background-color: #a8121f;
}

.user-info-header {
  background: linear-gradient(135deg, #ffffff 0%, #fff8f8 100%);
  color: #333;
  padding: 25px;
  margin-bottom: 20px;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.08);
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  width: 100%;
  box-sizing: border-box;
}

.nickname {
  color: #333;
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 8px;
  text-align: center;
}

.wallet-container {
  display: flex;
  gap: 40px;
  margin-left: 20px;
  padding: 15px;
  background-color: #fff4e8;
  border: 1px solid #e1251b;
  border-radius: 4px;
  align-items: center;
  width: 400px;
  box-sizing: border-box;
}

.user-info-header {
  align-items: flex-start;
  padding: 20px;
}

.wallet-info-header .wallet-item {
  text-align: center;
}

.avatar-wrapper {
  margin-bottom: 15px;
  position: relative;
  display: flex;
  justify-content: flex-start;
}

.avatar-img {
  width: 90px;
  height: 90px;
  border-radius: 50%;
  border: 4px solid #e1251b;
  margin-bottom: 10px;
  box-shadow: 0 4px 8px rgba(225, 37, 27, 0.2);
  transition: transform 0.3s ease;
}

.avatar-img:hover {
  transform: scale(1.05);
}

.user-basic-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

.user-basic-info h2 {
  font-size: 24px;
  margin-bottom: 8px;
  font-weight: 600;
  color: #e1251b;
  letter-spacing: 0.5px;
}

.membership-level {
  color: #e1251b;
  font-size: 14px;
  margin-bottom: 8px;
  font-weight: 500;
  background-color: #fff4e8;
  padding: 4px 8px;
  border-radius: 4px;
  display: inline-block;
}

.info-row {
  display: flex;
  justify-content: space-between;
  font-size: 14px;
  color: #999;
  margin-bottom: 12px;
}

.last-login {
  color: #999;
}

.edit-profile-btn {
  background-color: #e1251b;
  color: white;
  border: 1px solid #e1251b;
  border-radius: 6px;
  padding: 8px 20px;
  font-size: 14px;
  margin-top: 15px;
  font-weight: 500;
  transition: all 0.3s ease;
  box-shadow: 0 2px 4px rgba(225, 37, 27, 0.2);
}

.edit-profile-btn:hover {
  background-color: #c81623;
  border-color: #c81623;
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(225, 37, 27, 0.3);
}

.edit-profile-btn:hover {
    background-color: #f8f8f8;
  }
  
  /* 侧边栏和主内容区域 */
  .profile-content {
    display: flex;
    background-color: white;
    min-height: calc(100vh - 120px);
  }
  
  .sidebar {
    width: 200px;
    background-color: #f8f8f8;
    border-right: 1px solid #f0f0f0;
    padding: 20px 0;
  }
  
  /* 已移至用户信息头部 */
  
  .wallet-item {
    display: flex;
    justify-content: space-between;
    margin-bottom: 10px;
    font-size: 14px;
  }
  
  .wallet-item:last-child {
    margin-bottom: 0;
  }
  
  .wallet-label {
  color: #666;
  font-size: 18px;
  font-weight: bold;
}
  
  .wallet-value {
  color: #e1251b;
  font-weight: bold;
  font-size: 24px;
}
  
  .nav-menu {
    padding: 0 15px;
  }
  
  .nav-item {
  padding: 14px 18px;
  font-size: 14px;
  color: #333;
  cursor: pointer;
  border-radius: 6px;
  margin-bottom: 8px;
  transition: all 0.3s ease;
  border-left: 3px solid transparent;
  font-weight: 500;
}

.nav-item:hover {
  background-color: #fff4e8;
  color: #e1251b;
  border-left-color: #e1251b;
  transform: translateX(3px);
}
  
  .main-content {
    flex: 1;
    padding: 20px;
  }
  
  /* 功能链接区域 */
  .feature-links {
    background-color: white;
    border: 1px solid #f0f0f0;
    border-radius: 4px;
    padding: 15px;
    margin-top: 20px;
  }
  
  .link-section {
    margin-bottom: 0;
  }
  
  .link-group {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 10px;
  }
  
  .feature-link {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 10px 15px;
    background-color: #fff;
    border: 1px solid #f0f0f0;
    border-radius: 4px;
    color: #333;
    text-decoration: none;
    cursor: pointer;
    font-size: 14px;
  }
  
  .feature-link:hover {
    background-color: #fff4e8;
    border-color: #e1251b;
    color: #e1251b;
  }
  
  .link-text {
    font-size: 14px;
  }
  
  .link-arrow {
    font-size: 16px;
    color: #999;
  }
  
  .feature-link:hover .link-arrow {
    color: #e1251b;
  }

/* 订单管理区域 */
.order-management {
  background-color: white;
  border: 1px solid #f0f0f0;
  border-radius: 4px;
  padding: 15px;
  margin-bottom: 20px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 1px solid #f5f5f5;
}

.section-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.view-all {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #666;
  text-decoration: none;
  font-size: 12px;
  cursor: pointer;
  transition: color 0.2s;
}

.view-all:hover {
  color: #e1251b;
}

.arrow {
  font-size: 14px;
  font-weight: bold;
}

.status-tabs {
  display: flex;
  justify-content: space-between;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 1px solid #f5f5f5;
}

.status-tab {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 5px;
  padding: 10px 5px;
  cursor: pointer;
  transition: all 0.2s;
  font-size: 12px;
  color: #666;
  flex: 1;
}

.status-tab:hover {
  color: #e1251b;
  background-color: #fff4e8;
  border-radius: 4px;
}

.status-icon-img {
  width: 24px;
  height: 24px;
  object-fit: contain;
}

.status-text {
  font-size: 12px;
  text-align: center;
}

.order-list {
  margin-top: 15px;
}

.order-item {
  border: 1px solid #f5f5f5;
  border-radius: 0;
  padding: 15px;
  margin-bottom: 10px;
  background-color: white;
  box-shadow: none;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
  padding-bottom: 10px;
  border-bottom: 1px solid #f5f5f5;
}

.order-date {
  font-size: 12px;
  color: #999;
}

.order-number {
  font-size: 12px;
  color: #666;
}

.order-status {
  font-size: 12px;
  color: #e1251b;
}

.order-content {
  display: flex;
  align-items: center;
}

.order-image {
  width: 60px;
  height: 60px;
  margin-right: 15px;
}

.order-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 4px;
}

.order-details {
  flex: 1;
}

.product-name {
  font-size: 14px;
  font-weight: 500;
  margin-bottom: 5px;
  color: #333;
}

.product-desc {
  font-size: 12px;
  color: #999;
  margin-bottom: 10px;
}

.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.product-price {
  font-size: 16px;
  color: #e1251b;
  font-weight: bold;
}

.action-btn {
  background-color: #e1251b;
  color: white;
  border: none;
  border-radius: 4px;
  padding: 5px 15px;
  font-size: 12px;
  cursor: pointer;
}

.action-btn:hover {
  background-color: #c81623;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .profile-content {
    flex-direction: column;
  }
  
  .sidebar {
    width: 100%;
    border-right: none;
    border-bottom: 1px solid #f0f0f0;
  }
  
  .link-group {
    grid-template-columns: 1fr;
  }
  
  .status-tabs {
    flex-wrap: wrap;
  }
  
  .status-tab {
    flex: 0 0 auto;
    min-width: 60px;
  }
}

@media (max-width: 480px) {
  .user-info-header {
    padding: 15px;
  }
  
  .avatar-img {
    width: 60px;
    height: 60px;
  }
  
  .order-content {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .order-image {
    margin-right: 0;
    margin-bottom: 10px;
  }
  
  .order-footer {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
}
</style>
