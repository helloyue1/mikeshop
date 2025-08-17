<template>
  <div class="mike-mall min-h-screen bg-gray-50 flex flex-col">
    <!-- 顶部通知栏 -->
    <div class="bg-red-600 text-white text-center py-2 text-sm">
      <p>mike购物商城 - 全场商品享8折优惠，新用户注册即送100元优惠券！<a href="#" class="underline ml-2">立即领取</a></p>
    </div>

    <!-- 头部导航 -->
    <header class="bg-white shadow-md sticky top-0 z-50 transition-all duration-300" id="main-header">
      <div class="container mx-auto px-4">
        <!-- 顶部信息栏 -->
        <div class="flex items-center justify-between py-3 text-sm text-gray-600">
          <div class="flex space-x-4">
            <a href="#" class="hover:text-red-600 transition-colors flex items-center"><i class="bi bi-map-marker mr-1"></i> 北京</a>
            <a href="#" class="hover:text-red-600 transition-colors">我的订单</a>
            <a href="#" class="hover:text-red-600 transition-colors">会员中心</a>
            <a href="#" class="hover:text-red-600 transition-colors">客户服务</a>
          </div>
          <div class="flex space-x-4">
            <div v-if="user.isLoggedIn" class="user-menu relative group">
  <a href="/profile" target="_blank" class="hover:text-red-600 transition-colors flex items-center cursor-pointer">
    <i class="bi bi-user mr-1"></i> {{ user.nickname }}
  </a>
  <div class="dropdown-menu absolute top-full right-0 bg-white shadow-lg rounded-md p-4 mt-1 w-48 opacity-0 invisible group-hover:opacity-100 group-hover:visible transition-all duration-200 transform origin-top-right scale-95 group-hover:scale-100 z-50">
    <div class="text-center pb-2 border-b border-gray-100">
      <p class="text-sm font-medium text-gray-800">欢迎回来</p>
      <p class="text-base font-bold text-red-600">{{ user.nickname }}</p>
    </div>
    <div class="grid grid-cols-2 gap-2 mt-3">
      <a href="/profile" class="text-sm py-1 hover:text-red-600 flex justify-center">个人中心</a>
      <a href="/member-center" class="text-sm py-1 hover:text-red-600 flex justify-center">会员中心</a>
      <a href="/orders" class="text-sm py-1 hover:text-red-600 flex justify-center">我的订单</a>
      <a href="#" @click="handleLogout" class="text-sm py-1 hover:text-red-600 flex justify-center">退出登录</a>
    </div>
  </div>
</div>
<div v-else class="flex items-center space-x-2">
  <a href="/login" class="hover:text-red-600 transition-colors flex items-center"><i class="bi bi-user mr-1"></i> 登录</a>
  <span class="text-gray-300">|</span>
  <a href="/register" class="hover:text-red-600 transition-colors">注册</a>
