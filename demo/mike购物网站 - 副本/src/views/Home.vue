<template>
  <div class="home-container">
    <!-- 顶部导航栏 -->
    <header class="navbar">
      <div class="logo">
        <span class="logo-icon"></span>
        <h1>MIKE SHOP</h1>
      </div>
      <div class="search-container">
        <input type="text" placeholder="搜索产品..." class="search-input">
        <button class="search-btn">搜索</button>
      </div>
      <div class="user-actions">
        <button class="cart-btn" @click="openCart">
          <img src="/images/购物车.png" alt="购物车" class="cart-icon-img">
          <span class="cart-count">{{ cartcount }}</span>
        </button>
        <div class="user-profile">
          <div class="avatar" @click="handleAvatarClick">
            <img v-if="user.isLoggedIn" :src="user.avatar" alt="用户头像" class="avatar-img">
            <img v-else src="/images/登录前.png" alt="未登录头像" class="default-avatar-img">
          </div>
          <div class="user-menu" :class="{ 'show': menuOpen }">
            <button v-if="user.isLoggedIn" @click="goToProfile" class="menu-item">个人中心</button>
            <button v-if="user.isLoggedIn" @click="logout" class="menu-item">退出登录</button>
            <button v-else @click="login" class="menu-item">请登录</button>
          </div>
        </div>
      </div>
    </header>

    <!-- 轮播图 -->
    <div class="carousel-container">
        <div class="carousel-wrapper">
          <div class="carousel-track" :style="{ transform: `translateX(-${currentIndex * 100}%)` }">
            <div class="carousel-slide" v-for="(slide, index) in slides" :key="index">
              <img :src="slide.image" :alt="slide.alt" class="carousel-image">
              <div class="carousel-caption">{{ slide.caption }}</div>
            </div>
          </div>
        </div>
        <button class="carousel-button prev" @click="prevSlide">‹</button>
        <button class="carousel-button next" @click="nextSlide">›</button>
        <div class="carousel-indicators">
          <button v-for="(slide, index) in slides" :key="index" class="indicator" :class="{ active: currentIndex === index }" @click="goToSlide(index)"></button>
        </div>
      </div>

    <div class="main-content">
      <!-- 产品展示区 -->
      <main class="products-container">
        <div class="collection-title">
          <h2>BusenVUE3 系列</h2>
          <div class="view-toggle">
            <button class="grid-view active"><i class="grid-icon"></i></button>
            <button class="list-view"><i class="list-icon"></i></button>
          </div>
        </div>

        <div class="products-grid">
          <div v-if="loading" class="loading">加载中...</div>
          <div v-else-if="products.length === 0" class="no-products">暂无商品</div>
          <ProductCard 
            v-else 
            v-for="product in products" 
            :key="product.product_id" 
            :product="product" 
          />
        </div>
      </main>

      <!-- 侧边栏 -->
      <aside class="sidebar">
  <div class="sidebar-header">
    <h2>MIKE SHOP</h2>
  </div>
        <nav class="sidebar-nav">
          <ul>
            <li class="nav-item active">
              <a href="#"><img src="/images/首页.png" alt="首页" class="sidebar-icon"> 首页</a>
            </li>
            <li class="nav-item">
              <a href="#"><img src="/images/男装.png" alt="男装" class="sidebar-icon"> 男装</a>
            </li>
            <li class="nav-item">
              <a href="#"><img src="/images/女装.png" alt="女装" class="sidebar-icon"> 女装</a>
            </li>
            <li class="nav-item">
              <a href="#"><img src="/images/配饰.png" alt="配饰" class="sidebar-icon"> 配饰</a>
            </li>
            <li class="nav-item">
              <a href="#"><img src="/images/配饰.png" alt="包袋" class="sidebar-icon"> 包袋</a>
            </li>
            <li class="nav-item">
              <a href="#"><img src="/images/皮鞋.png" alt="鞋履" class="sidebar-icon"> 鞋履</a>
            </li>
            <li class="nav-item">
              <a href="#"><img src="/images/男装.png" alt="新品上市" class="sidebar-icon"> 新品上市</a>
            </li>
            <li class="nav-item">
              <a href="#"><img src="/images/女装.png" alt="折扣专区" class="sidebar-icon"> 折扣专区</a>
            </li>
            <li class="nav-item">
              <a href="#"><img src="/images/登录前.png" alt="关于我们" class="sidebar-icon"> 关于我们</a>
            </li>
            <li class="nav-item">
              <a href="#"><img src="/images/登录前.png" alt="联系我们" class="sidebar-icon"> 联系我们</a>
            </li>
          </ul>
        </nav>
        <div class="sidebar-footer">
          <p>© 2023 MIKE SHOP. 保留所有权利。</p>
        </div>
      </aside>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue';
