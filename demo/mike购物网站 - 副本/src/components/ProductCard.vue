<template>
  <div 
    v-if="product.status !== '下架'"
    class="product-card"
    @click="goToProductDetail"
  >
    <div class="product-image">
      <img 
        :src="product.image || 'http://localhost:3001/uploads/default-product.png'" 
        :alt="product.product_name"
        class="product-img"
      />
      
      <!-- 商品状态标签 -->
      <div 
        class="product-status"
        :class="product.status"
      >
        {{ getStatusText(product.status) }}
      </div>
      
      <div class="product-overlay">
        <button class="add-to-cart-btn" @click.stop="addToCart">
          加入购物车
        </button>
      </div>
    </div>
    
    <div class="product-details">
      <h3 class="product-name">{{ product.product_name }}</h3>
      <p class="product-category">{{ product.category }}</p>
      <p class="product-price">{{ formatPrice(product.price) }}</p>
      
      <div class="product-info">
        <span>库存: {{ product.stock }}</span>
        <span>销量: {{ product.sales }}</span>
      </div>
      
      <p class="product-description">{{ product.description }}</p>
      <p class="product-spec">{{ product.product_value }}</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router';

const router = useRouter();

type Product = {
  product_id: number;
  product_name: string;
  category: string;
  price: number;
  stock: number;
  sales: number;
  image: string;
  status: string;
  description: string;
  product_value: string;
  create_time: string;
  update_time: string;
};

const props = defineProps<{
  product: Product;
}>();

const formatPrice = (price: number) => {
  return `¥${price.toLocaleString()}`;
};

const getStatusText = (status: string) => {
  switch(status) {
    case 'active': return '在售';
    case 'inactive': return '下架';
    case 'out_of_stock': return '缺货';
    default: return status;
  }
};

const goToProductDetail = () => {
  router.push(`/product/${props.product.product_id}`);
};

const addToCart = (event: MouseEvent) => {
  event.stopPropagation();
  alert(`已将 ${props.product.product_name} 加入购物车`);
};
</script>

<style scoped>
.product-card {
  background-color: #fff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  cursor: pointer;
  position: relative;
}

.product-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.product-image {
  position: relative;
  width: 100%;
  height: 280px;
  overflow: hidden;
}

.add-icon-btn {
  position: absolute;
  bottom: 0.5rem;
  right: 0.5rem;
  width: 32px;
  height: 32px;
  background-color: #ff0000;
  color: white;
  border: none;
  border-radius: 50%;
  font-size: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  transition: all 0.3s ease;
}

.add-icon-btn:hover {
  box-shadow: 0 4px 8px rgba(0,0,0,0.2);
  transform: translateY(-2px);
}

.product-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
}

.product-card:hover .product-img {
  transform: scale(1.05);
}

.product-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.2);
  display: flex;
  justify-content: center;
  align-items: center;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.product-card:hover .product-overlay {
  opacity: 1;
}

.add-to-cart-btn {
  padding: 0.75rem 1.5rem;
  background-color: #fff;
  color: #000;
  border: 1px solid #000;
  border-radius: 4px;
  cursor: pointer;
  font-size: 1rem;
  font-weight: 500;
  transition: background-color 0.3s ease, color 0.3s ease;
}

.add-to-cart-btn:hover {
  background-color: #000;
  color: #fff;
}

.product-details {
  padding: 0.5rem;
}

.product-name {
  font-size: 1rem;
  font-weight: 600;
  margin-bottom: 0.35rem;
  color: #333;
}

.product-price {
  font-size: 0.95rem;
  font-weight: 700;
  color: #000;
}

.product-category {
  font-size: 0.8rem;
  color: #666;
  margin-bottom: 0.25rem;
}

.product-info {
  display: flex;
  justify-content: space-between;
  margin: 0.25rem 0;
  font-size: 0.8rem;
  color: #888;
}

.product-description {
  font-size: 0.8rem;
  color: #555;
  margin: 0.25rem 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.product-spec {
  font-size: 0.8rem;
  color: #777;
  margin-top: 0.25rem;
}

.product-status {
  position: absolute;
  top: 0.5rem;
  left: 0.5rem;
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  font-size: 0.7rem;
  font-weight: 600;
  color: white;
}

.product-status.active {
  background-color: #4CAF50;
}

.product-status.inactive {
  background-color: #f44336;
}

.product-status.out_of_stock {
  background-color: #ff9800;
}
</style>