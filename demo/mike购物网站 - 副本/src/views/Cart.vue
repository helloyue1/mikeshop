<template>
  <div class="jd-cart-container">
    <!-- 京东风格顶部导航 -->
    <div class="jd-header">
      <div class="jd-header-content">
        <button class="jd-back-btn" @click="goBack">
          <span class="jd-back-icon">‹</span>
        </button>
        <h1 class="jd-page-title">购物车</h1>
      </div>
    </div>

    <!-- 购物车主内容区 -->
    <div class="jd-cart-main">
      <!-- 加载状态 -->
      <div v-if="loading" class="jd-loading">
        <div class="jd-loading-spinner"></div>
        <p>加载购物车数据中...</p>
      </div>

      <!-- 错误信息 -->
      <div v-if="error" class="jd-error">
        <p>{{ error }}</p>
        <button @click="fetchCartData" class="jd-retry-btn">重试</button>
      </div>

      <!-- 购物车为空时显示 -->
      <div v-if="!loading && !error && !cartItems.length" class="jd-empty-cart">
        <div class="jd-empty-icon">🛒</div>
        <h2>购物车还是空的</h2>
        <p>去挑选喜欢的商品吧~</p>
        <button class="jd-btn jd-btn-primary" @click="goToHome">去逛逛</button>
      </div>

      <!-- 购物车有商品时显示 -->
      <div v-if="!loading && !error && cartItems.length" class="jd-cart-content">
        <!-- 购物车商品列表 -->
        <div class="jd-cart-items">
          <div class="jd-cart-header">
            <div class="jd-checkbox-wrapper">
              <input 
                type="checkbox" 
                class="jd-checkbox" 
                :checked="allSelected" 
                @change="toggleAll"
              >
              <span>全选</span>
            </div>
            <div class="jd-cart-header-info">
              <span class="jd-header-item">商品信息</span>
              <span class="jd-header-item">单价</span>
              <span class="jd-header-item">数量</span>
              <span class="jd-header-item">小计</span>
              <span class="jd-header-item">操作</span>
            </div>
          </div>

          <!-- 购物车商品项 -->
          <div v-for="(item, index) in cartItems" :key="item.id" class="jd-cart-item">
            <div class="jd-item-checkbox">
              <input 
                type="checkbox" 
                class="jd-checkbox" 
                :checked="item.selected" 
                @change="toggleItem(index)"
              >
            </div>
            
            <div class="jd-item-content">
              <div class="jd-item-image">
                <img :src="item.image" :alt="item.name" class="jd-product-img">
              </div>
              
              <div class="jd-item-info">
                <h3 class="jd-item-name">{{ item.name }}</h3>
                <p class="jd-item-spec">{{ item.spec }}</p>
                <div class="jd-item-tags">
                  <span v-if="item.isGift" class="jd-tag jd-tag-gift">赠品</span>
                  <span v-if="item.promotion" class="jd-tag jd-tag-promotion">{{ item.promotion }}</span>
                </div>
              </div>
              
              <div class="jd-item-price">
                <span class="jd-price-symbol">¥</span>
                <span class="jd-price-amount">{{ formatPrice(item.price) }}</span>
              </div>
              
              <div class="jd-item-quantity">
                <div class="jd-quantity-control">
                  <button 
                    class="jd-quantity-btn" 
                    :disabled="item.quantity <= 1"
                    @click="decreaseQuantity(index)"
                  >-</button>
                  <input 
                    type="number" 
                    v-model="item.quantity" 
                    class="jd-quantity-input" 
                    min="1" 
                    max="99"
                    @change="updateQuantity(index)"
                  >
                  <button 
                    class="jd-quantity-btn" 
                    :disabled="item.quantity >= 99"
                    @click="increaseQuantity(index)"
                  >+</button>
                </div>
              </div>
              
              <div class="jd-item-subtotal">
                <span class="jd-price-symbol">¥</span>
                <span class="jd-price-amount">{{ formatPrice(item.price * item.quantity) }}</span>
              </div>
              
              <div class="jd-item-actions">
                <button class="jd-action-btn jd-btn-delete" @click="removeItem(index)">
                  删除
                </button>
                <button class="jd-action-btn jd-btn-favorite" @click="addToFavorites(index)">
                  移入收藏夹
                </button>
              </div>
            </div>
          </div>
        </div>

        <!-- 购物车底部结算栏 -->
        <div class="jd-cart-footer">
          <div class="jd-footer-left">
            <div class="jd-checkbox-wrapper">
              <input 
                type="checkbox" 
                class="jd-checkbox" 
                :checked="allSelected" 
                @change="toggleAll"
              >
              <span>全选</span>
            </div>
            <button class="jd-action-btn jd-btn-delete-selected" @click="removeSelected">
              删除选中商品
            </button>
            <button class="jd-action-btn jd-btn-clear-cart" @click="clearCart">
              清空购物车
            </button>
          </div>
          
          <div class="jd-footer-right">
            <div class="jd-summary-info">
              <div class="jd-summary-item">
                <span>已选商品 </span>
                <span class="jd-highlight">{{ selectedCount }}</span>
                <span> 件</span>
              </div>
              <div class="jd-summary-item">
                <span>合计：</span>
                <span class="jd-total-price">
                  <span class="jd-price-symbol">¥</span>
                  <span class="jd-price-amount">{{ formatPrice(totalPrice) }}</span>
                </span>
              </div>
              <div class="jd-discount-info" v-if="discount > 0">
                <span>已优惠：</span>
                <span class="jd-discount-amount">¥{{ formatPrice(discount) }}</span>
              </div>
            </div>
            
            <button 
              class="jd-checkout-btn" 
              :disabled="selectedCount === 0"
              @click="checkout"
            >
              结算
            </button>
          </div>
        </div>

        <!-- 推荐商品区域 -->
        <div v-if="!loading && !error" class="jd-recommend-section">
          <h2 class="jd-recommend-title">猜你喜欢</h2>
          <div class="jd-recommend-grid">
            <div v-for="product in recommendProducts" :key="product.id" class="jd-recommend-item">
              <img :src="product.image" :alt="product.name" class="jd-recommend-img">
              <h3 class="jd-recommend-name">{{ product.name }}</h3>
              <div class="jd-recommend-price">
                <span class="jd-price-symbol">¥</span>
                <span class="jd-price-amount">{{ formatPrice(product.price) }}</span>
              </div>
              <button class="jd-add-to-cart-btn" @click="addToCart(product)">
                加入购物车
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue';
import { useRouter } from 'vue-router';
import apiService from '../services/apiService';

