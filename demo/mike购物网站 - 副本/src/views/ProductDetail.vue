<template>
  <div class="jd-product-detail">
    <!-- 京东风格顶部导航 -->
    <div class="jd-header">
      <div class="jd-header-content">
        <button class="jd-back-btn" @click="goBack">
          <span class="jd-back-icon">‹</span>
        </button>
        <h1 class="jd-page-title">商品详情</h1>
      </div>
    </div>

    <!-- 商品主体内容 -->
    <div class="jd-product-container">
      <!-- 左侧商品图片 -->
      <div class="jd-product-gallery">
        <div class="jd-main-image">
          <img :src="product.image" :alt="product.name" class="jd-product-img">
        </div>
        <div class="jd-thumbnail-list">
          <div 
            v-for="(image, index) in product.images" 
            :key="index" 
            class="jd-thumbnail"
            :class="{ active: currentImageIndex === index }"
            @click="selectImage(index)"
          >
            <img :src="image" :alt="`${product.name}-${index}`" class="jd-thumbnail-img">
          </div>
        </div>
      </div>

      <!-- 右侧商品信息 -->
      <div class="jd-product-info">
        <div class="jd-product-title">
          <h2>{{ product.name }}</h2>
          <div class="jd-product-subtitle">{{ product.description }}</div>
          <!-- 商品规格展示栏 -->
          <div class="jd-product-specs-display" v-if="product.specifications && product.specifications.length > 0">
            <div class="spec-display">
              {{ product.specifications.map(spec => `${spec.name}: ${spec.value}`).join(' | ') }}
            </div>
          </div>
        </div>

        <!-- 价格区域 -->
        <div class="jd-price-section">
          <div class="jd-current-price">
          <span class="jd-price-symbol">¥</span>
          <span class="jd-price-amount">{{ formatPrice(currentPrice) }}</span>
        </div>
          <div class="jd-original-price">
          <span class="jd-price-symbol">¥</span>
          <span class="jd-price-amount">{{ formatPrice(product.originalPrice) }}</span>
        </div>

        <!-- 库存信息 -->
        <div class="jd-stock-info">
  <span>库存: {{ currentStock }}件</span>
</div>
          <div class="jd-discount">{{ calculateDiscount() }}折</div>
        </div>

        <!-- 促销信息 -->
        <div class="jd-promotion-section">
          <div class="jd-promotion-item">
            <span class="jd-promotion-tag">满减</span>
            <span class="jd-promotion-text">满299元减50元</span>
          </div>
          <div class="jd-promotion-item">
            <span class="jd-promotion-tag">优惠券</span>
            <span class="jd-promotion-text">领取20元优惠券</span>
          </div>
        </div>

        <!-- 规格选择 -->
        <div class="jd-dynamic-specs">
    <div v-for="(values, name) in groupedSpecifications" :key="name" class="jd-dynamic-spec-group">
          
      <div class="jd-dynamic-spec-title">{{ name }}</div>
      <div class="jd-dynamic-spec-values">
        <button
          v-for="(value, index) in values"
          :key="index"
          class="jd-spec-option-btn"
          :class="{ 'active': selectedSpecs[name] === value }"
          @click="selectSpecification(name, value)"
        >
          {{ value }}
        </button>
      </div>
    </div>
  </div>

        <!-- 数量选择 -->
        <div class="jd-quantity-section">
          <div class="jd-quantity-title">数量</div>
          <div class="jd-quantity-control">
            <button class="jd-quantity-btn" @click="decreaseQuantity">-</button>
            <input type="number" v-model="quantity" class="jd-quantity-input" min="1" max="99">
            <button class="jd-quantity-btn" @click="increaseQuantity">+</button>
          </div>
        </div>

        <!-- 购买按钮 -->
        <div class="jd-action-buttons">
          <button class="jd-btn jd-btn-favorite" :class="{ active: isFavorite }" @click="toggleFavorite">
            <span class="jd-favorite-icon">{}</span>
            {{ isFavorite ? '已收藏' : '收藏' }}{ isFavorite ? '❤️' : '🤍' }
          </button>
          <button class="jd-btn jd-btn-cart" @click="addToCart">
            <span class="jd-cart-icon">🛒</span>
            加入购物车
          </button>
          <button class="jd-btn jd-btn-buy" @click="buyNow">
            立即购买
          </button>
          <button class="jd-btn jd-btn-service" @click="openProductCustomerService">
            <span class="jd-service-icon">💬</span>
            客服咨询
          </button>
        </div>

        <!-- 服务保障 -->
        <div class="jd-service-guarantee">
          <div class="jd-service-item">
            <span class="jd-service-icon">✓</span>
            <span>正品保证</span>
          </div>
          <div class="jd-service-item">
            <span class="jd-service-icon">✓</span>
            <span>极速发货</span>
          </div>
          <div class="jd-service-item">
            <span class="jd-service-icon">✓</span>
            <span>七天无理由退换</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 商品详情标签页 -->
    <div class="jd-detail-tabs">
      <div class="jd-tab-headers">
        <button 
          v-for="(tab, index) in tabs" 
          :key="index"
          class="jd-tab-header"
          :class="{ active: activeTab === index }"
          @click="switchTab(index)"
        >
          {{ tab }}
        </button>
      </div>
      <div class="jd-tab-content">
        <div v-if="activeTab === 0" class="jd-tab-panel">
          <div class="jd-product-description">
            <h3>商品详情</h3>
            <p>{{ product.details }}</p>
            <div class="jd-detail-images">
              <img v-for="(img, index) in product.detailImages" :key="index" :src="img" :alt="`详情图${index + 1}`" class="jd-detail-img">
            </div>
          </div>
        </div>
        <div v-if="activeTab === 1" class="jd-tab-panel">
          <div class="jd-product-specs" v-if="Object.keys(groupedSpecifications).length > 0">
            <h3>规格参数</h3>
            <table class="jd-specs-table">
              <tr v-for="(spec, index) in product.specifications" :key="index">
                <td class="jd-spec-name">{{ spec.name }}</td>
                <td class="jd-spec-value">{{ spec.value }}</td>
              </tr>
            </table>
          </div>
        </div>
        <div v-if="activeTab === 2" class="jd-tab-panel">
          <div class="jd-product-reviews">
            <h3>用户评价</h3>
            <div class="jd-review-summary">
              <div class="jd-review-score">4.8</div>
              <div class="jd-review-info">
                <div class="jd-review-stars">★★★★★</div>
                <div class="jd-review-count">共{{ product.reviews.length }}条评价</div>
              </div>
            </div>
            <div class="jd-review-list">
              <div v-for="(review, index) in product.reviews" :key="index" class="jd-review-item">
                <div class="jd-review-header">
                  <span class="jd-review-user">{{ review.user }}</span>
                  <span class="jd-review-rating">{{ '★'.repeat(review.rating) }}</span>
                </div>
                <div class="jd-review-content">{{ review.content }}</div>
                <div class="jd-review-date">{{ review.date }}</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import axios from 'axios';
