<template>
  <div class="order-success-container">
    <!-- 顶部导航 -->
    <div class="success-header">
      <div class="header-content">
        <div class="back-btn" @click="goBack">
          <span>←</span> 返回
        </div>
        <h1>订单提交成功</h1>
        <div class="header-right"></div>
      </div>
    </div>

    <!-- 成功提示 -->
    <div class="success-banner">
      <div class="success-icon">✓</div>
      <div class="success-text">
        <h2>订单提交成功！</h2>
        <p>订单号：{{ orderInfo.orderNumber }}</p>
        <p>应付金额：<span class="amount">¥{{ orderInfo.totalAmount.toFixed(2) }}</span></p>
      </div>
    </div>

    <!-- 订单状态时间线 -->
    <div class="order-timeline">
      <div class="timeline-item active">
        <div class="timeline-dot">1</div>
        <div class="timeline-content">
          <div class="timeline-title">提交订单</div>
          <div class="timeline-time">{{ orderInfo.submitTime }}</div>
        </div>
      </div>
      <div class="timeline-item">
        <div class="timeline-dot">2</div>
        <div class="timeline-content">
          <div class="timeline-title">付款成功</div>
          <div class="timeline-time">待付款</div>
        </div>
      </div>
      <div class="timeline-item">
        <div class="timeline-dot">3</div>
        <div class="timeline-content">
          <div class="timeline-title">商家发货</div>
          <div class="timeline-time">待发货</div>
        </div>
      </div>
      <div class="timeline-item">
        <div class="timeline-dot">4</div>
        <div class="timeline-content">
          <div class="timeline-title">确认收货</div>
          <div class="timeline-time">待收货</div>
        </div>
      </div>
    </div>

    <!-- 订单信息 -->
    <div class="order-info-section">
      <div class="section-title">
        <h2>订单信息</h2>
      </div>
      
      <div class="order-details">
        <!-- 收货信息 -->
        <div class="detail-item">
          <div class="detail-label">收货信息</div>
          <div class="detail-value">
            <div class="address-info">
              <div class="contact">{{ orderInfo.address.name }} {{ orderInfo.address.phone }}</div>
              <div class="address">{{ orderInfo.address.fullAddress }}</div>
            </div>
          </div>
        </div>
        
        <!-- 支付方式 -->
        <div class="detail-item">
          <div class="detail-label">支付方式</div>
          <div class="detail-value">{{ orderInfo.paymentMethod }}</div>
        </div>
        
        <!-- 配送方式 -->
        <div class="detail-item">
          <div class="detail-label">配送方式</div>
          <div class="detail-value">普通快递 ¥{{ orderInfo.shippingFee.toFixed(2) }}</div>
        </div>
        
        <!-- 发票信息 -->
        <div class="detail-item" v-if="orderInfo.invoice.type !== 'none'">
          <div class="detail-label">发票信息</div>
          <div class="detail-value">
            {{ orderInfo.invoice.type === 'personal' ? '个人发票' : '企业发票' }}
            <span v-if="orderInfo.invoice.title">- {{ orderInfo.invoice.title }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 商品清单 -->
    <div class="products-section">
      <div class="section-title">
        <h2>商品清单</h2>
      </div>
      
      <div class="products-list">
        <div v-for="item in orderInfo.items" :key="item.id" class="product-item">
          <div class="product-image">
            <img :src="item.image" :alt="item.name" />
          </div>
          <div class="product-info">
            <div class="product-name">{{ item.name }}</div>
            <div class="product-spec" v-if="item.productValue">
              规格：{{ item.productValue }}
            </div>
            <div class="product-price">
              <span class="price">¥{{ item.price.toFixed(2) }}</span>
              <span class="quantity">x{{ item.quantity }}</span>
            </div>
          </div>
          <div class="product-subtotal">
            ¥{{ item.subtotal.toFixed(2) }}
          </div>
        </div>
      </div>
    </div>

    <!-- 费用明细 -->
    <div class="fee-summary">
      <div class="fee-item">
        <span>商品总额</span>
        <span>¥{{ orderInfo.subtotal.toFixed(2) }}</span>
      </div>
      <div class="fee-item">
        <span>运费</span>
        <span>¥{{ orderInfo.shippingFee.toFixed(2) }}</span>
      </div>
      <div class="fee-item discount" v-if="orderInfo.discount > 0">
        <span>优惠</span>
        <span>-¥{{ orderInfo.discount.toFixed(2) }}</span>
      </div>
      <div class="fee-item total">
        <span>实付金额</span>
        <span class="total-amount">¥{{ orderInfo.totalAmount.toFixed(2) }}</span>
      </div>
    </div>

    <!-- 操作按钮 -->
    <div class="action-buttons">
      <button class="btn-secondary" @click="viewOrderDetail">查看订单详情</button>
      <button class="btn-primary" @click="goToPayment" v-if="orderInfo.status === 'pending_payment'">
        立即支付
      </button>
      <button class="btn-primary" @click="continueShopping" v-else>
        继续购物
      </button>
    </div>

    <!-- 推荐商品 -->
    <div class="recommendation-section">
      <div class="section-title">
        <h2>为您推荐</h2>
      </div>
      <div class="recommendation-grid">
        <div v-for="item in recommendedProducts" :key="item.id" class="recommendation-item">
          <img :src="item.image" :alt="item.name" />
          <div class="item-name">{{ item.name }}</div>
          <div class="item-price">¥{{ item.price }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';

const router = useRouter();
const route = useRoute();

// 订单信息
const orderInfo = ref({
  orderNumber: '',
  submitTime: '',
  totalAmount: 0,
  status: 'pending_payment',
  address: {
    name: '',
    phone: '',
    fullAddress: ''
  },
  paymentMethod: '',
  shippingFee: 0,
  invoice: {
    type: 'none',
    title: ''
  },
  items: [],
  subtotal: 0,
  discount: 0
});

// 推荐商品
const recommendedProducts = ref([
  {
    id: 1,
    name: '时尚休闲男装',
    price: 299,
    image: '/images/男装.png'
  },
  {
    id: 2,
    name: '优雅女装',
    price: 399,
    image: '/images/女装.png'
  },
  {
    id: 3,
    name: '精品配饰',
    price: 199,
    image: '/images/配饰.png'
  },
  {
    id: 4,
    name: '舒适鞋履',
    price: 499,
    image: '/images/鞋子.png'
  }
]);

// 生成订单号
const generateOrderNumber = () => {
  const date = new Date();
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  const random = Math.floor(Math.random() * 10000).toString().padStart(4, '0');
  return `${year}${month}${day}${random}`;
};

// 格式化时间
const formatTime = (date: Date) => {
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  });
};

