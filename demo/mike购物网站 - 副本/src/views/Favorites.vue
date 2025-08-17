<template>
  <div class="jd-favorites-container">
    <!-- 京东风格顶部导航 -->
    <div class="jd-header">
      <div class="jd-header-content">
        <button class="jd-back-btn" @click="goBack">
          <span class="jd-back-icon">‹</span>
        </button>
        <h1 class="jd-page-title">我的收藏</h1>
      </div>
    </div>

    <!-- 收藏主内容区 -->
    <div class="jd-favorites-main">
      <!-- 收藏为空时显示 -->
      <div v-if="!favoriteItems.length" class="jd-empty-favorites">
        <div class="jd-empty-icon">❤️</div>
        <h2>收藏夹还是空的</h2>
        <p>去收藏喜欢的商品吧~</p>
        <button class="jd-btn jd-btn-primary" @click="goToHome">去逛逛</button>
      </div>

      <!-- 收藏有商品时显示 -->
      <div v-else class="jd-favorites-content">
        <!-- 收藏管理工具栏 -->
        <div class="jd-favorites-toolbar">
          <div class="jd-toolbar-left">
            <div class="jd-checkbox-wrapper">
              <input 
                type="checkbox" 
                class="jd-checkbox" 
                :checked="allSelected" 
                @change="toggleAll"
              >
              <span>全选</span>
            </div>
            <span class="jd-selected-count">已选 {{ selectedCount }} 件</span>
          </div>
          <div class="jd-toolbar-right">
            <button class="jd-action-btn jd-btn-delete-selected" @click="removeSelected">
              删除选中
            </button>
            <button class="jd-action-btn jd-btn-add-to-cart" @click="addSelectedToCart">
              加入购物车
            </button>
          </div>
        </div>

        <!-- 收藏商品网格 -->
        <div class="jd-favorites-grid">
          <div v-for="(item, index) in favoriteItems" :key="item.id" class="jd-favorite-item">
            <div class="jd-item-checkbox">
              <input 
                type="checkbox" 
                class="jd-checkbox" 
                :checked="item.selected" 
                @change="toggleItem(index)"
              >
            </div>
            
            <div class="jd-favorite-card">
              <div class="jd-item-image">
                <img :src="item.image" :alt="item.name" class="jd-product-img">
                <div class="jd-favorite-badge" v-if="item.isFavorite">
                  <span class="jd-heart-icon">❤️</span>
                </div>
              </div>
              
              <div class="jd-item-info">
                <h3 class="jd-item-name">{{ item.name }}</h3>
                <p class="jd-item-desc">{{ item.description }}</p>
                <div class="jd-item-tags">
                  <span v-if="item.promotion" class="jd-tag jd-tag-promotion">{{ item.promotion }}</span>
                  <span v-if="item.isNew" class="jd-tag jd-tag-new">新品</span>
                </div>
              </div>
              
              <div class="jd-item-price">
                <span class="jd-price-symbol">¥</span>
                <span class="jd-price-amount">{{ formatPrice(item.price) }}</span>
                <span class="jd-original-price" v-if="item.originalPrice">¥{{ formatPrice(item.originalPrice) }}</span>
              </div>
              
              <div class="jd-item-actions">
                <button class="jd-action-btn jd-btn-add-cart" @click="addToCart(item)">
                  加入购物车
                </button>
                <button class="jd-action-btn jd-btn-remove" @click="removeItem(index)">
                  取消收藏
                </button>
              </div>
            </div>
          </div>
        </div>

        <!-- 推荐商品区域 -->
        <div class="jd-recommend-section">
          <h2 class="jd-recommend-title">猜你喜欢</h2>
          <div class="jd-recommend-grid">
            <div v-for="product in recommendProducts" :key="product.id" class="jd-recommend-item">
              <img :src="product.image" :alt="product.name" class="jd-recommend-img">
              <h3 class="jd-recommend-name">{{ product.name }}</h3>
              <div class="jd-recommend-price">
                <span class="jd-price-symbol">¥</span>
                <span class="jd-price-amount">{{ formatPrice(product.price) }}</span>
              </div>
              <div class="jd-recommend-actions">
                <button class="jd-add-to-favorites-btn" @click="addToFavorites(product)">
                  收藏
                </button>
                <button class="jd-add-to-cart-btn" @click="addToCart(product)">
                  加入购物车
                </button>
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