import { showToast, showDialog } from 'vant';
import apiService from '../services/apiService';

const router = useRouter();
const route = useRoute();

// 从路由参数中获取商品ID
const productId = parseInt(route.params.id as string);
if (isNaN(productId)) {
  console.error('无效的商品ID');
  alert('商品不存在或已被移除');
  router.push('/');
}

// 商品数据
const product = ref({
  id: 1,
  name: 'BusenVUE3 时尚休闲男装',
  description: '2023年新款，舒适透气，时尚百搭',
  price: 299,
  originalPrice: 599,
  image: '/images/男装.png',
  images: [
    '/images/男装.png',
    '/images/女装.png',
    '/images/配饰.png'
  ],
  colors: ['黑色', '白色', '灰色', '蓝色'],
  sizes: ['S', 'M', 'L', 'XL', 'XXL'],
  details: 'BusenVUE3 时尚休闲男装，采用优质面料制作，手感柔软，透气性好。设计简约时尚，适合各种场合穿着。经典版型，修身显瘦，展现男士魅力。',
  detailImages: [
    '/images/男装.png',
    '/images/女装.png'
  ],
  specifications: [
    { name: '品牌', value: 'BusenVUE3' },
    { name: '材质', value: '棉95% 氨纶5%' },
    { name: '产地', value: '中国' },
    { name: '洗涤方式', value: '机洗或手洗' },
    { name: '适用季节', value: '四季' }
  ],
  reviews: [
    {
      user: '张***',
      rating: 5,
      content: '质量很好，穿着舒适，推荐购买！',
      date: '2023-10-15'
    },
    {
      user: '李***',
      rating: 4,
      content: '款式不错，就是尺码偏小，建议买大一码。',
      date: '2023-10-10'
    },
    {
      user: '王***',
      rating: 5,
      content: '物流很快，包装完好，商品质量满意。',
      date: '2023-10-05'
    }
  ],
  // 新增字段支持variants数据
  product_value: '',
  variants: []
});

// 状态管理
const currentImageIndex = ref(0);
const selectedImage = ref(''); // 添加selectedImage变量定义
const selectedColor = ref(0);
const selectedSize = ref(0);
const quantity = ref(1);
const activeTab = ref(0);
const tabs = ref(['商品详情', '规格参数', '用户评价']);

// 新增状态管理
const currentPrice = ref(0);
const currentStock = ref(0);
const selectedVariant = ref(null);

// 收藏功能
const isFavorite = ref(false);