</div>
            <a href="/cart" class="hover:text-red-600 transition-colors flex items-center relative group">
              <i class="bi bi-shopping-cart mr-1"></i> 购物车
              <span class="absolute -top-2 -right-2 bg-red-600 text-white text-xs rounded-full w-5 h-5 flex items-center justify-center transform group-hover:scale-110 transition-transform">3</span>
            </a>
          </div>
        </div>

        <!-- 主Logo和搜索栏 -->
        <div class="flex items-center justify-between py-4">
          <div class="flex items-center">
            <a href="#" class="text-3xl font-bold text-red-600 flex items-center">
              <i class="bi bi-shop-window mr-2"></i>
              <span>mike购物商城</span>
            </a>
          </div>

          <div class="w-1/2 relative">
            <div class="relative">
              <input
                type="text"
                placeholder="搜索商品、品牌、品类"
                class="w-full py-3 px-4 pr-12 rounded-full border-2 border-red-600 focus:outline-none focus:ring-2 focus:ring-red-300 focus:border-red-500 transition-all"
              >
              <button class="absolute right-0 top-0 h-full px-6 bg-red-600 text-white rounded-r-full hover:bg-red-700 transition-colors flex items-center">
                <i class="bi bi-search text-lg mr-2"></i> 搜索
              </button>
            </div>
            <div class="flex justify-between mt-2 text-xs text-gray-500">
              <div class="flex space-x-3">
                <a href="#" class="hover:text-red-600 transition-colors">手机</a>
                <a href="#" class="hover:text-red-600 transition-colors">电脑</a>
                <a href="#" class="hover:text-red-600 transition-colors">家电</a>
                <a href="#" class="hover:text-red-600 transition-colors">美妆</a>
                <a href="#" class="hover:text-red-600 transition-colors">服饰</a>
              </div>
              <a href="#" class="text-red-600 hover:underline">热搜榜</a>
            </div>
          </div>
        </div>

        <!-- 主导航菜单 -->
        <nav class="border-t border-gray-100">
          <ul class="flex">
            <li class="relative group">
              <a href="#" class="inline-block py-3 px-6 font-medium text-red-600 hover:bg-red-50 transition-colors">首页</a>
            </li>
            <li class="relative group">
              <a href="#" class="inline-block py-3 px-6 font-medium text-gray-700 hover:text-red-600 transition-colors">全部商品分类</a>
              <!-- 下拉菜单 -->
              <div class="absolute left-0 top-full w-56 bg-white shadow-lg rounded-b-lg overflow-hidden z-50 invisible opacity-0 group-hover:visible group-hover:opacity-100 transition-all duration-300 transform origin-top scale-95 group-hover:scale-100">
                <div v-for="(category, index) in categoryList" :key="index" :class="['p-3', index < categoryList.length - 1 ? 'border-b border-gray-100' : '']">
                  <a href="#" class="flex items-center justify-between py-2 px-3 rounded hover:bg-gray-50 transition-colors">
                    <span>{{ category }}</span>
                    <i class="bi bi-chevron-right text-xs text-gray-400"></i>
                  </a>
                </div>
              </div>
            </li>
            <li class="relative group">
              <a href="#" class="inline-block py-3 px-6 font-medium text-gray-700 hover:text-red-600 transition-colors">限时抢购</a>
            </li>
            <li class="relative group">
              <a href="#" class="inline-block py-3 px-6 font-medium text-gray-700 hover:text-red-600 transition-colors">优惠活动</a>
            </li>
            <li class="relative group">
              <a href="#" class="inline-block py-3 px-6 font-medium text-gray-700 hover:text-red-600 transition-colors">会员专区</a>
            </li>
            <li class="relative group">
              <a href="#" class="inline-block py-3 px-6 font-medium text-gray-700 hover:text-red-600 transition-colors">品牌闪购</a>
            </li>
          </ul>
        </nav>
      </div>
    </header>

    <!-- 主内容区 -->
    <main class="flex-grow">
      <!-- 轮播图区域 -->
      <section class="relative overflow-hidden bg-white" id="carousel">
        <div class="container mx-auto px-4">
          <div class="flex flex-col md:flex-row">
            <!-- 左侧分类导航 -->
            <div class="w-full md:w-64 bg-red-600 text-white z-10">
              <ul class="py-2">
                <li v-for="(category, index) in categoryList" :key="index" class="px-4 py-3 hover:bg-red-700 transition-colors cursor-pointer flex items-center justify-between group">
                  <span class="font-medium">{{ category }}</span>
                  <i class="bi bi-chevron-right text-xs opacity-70 group-hover:translate-x-1 transition-transform"></i>
                </li>
              </ul>
            </div>

            <!-- 轮播图 -->
            <div class="w-full md:flex-1 relative h-[300px] md:h-[400px] overflow-hidden">
              <div class="carousel-container h-full flex transition-transform duration-500 ease-out" id="carousel-container">
                <div class="carousel-slide min-w-full h-full relative" v-for="(image, index) in carouselImages" :key="image.id">
                  <img :src="image.image" :alt="image.title" class="w-full h-full object-cover">
                  <div class="absolute bottom-0 left-0 right-0 bg-gradient-to-t from-black/60 to-transparent p-6 text-white">
                    <h3 class="text-xl font-bold mb-2 transform translate-y-4 opacity-0 animate-fade-in-up">{{ image.title }}</h3>
                    <p class="text-sm opacity-90 mb-4 transform translate-y-4 opacity-0 animate-fade-in-up animation-delay-200">{{ image.subtitle }}</p>
                    <a :href="image.link" class="inline-block bg-red-600 hover:bg-red-700 text-white text-sm py-2 px-4 rounded transition-colors transform translate-y-4 opacity-0 animate-fade-in-up animation-delay-400">{{ image.buttonText }}</a>
                  </div>
                </div>
              </div>

              <!-- 轮播图控制按钮 -->
              <button class="absolute left-4 top-1/2 -translate-y-1/2 w-10 h-10 bg-white/30 backdrop-blur-sm rounded-full flex items-center justify-center text-white hover:bg-white/50 transition-colors" id="prev-btn">
                <i class="bi bi-chevron-left"></i>
              </button>
              <button class="absolute right-4 top-1/2 -translate-y-1/2 w-10 h-10 bg-white/30 backdrop-blur-sm rounded-full flex items-center justify-center text-white hover:bg-white/50 transition-colors" id="next-btn">
                <i class="bi bi-chevron-right"></i>
              </button>

              <!-- 轮播图指示器 -->
              <div class="absolute bottom-4 right-4 flex space-x-2" id="carousel-indicators">
                <button v-for="(image, index) in carouselImages" :key="index" 
                        :class="['w-3 h-3 rounded-full bg-white transition-all', 
                                index === currentSlide ? 'opacity-100' : 'opacity-50']"
                        @click="goToSlide(index)">
                </button>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- 促销活动区 -->
      <section class="py-8 bg-gray-50">
        <div class="container mx-auto px-4">
          <div class="grid grid-cols-2 md:grid-cols-4 gap-4">
            <a href="#" class="block bg-white rounded-lg overflow-hidden shadow-sm hover:shadow-md transition-shadow group">
              <div class="p-4 flex items-center">
                <div class="w-12 h-12 rounded-full bg-red-100 flex items-center justify-center text-red-600 mr-3 group-hover:scale-110 transition-transform">
                  <i class="bi bi-fire text-xl"></i>
                </div>
                <div>
                  <h3 class="font-medium text-gray-800">限时秒杀</h3>
                  <p class="text-xs text-gray-500">低至1折起</p>
                </div>
              </div>
            </a>
            <a href="#" class="block bg-white rounded-lg overflow-hidden shadow-sm hover:shadow-md transition-shadow group">
              <div class="p-4 flex items-center">
                <div class="w-12 h-12 rounded-full bg-orange-100 flex items-center justify-center text-orange-600 mr-3 group-hover:scale-110 transition-transform">
                  <i class="bi bi-gift text-xl"></i>
                </div>
                <div>
                  <h3 class="font-medium text-gray-800">品牌特惠</h3>
                  <p class="text-xs text-gray-500">精选品牌</p>
                </div>
              </div>
            </a>
            <a href="#" class="block bg-white rounded-lg overflow-hidden shadow-sm hover:shadow-md transition-shadow group">
              <div class="p-4 flex items-center">
                <div class="w-12 h-12 rounded-full bg-blue-100 flex items-center justify-center text-blue-600 mr-3 group-hover:scale-110 transition-transform">
                  <i class="bi bi-trophy text-xl"></i>
                </div>
                <div>
                  <h3 class="font-medium text-gray-800">新品上市</h3>
                  <p class="text-xs text-gray-500">首发优惠</p>
                </div>
              </div>
            </a>
            <a href="#" class="block bg-white rounded-lg overflow-hidden shadow-sm hover:shadow-md transition-shadow group">
              <div class="p-4 flex items-center">
                <div class="w-12 h-12 rounded-full bg-green-100 flex items-center justify-center text-green-600 mr-3 group-hover:scale-110 transition-transform">
                  <i class="bi bi-percent text-xl"></i>
                </div>
                <div>
                  <h3 class="font-medium text-gray-800">满减活动</h3>
                  <p class="text-xs text-gray-500">多买多省</p>
                </div>
              </div>
            </a>
          </div>
        </div>
      </section>

      <!-- 商品推荐区 -->
      <section class="py-10 bg-white">
        <div class="container mx-auto px-4">
          <div class="flex justify-between items-center mb-6">
            <h2 class="text-2xl font-bold text-gray-800">精选商品</h2>
            <a href="#" class="text-red-600 hover:text-red-700 flex items-center text-sm font-medium">
              查看更多
              <i class="bi bi-chevron-right ml-1 text-xs"></i>
            </a>
          </div>

          <div class="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-6 gap-4 md:gap-6">
            <!-- 加载状态 -->
            <div v-if="loading" class="col-span-full text-center py-8">
              <div class="inline-block animate-spin rounded-full h-8 w-8 border-b-2 border-red-600"></div>
              <p class="mt-2 text-gray-600">加载商品中...</p>
            </div>
            <!-- 商品卡片 -->
            <div class="group" v-for="(product, index) in products.slice(0, 6)" :key="product.id" v-if="!loading">
                <div class="bg-white rounded-lg overflow-hidden border border-gray-100 hover:border-red-200 hover:shadow-md transition-all duration-300 flex flex-col h-full cursor-pointer" @click="goToProductDetail(product.id)">
                <div class="relative overflow-hidden aspect-square">
                  <img :src="product.image || `https://picsum.photos/300/300?random=${index+10}`" :alt="product.name" class="w-full h-full object-cover transition-transform duration-500 group-hover:scale-110">
                  <div class="absolute top-2 left-2">
                    <span class="bg-red-600 text-white text-xs py-1 px-2 rounded-full">热销</span>
                  </div>
                  <div class="absolute inset-0 bg-black/20 opacity-0 group-hover:opacity-100 transition-opacity flex items-center justify-center">
                    <button class="bg-white text-red-600 py-2 px-4 rounded-full text-sm font-medium mr-2 transform translate-y-4 group-hover:translate-y-0 transition-transform duration-300">
                      <i class="bi bi-cart-plus mr-1"></i> 加入购物车
                    </button>
                  </div>
                </div>
                <div class="p-3 flex-grow flex flex-col">
                  <h3 class="font-medium text-gray-800 mb-1 line-clamp-2 group-hover:text-red-600 transition-colors">
                    {{ product.name }}
                  </h3>
                  <div class="flex items-center mb-1">
                    <div class="flex text-yellow-400 text-xs">
                      <i class="bi bi-star"></i>
                      <i class="bi bi-star"></i>
                      <i class="bi bi-star"></i>
                      <i class="bi bi-star"></i>
                      <i class="bi bi-star-half"></i>
                    </div>
                    <span class="text-xs text-gray-500 ml-1">{{ product.sales || Math.floor(Math.random() * 1000) + 100 }}评价</span>
                  </div>
                  <p class="text-red-600 font-bold mt-auto">¥{{ product.price }}</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- 动态分类商品区 -->
      <section v-for="(category, index) in categoryList" :key="index" :class="['py-10', index % 2 === 0 ? 'bg-gray-50' : 'bg-white']">
        <div class="container mx-auto px-4">
          <div class="flex justify-between items-center mb-6">
            <h2 class="text-2xl font-bold text-gray-800">{{ category }}</h2>
            <div class="flex space-x-4">
              <a href="#" class="text-sm font-medium text-gray-600 hover:text-red-600 transition-colors">更多</a>
            </div>
          </div>

          <div class="grid grid-cols-2 md:grid-cols-4 gap-4">
            <div class="group" v-for="(product, productIndex) in products.filter(p => p.category === category).slice(0, 8)" :key="product.id">
              <div class="bg-white rounded-lg overflow-hidden border border-gray-100 hover:border-red-200 hover:shadow-md transition-all duration-300 flex flex-col h-full cursor-pointer" @click="goToProductDetail(product.id)">
                <div class="relative overflow-hidden aspect-square">
                  <img :src="product.image || `https://picsum.photos/300/300?random=${productIndex+20+index*10}`" :alt="product.name" class="w-full h-full object-cover transition-transform duration-500 group-hover:scale-110">
                </div>
                <div class="p-3">
                  <h3 class="font-medium text-gray-800 text-sm mb-1 line-clamp-2 group-hover:text-red-600 transition-colors">
                    {{ product.name }}
                  </h3>
                  <p class="text-red-600 font-bold mt-1">¥{{ product.price }}</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- 底部横幅 -->
      <section class="py-8 bg-red-600">
        <div class="container mx-auto px-4">
          <div class="flex flex-col md:flex-row items-center justify-between">
            <div class="text-white text-center md:text-left mb-4 md:mb-0">
              <h3 class="text-xl font-bold mb-1">下载mike购物商城APP</h3>
              <p class="text-sm opacity-90">新用户注册即送100元优惠券</p>
            </div>
            <div class="flex space-x-4">
              <a href="#" class="bg-white text-red-600 py-2 px-6 rounded-full text-sm font-medium hover:bg-gray-100 transition-colors flex items-center">
                <i class="bi bi-qr-code-scan mr-2"></i> 扫码下载
              </a>
              <a href="#" class="bg-transparent border border-white text-white py-2 px-6 rounded-full text-sm font-medium hover:bg-white/10 transition-colors">
                <i class="bi bi-apple mr-2"></i> App Store
              </a>
              <a href="#" class="bg-transparent border border-white text-white py-2 px-6 rounded-full text-sm font-medium hover:bg-white/10 transition-colors">
                <i class="bi bi-android mr-2"></i> Android
              </a>
            </div>
          </div>
        </div>
      </section>
    <!-- 返回顶部按钮 -->