const router = useRouter();

// 购物车数据
const cartItems = ref<any[]>([]);
const loading = ref(false);
const error = ref('');

// 推荐商品数据
const recommendProducts = ref([
  {
    id: 4,
    name: 'BusenVUE3 男士T恤',
    price: 199,
    image: '/images/男装.png'
  },
  {
    id: 5,
    name: 'BusenVUE3 女士包包',
    price: 299,
    image: '/images/配饰.png'
  },
  {
    id: 6,
    name: 'BusenVUE3 运动鞋',
    price: 399,
    image: '/images/鞋子.png'
  },
  {
    id: 7,
    name: 'BusenVUE3 时尚手表',
    price: 899,
    image: '/images/配饰.png'
  }
]);

// 计算属性
const allSelected = computed(() => {
  return cartItems.value.length > 0 && cartItems.value.every(item => item.selected);
});

const selectedCount = computed(() => {
  return cartItems.value.filter(item => item.selected).length;
});

const totalPrice = computed(() => {
  return cartItems.value
    .filter(item => item.selected)
    .reduce((sum, item) => sum + item.price * item.quantity, 0);
});

const discount = computed(() => {
  // 模拟优惠计算
  const total = totalPrice.value;
  if (total >= 1000) return 100;
  if (total >= 500) return 50;
  if (total >= 299) return 20;
  return 0;
});

// 方法
const formatPrice = (price: number) => {
  return price.toFixed(2);
};

const goBack = () => {
  router.back();
};

const goToHome = () => {
  router.push('/');
};

const toggleAll = (event: Event) => {
  const checked = (event.target as HTMLInputElement).checked;
  cartItems.value.forEach(item => {
    item.selected = checked;
  });
};

const toggleItem = (index: number) => {
  cartItems.value[index].selected = !cartItems.value[index].selected;
};