const toggleFavorite = async () => {
  try {
    const userInfo = JSON.parse(localStorage.getItem('user') || '{}');
    console.log('当前用户信息:', userInfo);
    
    if (userInfo.id && userInfo.id !== 0) {
      // 如果用户已登录，调用后端API
      if (!isFavorite.value) {
        // 添加收藏 - 添加数据验证
        console.log('当前商品数据:', product.value);
        console.log('当前价格:', currentPrice.value);
        
        // 验证商品数据是否有效
        if (!product.value || !product.value.id || product.value.id === 0) {
          console.error('商品数据无效:', product.value);
          alert('商品数据加载中，请稍后再试');
          return;
        }
        
        // 确保所有必需字段都有值
        const collectData = {
          user_id: userInfo.id,
          product_id: product.value.id,
          collect_productName: product.value.name || '未知商品',
          collect_productPrice: (currentPrice.value || product.value.price || 0).toString(),
          collect_productImg: product.value.image || '/images/default-product.png',
          collect_description: product.value.description || ''
        };
        
        console.log('发送收藏数据:', collectData);
        
        const response = await apiService.insertUserCollect(collectData);
        if (response.success || response.code === 200) {
          isFavorite.value = true;
          alert('收藏成功');
        } else {
          alert(response.message || '收藏失败');
        }
      } else {
        // 删除收藏 - 传递用户ID和商品ID
        console.log('删除收藏 - 用户ID:', userInfo.id, '商品ID:', product.value.id);
        const response = await apiService.deleteUserCollect(userInfo.id, product.value.id);
        if (response.success || response.code === 200) {
          isFavorite.value = false;
          alert('已取消收藏');
        } else {
          alert(response.message || '取消收藏失败');
        }
      }
    } else {
      // 如果用户未登录，使用localStorage
      console.log('用户未登录，使用localStorage存储收藏');
      
      // 验证商品数据是否有效
      if (!product.value || !product.value.id || product.value.id === 0) {
        console.error('商品数据无效:', product.value);
        alert('商品数据加载中，请稍后再试');
        return;
      }
      
      isFavorite.value = !isFavorite.value;
      
      // 获取现有收藏数据
      const savedFavorites = localStorage.getItem('favorites');
      let favorites = savedFavorites ? JSON.parse(savedFavorites) : [];
      
      if (isFavorite.value) {
        // 添加到收藏
        const favoriteItem = {
          id: product.value.id,
          name: product.value.name || '未知商品',
          description: product.value.description || '',
          price: currentPrice.value || product.value.price || 0,
          originalPrice: product.value.originalPrice || 0,
          image: product.value.image || '/images/default-product.png',
          isFavorite: true,
          selected: false
        };
        
        // 检查是否已收藏
        const existingIndex = favorites.findIndex(item => item.id === product.value.id);
        if (existingIndex === -1) {
          favorites.push(favoriteItem);
          alert('收藏成功（本地存储）');
        }
      } else {
        // 从收藏中移除
        favorites = favorites.filter(item => item.id !== product.value.id);
        alert('已取消收藏（本地存储）');
      }
      
      // 保存到localStorage
      localStorage.setItem('favorites', JSON.stringify(favorites));
    }
  } catch (error) {
    console.error('收藏操作失败:', error);
    alert('操作失败，请重试');
  }
};

const checkIfFavorite = async () => {
  try {
    const userInfo = JSON.parse(localStorage.getItem('user') || '{}');
    console.log('检查收藏状态 - 用户信息:', userInfo);
    
    if (userInfo.id && userInfo.id !== 0) {
      // 如果用户已登录，通过后端API检查收藏状态
      console.log('用户已登录，调用API检查收藏状态');
      const response = await apiService.getUserCollect(userInfo.id);
      console.log('收藏状态API返回:', response);
      
      if (response.success || response.code === 200) {
        const userCollects = response.data || [];
        // 检查当前商品是否在收藏列表中
        isFavorite.value = userCollects.some((collect: any) => {
          console.log('检查收藏项:', collect, '商品ID:', product.value.id);
          return collect.product_id === product.value.id;
        });
        console.log('收藏状态:', isFavorite.value);
      } else {
        console.error('获取收藏列表失败:', response.message);
        isFavorite.value = false;
      }
    } else {
      // 如果用户未登录，检查localStorage
      console.log('用户未登录，检查localStorage收藏状态');
      const savedFavorites = localStorage.getItem('favorites');
      if (savedFavorites) {
        const favorites = JSON.parse(savedFavorites);
        isFavorite.value = favorites.some((item: any) => item.id === product.value.id);
        console.log('localStorage收藏状态:', isFavorite.value);
      } else {
        isFavorite.value = false;
        console.log('localStorage中无收藏数据');
      }
    }
  } catch (error) {
    console.error('检查收藏状态失败:', error);
    isFavorite.value = false;
  }
};

