<template>
  <div class="payment-container">
    <!-- 顶部导航 -->
    <div class="payment-header">
      <div class="header-content">
        <div class="back-btn" @click="goBack">
          <span>←</span> 返回
        </div>
        <h1>订单支付</h1>
        <div class="header-right"></div>
      </div>
    </div>

    <!-- 订单信息 -->
    <div class="order-info-section">
      <div class="section-title">
        <h2>订单信息</h2>
      </div>
      
      <div class="order-details">
        <div class="order-number">
          <span class="label">订单号：</span>
          <span class="value">{{ orderNumber }}</span>
        </div>
        <div class="payment-account">
          <span class="label">支付号码：</span>
          <span class="value">{{ paymentAccount }}</span>
        </div>
        <div class="order-amount">
          <span class="label">支付金额：</span>
          <span class="amount">¥{{ amount.toFixed(2) }}</span>
        </div>
        <div class="account-balance">
          <span class="label">账户余额：</span>
          <span class="balance">¥{{ userBalance.toFixed(2) }}</span>
        </div>
      </div>
    </div>

    <!-- 支付方式选择 -->
    <div class="payment-methods-section">
      <div class="section-title">
        <h2>选择支付方式</h2>
      </div>
      
      <div class="payment-methods">
        <div 
          v-for="method in paymentMethods" 
          :key="method.id"
          class="payment-method"
          :class="{ active: selectedPayment === method.id }"
          @click="selectPayment(method.id)"
        >
          <div class="payment-icon">{{ method.icon }}</div>
          <div class="payment-info">
            <div class="payment-name">{{ method.name }}</div>
            <div class="payment-desc">{{ method.description }}</div>
          </div>
          <div class="payment-select" v-if="selectedPayment === method.id">
            <div class="select-dot"></div>
          </div>
        </div>
        
        <!-- 账户余额支付 -->
        <div 
          class="payment-method"
          :class="{ active: selectedPayment === 'balance' }"
          @click="selectPayment('balance')"
        >
          <div class="payment-icon">💰</div>
          <div class="payment-info">
            <div class="payment-name">账户余额</div>
            <div class="payment-desc">当前余额：¥{{ userBalance.toFixed(2) }}</div>
          </div>
          <div class="payment-select" v-if="selectedPayment === 'balance'">
            <div class="select-dot"></div>
          </div>
          <div class="recharge-btn" v-if="userBalance < amount" @click.stop="goRecharge">
            充值
          </div>
        </div>
      </div>
    </div>

    <!-- 支付密码输入 -->
    <div class="password-section" v-if="selectedPayment === 'alipay' || selectedPayment === 'wechat' || selectedPayment === 'balance'">
      <div class="section-title">
        <h2>支付密码</h2>
      </div>
      
      <div class="password-input-container">
        <input 
          type="password" 
          v-model="paymentPassword" 
          placeholder="请输入支付密码"
          maxlength="6"
          class="password-input"
        />
      </div>
    </div>

    <!-- 银行卡信息 -->
    <div class="bank-card-section" v-if="selectedPayment === 'bank'">
      <div class="section-title">
        <h2>银行卡信息</h2>
      </div>
      
      <div class="bank-card-form">
        <div class="form-group">
          <label>卡号</label>
          <input 
            type="text" 
            v-model="bankCard.cardNumber" 
            placeholder="请输入银行卡号"
            maxlength="19"
          />
        </div>
        <div class="form-row">
          <div class="form-group">
            <label>有效期</label>
            <input 
              type="text" 
              v-model="bankCard.expiry" 
              placeholder="MM/YY"
              maxlength="5"
            />
          </div>
          <div class="form-group">
            <label>CVV</label>
            <input 
              type="text" 
              v-model="bankCard.cvv" 
              placeholder="CVV"
              maxlength="3"
            />
          </div>
        </div>
        <div class="form-group">
          <label>持卡人姓名</label>
          <input 
            type="text" 
            v-model="bankCard.cardholderName" 
            placeholder="请输入持卡人姓名"
          />
        </div>
      </div>
    </div>

    <!-- 优惠券选择 -->
    <div class="coupon-section">
      <div class="section-title">
        <h2>优惠券</h2>
        <span class="coupon-count">{{ availableCoupons.length }}张可用</span>
      </div>
      
      <div class="coupon-list">
        <div 
          v-for="coupon in availableCoupons" 
          :key="coupon.id"
          class="coupon-item"
          :class="{ active: selectedCoupon === coupon.id }"
          @click="selectCoupon(coupon.id)"
        >
          <div class="coupon-amount">
            <span class="currency">¥</span>
            <span class="value">{{ coupon.amount }}</span>
          </div>
          <div class="coupon-info">
            <div class="coupon-name">{{ coupon.name }}</div>
            <div class="coupon-condition">满{{ coupon.minAmount }}元可用</div>
            <div class="coupon-expiry">有效期至：{{ coupon.expiry }}</div>
          </div>
          <div class="coupon-select" v-if="selectedCoupon === coupon.id">
            ✓
          </div>
        </div>
        
        <div class="no-coupon" v-if="availableCoupons.length === 0">
          暂无可用优惠券
        </div>
      </div>
    </div>

    <!-- 支付金额明细 -->
    <div class="payment-summary">
      <div class="summary-item">
        <span>订单金额</span>
        <span>¥{{ amount.toFixed(2) }}</span>
      </div>
      <div class="summary-item" v-if="selectedCoupon">
        <span>优惠券</span>
        <span class="discount">-¥{{ getCouponDiscount().toFixed(2) }}</span>
      </div>
      <div class="summary-item total">
        <span>实付金额</span>
        <span class="total-amount">¥{{ getFinalAmount().toFixed(2) }}</span>
      </div>
    </div>

    <!-- 支付按钮 -->
    <div class="payment-actions">
      <button 
        class="payment-btn" 
        @click="processPayment"
        :disabled="!canPay || paying"
      >
        {{ paying ? '支付中...' : `支付 ¥${getFinalAmount().toFixed(2)}` }}
      </button>
    </div>

    <!-- 安全提示 -->
    <div class="security-tips">
      <div class="tip-item">
        <span class="tip-icon">🔒</span>
        <span>支付环境安全，请放心支付</span>
      </div>
      <div class="tip-item">
        <span class="tip-icon">🛡️</span>
        <span>7天无理由退款，购物无忧</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import apiService from '../services/apiService';

