<template>
  <div class="payment-success-container">
    <!-- 顶部导航 -->
    <div class="success-header">
      <div class="header-content">

        <h1>支付成功</h1>
        <div class="header-right"></div>
      </div>
    </div>

    <!-- 成功提示 -->
    <div class="success-banner">
      <div class="success-icon">✓</div>
      <div class="success-text">
        <h2>支付成功！</h2>
        <p>订单号：{{ orderNumber }}</p>
        <p>支付金额：<span class="amount">¥{{ amount.toFixed(2) }}</span></p>
        <p>支付方式：{{ getPaymentMethodName(paymentMethod) }}</p>
      </div>
    </div>

    <!-- 支付详情 -->
    <div class="payment-details-section">
      <div class="section-title">
        <h2>支付详情</h2>
      </div>
      
      <div class="payment-details">
        <div class="detail-item">
          <div class="detail-label">交易单号</div>
          <div class="detail-value">{{ transactionNumber }}</div>
        </div>
        <div class="detail-item">
          <div class="detail-label">支付时间</div>
          <div class="detail-value">{{ paymentTime }}</div>
        </div>
        <div class="detail-item">
          <div class="detail-label">支付状态</div>
          <div class="detail-value status-success">支付成功</div>
        </div>
        <div class="detail-item">
          <div class="detail-label">商户名称</div>
          <div class="detail-value">BusenVUE3 购物商城</div>
        </div>
      </div>
    </div>

    <!-- 订单状态 -->
    <div class="order-status-section">
      <div class="section-title">
        <h2>订单状态</h2>
      </div>
      
      <div class="order-status">
        <div class="status-item">
          <div class="status-icon">📦</div>
          <div class="status-info">
            <div class="status-title">商家正在备货</div>
            <div class="status-desc">预计1-2个工作日内发货</div>
          </div>
        </div>
        <div class="status-item">
          <div class="status-icon">🚚</div>
          <div class="status-info">
            <div class="status-title">等待发货</div>
            <div class="status-desc">发货后将通过短信通知您</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 推荐操作 -->
    <div class="recommended-actions">
      <div class="section-title">
        <h2>推荐操作</h2>
      </div>
      
      <div class="actions-grid">
        <div class="action-item" @click="viewOrderDetail">
          <div class="action-icon">📋</div>
          <div class="action-name">查看订单详情</div>
        </div>
        <div class="action-item" @click="viewAllOrders">
          <div class="action-icon">📦</div>
          <div class="action-name">查看所有订单</div>
        </div>
        <div class="action-item" @click="goToHome">
          <div class="action-icon">🏠</div>
          <div class="action-name">返回首页</div>
        </div>
        <div class="action-item" @click="continueShopping">
          <div class="action-icon">🛍️</div>
          <div class="action-name">继续购物</div>
        </div>
      </div>
    </div>

    <!-- 购物保障 -->
    <div class="shopping-guarantee">
      <div class="section-title">
        <h2>购物保障</h2>
      </div>
      
      <div class="guarantee-list">
        <div class="guarantee-item">
          <div class="guarantee-icon">🛡️</div>
          <div class="guarantee-info">
            <div class="guarantee-title">7天无理由退货</div>
            <div class="guarantee-desc">收货7天内可无理由退货</div>
          </div>
        </div>
        <div class="guarantee-item">
          <div class="guarantee-icon">⚡</div>
          <div class="guarantee-info">
            <div class="guarantee-title">极速退款</div>
            <div class="guarantee-desc">符合条件的订单可极速退款</div>
          </div>
        </div>
        <div class="guarantee-item">
          <div class="guarantee-icon">🎁</div>
          <div class="guarantee-info">
            <div class="guarantee-title">正品保证</div>
            <div class="guarantee-desc">100%正品保证，假一赔十</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 底部操作按钮 -->
    <div class="bottom-actions">
      <button class="btn-secondary" @click="viewOrderDetail">查看订单</button>
      <button class="btn-primary" @click="continueShopping">继续购物</button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';

const router = useRouter();
const route = useRoute();