import axios from 'axios';
import { useRouter } from 'vue-router';
import ProductCard from '../components/ProductCard.vue';

const router = useRouter();

// 购物车数据模拟
const cartcount = ref(0);

// 用户菜单状态
const menuOpen = ref(false);

// 用户状态
const user = ref({
  isLoggedIn: false,
  name: '',
  avatar: ''
});

// 处理头像点击
const handleAvatarClick = () => {
  if (user.value.isLoggedIn) {
    menuOpen.value = !menuOpen.value;
  } else {
    router.push('/login');
  }
};

// 关闭菜单
const closeMenu = () => {
  menuOpen.value = false;
};

// 登录功能
const login = () => {
  // 模拟登录
  user.value = {
    isLoggedIn: true,
    name: 'Mike',
    avatar: '/images/登录前.png'
  };
  console.log('登录后用户状态:', user.value);
  // 保存用户信息到localStorage
  localStorage.setItem('userInfo', JSON.stringify(user.value));
  console.log('用户信息已保存到localStorage');
  // 延迟一下再打开菜单，确保DOM已更新
  setTimeout(() => {
    // 登录成功后打开菜单
    menuOpen.value = true;
    console.log('登录成功，菜单状态设置为:', menuOpen.value);
    // 手动触发重绘
    const userMenu = document.querySelector('.user-menu');
    if (userMenu) {
      userMenu.classList.add('show');
      console.log('手动添加show类成功');
    } else {
      console.log('未找到user-menu元素');
    }
  }, 300);
  alert('登录成功！');
router.push('/');
};

// 初始化用户状态
const initializeUserState = () => {
  // 从localStorage获取用户信息
  const storedUserInfo = localStorage.getItem('userInfo');
  if (storedUserInfo) {
    user.value = JSON.parse(storedUserInfo);
    console.log('从localStorage加载用户状态:', user.value);
  } else {
    // 默认未登录状态
    user.value = {
      isLoggedIn: false,
      name: '',
      avatar: ''
    };
    console.log('初始化默认用户状态:', user.value);
  }
};

// 在组件挂载时立即初始化用户状态
initializeUserState();

// 点击页面其他区域关闭菜单
const handleClickOutside = (event: MouseEvent) => {
  const avatar = document.querySelector('.avatar');
  const userMenu = document.querySelector('.user-menu');

  const target = event.target as Node;
  if (avatar && userMenu && target && !avatar.contains(target) && !userMenu.contains(target)) {
    closeMenu();
  }
};

// 初始化用户状态并添加事件监听
onMounted(() => {
  initializeUserState();
  document.addEventListener('click', handleClickOutside);
  startAutoplay();
});

// 组件卸载时移除事件监听器
onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside);
  stopAutoplay();
});

// 退出登录
const logout = () => {
  console.log('退出登录前用户状态:', user.value);
  user.value = {
    isLoggedIn: false,
    name: '',
    avatar: ''
  };
  console.log('退出登录后用户状态:', user.value);
  // 清除localStorage中的用户信息
  localStorage.removeItem('userInfo');
  console.log('已清除localStorage中的用户信息');
  menuOpen.value = false;
  alert('已退出登录');
};

// 前往个人中心
const goToProfile = () => {
  menuOpen.value = false;
  router.push('/profile');
};

// 前往登录页面
const goToLogin = () => {
  router.push('/login');
};

// 打开购物车
const openCart = () => {
  router.push('/cart');
};

// 轮播图数据
const slides = [
  { image: '/images/轮播图/购物网站轮播图1.png', alt: '轮播图1', caption: '新品上市' },
  { image: '/images/轮播图/购物网站轮播图4.png', alt: '轮播图4', caption: '热门推荐' }
];

// 轮播图控制
const currentIndex = ref(0);
const autoplayInterval = ref<number | null>(null);

const nextSlide = () => {
  currentIndex.value = (currentIndex.value + 1) % slides.length;
};

const prevSlide = () => {
  currentIndex.value = (currentIndex.value - 1 + slides.length) % slides.length;
};

const goToSlide = (index: number) => {
  currentIndex.value = index;
};

const startAutoplay = () => {
  autoplayInterval.value = window.setInterval(nextSlide, 5000);
};

const stopAutoplay = () => {
  if (autoplayInterval.value) {
    clearInterval(autoplayInterval.value);
    autoplayInterval.value = null;
  }
};

// 产品数据
const products = ref<any[]>([]);
const loading = ref(false);

// 获取商品列表数据
const loadProducts = async () => {
  loading.value = true;
  try {
    const response = await axios.get('/api/getProductList');
    if (response.data.code === 200 && response.data.data) {
      products.value = response.data.data;
    }
  } catch (error) {
    console.error('获取商品列表失败:', error);
    alert('获取商品列表失败，请稍后重试');
  } finally {
    loading.value = false;
  }
};