// 生命周期钩子 - 获取商品数据
onMounted(async () => {
  let retryCount = 0;
  const maxRetries = 3;
  
  const fetchProductData = async () => {
    try {
      // 获取商品详情
      const response = await apiService.getProductInfo(productId);
      console.log('API返回的商品数据:', response);
      
      if (response.success || response.code === 200) {
        // 处理API返回的数据结构
        let productData = response.data !== undefined ? response.data : response;
        
        // 如果productData是数组，取第一个元素
        if (Array.isArray(productData)) {
          productData = productData[0] || {};
        }
        
        console.log('处理后的商品数据:', productData);
        
        // 确保商品数据有有效的ID
        if (!productData.id || productData.id === 0) {
          productData.id = parseInt(productId) || 1; // 使用路由参数中的ID或默认值1
        }
        
        product.value = productData;
        currentPrice.value = product.value.price || 0;
        currentStock.value = product.value.stock || 0;
        selectedImage.value = product.value.image || '/images/placeholder.png';
        selectedColor.value = product.value.colors && product.value.colors.length > 0 ? product.value.colors[0] : '暂无';
        selectedSize.value = product.value.sizes && product.value.sizes.length > 0 ? product.value.sizes[0] : '暂无';
        
        // 检查收藏状态
        await checkIfFavorite();
      } else {
        throw new Error(response.message || '获取商品详情失败');
      }
    } catch (error) {
      console.error('获取商品详情失败:', error);
      retryCount++;
      
      if (retryCount < maxRetries) {
        console.log(`第${retryCount}次重试获取商品数据...`);
        // 延迟1秒后重试
        await new Promise(resolve => setTimeout(resolve, 1000));
        return fetchProductData();
      } else {
        // 重试次数用完，显示错误但不阻止页面继续
        console.error('获取商品详情失败，已重试3次');
        // 使用默认数据，不显示错误提示
        const validProductId = parseInt(productId) || 1; // 确保ID有效
        product.value = {
          id: validProductId,
          name: '商品信息加载失败',
          description: '无法获取商品详情，请刷新页面重试',
          price: 0,
          originalPrice: 0,
          image: '/images/placeholder.png',
          images: ['/images/placeholder.png'],
          colors: ['暂无'],
          sizes: ['暂无'],
          details: '商品信息暂时无法加载，请稍后刷新页面重试。',
          detailImages: ['/images/placeholder.png'],
          specifications: [],
          reviews: [],
          product_value: '',
          variants: [],
          stock: 0
        };
        currentPrice.value = 0;
        currentStock.value = 0;
        selectedImage.value = product.value.image;
        selectedColor.value = product.value.colors && product.value.colors.length > 0 ? product.value.colors[0] : '暂无';
        selectedSize.value = product.value.sizes && product.value.sizes.length > 0 ? product.value.sizes[0] : '暂无';
      }
    }
  };
  
  await fetchProductData();
});

// 方法
const goBack = () => {
  router.back();
};

const selectImage = (index: number) => {
  currentImageIndex.value = index;
  selectedImage.value = product.value.images && product.value.images.length > index ? product.value.images[index] : product.value.image || '/images/placeholder.png';
};

const selectColor = (index: number) => {
  if (product.value.colors && product.value.colors.length > index) {
    selectedColor.value = index;
  }
};

const selectSize = (index: number) => {
  if (product.value.sizes && product.value.sizes.length > index) {
    selectedSize.value = index;
  }
};

const increaseQuantity = () => {
  if (quantity.value < 99) {
    quantity.value++;
  }
};

const decreaseQuantity = () => {
  if (quantity.value > 1) {
    quantity.value--;
  }
};

const formatPrice = (price: number) => {
  if (price === undefined || price === null || isNaN(price)) {
    return '0';
  }
  return price.toLocaleString();
};

const calculateDiscount = () => {
  // 处理originalPrice缺失或为0的情况
  if (!product.value.originalPrice || product.value.originalPrice <= 0 || isNaN(product.value.originalPrice)) {
    return '无';
  }
  if (!product.value.price || product.value.price <= 0 || isNaN(product.value.price)) {
    return '无';
  }
  return Math.round((product.value.price / product.value.originalPrice) * 10);
};

const addToCart = async () => {
  try {
    console.log('开始添加购物车...');
    
    // 检查用户是否登录
    const userInfo = JSON.parse(localStorage.getItem('user') || '{}');
    const token = localStorage.getItem('token');
    console.log('用户信息:', userInfo);
    console.log('用户token:', token);
    
    if (!userInfo.id) {
      console.error('用户未登录，用户ID不存在');
      alert('请先登录后再添加购物车');
      router.push('/login');
      return;
    }
    
    if (!token) {
      console.error('用户token不存在');
      alert('请先登录后再添加购物车');
      router.push('/login');
      return;
    }
    
    // 构建商品规格字符串
    let productValue = '';
    if (Object.keys(selectedSpecs.value).length > 0) {
      productValue = Object.entries(selectedSpecs.value)
        .map(([key, value]) => `${key}:${value}`)
        .join(';');
    }
    
    // 构建购物车数据
    const cartData = {
      user_id: userInfo.id,
      product_id: product.value.id,
      product_value: productValue,
      product_name: product.value.name,
      image: product.value.image,
      price: currentPrice.value
    };
    
    console.log('添加购物车数据:', cartData);
    
    // 调用添加购物车接口
    console.log('开始调用insertBuyCar接口...');
    const response = await apiService.insertBuyCar(cartData);
    console.log('接口返回结果:', response);
    
    if (response.success || response.code === 200) {
      alert(`成功将 ${quantity.value} 件商品加入购物车`);
    } else {
      console.error('接口返回失败:', response);
      alert(response.message || '添加购物车失败，请重试');
    }
  } catch (error) {
    console.error('添加购物车失败:', error);
    alert('添加购物车失败，请重试');
  }
};

