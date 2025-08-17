<template>
  <div class="order-detail-container">
    <div class="header">
      <van-nav-bar
        title="订单详情"
        left-text="返回"
        left-arrow
        @click-left="goBack"
      />
    </div>
    
    <div class="order-content" v-if="orderInfo">
      <!-- 订单状态 -->
      <div class="order-status">
        <div class="status-text">{{ getOrderStatusText(orderInfo.status) }}</div>
        <div class="status-desc">{{ getOrderStatusDesc(orderInfo.status) }}</div>
      </div>
      
      <!-- 收货地址 -->
      <div class="address-section">
        <div class="section-title">收货地址</div>
        <div class="address-info">
          <div class="user-info">
            <span class="name">{{ orderInfo.receiver_name }}</span>
            <span class="phone">{{ orderInfo.receiver_phone }}</span>
          </div>
          <div class="address">{{ orderInfo.receiver_address }}</div>
        </div>
      </div>
      
      <!-- 商品信息 -->
      <div class="products-section">
        <div class="section-title">商品信息</div>
        <div class="product-item" v-for="item in orderInfo.products" :key="item.id">
          <img :src="item.image" :alt="item.name" class="product-image">
          <div class="product-info">
            <div class="product-name">{{ item.name }}</div>
            <div class="product-spec">{{ item.spec || '' }}</div>
            <div class="product-price">
              <span class="price">¥{{ item.price }}</span>
              <span class="quantity">x{{ item.quantity }}</span>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 订单信息 -->
      <div class="order-info-section">
        <div class="section-title">订单信息</div>
        <div class="info-item">
          <span class="label">订单编号：</span>
          <span class="value">{{ orderInfo.order_number }}</span>
        </div>
        <div class="info-item">
          <span class="label">下单时间：</span>
          <span class="value">{{ orderInfo.created_at }}</span>
        </div>
        <div class="info-item">
          <span class="label">支付方式：</span>
          <span class="value">{{ getPaymentMethodText(orderInfo.payment_method) }}</span>
        </div>
        <div class="info-item">
          <span class="label">备注：</span>
          <span class="value">{{ orderInfo.remark || '无' }}</span>
        </div>
      </div>
      
      <!-- 费用明细 -->
      <div class="price-section">
        <div class="section-title">费用明细</div>
        <div class="price-item">
          <span class="label">商品总额：</span>
          <span class="value">¥{{ orderInfo.total_amount }}</span>
        </div>
        <div class="price-item">
          <span class="label">运费：</span>
          <span class="value">¥{{ orderInfo.shipping_fee || 0 }}</span>
        </div>
        <div class="price-item total">
          <span class="label">实付金额：</span>
          <span class="value">¥{{ orderInfo.final_amount }}</span>
        </div>
      </div>
    </div>
    
    <!-- 加载状态 -->
    <div class="loading-container" v-else-if="loading">
      <van-loading type="spinner" color="#1989fa" />
      <div class="loading-text">加载中...</div>
    </div>
    
    <!-- 错误状态 -->
    <div class="error-container" v-else>
      <van-empty description="订单信息加载失败" />
      <van-button type="primary" block @click="fetchOrderDetail">重新加载</van-button>
    </div>
    
    <!-- 客服按钮 -->
    <div class="customer-service-btn" v-if="orderInfo" @click="openCustomerService">
      <van-icon name="service" size="24" />
      <span>联系客服</span>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { showToast, showDialog } from 'vant';
import apiService from '../services/apiService';

const route = useRoute();
const router = useRouter();
const orderInfo = ref(null);
const loading = ref(true);

// 获取订单详情
const fetchOrderDetail = async () => {
  try {
    loading.value = true;
    const orderId = route.params.id;
    const response = await apiService.getOrderDetail(orderId);
    orderInfo.value = response.data;
  } catch (error) {
    console.error('获取订单详情失败:', error);
    showToast('获取订单详情失败');
  } finally {
    loading.value = false;
  }
};