// 收藏数据
const favoriteItems = ref([
  {
    id: 1,
    name: 'BusenVUE3 男士休闲衬衫',
    description: '舒适透气，时尚百搭',
    price: 299,
    originalPrice: 399,
    selected: true,
    image: '/images/男装.png',
    isFavorite: true,
    promotion: '热卖',
    isNew: false
  },
  {
    id: 2,
    name: 'BusenVUE3 女士连衣裙',
    description: '优雅设计，品质面料',
    price: 399,
    originalPrice: 499,
    selected: true,
    image: '/images/女装.png',
    isFavorite: true,
    promotion: '新品上市',
    isNew: true
  },
  {
    id: 3,
    name: 'BusenVUE3 商务皮鞋',
    description: '精致工艺，商务首选',
    price: 599,
    originalPrice: 799,
    selected: false,
    image: '/images/皮鞋.png',
    isFavorite: true,
    promotion: '',
    isNew: false
  },
  {
    id: 4,
    name: 'BusenVUE3 时尚手表',
    description: '精准计时，彰显品味',
    price: 899,
    originalPrice: 1299,
    selected: false,
    image: '/images/配饰.png',
    isFavorite: true,
    promotion: '限时优惠',
    isNew: false
  }
]);

// 推荐商品数据
const recommendProducts = ref([
  {
    id: 5,
    name: 'BusenVUE3 男士T恤',
    price: 199,
    image: '/images/男装.png'
  },
  {
    id: 6,
    name: 'BusenVUE3 女士包包',
    price: 299,
    image: '/images/配饰.png'
  },
  {
    id: 7,
    name: 'BusenVUE3 运动鞋',
    price: 399,
    image: '/images/鞋子.png'
  },
  {
    id: 8,
    name: 'BusenVUE3 时尚腰带',
    price: 159,
    image: '/images/配饰.png'
  }
]);

// 计算属性
const allSelected = computed(() => {
  return favoriteItems.value.length > 0 && favoriteItems.value.every(item => item.selected);
});

const selectedCount = computed(() => {
  return favoriteItems.value.filter(item => item.selected).length;
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
  favoriteItems.value.forEach(item => {
    item.selected = checked;
  });
};

const toggleItem = (index: number) => {
  favoriteItems.value[index].selected = !favoriteItems.value[index].selected;
};

const removeItem = async (index: number) => {
  if (confirm('确定要取消收藏这个商品吗？')) {
    try {
      const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}');
      const productId = favoriteItems.value[index].id;
      
      if (userInfo.id && productId) {
        // 如果用户已登录，调用后端API删除收藏
        const response = await apiService.deleteUserCollect(userInfo.id, productId);
        if (response.success || response.code === 200) {
          favoriteItems.value.splice(index, 1);
          alert('取消收藏成功');
        } else {
          alert(response.message || '取消收藏失败');
        }
      } else {
        // 如果用户未登录，只从localStorage删除
        favoriteItems.value.splice(index, 1);
        saveFavorites();
        alert('取消收藏成功');
      }
    } catch (error) {
      console.error('删除收藏失败:', error);
      alert('取消收藏失败，请重试');
    }
  }
};

const removeSelected = async () => {
  if (confirm('确定要取消收藏选中的商品吗？')) {
    try {
      const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}');
      
      if (userInfo.id) {
        // 如果用户已登录，需要逐个删除选中的收藏
        const selectedItems = favoriteItems.value.filter(item => item.selected);
        let successCount = 0;
        
        for (const item of selectedItems) {
          try {
            const response = await apiService.deleteUserCollect(userInfo.id, item.id);
            if (response.success || response.code === 200) {
              successCount++;
            } else {
              console.error(`删除商品 ${item.id} 收藏失败:`, response.message);
            }
          } catch (error) {
            console.error(`删除商品 ${item.id} 收藏时出错:`, error);
          }
        }
        
        // 从本地移除所有选中的商品
        favoriteItems.value = favoriteItems.value.filter(item => !item.selected);
        
        if (successCount === selectedItems.length) {
          alert('取消收藏成功');
        } else {
          alert(`取消收藏部分成功，成功 ${successCount}/${selectedItems.length} 件商品`);
        }
      } else {
        // 如果用户未登录，只从localStorage删除
        favoriteItems.value = favoriteItems.value.filter(item => !item.selected);
        saveFavorites();
        alert('取消收藏成功');
      }
    } catch (error) {
      console.error('删除收藏失败:', error);
      alert('取消收藏失败，请重试');
    }
  }
};