const router = useRouter();
const route = useRoute();

// 订单信息
const orderNumber = ref('');
const amount = ref(0);
const paymentAccount = ref('');

// 用户余额
const userBalance = ref(0);

// 支付方式
const selectedPayment = ref('alipay');
const paymentPassword = ref('');

// 银行卡信息
const bankCard = ref({
  cardNumber: '',
  expiry: '',
  cvv: '',
  cardholderName: ''
});

// 优惠券
const selectedCoupon = ref(null);
const availableCoupons = ref([
  {
    id: 1,
    name: '新用户专享券',
    amount: 10,
    minAmount: 100,
    expiry: '2024-12-31'
  },
  {
    id: 2,
    name: '满200减20',
    amount: 20,
    minAmount: 200,
    expiry: '2024-12-31'
  }
]);

// 支付状态
const paying = ref(false);

// 支付方式列表
const paymentMethods = ref([
  {
    id: 'alipay',
    name: '支付宝',
    icon: '💰',
    description: '推荐使用支付宝支付'
  },
  {
    id: 'wechat',
    name: '微信支付',
    icon: '💚',
    description: '使用微信余额支付'
  },
  {
    id: 'bank',
    name: '银行卡支付',
    icon: '💳',
    description: '支持各大银行银行卡'
  },
  {
    id: 'cod',
    name: '货到付款',
    icon: '📦',
    description: '收到商品后再付款'
  },
  {
    id: 'balance',
    name: '账户余额',
    icon: '💰',
    description: '使用账户余额支付'
  }
]);

// 获取用户余额
const getUserBalance = async () => {
  try {
    const userInfo = localStorage.getItem('userInfo');
    if (userInfo) {
      const user = JSON.parse(userInfo);
      // 调用selectAccout接口查询用户余额
      const response = await apiService.selectAccountInfoByid(user.id);
      if (response && response.data) {
        // 使用total_asset字段作为余额
        userBalance.value = response.data.total_asset || 0;
      }
    }
  } catch (error) {
    console.error('获取用户余额失败:', error);
    // 如果API调用失败，使用默认值
    userBalance.value = 0;
  }
};