const increaseQuantity = (index: number) => {
  if (cartItems.value[index].quantity < 99) {
    cartItems.value[index].quantity++;
  }
};

const decreaseQuantity = (index: number) => {
  if (cartItems.value[index].quantity > 1) {
    cartItems.value[index].quantity--;
  }
};

const updateQuantity = (index: number) => {
  const quantity = cartItems.value[index].quantity;
  if (quantity < 1) cartItems.value[index].quantity = 1;
  if (quantity > 99) cartItems.value[index].quantity = 99;
};

const removeItem = async (index: number) => {
  if (confirm('确定要删除这个商品吗？')) {
    try {
      const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}');
      if (!userInfo.id) {
        error.value = '请先登录';
        return;
      }
      
      await apiService.deleteBuyCar(userInfo.id, cartItems.value[index].product_id);
      // 从购物车列表中移除该商品
      cartItems.value.splice(index, 1);
    } catch (err) {
      console.error('删除购物车商品失败:', err);
      error.value = '删除商品失败，请重试';
    }
  }
};

const addToFavorites = (index: number) => {
  alert('商品已移入收藏夹');
};

const removeSelected = () => {
  if (confirm('确定要删除选中的商品吗？')) {
    cartItems.value = cartItems.value.filter(item => !item.selected);
  }
};

const clearCart = () => {
  if (confirm('确定要清空购物车吗？')) {
    cartItems.value = [];
  }
};

const checkout = () => {
  if (selectedCount.value === 0) {
    alert('请选择要结算的商品');
    return;
  }
  
  // 获取选中的商品数据
  const selectedItems = cartItems.value.filter(item => item.selected);
  
  // 将选中的商品数据存储到sessionStorage中，以便在确认订单页面使用
  sessionStorage.setItem('checkoutItems', JSON.stringify(selectedItems));
  
  // 跳转到确认订单页面
  router.push('/checkout');
};

const addToCart = (product: any) => {
  alert(`${product.name} 已加入购物车`);
};

// 获取购物车数据
const fetchCartData = async () => {
  try {
    loading.value = true;
    error.value = '';
    
    const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}');
    if (!userInfo.id) {
      error.value = '请先登录';
      return;
    }
    
    const response = await apiService.getBuyCar(userInfo.id);
    console.log('购物车数据:', response);
    
    // 将后端数据转换为前端需要的格式
    if (response.data && Array.isArray(response.data)) {
      cartItems.value = response.data.map((item: any) => {
        // 确保product_id有正确的值，如果为undefined则使用id字段
        const productId = item.product_id !== undefined ? item.product_id : item.id;
        console.log('处理购物车商品:', {
          原始数据: item,
          product_id: item.product_id,
          处理后的product_id: productId
        });
        
        return {
          id: item.id || productId,
          product_id: productId,
          name: item.product_name,
          price: item.price,
          quantity: item.quantity || 1,
          image: item.image,
          selected: false,
          spec: item.product_value || '',
          isGift: false,
          promotion: ''
        };
      });
    } else {
      cartItems.value = [];
    }
  } catch (err) {
    console.error('获取购物车数据失败:', err);
    error.value = '获取购物车数据失败，请重试';
    cartItems.value = [];
  } finally {
    loading.value = false;
  }
};

// 生命周期
onMounted(() => {
  fetchCartData();
});

// 监听购物车数据变化（如果需要的话）
watch(cartItems, (newItems) => {
  console.log('购物车数据变化:', newItems);
}, { deep: true });
</script>

<style scoped>
/* 京东风格购物车样式 */
.jd-cart-container {
  min-height: 100vh;
  background-color: #f5f5f5;
}