const buyNow = () => {
  console.log('buyNow方法被调用');
  console.log('当前页面路径:', route.fullPath);
  console.log('当前路由参数:', route.params);
  
  // 检查用户是否登录
  const userInfo = JSON.parse(localStorage.getItem('user') || '{}');
  const token = localStorage.getItem('token');
  console.log('用户信息:', userInfo);
  console.log('用户token:', token);
  console.log('用户ID:', userInfo.id);
  console.log('token是否存在:', !!token);
  
  if (!userInfo.id || !token) {
    console.log('用户未登录，跳转到登录页');
    alert('请先登录后再购买');
    console.log('准备跳转到登录页');
    router.push('/login');
    console.log('已调用router.push("/login")');
    return;
  }
  
  console.log('用户已登录，继续购买流程');
  
  // 构建商品规格字符串
  let productValue = '';
  if (Object.keys(selectedSpecs.value).length > 0) {
    productValue = Object.entries(selectedSpecs.value)
      .map(([key, value]) => `${key}:${value}`)
      .join(';');
  }
  
  console.log('商品信息:');
  console.log('- 商品ID:', product.value.id);
  console.log('- 商品数量:', quantity.value);
  console.log('- 商品规格:', productValue);
  console.log('- 商品对象:', product.value);
  
  // 跳转到结算页面，传递商品信息
  console.log('准备跳转到结算页面');
  const routeParams = {
    name: 'checkout',
    query: {
      productId: product.value.id,
      quantity: quantity.value,
      productValue: productValue
    }
  };
  console.log('路由参数:', routeParams);
  
  try {
    console.log('调用router.push...');
    router.push(routeParams);
    console.log('router.push已调用成功');
  } catch (error) {
    console.error('router.push调用失败:', error);
    alert('跳转失败，请重试');
  }
};

const switchTab = (index: number) => {
  activeTab.value = index;
};

// 格式化商品规格显示
// 动态规格参数处理
const groupedSpecifications = ref({});
const selectedSpecs = ref({});

const processSpecifications = (specs: { name: string; value: string }[]) => {
  const groups: Record<string, string[]> = {};
  specs.forEach(spec => {
    if (!groups[spec.name]) {
      groups[spec.name] = [];
    }
    // 避免重复值
    if (!groups[spec.name].includes(spec.value)) {
      groups[spec.name].push(spec.value);
      // 默认选择第一个值
      if (groups[spec.name].length === 1) {
        (selectedSpecs.value as Record<string, string>)[spec.name] = spec.value;
      }
    }
  });
  console.log('规格分组数据:', groups);
  return groups;
};

const selectSpecification = (name: string, value: string) => {
  (selectedSpecs.value as Record<string, string>)[name] = value;
  updateVariantPrice();
};

// 根据选择的规格更新价格和库存
const updateVariantPrice = () => {
  if (!product.value.variants || product.value.variants.length === 0) {
    currentPrice.value = product.value.price || 0;
    currentStock.value = product.value.stock || 0;
    return;
  }

  // 构建当前选择的规格组合字符串
  const selectedCombination = Object.entries(selectedSpecs.value)
    .map(([key, value]) => `${key}:${value}`)
    .join(';');

  // 查找匹配的variant
  const matchedVariant = product.value.variants.find((variant: any) => {
    return variant.combination === selectedCombination;
  });

  if (matchedVariant) {
    selectedVariant.value = matchedVariant;
    currentPrice.value = (matchedVariant as any).price || product.value.price || 0;
    currentStock.value = (matchedVariant as any).stock || 0;
  } else {
    selectedVariant.value = null;
    currentPrice.value = product.value.price || 0;
    currentStock.value = 0;
  }
};

// 检查是否所有规格都已选择
const hasAllSpecsSelected = () => {
  const specKeys = Object.keys(groupedSpecifications.value);
  const selectedKeys = Object.keys(selectedSpecs.value);
  return specKeys.length > 0 && specKeys.every(key => selectedKeys.includes(key));
};

const formatSpecifications = (input: string | { name: string; value: string }[]) => {
  // 如果输入是数组，直接使用并处理
  if (Array.isArray(input)) {
    const groups = processSpecifications(input);
    groupedSpecifications.value = groups;
    return input;
  }
  
  const productValue = input || '';
  if (productValue.trim() === '') {
    return [];
  }
  
  try {
    console.log('规格输入数据:', input);
    const specs: { name: string; value: string }[] = [];
    // 支持分号分隔或空格分隔的键值对格式
let pairs;
if (productValue.includes(';')) {
  pairs = productValue.split(';');
} else {
  // 处理纯空格分隔的键值对（如"颜色 黑色 内存 256G"）
  const tokens = productValue.split(/\s+/).filter(t => t);
  pairs = [];
  for (let i = 0; i < tokens.length; i += 2) {
    if (i + 1 < tokens.length) {
      pairs.push(`${tokens[i]}=${tokens[i+1]}`);
    }
  }
}
    
    for (const pair of pairs) {
      const trimmedPair = pair.trim();
      if (trimmedPair) {
        // 支持等号周围有空格的情况
// 支持等号或空格分隔的属性值格式
const [attribute, value] = trimmedPair.split(/[=\s]+/);
        if (attribute && value) {
          // 分割逗号分隔的多个值
          const values = value.split(',');
          values.forEach(v => {
            const trimmedValue = v.trim();
            if (trimmedValue && !specs.some(s => s.name === attribute && s.value === trimmedValue)) {
              specs.push({
                name: attribute.trim(),
                value: trimmedValue
              });
            }
          });
        }
      }
    }
    
    const groups = processSpecifications(specs);
    groupedSpecifications.value = groups;
    console.log('解析后的规格数据:', specs);
    return specs;
  } catch (error) {
    console.error('解析商品规格失败:', error);
    return [];
  }
};