// 初始化支付页面
const initPayment = () => {
  const queryOrderNumber = route.query.orderNumber;
  const queryAmount = route.query.amount;
  const queryAccountInfo = route.query.accountInfo;
  
  if (queryOrderNumber) {
    orderNumber.value = queryOrderNumber as string;
  }
  
  if (queryAmount) {
    amount.value = parseFloat(queryAmount as string);
  }
  
  if (queryAccountInfo) {
    paymentAccount.value = queryAccountInfo as string;
  }
  
  // 如果没有从路由获取到数据，使用默认值
  if (!orderNumber.value) {
    orderNumber.value = 'ORD' + Date.now();
  }
  
  if (!amount.value || isNaN(amount.value)) {
    amount.value = 599;
  }
};

// 选择支付方式
const selectPayment = (methodId: string) => {
  selectedPayment.value = methodId;
};

// 选择优惠券
const selectCoupon = (couponId: number | null) => {
  if (selectedCoupon.value === couponId) {
    selectedCoupon.value = null;
  } else {
    selectedCoupon.value = couponId;
  }
};

// 获取优惠券折扣
const getCouponDiscount = () => {
  if (!selectedCoupon.value) return 0;
  
  const coupon = availableCoupons.value.find(c => c.id === selectedCoupon.value);
  if (!coupon) return 0;
  
  // 检查是否满足使用条件
  if (amount.value >= coupon.minAmount) {
    return coupon.amount;
  }
  
  return 0;
};

// 获取最终支付金额
const getFinalAmount = () => {
  const discount = getCouponDiscount();
  return Math.max(0, amount.value - discount);
};

// 格式化银行卡号
const formatCardNumber = () => {
  let cardNumber = bankCard.value.cardNumber.replace(/\s+/g, '');
  cardNumber = cardNumber.replace(/(\d{4})/g, '$1 ').trim();
  bankCard.value.cardNumber = cardNumber;
};

// 格式化有效期
const formatExpiry = () => {
  let expiry = bankCard.value.expiry.replace(/\D/g, '');
  if (expiry.length >= 2) {
    expiry = expiry.substring(0, 2) + '/' + expiry.substring(2, 4);
  }
  bankCard.value.expiry = expiry;
};

// 验证支付信息
const validatePayment = () => {
  if (selectedPayment.value === 'alipay' || selectedPayment.value === 'wechat') {
    if (!paymentPassword.value || paymentPassword.value.length < 6) {
      alert('请输入6位支付密码');
      return false;
    }
  }
  
  if (selectedPayment.value === 'bank') {
    if (!bankCard.value.cardNumber || bankCard.value.cardNumber.replace(/\s/g, '').length < 16) {
      alert('请输入正确的银行卡号');
      return false;
    }
    if (!bankCard.value.expiry || bankCard.value.expiry.length !== 5) {
      alert('请输入正确的有效期');
      return false;
    }
    if (!bankCard.value.cvv || bankCard.value.cvv.length !== 3) {
      alert('请输入正确的CVV');
      return false;
    }
    if (!bankCard.value.cardholderName) {
      alert('请输入持卡人姓名');
      return false;
    }
  }
  
  return true;
};