const addToCart = (item: any) => {
  alert(`${item.name} 已加入购物车`);
};

const addSelectedToCart = () => {
  if (selectedCount.value === 0) {
    alert('请选择要加入购物车的商品');
    return;
  }
  const selectedItems = favoriteItems.value.filter(item => item.selected);
  alert(`已将 ${selectedCount.value} 件商品加入购物车`);
};

const addToFavorites = async (product: any) => {
  try {
    const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}');
    if (userInfo.id) {
      // 如果用户已登录，调用后端API添加收藏
      const collectData = {
        user_id: userInfo.id,
        product_id: product.id,
        collect_productName: product.name,
        collect_productPrice: product.price.toString(),
        collect_productImg: product.image,
        collect_description: product.description || ''
      };
      
      const response = await apiService.insertUserCollect(collectData);
      if (response.success || response.code === 200) {
        // 添加到本地收藏列表
        favoriteItems.value.push({
          id: product.id,
          name: product.name,
          description: product.description || '',
          price: product.price,
          image: product.image,
          selected: false,
          isFavorite: true
        });
        alert('收藏成功');
      } else {
        alert(response.message || '收藏失败');
      }
    } else {
      // 如果用户未登录，只添加到localStorage
      const favoriteItem = {
        id: product.id,
        name: product.name,
        description: product.description || '',
        price: product.price,
        image: product.image,
        selected: false,
        isFavorite: true
      };
      
      // 检查是否已收藏
      const existingIndex = favoriteItems.value.findIndex(item => item.id === product.id);
      if (existingIndex === -1) {
        favoriteItems.value.push(favoriteItem);
        saveFavorites();
        alert('收藏成功');
      } else {
        alert('该商品已在收藏夹中');
      }
    }
  } catch (error) {
    console.error('添加收藏失败:', error);
    alert('收藏失败，请重试');
  }
};

// 生命周期
onMounted(async () => {
  try {
    // 获取用户信息
    const userInfo = JSON.parse(localStorage.getItem('user') || '{}');
    if (userInfo.id) {
      // 从后端获取收藏数据
      const response = await apiService.getUserCollect(userInfo.id);
      if (response.success || response.code === 200) {
        // 将后端数据转换为前端格式
        favoriteItems.value = response.data.map((item: any) => ({
          id: item.product_id,
          name: item.collect_productName,
          description: item.collect_description,
          price: parseFloat(item.collect_productPrice),
          image: item.collect_productImg,
          selected: false,
          isFavorite: true
        }));
      }
    } else {
      // 如果用户未登录，从localStorage加载收藏数据
      const savedFavorites = localStorage.getItem('favorites');
      if (savedFavorites) {
        favoriteItems.value = JSON.parse(savedFavorites);
      }
    }
  } catch (error) {
    console.error('获取收藏数据失败:', error);
    // 如果API调用失败，从localStorage加载收藏数据
    const savedFavorites = localStorage.getItem('favorites');
    if (savedFavorites) {
      favoriteItems.value = JSON.parse(savedFavorites);
    }
  }
});

// 监听收藏数据变化，保存到localStorage
const saveFavorites = () => {
  localStorage.setItem('favorites', JSON.stringify(favoriteItems.value));
};

// 监听收藏数据变化
favoriteItems.value.forEach((item, index) => {
  Object.defineProperty(favoriteItems.value, index, {
    get() {
      return item;
    },
    set(newValue) {
      item = newValue;
      saveFavorites();
    }
  });
});
</script>