/* 顶部导航 */
.jd-header {
  background: linear-gradient(135deg, #e1251b 0%, #c81623 100%);
  color: white;
  padding: 0;
  position: sticky;
  top: 0;
  z-index: 100;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.jd-header-content {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  width: 100%;
}

.jd-back-btn {
  background: rgba(255, 255, 255, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: 50%;
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s ease;
  margin-right: 16px;
  backdrop-filter: blur(10px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.25);
}

.jd-back-btn:hover {
  background: rgba(255, 255, 255, 0.3);
  transform: scale(1.05);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.3);
}

.jd-back-btn:active {
  transform: scale(0.98);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
}

.jd-back-icon {
  font-size: 18px;
  font-weight: 300;
  color: white;
  display: inline-block;
  transform: translateX(-1px);
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
}

.jd-page-title {
  font-size: 18px;
  font-weight: 500;
  margin: 0;
}

/* 主内容区 */
.jd-cart-main {
  width: 100%;
  padding: 20px 16px;
}

/* 空购物车 */
.jd-empty-cart {
  text-align: center;
  padding: 80px 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.jd-empty-icon {
  font-size: 80px;
  margin-bottom: 20px;
  opacity: 0.6;
}

.jd-empty-cart h2 {
  font-size: 24px;
  color: #333;
  margin-bottom: 10px;
}

.jd-empty-cart p {
  color: #666;
  margin-bottom: 30px;
}

.jd-btn {
  padding: 12px 30px;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.jd-btn-primary {
  background: #e1251b;
  color: white;
}

.jd-btn-primary:hover {
  background: #c81623;
}

/* 购物车内容 */
.jd-cart-content {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

/* 加载状态 */
.jd-loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  text-align: center;
}

.jd-loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid #e1251b;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 20px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.jd-loading p {
  color: #666;
  font-size: 16px;
  margin: 0;
}

/* 错误信息 */
.jd-error {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  text-align: center;
  background: #fff1f0;
  border-radius: 8px;
  margin: 20px 0;
}

.jd-error p {
  color: #e1251b;
  font-size: 16px;
  margin: 0 0 20px 0;
}

.jd-retry-btn {
  padding: 10px 24px;
  background: #e1251b;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.jd-retry-btn:hover {
  background: #c81623;
}

/* 购物车头部 */
.jd-cart-header {
  display: flex;
  align-items: center;
  padding: 16px 20px;
  background: #fafafa;
  border-bottom: 1px solid #eee;
}

.jd-checkbox-wrapper {
  display: flex;
  align-items: center;
  margin-right: 20px;
  min-width: 80px;
}

.jd-checkbox {
  width: 18px;
  height: 18px;
  margin-right: 8px;
  cursor: pointer;
}

.jd-cart-header-info {
  display: flex;
  flex: 1;
  align-items: center;
}

.jd-header-item {
  flex: 1;
  text-align: center;
  color: #666;
  font-size: 14px;
}

.jd-header-item:nth-child(1) {
  flex: 3;
  text-align: left;
}

/* 购物车商品项 */
.jd-cart-item {
  display: flex;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #eee;
  transition: background-color 0.2s ease;
}

.jd-cart-item:hover {
  background-color: #fafafa;
}

.jd-item-checkbox {
  margin-right: 20px;
}

.jd-item-content {
  display: flex;
  align-items: center;
  flex: 1;
}

.jd-item-image {
  width: 80px;
  height: 80px;
  margin-right: 20px;
  flex-shrink: 0;
}

.jd-product-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 4px;
}

.jd-item-info {
  flex: 3;
  margin-right: 20px;
}

.jd-item-name {
  font-size: 16px;
  color: #333;
  margin-bottom: 8px;
  font-weight: 500;
}

.jd-item-spec {
  font-size: 14px;
  color: #666;
  margin-bottom: 8px;
}

.jd-item-tags {
  display: flex;
  gap: 8px;
}

.jd-tag {
  padding: 2px 8px;
  border-radius: 2px;
  font-size: 12px;
}

.jd-tag-gift {
  background: #fff1f0;
  color: #e1251b;
  border: 1px solid #ffcdd0;
}

.jd-tag-promotion {
  background: #fff7e6;
  color: #fa8c16;
  border: 1px solid #ffd591;
}

.jd-item-price {
  flex: 1;
  text-align: center;
  color: #e1251b;
  font-weight: 500;
}

.jd-price-symbol {
  font-size: 14px;
}

.jd-price-amount {
  font-size: 16px;
}

.jd-item-quantity {
  flex: 1;
  text-align: center;
}

.jd-quantity-control {
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid #ddd;
  border-radius: 4px;
  width: 120px;
  margin: 0 auto;
}

.jd-quantity-btn {
  width: 32px;
  height: 32px;
  border: none;
  background: #f5f5f5;
  cursor: pointer;
  font-size: 16px;
  transition: background-color 0.2s ease;
}

.jd-quantity-btn:hover:not(:disabled) {
  background: #e8e8e8;
}

.jd-quantity-btn:disabled {
  color: #ccc;
  cursor: not-allowed;
}

.jd-quantity-input {
  width: 40px;
  height: 32px;
  border: none;
  border-left: 1px solid #ddd;
  border-right: 1px solid #ddd;
  text-align: center;
  font-size: 14px;
}

.jd-item-subtotal {
  flex: 1;
  text-align: center;
  color: #e1251b;
  font-weight: 500;
}

.jd-item-actions {
  flex: 1;
  text-align: center;
  display: flex;
  flex-direction: column;
  gap: 8px;
  align-items: center;
}

.jd-action-btn {
  padding: 6px 12px;
  border: none;
  border-radius: 4px;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.jd-btn-delete {
  background: #fff1f0;
  color: #e1251b;
  border: 1px solid #ffcdd0;
}

.jd-btn-delete:hover {
  background: #ffe0df;
}

.jd-btn-favorite {
  background: #f0f9ff;
  color: #1890ff;
  border: 1px solid #bae7ff;
}

.jd-btn-favorite:hover {
  background: #e6f7ff;
}

/* 购物车底部 */
.jd-cart-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px;
  background: #fafafa;
  border-top: 1px solid #eee;
}

.jd-footer-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.jd-btn-delete-selected {
  background: #fff1f0;
  color: #e1251b;
  border: 1px solid #ffcdd0;
}

.jd-btn-delete-selected:hover {
  background: #ffe0df;
}

.jd-btn-clear-cart {
  background: #f5f5f5;
  color: #666;
  border: 1px solid #ddd;
}

.jd-btn-clear-cart:hover {
  background: #e8e8e8;
}

.jd-footer-right {
  display: flex;
  align-items: center;
  gap: 30px;
}

.jd-summary-info {
  text-align: right;
}

.jd-summary-item {
  margin-bottom: 8px;
  color: #666;
  font-size: 14px;
}

.jd-summary-item:last-child {
  margin-bottom: 0;
}

.jd-highlight {
  color: #e1251b;
  font-weight: 500;
}

.jd-total-price {
  color: #e1251b;
  font-size: 18px;
  font-weight: 500;
}

.jd-discount-info {
  color: #52c41a;
  font-size: 12px;
}

.jd-discount-amount {
  color: #52c41a;
  font-weight: 500;
}

.jd-checkout-btn {
  padding: 12px 40px;
  background: #e1251b;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.jd-checkout-btn:hover:not(:disabled) {
  background: #c81623;
}

.jd-checkout-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
}

/* 推荐商品 */
.jd-recommend-section {
  margin-top: 40px;
}

.jd-recommend-title {
  font-size: 20px;
  color: #333;
  margin-bottom: 20px;
  font-weight: 500;
}

.jd-recommend-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 20px;
}