// 加载订单信息
const loadOrderInfo = () => {
  // 从路由参数或localStorage获取订单信息
  const orderData = route.query.orderData ? JSON.parse(route.query.orderData as string) : null;
  
  if (orderData) {
    orderInfo.value = {
      orderNumber: generateOrderNumber(),
      submitTime: formatTime(new Date()),
      totalAmount: orderData.total || 0,
      status: 'pending_payment',
      address: orderData.address || {
        name: '张三',
        phone: '13800138000',
        fullAddress: '广东省深圳市南山区科技园路1001号腾讯大厦'
      },
      paymentMethod: getPaymentMethodName(orderData.payment_method),
      shippingFee: orderData.shipping || 0,
      invoice: {
        type: orderData.invoice_type || 'none',
        title: orderData.invoice_title || ''
      },
      items: orderData.items || [],
      subtotal: orderData.subtotal || 0,
      discount: orderData.discount || 0
    };
  } else {
    // 使用模拟数据
    orderInfo.value = {
      orderNumber: generateOrderNumber(),
      submitTime: formatTime(new Date()),
      totalAmount: 599,
      status: 'pending_payment',
      address: {
        name: '张三',
        phone: '13800138000',
        fullAddress: '广东省深圳市南山区科技园路1001号腾讯大厦'
      },
      paymentMethod: '支付宝',
      shippingFee: 10,
      invoice: {
        type: 'none',
        title: ''
      },
      items: [
        {
          id: 1,
          name: 'BusenVUE3 时尚休闲男装',
          image: '/images/男装.png',
          price: 299,
          quantity: 2,
          productValue: '颜色:黑色;尺码:L',
          subtotal: 598
        }
      ],
      subtotal: 598,
      discount: 9
    };
  }
};

// 获取支付方式名称
const getPaymentMethodName = (methodId: string) => {
  const methods: Record<string, string> = {
    'alipay': '支付宝',
    'wechat': '微信支付',
    'bank': '银行卡支付',
    'cod': '货到付款'
  };
  return methods[methodId] || '支付宝';
};

// 查看订单详情
const viewOrderDetail = () => {
  router.push({
    name: 'orders'
  });
};

// 去支付
const goToPayment = () => {
  router.push({
    name: 'payment',
    query: { 
      orderNumber: orderInfo.value.orderNumber,
      amount: orderInfo.value.totalAmount
    }
  });
};

// 继续购物
const continueShopping = () => {
  router.push('/');
};

// 返回
const goBack = () => {
  router.back();
};

onMounted(() => {
  loadOrderInfo();
});
</script>

<style scoped>
.order-success-container {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 80px;
}

/* 顶部导航 */
.success-header {
  background-color: #fff;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  max-width: 1200px;
  margin: 0 auto;
}

.back-btn {
  cursor: pointer;
  font-size: 16px;
  color: #333;
}

.header-content h1 {
  font-size: 18px;
  font-weight: 500;
  color: #333;
  margin: 0;
}