<style scoped>
/* 京东风格收藏页面样式 */
.jd-favorites-container {
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
.jd-favorites-main {
  width: 100%;
  padding: 20px 16px;
}

/* 空收藏 */
.jd-empty-favorites {
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

.jd-empty-favorites h2 {
  font-size: 24px;
  color: #333;
  margin-bottom: 10px;
}

.jd-empty-favorites p {
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

/* 收藏内容 */
.jd-favorites-content {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

/* 工具栏 */
.jd-favorites-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px;
  background: #fafafa;
  border-bottom: 1px solid #eee;
}

.jd-toolbar-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.jd-toolbar-right {
  display: flex;
  gap: 12px;
}

.jd-checkbox-wrapper {
  display: flex;
  align-items: center;
}

.jd-checkbox {
  width: 18px;
  height: 18px;
  margin-right: 8px;
  cursor: pointer;
}

.jd-selected-count {
  color: #666;
  font-size: 14px;
}

.jd-action-btn {
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.jd-btn-delete-selected {
  background: #fff1f0;
  color: #e1251b;
  border: 1px solid #ffcdd0;
}

.jd-btn-delete-selected:hover {
  background: #ffe0df;
}

.jd-btn-add-to-cart {
  background: #e1251b;
  color: white;
}

.jd-btn-add-to-cart:hover {
  background: #c81623;
}

/* 收藏商品网格 */
.jd-favorites-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
  padding: 20px;
}

.jd-favorite-item {
  position: relative;
}

.jd-item-checkbox {
  position: absolute;
  top: 10px;
  left: 10px;
  z-index: 10;
}

.jd-favorite-card {
  background: white;
  border-radius: 8px;
  border: 1px solid #eee;
  overflow: hidden;
  transition: all 0.2s ease;
  height: 100%;
}

.jd-favorite-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.jd-item-image {
  position: relative;
  width: 100%;
  height: 200px;
  overflow: hidden;
}

.jd-product-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.jd-favorite-badge {
  position: absolute;
  top: 10px;
  right: 10px;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 50%;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.jd-heart-icon {
  font-size: 16px;
}

.jd-item-info {
  padding: 16px;
}

.jd-item-name {
  font-size: 16px;
  color: #333;
  margin-bottom: 8px;
  font-weight: 500;
  line-height: 1.4;
  height: 44px;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.jd-item-desc {
  font-size: 14px;
  color: #666;
  margin-bottom: 8px;
  height: 20px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.jd-item-tags {
  display: flex;
  gap: 8px;
  margin-bottom: 12px;
}

.jd-tag {
  padding: 2px 8px;
  border-radius: 2px;
  font-size: 12px;
}

.jd-tag-promotion {
  background: #fff7e6;
  color: #fa8c16;
  border: 1px solid #ffd591;
}

.jd-tag-new {
  background: #f6ffed;
  color: #52c41a;
  border: 1px solid #b7eb8f;
}

.jd-item-price {
  padding: 0 16px 16px;
  display: flex;
  align-items: baseline;
  gap: 8px;
}

.jd-price-symbol {
  font-size: 14px;
  color: #e1251b;
}

.jd-price-amount {
  font-size: 18px;
  color: #e1251b;
  font-weight: 500;
}

.jd-original-price {
  font-size: 14px;
  color: #999;
  text-decoration: line-through;
}

.jd-item-actions {
  padding: 0 16px 16px;
  display: flex;
  gap: 8px;
}

.jd-btn-add-cart {
  flex: 1;
  background: #e1251b;
  color: white;
  padding: 8px 12px;
  font-size: 14px;
}

.jd-btn-add-cart:hover {
  background: #c81623;
}

.jd-btn-remove {
  flex: 1;
  background: #f5f5f5;
  color: #666;
  border: 1px solid #ddd;
  padding: 8px 12px;
  font-size: 14px;
}

.jd-btn-remove:hover {
  background: #e8e8e8;
}

/* 推荐商品 */
.jd-recommend-section {
  margin-top: 40px;
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
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
  background: #fafafa;
  border-radius: 8px;
  padding: 16px;
  text-align: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
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
  height: 40px;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.jd-recommend-price {
  color: #e1251b;
  font-weight: 500;
  margin-bottom: 12px;
}

.jd-recommend-actions {
  display: flex;
  gap: 8px;
}

.jd-add-to-favorites-btn {
  flex: 1;
  padding: 6px 12px;
  background: #fff1f0;
  color: #e1251b;
  border: 1px solid #ffcdd0;
  border-radius: 4px;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.jd-add-to-favorites-btn:hover {
  background: #ffe0df;
}

.jd-add-to-cart-btn {
  flex: 1;
  padding: 6px 12px;
  background: #e1251b;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 12px;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.jd-add-to-cart-btn:hover {
  background: #c81623;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .jd-favorites-grid {
    grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
    gap: 12px;
    padding: 12px;
  }
  
  .jd-favorites-toolbar {
    flex-direction: column;
    gap: 12px;
    align-items: flex-start;
  }
  
  .jd-toolbar-right {
    width: 100%;
    justify-content: flex-end;
  }
  
  .jd-recommend-grid {
    grid-template-columns: repeat(auto-fill, minmax(140px, 1fr));
    gap: 12px;
  }
  
  .jd-item-name {
    height: 40px;
    font-size: 14px;
  }
  
  .jd-item-desc {
    font-size: 12px;
  }
  
  .jd-item-actions {
    flex-direction: column;
  }
}
</style>