// 获取商品详情数据
const loadProductDetail = async () => {
  try {
    const productId = route.params.id;
    if (!productId) {
      console.error('商品ID不存在');
      alert('商品ID不存在，请检查URL参数');
      router.push('/'); // 重定向到首页或商品列表页
      return;
    }
    
    // 使用apiService获取商品详情
    const response = await apiService.getProductInfo(productId);
    
    // 从API响应中正确提取商品数据
    const productData = response.data && response.data[0] || {};
    
    console.log('商品数据:', productData);
    console.log('商品规格原始数据:', productData.product_value);
    console.log('商品变体数据:', productData.variants);
    
    if (productData) {
      // 解析变体数据
      let parsedVariants = []
      try {
        // 优先使用variants字段（数组格式）
        if (productData.variants && Array.isArray(productData.variants)) {
          parsedVariants = productData.variants
        } 
        // 尝试解析字符串格式的variants
        else if (productData.variants && typeof productData.variants === 'string') {
          const variantArray = JSON.parse(productData.variants)
          parsedVariants = variantArray.map((v: any) => ({
            combination: v.combination,
            price: Number(v.price),
            stock: Number(v.stock)
          }))
        }
      } catch (e) {
        console.error('解析变体数据失败:', e)
        parsedVariants = []
      }

      // 更新商品数据
      product.value = {
        id: productData.product_id || productData.id || 0,
        product_id: productData.product_id || productData.id || 0, // 保留原始product_id字段
        name: productData.product_name || productData.name || '',
        description: productData.description || '',
        price: Number(productData.price) || 0,
        originalPrice: Number(productData.original_price) || Number(productData.price) || 0,
        image: productData.image || (productData.images ? productData.images.split(',')[0] : '') || '',
        images: productData.images ? productData.images.split(',') : [],
        colors: productData.colors ? productData.colors.split(',') : [],
        sizes: productData.sizes ? productData.sizes.split(',') : [],
        details: productData.details || productData.description || '',
        detailImages: productData.detail_images ? productData.detail_images.split(',') : [],
        reviews: productData.reviews || [],
        specifications: formatSpecifications(productData.product_value || ''),
        product_value: productData.product_value || '',
        stock: Number(productData.stock) || 0,
        variants: parsedVariants
      };
      
      console.log('商品详情数据处理结果:', product.value);
      
      // 当变体数据为空时，根据product_value生成默认变体
      if (parsedVariants.length === 0 && productData.product_value) {
        const specs = formatSpecifications(productData.product_value);
        const specGroups = {};
        
        // 按规格名称分组
        specs.forEach(spec => {
          if (!specGroups[spec.name]) {
            specGroups[spec.name] = [];
          }
          if (!specGroups[spec.name].includes(spec.value)) {
            specGroups[spec.name].push(spec.value);
          }
        });
        
        // 生成所有规格组合
        const generateCombinations = (groups) => {
          const names = Object.keys(groups);
          if (names.length === 0) return [];
          
          let combinations = groups[names[0]].map(value => [{name: names[0], value}]);
          
          for (let i = 1; i < names.length; i++) {
            const temp = [];
            combinations.forEach(comb => {
              groups[names[i]].forEach(value => {
                temp.push([...comb, {name: names[i], value}]);
              });
            });
            combinations = temp;
          }
          
          return combinations.map(comb => 
            comb.map(item => `${item.name}:${item.value}`).join(';')
          );
        };
        
        const combinations = generateCombinations(specGroups);
        parsedVariants = combinations.map(comb => ({
          combination: comb,
          price: Number(productData.price) || 0,
          stock: Number(productData.stock) || 0
        }));
      }
      
      // 初始化当前价格
      currentPrice.value = product.value.price;
      
      // 如果有变体数据，更新价格显示逻辑
      if (parsedVariants.length > 0) {
        // 默认选择第一个变体的价格
        const firstVariant = parsedVariants[0];
        if (firstVariant) {
          currentPrice.value = firstVariant.price;
          currentStock.value = firstVariant.stock;
        }
      }
    }
  } catch (error) {
    console.error('获取商品详情失败:', error);
 
    
    // 初始化当前价格
    currentPrice.value = product.value.price;
    currentStock.value = 0;
    if (product.value.variants && product.value.variants.length > 0) {
      const firstVariant = product.value.variants[0];
      if (firstVariant) {
        currentPrice.value = (firstVariant as { price: number }).price;
        currentStock.value = firstVariant.stock;
      }
    } else {
      currentStock.value = 999;
    }
  }
};

// 打开商品客服功能
const openProductCustomerService = () => {
  // 跳转到客服页面
  router.push('/customer-service');
};

onMounted(() => {
  loadProductDetail();
});
</script>

<style scoped>
.jd-product-detail {
  width: 100%;
  height: 100vh;
  min-height: 100vh;
  background-color: #f5f5f5;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
  position: absolute;
  top: 0;
  left: 0;
  margin: 0;
  padding: 0;
  overflow-y: auto;
}