<button id="backToTop" class="fixed bottom-6 right-6 w-12 h-12 bg-red-600 text-white rounded-full shadow-lg flex items-center justify-center opacity-0 invisible transition-all duration-300 hover:bg-red-700 z-50">
  <i class="bi bi-arrow-up text-lg"></i>
</button>

</main>

    <!-- 页脚 -->
    <footer class="bg-gray-800 text-white pt-12 pb-6">
      <div class="container mx-auto px-4">
        <div class="grid grid-cols-2 md:grid-cols-4 lg:grid-cols-5 gap-8 mb-8">
          <div class="lg:col-span-2">
            <h3 class="text-lg font-bold mb-4">mike购物商城</h3>
            <p class="text-gray-400 text-sm mb-4">mike购物商城是一家综合性在线购物平台，提供丰富的商品种类和优质的购物体验。我们致力于为用户提供最具性价比的商品和最贴心的服务。</p>
            <div class="flex space-x-4">
              <a href="#" class="w-10 h-10 rounded-full bg-gray-700 flex items-center justify-center hover:bg-red-600 transition-colors">
                <i class="bi bi-weibo"></i>
              </a>
              <a href="#" class="w-10 h-10 rounded-full bg-gray-700 flex items-center justify-center hover:bg-red-600 transition-colors">
                <i class="bi bi-wechat"></i>
              </a>
              <a href="#" class="w-10 h-10 rounded-full bg-gray-700 flex items-center justify-center hover:bg-red-600 transition-colors">
                <i class="bi bi-instagram"></i>
              </a>
              <a href="#" class="w-10 h-10 rounded-full bg-gray-700 flex items-center justify-center hover:bg-red-600 transition-colors">
                <i class="bi bi-youtube"></i>
              </a>
              </div>
            </div>
          </div>

          <div>
            <h4 class="text-sm font-bold uppercase mb-4">购物指南</h4>
            <ul class="space-y-2 text-sm">
              <li><a href="#" class="text-gray-400 hover:text-white transition-colors">购物流程</a></li>
              <li><a href="#" class="text-gray-400 hover:text-white transition-colors">会员介绍</a></li>
              <li><a href="#" class="text-gray-400 hover:text-white transition-colors">常见问题</a></li>
              <li><a href="#" class="text-gray-400 hover:text-white transition-colors">联系客服</a></li>
            </ul>
          </div>

          <div>
            <h4 class="text-sm font-bold uppercase mb-4">支付方式</h4>
            <ul class="space-y-2 text-sm">
              <li><a href="#" class="text-gray-400 hover:text-white transition-colors">在线支付</a></li>
              <li><a href="#" class="text-gray-400 hover:text-white transition-colors">银行转账</a></li>
              <li><a href="#" class="text-gray-400 hover:text-white transition-colors">货到付款</a></li>
              <li><a href="#" class="text-gray-400 hover:text-white transition-colors">优惠券使用</a></li>
            </ul>
          </div>

          <div>
            <h4 class="text-sm font-bold uppercase mb-4">配送方式</h4>
            <ul class="space-y-2 text-sm">
              <li><a href="#" class="text-gray-400 hover:text-white transition-colors">上门自提</a></li>
              <li><a href="#" class="text-gray-400 hover:text-white transition-colors">211限时达</a></li>
              <li><a href="#" class="text-gray-400 hover:text-white transition-colors">配送服务查询</a></li>
              <li><a href="#" class="text-gray-400 hover:text-white transition-colors">配送费收取标准</a></li>
            </ul>
          </div>
        </div>

        <div class="border-t border-gray-700 pt-6 mt-6 text-center text-gray-500 text-xs">
          <p>© 2023 mike购物商城 版权所有 | 京ICP备12345678号</p>
        </div>
      </footer>

      <!-- 返回顶部按钮 -->
      <button id="back-to-top" class="fixed bottom-6 right-6 bg-red-600 text-white w-12 h-12 rounded-full shadow-lg flex items-center justify-center opacity-0 invisible transition-all duration-300 hover:bg-red-700">
        <i class="bi bi-chevron-up"></i>
      </button>
    </div>
  </template>

  <script setup>