// 支付信息
const orderNumber = ref('');
const amount = ref(0);
const paymentMethod = ref('');
const transactionNumber = ref('');
const paymentTime = ref('');

// 生成交易单号
const generateTransactionNumber = () => {
  const date = new Date();
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  const random = Math.floor(Math.random() * 1000000).toString().padStart(6, '0');
  return `PAY${year}${month}${day}${random}`;
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

// 初始化支付成功页面
const initPaymentSuccess = () => {
  const queryOrderNumber = route.query.orderNumber;
  const queryAmount = route.query.amount;
  const queryPaymentMethod = route.query.paymentMethod;
  
  if (queryOrderNumber) {
    orderNumber.value = queryOrderNumber as string;
  }
  
  if (queryAmount) {
    amount.value = parseFloat(queryAmount as string);
  }
  
  if (queryPaymentMethod) {
    paymentMethod.value = queryPaymentMethod as string;
  }
  
  // 生成交易单号和支付时间
  transactionNumber.value = generateTransactionNumber();
  paymentTime.value = formatTime(new Date());
  
  // 如果没有从路由获取到数据，使用默认值
  if (!orderNumber.value) {
    orderNumber.value = 'ORD' + Date.now();
  }
  
  if (!amount.value || isNaN(amount.value)) {
    amount.value = 599;
  }
  
  if (!paymentMethod.value) {
    paymentMethod.value = 'alipay';
  }
};

// 查看订单详情
const viewOrderDetail = () => {
  router.push('/orders');
};

// 查看所有订单
const viewAllOrders = () => {
  router.push('/orders');
};

// 返回首页
const goToHome = () => {
  router.push('/');
};

// 继续购物
const continueShopping = () => {
  router.push('/mikeshop');
};



onMounted(() => {
  initPaymentSuccess();
});
</script>

<style scoped>
.payment-success-container {
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

/* 支付详情 */
.payment-details-section {
  background-color: #fff;
  margin: 12px 0;
}

.payment-details {
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

.status-success {
  color: #52c41a;
  font-weight: 500;
}

/* 订单状态 */
.order-status-section {
  background-color: #fff;
  margin: 12px 0;
}

.order-status {
  padding: 16px;
}

.status-item {
  display: flex;
  align-items: flex-start;
  margin-bottom: 20px;
  padding: 16px;
  background-color: #f8f9fa;
  border-radius: 8px;
}

.status-item:last-child {
  margin-bottom: 0;
}

.status-icon {
  font-size: 24px;
  margin-right: 12px;
  flex-shrink: 0;
}

.status-info {
  flex: 1;
}

.status-title {
  font-size: 16px;
  font-weight: 500;
  color: #333;
  margin-bottom: 4px;
}

.status-desc {
  font-size: 14px;
  color: #666;
}

/* 推荐操作 */
.recommended-actions {
  background-color: #fff;
  margin: 12px 0;
}

.actions-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
  padding: 16px;
}

.action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 20px;
  border: 1px solid #e8e8e8;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.action-item:hover {
  border-color: #e4393c;
  box-shadow: 0 2px 8px rgba(228, 57, 60, 0.1);
  transform: translateY(-2px);
}

.action-icon {
  font-size: 32px;
  margin-bottom: 8px;
}

.action-name {
  font-size: 14px;
  color: #333;
  text-align: center;
}

/* 购物保障 */
.shopping-guarantee {
  background-color: #fff;
  margin: 12px 0;
}

.guarantee-list {
  padding: 16px;
}

.guarantee-item {
  display: flex;
  align-items: flex-start;
  margin-bottom: 16px;
}

.guarantee-item:last-child {
  margin-bottom: 0;
}

.guarantee-icon {
  font-size: 24px;
  margin-right: 12px;
  flex-shrink: 0;
}

.guarantee-info {
  flex: 1;
}

.guarantee-title {
  font-size: 16px;
  font-weight: 500;
  color: #333;
  margin-bottom: 4px;
}

.guarantee-desc {
  font-size: 14px;
  color: #666;
}

/* 底部操作按钮 */
.bottom-actions {
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

@media (max-width: 768px) {
  .actions-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>