/* 京东风格头部 */
.jd-header {
  background: linear-gradient(90deg, #e1251b 0%, #c81623 100%);
  padding: 12px;
  position: sticky;
  top: 0;
  z-index: 100;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.jd-header-content {
  display: flex;
  align-items: center;
  width: 100%;
  margin: 0;
}

.jd-back-btn {
  background: rgba(255, 255, 255, 0.15);
  border: 1px solid rgba(255, 255, 255, 0.25);
  color: white;
  font-size: 18px;
  cursor: pointer;
  padding: 6px 10px;
  margin-right: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
  width: 36px;
  height: 36px;
  transition: all 0.2s ease;
  backdrop-filter: blur(8px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.jd-back-btn:hover {
  background: rgba(255, 255, 255, 0.25);
  border-color: rgba(255, 255, 255, 0.4);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

.jd-back-btn:active {
  transform: translateY(0);
  background: rgba(255, 255, 255, 0.1);
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
}

.jd-back-icon {
  font-size: 18px;
  font-weight: 400;
  line-height: 1;
  display: block;
}

.jd-page-title {
  color: white;
  font-size: 18px;
  font-weight: 500;
  margin: 0;
}

/* 商品主体容器 */
.jd-product-container {
  width: 100%;
  display: flex;
  gap: 20px;
  padding: 20px;
  background-color: white;
}

/* 商品图片区域 */
.jd-product-gallery {
  flex: 0 0 400px;
}

.jd-main-image {
  width: 100%;
  height: 400px;
  border: 1px solid #eee;
  border-radius: 4px;
  overflow: hidden;
  margin-bottom: 10px;
}

.jd-product-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.jd-thumbnail-list {
  display: flex;
  gap: 10px;
}

.jd-thumbnail {
  width: 80px;
  height: 80px;
  border: 2px solid #eee;
  border-radius: 4px;
  overflow: hidden;
  cursor: pointer;
  transition: border-color 0.3s;
}

.jd-thumbnail.active {
  border-color: #e1251b;
}

.jd-thumbnail-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* 商品信息区域 */
.jd-product-info {
  flex: 1;
  padding: 20px 0;
}

.jd-product-title h2 {
  font-size: 24px;
  font-weight: 600;
  color: #333;
  margin: 0 0 8px 0;
}

.jd-product-subtitle {
  color: #666;
  font-size: 14px;
  margin-bottom: 20px;
}

/* 商品规格展示栏 */
.jd-product-specs-display {
  margin: 12px 0;
  padding: 10px 0;
  border-top: 1px solid #f0f0f0;
  border-bottom: 1px solid #f0f0f0;
}

.spec-display {
  font-size: 12px;
  color: #666;
  line-height: 1.4;
  max-width: 100%;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  transition: all 0.3s ease;
}

.spec-display:hover {
  white-space: normal;
  word-wrap: break-word;
  overflow: visible;
  color: #e1251b;
}

/* 价格区域 */
.jd-price-section {
  background-color: #fef0f0;
  padding: 15px;
  border-radius: 4px;
  margin-bottom: 20px;
}

.jd-current-price {
  display: flex;
  align-items: baseline;
  margin-bottom: 8px;
}

.jd-price-symbol {
  color: #e1251b;
  font-size: 16px;
  font-weight: 500;
  margin-right: 2px;
}

.jd-current-price .jd-price-amount {
  color: #e1251b;
  font-size: 28px;
  font-weight: 700;
}

.jd-original-price {
  display: flex;
  align-items: baseline;
  margin-bottom: 8px;
}

.jd-original-price .jd-price-amount {
  color: #999;
  font-size: 14px;
  text-decoration: line-through;
}

.jd-discount {
  display: inline-block;
  background-color: #e1251b;
  color: white;
  padding: 2px 6px;
  border-radius: 2px;
  font-size: 12px;
  font-weight: 500;
}

/* 促销信息 */
.jd-promotion-section {
  margin-bottom: 20px;
}

/* 库存信息 */
.stock-info {
  font-size: 14px;
  color: #666;
  margin-top: 10px;
}

.stock-info span {
  color: #ff6b35;
  font-weight: bold;
}

.jd-promotion-item {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
}

.jd-promotion-tag {
  background-color: #e1251b;
  color: white;
  padding: 2px 6px;
  border-radius: 2px;
  font-size: 12px;
  margin-right: 8px;
  font-weight: 500;
}

.jd-promotion-text {
  color: #666;
  font-size: 14px;
}

/* 规格选择 */
.jd-dynamic-specs {
  margin: 15px 0;
}

.jd-dynamic-spec-group {
  margin-bottom: 15px;
}

.jd-dynamic-spec-title {
  font-weight: bold;
  margin-bottom: 8px;
  color: #333;
}

.jd-dynamic-spec-values {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.jd-spec-option-btn {
  padding: 8px 16px;
  border: 1px solid #ddd;
  border-radius: 4px;
  background: #fff;
  cursor: pointer;
  transition: all 0.2s;
}

.jd-spec-option-btn.active {
  border-color: #e54d42;
  background-color: #fff0f0;
  color: #e54d42;
}

.jd-spec-option-btn:hover:not(.active) {
  border-color: #ff7d00;
}

/* 数量选择 */
.jd-quantity-section {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.jd-quantity-title {
  color: #666;
  font-size: 14px;
  margin-right: 20px;
  font-weight: 500;
}

.jd-quantity-control {
  display: flex;
  align-items: center;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.jd-quantity-btn {
  width: 32px;
  height: 32px;
  border: none;
  background-color: #f5f5f5;
  color: #333;
  font-size: 16px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
}

.jd-quantity-btn:hover {
  background-color: #e8e8e8;
}

.jd-quantity-input {
  width: 50px;
  height: 32px;
  border: none;
  border-left: 1px solid #ddd;
  border-right: 1px solid #ddd;
  text-align: center;
  font-size: 14px;
  outline: none;
}

/* 购买按钮 */
.jd-action-buttons {
  display: flex;
  gap: 15px;
  margin-bottom: 20px;
}

.jd-btn {
  flex: 1;
  padding: 12px 24px;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.jd-btn-cart {
  background-color: #ff9900;
  color: white;
}

.jd-btn-cart:hover {
  background-color: #ff8800;
}

.jd-btn-buy {
  background-color: #e1251b;
  color: white;
}

.jd-btn-buy:hover {
  background-color: #c81623;
}

.jd-btn-service {
  background-color: #667eea;
  color: white;
}

.jd-btn-service:hover {
  background-color: #5a67d8;
}

.jd-service-icon {
  font-size: 18px;
}

.jd-cart-icon {
  font-size: 18px;
}

/* 服务保障 */
.jd-service-guarantee {
  display: flex;
  gap: 20px;
  padding: 15px 0;
  border-top: 1px solid #eee;
}

.jd-service-item {
  display: flex;
  align-items: center;
  gap: 5px;
  color: #666;
  font-size: 12px;
}

.jd-service-icon {
  color: #e1251b;
  font-weight: bold;
}

/* 标签页 */
.jd-detail-tabs {
  width: 100%;
  margin: 20px 0;
  background-color: white;
  border-radius: 4px;
  overflow: hidden;
}

.jd-tab-headers {
  display: flex;
  border-bottom: 1px solid #eee;
}

.jd-tab-header {
  flex: 1;
  padding: 15px;
  border: none;
  background-color: white;
  color: #666;
  font-size: 16px;
  cursor: pointer;
  transition: all 0.3s;
  position: relative;
}

.jd-tab-header:hover {
  color: #e1251b;
}

.jd-tab-header.active {
  color: #e1251b;
  font-weight: 500;
}

.jd-tab-header.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 2px;
  background-color: #e1251b;
}

.jd-tab-content {
  padding: 20px;
}

.jd-tab-panel {
  min-height: 300px;
}

.jd-product-description h3,
.jd-product-specs h3,
.jd-product-reviews h3 {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin-bottom: 15px;
}

.jd-product-description p {
  color: #666;
  line-height: 1.6;
  margin-bottom: 20px;
}

.jd-detail-images {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.jd-detail-img {
  width: 100%;
  border-radius: 4px;
}

.jd-specs-table {
  width: 100%;
  border-collapse: collapse;
}

.jd-specs-table td {
  padding: 12px;
  border-bottom: 1px solid #eee;
  font-size: 14px;
}

.jd-spec-name {
  color: #666;
  width: 120px;
}

.jd-spec-value {
  color: #333;
}

.jd-review-summary {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 20px;
  padding: 15px;
  background-color: #fef0f0;
  border-radius: 4px;
}

.jd-review-score {
  font-size: 36px;
  font-weight: 700;
  color: #e1251b;
}

.jd-review-info {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.jd-review-stars {
  color: #e1251b;
  font-size: 16px;
}

.jd-review-count {
  color: #666;
  font-size: 14px;
}

.jd-review-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.jd-review-item {
  padding: 15px;
  border: 1px solid #eee;
  border-radius: 4px;
}

.jd-review-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.jd-review-user {
  color: #333;
  font-weight: 500;
}

.jd-review-rating {
  color: #e1251b;
}

.jd-review-content {
  color: #666;
  line-height: 1.6;
  margin-bottom: 8px;
}

.jd-review-date {
  color: #999;
  font-size: 12px;
}

/* 收藏按钮样式 */
.jd-btn-favorite {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 5px;
  padding: 12px 20px;
  border: 1px solid #ddd;
  border-radius: 4px;
  background-color: white;
  color: #666;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
  flex: 1;
}

.jd-btn-favorite:hover {
  border-color: #e1251b;
  color: #e1251b;
}

.jd-btn-favorite.active {
  background-color: #fff5f5;
  border-color: #e1251b;
  color: #e1251b;
}

.jd-favorite-icon {
  font-size: 16px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .jd-product-container {
    flex-direction: column;
    padding: 10px;
  }

  .jd-product-gallery {
    flex: none;
    width: 100%;
  }

  .jd-main-image {
    height: 300px;
  }

  .jd-action-buttons {
    flex-direction: column;
  }

  .jd-service-guarantee {
    flex-direction: column;
    gap: 10px;
  }

  .jd-review-summary {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
}
</style>