// 在组件挂载时加载商品数据
onMounted(() => {
  loadProducts();
});
</script>

<style scoped>
/* 基础样式重置 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
  font-family: 'Helvetica Neue', Arial, sans-serif;
}

html, body {
  width: 100%;
  max-width: 100%;
  overflow-x: hidden;
  margin: 0;
  padding: 0;
}

#app {
  width: 100%;
  max-width: 100%;
  margin: 0;
  padding: 0;
}

html, body {
  width: 100%;
  max-width: 100%;
  overflow-x: hidden;
}

#app {
  width: 100%;
  max-width: 100%;
}

.home-container {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  width: 100%;
  max-width: 100%;
  margin: 0;
  padding: 0;
  overflow: hidden;
}

/* 顶部导航栏样式 */
.navbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 5%; /* 均衡内边距 */
  background-color: #fff;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 60px;
  transform-origin: bottom;
  transform: scaleY(0.95);
  z-index: 200;
  width: 100%; /* 全屏宽度 */
  max-width: 100%;
  height: 90px;
  overflow: visible;
}

.logo {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.logo-icon {
  display: inline-block;
  width: 24px;
  height: 24px;
  background-color: #000;
  border-radius: 50%;
}

.logo h1 {
  font-size: 1.5rem;
  font-weight: 700;
}

.search-container {
  display: flex;
  max-width: 800px;
  width: 100%;
}

.search-input {
  flex: 1;
  padding: 0.75rem 1rem;
  border: 1px solid #e0e0e0;
  border-radius: 4px 0 0 4px;
  font-size: 1rem;
}

.search-btn {
  padding: 0.75rem 1.5rem;
  background-color: #000;
  color: #fff;
  border: none;
  border-radius: 0 4px 4px 0;
  cursor: pointer;
  font-size: 1rem;
}

.user-actions {
  display: flex;
  gap: 1.5rem;
  align-items: center;
  position: relative;
}

.test-btn {
  padding: 0.5rem 1rem;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.user-btn, .cart-btn, .menu-btn {
  background: none;
  border: none;
  cursor: pointer;
  font-size: 1.25rem;
  position: relative;
}

.cart-count {
  position: absolute;
  top: -8px;
  right: -8px;
  background-color: #ff4d4f;
  color: white;
  font-size: 0.7rem;
  width: 18px;
  height: 18px;
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
}

.user-profile {
  position: relative;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.default-avatar-img, .avatar-img {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
  cursor: pointer;
}

.user-profile {
  position: relative;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.user-menu {
  position: absolute;
  top: 100%;
  margin-top: 8px; /* 使用margin-top代替top的calc计算 */
  right: 0;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  padding: 0.75rem 0;
  width: 140px;
  z-index: 999999; /* 提高z-index确保在最上层显示 */
  overflow: visible;
  opacity: 0;
  visibility: hidden;
  transform: translateY(-10px);
  transition: all 0.2s ease;
  max-width: calc(100vw - 20px); /* 确保菜单不会超出视口宽度 */
  white-space: nowrap; /* 防止菜单内容换行 */
  clip-path: polygon(0 0, 100% 0, 100% 100%, 0 100%); /* 确保内容不会被裁剪 */
}

.user-menu.show {
  opacity: 1;
  visibility: visible;
  transform: translateY(0);
}

.menu-item {
  display: block;
  width: 100%;
  text-align: left;
  padding: 0.5rem 1rem;
  background: none;
  border: none;
  cursor: pointer;
  font-size: 0.9rem;
}

.menu-item:hover {
  background-color: #f5f5f5;
}

/* 主要内容区域 */
.main-content {
  display: flex;
  flex-direction: column;
  gap: 2rem;
  flex: 1;
  margin-top: 90px; /* 为导航栏留出空间 */
  width: calc(100% - 160px); /* 减去侧边栏宽度 */
  margin-left: 160px; /* 为侧边栏留出空间 */
}

/* 侧边栏样式 */
.sidebar {
  width: 160px;
  background-color: #fff;
  box-shadow: 2px 0 15px rgba(0, 0, 0, 0.15);
  position: fixed;
  height: calc(100vh - 90px);
  overflow-y: auto;
  z-index: 100;
  top: 90px; /* 匹配导航栏高度 */
  left: 0;
  transition: all 0.3s ease;
}

@media screen and (max-width: 768px) {
  .sidebar {
    width: 120px;
  }
  .main-content {
    margin-left: 120px;
  }
}

.sidebar-header {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 1.5rem 1rem 1rem;
  border-bottom: 1px solid #f0f0f0;
}

.sidebar-header h2 {
  font-size: 1.1rem;
  font-weight: 700;
}

.sidebar-nav {
  padding: 1rem 0;
}

.sidebar-footer {
  padding: 1.5rem;
  border-top: 1px solid #f0f0f0;
  font-size: 0.875rem;
  color: #777;
}

.nav-item {
  padding: 0.75rem 1rem;
}

.nav-item.active {
  background-color: #f5f5f5;
  border-left: 3px solid #000;
}

.nav-item a {
  display: flex;
  align-items: center;
  gap: 0.8rem;
  text-decoration: none;
  color: #333;
  font-size: 0.95rem;
}

.nav-item a:hover {
  color: #000;
  font-weight: 500;
}

.sidebar-icon {
  display: inline-block;
  width: 18px;
  height: 18px;
  object-fit: contain;
  margin-right: 0.8rem;
}

.cart-icon-img {
  width: 24px;
  height: 24px;
  object-fit: contain;
}

.cart-count {
  position: absolute;
  bottom: -8px;
  right: -8px;
  background-color: #ff4d4f;
  color: white;
  font-size: 0.7rem;
  width: 18px;
  height: 18px;
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
}

.sidebar-nav ul {
  list-style: none;
}

/* 产品展示区域 */
.products-container {
  flex: 1;
  padding: 0.5rem 0; /* 移除左右内边距 */
  margin: 10px 0;
  width: 100%; /* 完全填充可用空间 */
}

.collection-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}

.collection-title h2 {
  font-size: 0.9rem;
  font-weight: 700;
}

.view-toggle {
  display: flex;
  gap: 0.5rem;
}

.grid-view, .list-view {
  padding: 0.5rem;
  background-color: #f5f5f5;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.grid-view.active, .list-view.active {
  background-color: #000;
  color: #fff;
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr); /* 一行显示5个商品 */
  gap: 1rem;
  width: 100%; /* 完全填充可用空间 */
  min-height: 400px;
}

.loading {
  grid-column: 1 / -1;
  text-align: center;
  padding: 3rem;
  font-size: 1.2rem;
  color: #666;
}

.no-products {
  grid-column: 1 / -1;
  text-align: center;
  padding: 3rem;
  font-size: 1.2rem;
  color: #999;
}

/* 响应式调整 */
@media (max-width: 1400px) {
  .products-grid {
    grid-template-columns: repeat(4, 1fr);
  }
}

@media (max-width: 1200px) {
  .products-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 992px) {
  .products-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .products-grid {
    grid-template-columns: 1fr;
  }
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .products-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 992px) {
  .products-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .products-grid {
    grid-template-columns: 1fr;
  }
}

/* 轮播图样式 */
.carousel-container {
  position: relative;
  width: 100%; /* 完全填充可用空间 */
  height: auto; /* 自动高度 */
  padding-top: 33.33%; /* 使用padding-top创建3:1的宽高比 */
  margin: 0 auto; /* 水平居中 */
  max-width: 1200px; /* 限制最大宽度 */
  overflow: hidden;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  transform-origin: top right;
  transform: translate(10px, 10px) scale(0.95); /* 从右上往左下缩小并保持右下移动10px */
}

.carousel-wrapper {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  overflow: hidden;
}

.carousel-track {
  display: flex;
  height: 100%;
  transition: transform 0.5s ease-in-out;
}

.carousel-slide {
  min-width: 100%;
  height: 100%;
  position: relative;
}

.carousel-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.carousel-caption {
  position: absolute;
  bottom: 20px;
  left: 50%;
  transform: translateX(-50%);
  background-color: rgba(0, 0, 0, 0.7);
  color: white;
  padding: 10px 20px;
  border-radius: 4px;
  font-size: 1.2rem;
  font-weight: bold;
}

.carousel-button {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  background-color: rgba(255, 255, 255, 0.8);
  border: none;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  font-size: 20px;
  cursor: pointer;
  z-index: 10;
  transition: background-color 0.3s ease;
}

.carousel-button:hover {
  background-color: rgba(255, 255, 255, 0.9);
}

.carousel-button.prev {
  left: 25px;
}

.carousel-button.next {
  right: 30px;
  /* 向右移动10px，当前距离右侧30px */
}

.carousel-indicators {
  position: absolute;
  bottom: 10px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 8px;
  z-index: 10;
}

.indicator {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background-color: rgba(255, 255, 255, 0.5);
  border: none;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.indicator.active {
  background-color: white;
}

@media (max-width: 768px) {
  .carousel-container {
    height: 250px;
  }
  
  .carousel-caption {
    font-size: 1rem;
    padding: 8px 16px;
  }
  
  .carousel-button {
    width: 30px;
    height: 30px;
    font-size: 16px;
  }
}
</style>