/* 成功提示 */
.success-banner {
  background: linear-gradient(135deg, #e8f5e8 0%, #f0f9f0 100%);
  padding: 24px 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
}

.success-icon {
  width: 48px;
  height: 48px;
  background-color: #52c41a;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 24px;
  font-weight: bold;
  margin-right: 16px;
}

.success-text h2 {
  font-size: 20px;
  color: #52c41a;
  margin: 0 0 8px 0;
}

.success-text p {
  margin: 4px 0;
  color: #666;
  font-size: 14px;
}

.success-text .amount {
  color: #e4393c;
  font-weight: bold;
  font-size: 16px;
}

/* 订单状态时间线 */
.order-timeline {
  background-color: #fff;
  padding: 20px 16px;
  margin: 12px 0;
}

.timeline-item {
  display: flex;
  align-items: flex-start;
  margin-bottom: 20px;
  position: relative;
}

.timeline-item:last-child {
  margin-bottom: 0;
}

.timeline-dot {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background-color: #ddd;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: bold;
  margin-right: 12px;
  flex-shrink: 0;
}

.timeline-item.active .timeline-dot {
  background-color: #e4393c;
}

.timeline-content {
  flex: 1;
}

.timeline-title {
  font-size: 16px;
  font-weight: 500;
  color: #333;
  margin-bottom: 4px;
}

.timeline-item.active .timeline-title {
  color: #e4393c;
}

.timeline-time {
  font-size: 12px;
  color: #999;
}

/* 通用区域样式 */
.section-title {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px;
  background-color: #fff;
  border-bottom: 1px solid #f0f0f0;
}

.section-title h2 {
  font-size: 16px;
  font-weight: 500;
  color: #333;
  margin: 0;
}

/* 订单信息 */
.order-info-section {
  background-color: #fff;
  margin: 12px 0;
}

.order-details {
  padding: 16px;
}

.detail-item {
  display: flex;
  margin-bottom: 16px;
}

.detail-item:last-child {
  margin-bottom: 0;
}

.detail-label {
  width: 80px;
  color: #666;
  font-size: 14px;
  flex-shrink: 0;
}

.detail-value {
  flex: 1;
  color: #333;
  font-size: 14px;
}

.address-info .contact {
  font-weight: 500;
  margin-bottom: 4px;
}

.address-info .address {
  color: #666;
  line-height: 1.4;
}

/* 商品清单 */
.products-section {
  background-color: #fff;
  margin: 12px 0;
}

.products-list {
  padding: 16px;
}

.product-item {
  display: flex;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
}

.product-item:last-child {
  border-bottom: none;
}

.product-image {
  width: 60px;
  height: 60px;
  margin-right: 12px;
  flex-shrink: 0;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 4px;
}

.product-info {
  flex: 1;
  margin-right: 12px;
}

.product-name {
  font-size: 14px;
  color: #333;
  margin-bottom: 4px;
  line-height: 1.3;
}

.product-spec {
  font-size: 12px;
  color: #999;
  margin-bottom: 4px;
}

.product-price {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.product-price .price {
  color: #e4393c;
  font-weight: 500;
}

.product-price .quantity {
  color: #666;
  font-size: 12px;
}

.product-subtotal {
  font-size: 14px;
  color: #333;
  font-weight: 500;
  flex-shrink: 0;
}

/* 费用明细 */
.fee-summary {
  background-color: #fff;
  padding: 16px;
  margin: 12px 0;
}

.fee-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
  font-size: 14px;
}

.fee-item:last-child {
  margin-bottom: 0;
}

.fee-item.discount span:last-child {
  color: #e4393c;
}

.fee-item.total {
  font-weight: bold;
  font-size: 16px;
  padding-top: 8px;
  border-top: 1px solid #f0f0f0;
  margin-top: 8px;
}

.total-amount {
  color: #e4393c;
  font-size: 18px;
}

/* 操作按钮 */
.action-buttons {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background-color: #fff;
  padding: 12px 16px;
  box-shadow: 0 -2px 8px rgba(0,0,0,0.1);
  display: flex;
  gap: 12px;
  z-index: 100;
}

.btn-secondary {
  flex: 1;
  padding: 12px 24px;
  border: 1px solid #ddd;
  background-color: #fff;
  color: #333;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
}

.btn-primary {
  flex: 1;
  padding: 12px 24px;
  background-color: #e4393c;
  color: #fff;
  border: none;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
}

.btn-primary:hover {
  background-color: #c1272d;
}

/* 推荐商品 */
.recommendation-section {
  background-color: #fff;
  margin: 12px 0;
  padding-bottom: 16px;
}

.recommendation-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
  padding: 0 16px;
}

.recommendation-item {
  text-align: center;
}

.recommendation-item img {
  width: 100%;
  height: 120px;
  object-fit: cover;
  border-radius: 4px;
  margin-bottom: 8px;
}

.item-name {
  font-size: 12px;
  color: #333;
  margin-bottom: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.item-price {
  font-size: 14px;
  color: #e4393c;
  font-weight: 500;
}

@media (max-width: 768px) {
 .recommendation-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>