// 处理支付
const processPayment = async () => {
  if (!validatePayment()) return;
  
  // 如果选择账户余额支付，检查余额是否充足
  if (selectedPayment.value === 'balance') {
    if (userBalance.value < getFinalAmount()) {
      alert('账户余额不足，请选择其他支付方式或充值');
      return;
    }
  }
  
  paying.value = true;
  
  try {
    const paymentData = {
      orderNumber: orderNumber.value,
      amount: getFinalAmount(),
      paymentMethod: selectedPayment.value,
      couponId: selectedCoupon.value,
      paymentPassword: paymentPassword.value,
      bankCard: selectedPayment.value === 'bank' ? bankCard.value : null
    };
    
    console.log('支付数据:', paymentData);
    
    // 模拟支付处理
    await new Promise(resolve => setTimeout(resolve, 2000));
    
    // 如果选择账户余额支付，扣减余额
    if (selectedPayment.value === 'balance') {
      userBalance.value -= getFinalAmount();
      // 注意：实际项目中余额扣减应该通过后端API来完成
      // 这里只是前端显示更新，真正的余额扣减应该在后端处理
    }
    
    // 模拟支付成功
    alert('支付成功！');
    
    // 更新订单状态为待发货
      try {
        const orderId = parseInt(orderNumber.value);
        if (isNaN(orderId)) {
          throw new Error('订单号格式无效');
        }
        
        await apiService.updateOrder('待发货', { order_id: orderId });
        // 存储订单号到sessionStorage和localStorage，用于后续更新订单信息
        sessionStorage.setItem('order_id', orderNumber.value);
        localStorage.setItem('currentOrderId', orderNumber.value);
        console.log('订单状态已更新为待发货，订单号:', orderNumber.value);
      } catch (error) {
        console.error('更新订单状态失败:', error);
        alert('更新订单状态失败，请联系客服');
      }

    // 更新商品状态为待发货
    try {
      const checkoutItems = sessionStorage.getItem('checkoutItems');
      if (checkoutItems) {
        const items = JSON.parse(checkoutItems);
        
        // 遍历商品并更新状态为待发货
        for (const item of items) {
          try {
            // 获取商品当前信息
            const productResponse = await apiService.getProductInfo(item.productId);
            if (productResponse && productResponse.data) {
              // 更新商品状态为待发货
              const updateData = {
                ...productResponse.data,
                status: '待发货'
              };
              await apiService.updateProduct(updateData);
              console.log(`已更新商品状态为待发货: ${item.productId}`);
            }
          } catch (error) {
            console.error(`更新商品状态失败: ${item.productId}`, error);
          }
        }
      }
    } catch (error) {
      console.error('更新商品状态失败:', error);
    }
    
    // 支付成功后删除购物车中的商品
    try {
      const userInfo = localStorage.getItem('userInfo');
      if (userInfo) {
        const user = JSON.parse(userInfo);
        const checkoutItems = sessionStorage.getItem('checkoutItems');
        
        if (checkoutItems) {
          const items = JSON.parse(checkoutItems);
          
          // 遍历商品并删除购物车中的商品
          for (const item of items) {
            try {
              await apiService.deleteBuyCar(user.id, item.productId);
              console.log(`已删除购物车商品: ${item.productId}`);
            } catch (error) {
              console.error(`删除购物车商品失败: ${item.productId}`, error);
            }
          }
          
          // 清除sessionStorage中的checkoutItems
          sessionStorage.removeItem('checkoutItems');
        }
      }
    } catch (error) {
      console.error('删除购物车商品失败:', error);
    }
    
    // 先跳转到mikeshop页面，然后立即跳转到支付成功页面
    // 这样浏览器返回时会返回到mikeshop页面
    router.push('/mikeshop');
    setTimeout(() => {
      router.replace({
        path: '/payment-success',
        query: {
          orderNumber: orderNumber.value,
          amount: getFinalAmount(),
          paymentMethod: selectedPayment.value
        }
      });
    }, 100);
    
  } catch (error) {
    console.error('支付失败:', error);
    alert('支付失败，请重试');
  } finally {
    paying.value = false;
  }
};

// 是否可以支付
const canPay = computed(() => {
  if (selectedPayment.value === 'alipay' || selectedPayment.value === 'wechat') {
    return paymentPassword.value.length === 6;
  }
  
  if (selectedPayment.value === 'bank') {
    return bankCard.value.cardNumber && 
           bankCard.value.expiry && 
           bankCard.value.cvv && 
           bankCard.value.cardholderName;
  }
  
  return true;
});

// 返回
const goBack = () => {
  router.back();
};

// 前往充值
const goRecharge = () => {
  router.push({
    name: 'recharge',
    query: {
      orderNumber: orderNumber.value,
      amount: (amount.value - userBalance.value).toFixed(2)
    }
  });
};

// 监听银行卡号输入变化
const watchCardNumber = () => {
  formatCardNumber();
};

// 监听有效期输入变化
const watchExpiry = () => {
  formatExpiry();
};

onMounted(() => {
  initPayment();
  getUserBalance();
});
</script>

<style scoped>
.payment-container {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 100px;
}