import { ref, onMounted, onUnmounted } from 'vue';
import { useRouter } from 'vue-router';
import apiService from '../services/apiService';
import axios from 'axios';

const router = useRouter();
const user = ref({ isLoggedIn: false, nickname: '' });

// 商品数据
const products = ref([]);
const loading = ref(false);

// 分类数据
const categoryList = ref([]);

onMounted(() => {
  const storedUser = localStorage.getItem('user');
  const token = localStorage.getItem('token');
  if (storedUser && token) {
    try {
      const parsedUser = JSON.parse(storedUser);
      user.value = {
        isLoggedIn: true,
        nickname: parsedUser.nickname || '用户'
      };
    } catch (e) {
      console.error('Failed to parse user data', e);
    }
  }
  
  // 加载轮播图、商品数据和分类数据
  loadCarouselImages();
  loadProducts();
  loadCategories();
});

// 跳转到商品详情页面
const goToProductDetail = (productId) => {
  router.push(`/product/${productId}`);
};

const goToProfile = () => {
  router.push('/profile');
};

const handleLogout = () => {
  localStorage.removeItem('token');
  localStorage.removeItem('user');
  user.value = { isLoggedIn: false, nickname: '' };
  router.push('/mikeshop');
};

// 加载轮播图图片
const loadCarouselImages = async () => {
  try {
    const response = await apiService.getCarouselImages();
    console.log('轮播图响应数据:', response);
    
    // 明确从response.data获取图片数据数组
    const imagesData = response.data || [];
    if (!Array.isArray(imagesData)) {
      console.warn('图片数据格式不正确，期望数组类型');
    }
    
    // 根据分类类型筛选轮播图片
    const carouselData = imagesData.filter(image => image.image_category === '轮播图片');
    console.log('筛选后的轮播图数据:', carouselData);
    console.log('所有图片数据:', imagesData);
    
    if (carouselData.length > 0) {
      carouselImages.value = carouselData.map(image => {
        // 使用实际的上传服务器地址
        const imagePath = image.image_url.startsWith('http') 
          ? image.image_url 
          : `http://localhost:3001${image.image_url}`;
        console.log(`图片 ${image.image_id} 路径:`, imagePath);
        return {
          id: image.image_id,
          image: imagePath,
          title: '精选商品',
          subtitle: '限时特惠',
          buttonText: '立即购买',
          link: '#'
        };
      });
      slideCount.value = carouselImages.value.length;
      console.log('处理后的轮播图:', carouselImages.value);
    } else {
      console.log('没有找到轮播图片，使用默认图片');
      
      // 使用实际的轮播图文件
      carouselImages.value = [
        {
          id: 1,
          image: 'http://localhost:3001/uploads/1754983984213-543974557.png',
          title: '夏季新品上市',
          subtitle: '全场低至5折，限时抢购',
          link: '#',
          buttonText: '立即查看'
        },
        {
          id: 2,
          image: 'http://localhost:3001/uploads/1754983992167-861348650.png',
          title: '会员专享福利',
          subtitle: '会员购物享双倍积分',
          link: '#',
          buttonText: '了解更多'
        },
        {
          id: 3,
          image: 'http://localhost:3001/uploads/1754983999538-335558296.png',
          title: '品牌特惠专场',
          subtitle: '精选品牌低至3折起',
          link: '#',
          buttonText: '进入专场'
        }
      ];
      slideCount.value = 3;
    }
  } catch (error) {
    console.error('加载轮播图失败:', error);
    // 使用实际的轮播图文件作为默认
    carouselImages.value = [
      {
        id: 1,
        image: 'http://localhost:3001/uploads/1754983984213-543974557.png',
        title: '夏季新品上市',
        subtitle: '全场低至5折，限时抢购',
        link: '#',
        buttonText: '立即查看'
      },
      {
        id: 2,
        image: 'http://localhost:3001/uploads/1754983992167-861348650.png',
        title: '会员专享福利',
        subtitle: '会员购物享双倍积分',
        link: '#',
        buttonText: '了解更多'
      },
      {
        id: 3,
        image: 'http://localhost:3001/uploads/1754983999538-335558296.png',
        title: '品牌特惠专场',
        subtitle: '精选品牌低至3折起',
        link: '#',
        buttonText: '进入专场'
      }
    ];
    slideCount.value = 3;
  }
};