.jd-recommend-item {
  background: white;
  border-radius: 8px;
  padding: 16px;
  text-align: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: transform 0.2s ease;
}

.jd-recommend-item:hover {
  transform: translateY(-2px);
}

.jd-recommend-img {
  width: 120px;
  height: 120px;
  object-fit: cover;
  border-radius: 4px;
  margin-bottom: 12px;
}

.jd-recommend-name {
  font-size: 14px;
  color: #333;
  margin-bottom: 8px;
  font-weight: 500;
}

.jd-recommend-price {
  color: #e1251b;
  font-weight: 500;
  margin-bottom: 12px;
}

.jd-add-to-cart-btn {
  padding: 8px 20px;
  background: #e1251b;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.jd-add-to-cart-btn:hover {
  background: #c81623;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .jd-cart-header-info {
    display: none;
  }
  
  .jd-item-content {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .jd-item-info {
    margin-right: 0;
    margin-bottom: 12px;
  }
  
  .jd-item-price,
  .jd-item-quantity,
  .jd-item-subtotal,
  .jd-item-actions {
    width: 100%;
    text-align: left;
    margin-bottom: 12px;
  }
  
  .jd-cart-footer {
    flex-direction: column;
    gap: 20px;
  }
  
  .jd-footer-right {
    width: 100%;
    justify-content: space-between;
  }
  
  .jd-recommend-grid {
    grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  }
}
</style>