// 返回上一页
const goBack = () => {
  router.back();
};

// 获取订单状态文本
const getOrderStatusText = (status) => {
  const statusMap = {
    'pending': '待支付',
    'paid': '待发货',
    'shipped': '待收货',
    'completed': '已完成',
    'cancelled': '已取消'
  };
  return statusMap[status] || '未知状态';
};

// 获取订单状态描述
const getOrderStatusDesc = (status) => {
  const descMap = {
    'pending': '请尽快完成支付',
    'paid': '商家正在准备商品',
    'shipped': '商品正在配送中',
    'completed': '交易已完成',
    'cancelled': '订单已取消'
  };
  return descMap[status] || '';
};

// 获取支付方式文本
const getPaymentMethodText = (method) => {
  const methodMap = {
    'alipay': '支付宝',
    'wechat': '微信支付',
    'balance': '账户余额'
  };
  return methodMap[method] || method;
};

// 打开客服功能
const openCustomerService = () => {
  // 跳转到客服页面
  router.push('/customer-service');
};

onMounted(() => {
  fetchOrderDetail();
});
</script>

<style scoped>
.order-detail-container {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.header {
  position: sticky;
  top: 0;
  z-index: 100;
  background-color: #fff;
}

.order-content {
  padding: 16px;
}

.order-status {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 20px;
  border-radius: 12px;
  margin-bottom: 16px;
  text-align: center;
}

.status-text {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 8px;
}

.customer-service-btn {
  position: fixed;
  bottom: 20px;
  right: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 12px 16px;
  border-radius: 25px;
  display: flex;
  align-items: center;
  gap: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  cursor: pointer;
  z-index: 1000;
  transition: all 0.3s ease;
}

.customer-service-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.2);
}

.customer-service-btn:active {
  transform: translateY(0);
}

.status-desc {
  font-size: 14px;
  opacity: 0.9;
}

.section-title {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 12px;
  color: #333;
}

.address-section,
.products-section,
.order-info-section,
.price-section {
  background-color: white;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.address-info {
  padding: 12px 0;
}

.user-info {
  margin-bottom: 8px;
}

.name {
  font-size: 16px;
  font-weight: bold;
  margin-right: 12px;
}

.phone {
  font-size: 14px;
  color: #666;
}

.address {
  font-size: 14px;
  color: #333;
  line-height: 1.5;
}

.product-item {
  display: flex;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
}

.product-item:last-child {
  border-bottom: none;
}

.product-image {
  width: 80px;
  height: 80px;
  border-radius: 8px;
  object-fit: cover;
  margin-right: 12px;
}

.product-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.product-name {
  font-size: 14px;
  font-weight: bold;
  color: #333;
  margin-bottom: 4px;
}

.product-spec {
  font-size: 12px;
  color: #999;
  margin-bottom: 8px;
}

.product-price {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.price {
  font-size: 16px;
  font-weight: bold;
  color: #ff6b6b;
}

.quantity {
  font-size: 14px;
  color: #666;
}

.info-item {
  display: flex;
  justify-content: space-between;
  padding: 8px 0;
  border-bottom: 1px solid #f0f0f0;
}

.info-item:last-child {
  border-bottom: none;
}

.label {
  font-size: 14px;
  color: #666;
}

.value {
  font-size: 14px;
  color: #333;
}

.price-item {
  display: flex;
  justify-content: space-between;
  padding: 8px 0;
}

.price-item.total {
  border-top: 1px solid #f0f0f0;
  padding-top: 12px;
  margin-top: 8px;
}

.price-item.total .label,
.price-item.total .value {
  font-size: 16px;
  font-weight: bold;
  color: #ff6b6b;
}

.loading-container,
.error-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  text-align: center;
}

.loading-text {
  margin-top: 16px;
  font-size: 14px;
  color: #666;
}

.error-container .van-button {
  margin-top: 20px;
  width: 200px;
}
</style>