// 加载分类数据
const loadCategories = async () => {
  try {
    const response = await axios.get('http://localhost:8080/api/selectCategory');
    if (response.data && response.data.data) {
      // 提取分类名称
      categoryList.value = response.data.data.map(item => item.category_name);
      console.log('分类数据加载成功:', categoryList.value);
    }
  } catch (error) {
    console.error('加载分类数据失败:', error);
  }
};

// 加载商品数据
const loadProducts = async () => {
  try {
    loading.value = true;
    const response = await apiService.getProductList();
    if (response.code === 200 && response.data) {
      // 处理后端返回的商品数据，过滤掉下架商品
      const allProducts = response.data.map((product) => ({
        id: product.product_id,
        product_id: product.product_id, // 保留原始product_id字段
        name: product.product_name,
        category: product.category,
        price: product.price,
        stock: product.stock,
        sales: product.sales,
        status: product.status,
        description: product.description,
        image: product.image
      }));
      
      // 过滤掉状态为"下架"的商品
      products.value = allProducts.filter(product => product.status !== '下架');
      
      console.log('商品列表数据处理结果:', products.value);
      console.log('已过滤下架商品，原始商品数量:', allProducts.length, '过滤后商品数量:', products.value.length);
    }
  } catch (error) {
    console.error('加载商品列表失败:', error);
  } finally {
    loading.value = false;
  }
};

  // 轮播图功能
  const currentSlide = ref(0);
  const slideCount = ref(3);
  const carouselImages = ref([]);
  let slideInterval;

  onMounted(() => {
    // 初始化轮播图自动播放
    startSlideInterval();

    // 初始化返回顶部按钮
    window.addEventListener('scroll', handleScroll);

    // 初始化轮播图控制按钮
    document.getElementById('prev-btn').addEventListener('click', prevSlide);
    document.getElementById('next-btn').addEventListener('click', nextSlide);

    // 初始化轮播图指示器
    const indicators = document.querySelectorAll('#carousel-indicators button');
    indicators.forEach((indicator, index) => {
      indicator.addEventListener('click', () => goToSlide(index));
    });

    // 返回顶部按钮点击事件
    document.getElementById('back-to-top').addEventListener('click', () => {
      window.scrollTo({ top: 0, behavior: 'smooth' });
    });

    // 导航栏滚动效果
    window.addEventListener('scroll', () => {
      const header = document.getElementById('main-header');
      if (window.scrollY > 100) {
        header.classList.add('py-2', 'shadow-lg');
        header.classList.remove('py-4');
      } else {
        header.classList.add('py-4');
        header.classList.remove('py-2', 'shadow-lg');
      }
    });
  });

  onUnmounted(() => {
    // 清除轮播图自动播放
    clearInterval(slideInterval);
    window.removeEventListener('scroll', handleScroll);
  });

  // 开始轮播图自动播放
  const startSlideInterval = () => {
    slideInterval = setInterval(() => {
      currentSlide.value = (currentSlide.value + 1) % slideCount.value;
      updateSlidePosition();
    }, 5000);
  };

  // 上一张幻灯片
  const prevSlide = () => {
    clearInterval(slideInterval);
    currentSlide.value = (currentSlide.value - 1 + slideCount.value) % slideCount.value;
    updateSlidePosition();
    startSlideInterval();
  };

  // 下一张幻灯片
  const nextSlide = () => {
    clearInterval(slideInterval);
    currentSlide.value = (currentSlide.value + 1) % slideCount.value;
    updateSlidePosition();
    startSlideInterval();
  };

  // 跳转到指定幻灯片
  const goToSlide = (index) => {
    clearInterval(slideInterval);
    currentSlide.value = index;
    updateSlidePosition();
    startSlideInterval();
  };

  // 更新幻灯片位置
  const updateSlidePosition = () => {
    const container = document.getElementById('carousel-container');
    if (container) {
      container.style.transform = `translateX(-${currentSlide.value * 100}%)`;
    }

    // 更新指示器状态
    const indicators = document.querySelectorAll('#carousel-indicators button');
    indicators.forEach((indicator, index) => {
      if (index === currentSlide.value) {
        indicator.classList.add('opacity-100');
        indicator.classList.remove('opacity-50');
      } else {
        indicator.classList.add('opacity-50');
        indicator.classList.remove('opacity-100');
      }
    });
  };

  // 处理滚动事件，控制返回顶部按钮显示/隐藏
  const handleScroll = () => {
    const backToTopButton = document.getElementById('back-to-top');
    if (window.scrollY > 300) {
      backToTopButton.classList.remove('opacity-0', 'invisible');
      backToTopButton.classList.add('opacity-100', 'visible');
    } else {
      backToTopButton.classList.add('opacity-0', 'invisible');
      backToTopButton.classList.remove('opacity-100', 'visible');
    }
  };
  </script>

  <style scoped>
  /* 自定义动画 */
  @keyframes fadeInUp {
    from {
      opacity: 0;
      transform: translateY(20px);
    }
    to {
      opacity: 1;
      transform: translateY(0);
    }
  }

  .animate-fade-in-up {
    animation: fadeInUp 0.5s ease-out forwards;
  }

  .animation-delay-200 {
    animation-delay: 0.2s;
  }

  .animation-delay-400 {
    animation-delay: 0.4s;
  }

  /* 平滑滚动 */
  html {
    scroll-behavior: smooth;
  }

  /* 自定义滚动条 */
  ::-webkit-scrollbar {
    width: 6px;
    height: 6px;
  }

  ::-webkit-scrollbar-track {
    background: #f1f1f1;
  }

  ::-webkit-scrollbar-thumb {
    background: #d1d1d1;
    border-radius: 3px;
  }

  ::-webkit-scrollbar-thumb:hover {
  background: #b1b1b1;
}

/* 用户菜单样式 */
.user-menu {
  position: relative;
}

.dropdown-menu {
  z-index: 100;
}

.dropdown-menu a:hover {
  color: #e1251b !important;
}

.user-menu:hover .dropdown-menu {
  opacity: 1;
  visibility: visible;
  transform: scale(100%);
}

.user-menu .dropdown-menu {
  opacity: 0;
  visibility: hidden;
  transform: scale(95%);
  transition: all 0.2s ease-in-out;
}
</style>