/* 顶部导航 */
.payment-header {
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

.order-number,
.order-amount {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  font-size: 14px;
}

.order-number:last-child,
.order-amount:last-child {
  margin-bottom: 0;
}

.label {
  color: #666;
}

.value {
  color: #333;
  font-weight: 500;
}

.amount {
  color: #e4393c;
  font-size: 18px;
  font-weight: bold;
}

/* 支付方式 */
.payment-methods-section {
  background-color: #fff;
  margin: 12px 0;
}

.payment-methods {
  padding: 16px;
}

.payment-method {
  display: flex;
  align-items: center;
  padding: 16px;
  margin-bottom: 12px;
  border: 1px solid #e8e8e8;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.payment-method:last-child {
  margin-bottom: 0;
}

.payment-method:hover {
  border-color: #e4393c;
  box-shadow: 0 2px 8px rgba(228, 57, 60, 0.1);
}

.payment-method.active {
  border-color: #e4393c;
  background-color: #fff5f5;
}

.payment-icon {
  font-size: 24px;
  margin-right: 12px;
}

.payment-info {
  flex: 1;
}

.payment-name {
  font-size: 16px;
  font-weight: 500;
  color: #333;
  margin-bottom: 4px;
}

.payment-desc {
  font-size: 12px;
  color: #999;
}

.payment-select {
  width: 20px;
  height: 20px;
  border-radius: 50%;
  border: 2px solid #e4393c;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-left: 12px;
}

.select-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background-color: #e4393c;
}

/* 支付密码 */
.password-section {
  background-color: #fff;
  margin: 12px 0;
}

.password-input-container {
  padding: 16px;
}

.password-input {
  width: 100%;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 16px;
  text-align: center;
  letter-spacing: 8px;
}

/* 银行卡信息 */
.bank-card-section {
  background-color: #fff;
  margin: 12px 0;
}

.bank-card-form {
  padding: 16px;
}

.form-group {
  margin-bottom: 16px;
}

.form-group:last-child {
  margin-bottom: 0;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-size: 14px;
  color: #666;
}

.form-group input {
  width: 100%;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.form-row {
  display: flex;
  gap: 16px;
}

.form-row .form-group {
  flex: 1;
}

/* 优惠券 */
.coupon-section {
  background-color: #fff;
  margin: 12px 0;
}

.coupon-count {
  font-size: 12px;
  color: #999;
}

.coupon-list {
  padding: 16px;
}

.coupon-item {
  display: flex;
  align-items: center;
  padding: 12px;
  margin-bottom: 12px;
  border: 1px solid #e8e8e8;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.coupon-item:last-child {
  margin-bottom: 0;
}

.coupon-item:hover {
  border-color: #e4393c;
  box-shadow: 0 2px 8px rgba(228, 57, 60, 0.1);
}

.coupon-item.active {
  border-color: #e4393c;
  background-color: #fff5f5;
}

.coupon-amount {
  width: 60px;
  height: 60px;
  background: linear-gradient(135deg, #e4393c 0%, #c1272d 100%);
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: bold;
  margin-right: 12px;
  flex-shrink: 0;
}

.currency {
  font-size: 12px;
}

.value {
  font-size: 18px;
}

.coupon-info {
  flex: 1;
}

.coupon-name {
  font-size: 14px;
  font-weight: 500;
  color: #333;
  margin-bottom: 4px;
}

.coupon-condition,
.coupon-expiry {
  font-size: 12px;
  color: #999;
  margin-bottom: 2px;
}

.coupon-select {
  color: #e4393c;
  font-weight: bold;
  margin-left: 12px;
}

.no-coupon {
  text-align: center;
  color: #999;
  padding: 20px;
}

/* 支付金额明细 */
.payment-summary {
  background-color: #fff;
  padding: 16px;
  margin: 12px 0;
}

.summary-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
  font-size: 14px;
}

.summary-item:last-child {
  margin-bottom: 0;
}

.summary-item.discount span:last-child {
  color: #e4393c;
}

.summary-item.total {
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

/* 支付按钮 */
.payment-actions {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background-color: #fff;
  padding: 12px 16px;
  box-shadow: 0 -2px 8px rgba(0,0,0,0.1);
  z-index: 100;
}

.payment-btn {
  width: 100%;
  padding: 14px;
  background-color: #e4393c;
  color: #fff;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.payment-btn:hover:not(:disabled) {
  background-color: #c1272d;
}

.payment-btn:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

/* 安全提示 */
.security-tips {
  background-color: #fff;
  padding: 16px;
  margin: 12px 0;
  border-radius: 8px;
}

.tip-item {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
  font-size: 12px;
  color: #666;
}

.tip-item:last-child {
  margin-bottom: 0;
}

.tip-icon {
  margin-right: 8px;
}

.recharge-btn {
  padding: 6px 12px;
  background-color: #e4393c;
  color: white;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
  margin-left: 12px;
}

.recharge-btn:hover {
  background-color: #c1272d;
}
</style>