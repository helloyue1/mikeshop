<template>
  <div class="jd-account-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <button class="back-btn" @click="goBack">← 返回</button>
      <h1 class="page-title">账户余额</h1>
    </div>
    

    <!-- 京东风格资产总览卡片 -->
    <div class="jd-asset-overview jd-card-shadow">
      <div class="jd-asset-header">
        <div class="jd-asset-title">资产总览</div>
        <div class="jd-asset-update">更新时间: {{ lastUpdateTime }}</div>
      </div>
      <div class="jd-asset-amount">
        <span class="jd-currency">¥</span>
        <span class="jd-balance">{{ totalAsset.toFixed(2) }}</span>
      </div>
      <div class="jd-asset-actions">
        <button class="jd-btn jd-btn-sm jd-btn-primary" @click="openRechargeModal">充值</button>
        <button class="jd-btn jd-btn-sm jd-btn-default" @click="openWithdrawModal">提现</button>
      </div>
    </div>

    <!-- 京东风格资产明细 -->
    <div class="jd-asset-details jd-card-shadow">
      <div class="jd-details-header">
        <h2 class="jd-details-title">资产明细</h2>
      </div>
      <div class="jd-details-grid">
        <div class="jd-detail-item">
          <div class="jd-detail-icon jd-points-icon">积分</div>
          <div class="jd-detail-info">
            <div class="jd-detail-name">积分余额</div>
            <div class="jd-detail-value">{{ pointsBalance }}</div>
          </div>
        </div>
        <div class="jd-detail-item">
          <div class="jd-detail-icon jd-diamond-icon">钻石</div>
          <div class="jd-detail-info">
            <div class="jd-detail-name">钻石余额</div>
            <div class="jd-detail-value">{{ diamondBalance }}</div>
          </div>
        </div>
        <div class="jd-detail-item">
          <div class="jd-detail-icon jd-coupon-icon">券</div>
          <div class="jd-detail-info">
            <div class="jd-detail-name">优惠券</div>
            <div class="jd-detail-value">{{ couponCount }}张</div>
          </div>
        </div>
        <div class="jd-detail-item">
          <div class="jd-detail-icon jd-max-icon">最高</div>
          <div class="jd-detail-info">
            <div class="jd-detail-name">最高面值</div>
            <div class="jd-detail-value">¥{{ maxCouponValue }}</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 京东风格交易记录 -->
    <div class="jd-transactions jd-card-shadow">
      <div class="jd-transactions-header">
        <h2 class="jd-transactions-title">交易记录</h2>
        <div class="jd-transactions-filters">
          <select v-model="transactionFilter" class="jd-select">
            <option value="">全部类型</option>
            <option value="充值">充值</option>
            <option value="提现">提现</option>
            <option value="消费">消费</option>
          </select>
          <input type="date" v-model="transactionDate" class="jd-date-input" placeholder="选择日期">
        </div>
      </div>
      <div class="jd-transactions-list">
        <div v-for="transaction in filteredTransactions" :key="transaction.id" class="jd-transaction-item">
          <div class="jd-transaction-info">
            <div class="jd-transaction-type">{{ transaction.type }}</div>
            <div class="jd-transaction-description">{{ transaction.description }}</div>
            <div class="jd-transaction-date">{{ transaction.date }}</div>
          </div>
          <div class="jd-transaction-amount" :class="{ 'jd-positive': transaction.amount > 0, 'jd-negative': transaction.amount < 0 }">
            {{ transaction.amount > 0 ? '+' : '' }}{{ transaction.amount.toFixed(2) }}
          </div>
          <div class="jd-transaction-status jd-status-{{ transaction.status === '成功' ? 'success' : 'pending' }}">
            {{ transaction.status }}
          </div>
        </div>
        <div v-if="filteredTransactions.length === 0" class="jd-empty-transactions">
          <div class="jd-empty-icon">📋</div>
          <p class="jd-empty-text">暂无交易记录</p>
        </div>
      </div>
    </div>

    <!-- 京东风格充值模态框 -->
    <div v-if="showRechargeModal" class="jd-modal">
      <div class="jd-modal-backdrop" @click="showRechargeModal = false"></div>
      <div class="jd-modal-content">
        <div class="jd-modal-header">
          <h3 class="jd-modal-title">账户充值</h3>
          <button class="jd-modal-close" @click="showRechargeModal = false">×</button>
        </div>
        <form @submit.prevent="submitRecharge" class="jd-modal-form">
          <div class="jd-form-group">
            <label class="jd-form-label">充值金额 (元) <span class="jd-required">*</span></label>
            <div class="jd-input-group">
              <span class="jd-input-prefix">¥</span>
              <input type="number" v-model="rechargeAmount" class="jd-form-input" required min="0.01" step="0.01" placeholder="请输入充值金额">
            </div>
            <div class="jd-form-hint">最低充值金额: ¥0.01</div>
          </div>

          <div class="jd-form-group">
            <label class="jd-form-label">充值方式 <span class="jd-required">*</span></label>
            <div class="jd-payment-methods">
              <div 
                class="jd-payment-method" 
                :class="{ 'jd-selected': rechargeMethod === 'alipay' }"
                @click="rechargeMethod = 'alipay'"
              >
                <div class="jd-payment-icon">支付宝</div>
                <div class="jd-payment-name">支付宝</div>
              </div>
              <div 
                class="jd-payment-method" 
                :class="{ 'jd-selected': rechargeMethod === 'wechat' }"
                @click="rechargeMethod = 'wechat'"
              >
                <div class="jd-payment-icon">微信</div>
                <div class="jd-payment-name">微信支付</div>
              </div>
              <div 
                class="jd-payment-method" 
                :class="{ 'jd-selected': rechargeMethod === 'credit_card' }"
                @click="rechargeMethod = 'credit_card'"
              >
                <div class="jd-payment-icon">银行卡</div>
                <div class="jd-payment-name">银行卡</div>
              </div>
            </div>
          </div>

          <div class="jd-form-actions">
            <button type="button" class="jd-btn jd-btn-default" @click="showRechargeModal = false">取消</button>
            <button type="submit" class="jd-btn jd-btn-primary">确认充值</button>
          </div>
        </form>
      </div>
    </div>

    <!-- 京东风格提现模态框 -->
    <div v-if="showWithdrawModal" class="jd-modal">
      <div class="jd-modal-backdrop" @click="showWithdrawModal = false"></div>
      <div class="jd-modal-content">
        <div class="jd-modal-header">
          <h3 class="jd-modal-title">账户提现</h3>
          <button class="jd-modal-close" @click="showWithdrawModal = false">×</button>
        </div>
        <form @submit.prevent="submitWithdraw" class="jd-modal-form">
          <div class="jd-form-group">
            <label class="jd-form-label">提现金额 (元) <span class="jd-required">*</span></label>
            <div class="jd-input-group">
              <span class="jd-input-prefix">¥</span>
              <input type="number" v-model="withdrawAmount" class="jd-form-input" required min="0.01" step="0.01" :max="totalAsset" placeholder="请输入提现金额">
            </div>
            <div class="jd-form-hint jd-info-hint">当前可提现金额: <span class="jd-highlight">¥{{ totalAsset.toFixed(2) }}</span></div>
          </div>

          <div class="jd-form-group">
            <label class="jd-form-label">提现方式 <span class="jd-required">*</span></label>
            <div class="jd-payment-methods">
              <div 
                class="jd-payment-method" 
                :class="{ 'jd-selected': withdrawMethod === 'alipay' }"
                @click="withdrawMethod = 'alipay'"
              >
                <div class="jd-payment-icon">支付宝</div>
                <div class="jd-payment-name">支付宝</div>
              </div>
              <div 
                class="jd-payment-method" 
                :class="{ 'jd-selected': withdrawMethod === 'wechat' }"
                @click="withdrawMethod = 'wechat'"
              >
                <div class="jd-payment-icon">微信</div>
                <div class="jd-payment-name">微信支付</div>
              </div>
              <div 
                class="jd-payment-method" 
                :class="{ 'jd-selected': withdrawMethod === 'bank_card' }"
                @click="withdrawMethod = 'bank_card'"
              >
                <div class="jd-payment-icon">银行卡</div>
                <div class="jd-payment-name">银行卡</div>
              </div>
            </div>
          </div>

          <div class="jd-form-group" v-if="withdrawMethod">
            <label class="jd-form-label">
              {{ withdrawMethod === 'alipay' ? '支付宝账号' : withdrawMethod === 'wechat' ? '微信账号' : '银行卡号' }} 
              <span class="jd-required">*</span>
            </label>
            <input 
              type="text" 
              v-model="withdrawAccount" 
              class="jd-form-input" 
              required 
              :placeholder="withdrawMethod === 'alipay' ? '请输入支付宝账号' : withdrawMethod === 'wechat' ? '请输入微信账号' : '请输入银行卡号'"
            >
          </div>

          <div class="jd-form-actions">
            <button type="button" class="jd-btn jd-btn-default" @click="showWithdrawModal = false">取消</button>
            <button type="submit" class="jd-btn jd-btn-primary">确认提现</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue';
import { useRouter } from 'vue-router';
import apiService from '@/services/apiService';

const router = useRouter();

// 返回按钮
const goBack = () => { 
  router.go(-1); 
};

// 资产数据
const totalAsset = ref(1256.80);
const accountDataExists = ref(false);
const accoutId = ref(null);
const lastUpdateTime = ref('2023-11-15 14:30:22');
const pointsBalance = ref(5800);
const diamondBalance = ref(120);
const couponCount = ref(5);
const maxCouponValue = ref(100);

// 交易记录
const transactions = ref([
  { id: 1, date: '2023-11-10', type: '充值', description: '支付宝充值', amount: 500, status: '成功' },
  { id: 2, date: '2023-11-05', type: '消费', description: '购买商品', amount: -129.90, status: '成功' },
  { id: 3, date: '2023-10-28', type: '获得', description: '购物返积分', amount: 50, status: '成功' },
  { id: 4, date: '2023-10-20', type: '提现', description: '提现到微信', amount: -200, status: '成功' },
  { id: 5, date: '2023-10-15', type: '获得', description: '签到奖励', amount: 5, status: '成功' },
]);

// 交易筛选
const transactionFilter = ref('');
const transactionDate = ref('');

// 充值相关
const showRechargeModal = ref(false);
const rechargeAmount = ref(0);
const rechargeMethod = ref('');

// 提现相关
const showWithdrawModal = ref(false);
const withdrawAmount = ref(0);
const withdrawMethod = ref('');
const withdrawAccount = ref('');

// 加载交易记录
const loadTransactions = async () => {
  try {
    const userId = getCurrentUserId();
    if (!userId) return;

    let transData;
    if (transactionFilter.value && transactionDate.value) {
      // 组合查询
      transData = await apiService.getTransInfoByDateAndType(transactionDate.value, transactionFilter.value);
    } else if (transactionFilter.value) {
      // 按类型查询
      transData = await apiService.getTransByType(transactionFilter.value);
    } else if (transactionDate.value) {
      // 按日期查询
      transData = await apiService.getTransInfoByDate(transactionDate.value);
    } else {
      // 查询所有
      transData = await apiService.selectTransByUserId(userId);
    }

    if (transData && transData.data) {
      transactions.value = transData.data.map(item => ({
        id: item.trans_id,
        date: item.trans_date,
        type: item.trans_type,
        description: item.description,
        amount: parseFloat(item.amount),
        status: item.status
      }));
    }
  } catch (error) {
    console.error('加载交易记录失败:', error);
  }
};

// 监听筛选条件变化重新加载数据
watch([transactionFilter, transactionDate], loadTransactions);

// 初始加载交易记录
onMounted(loadTransactions);

// 筛选后的交易记录
const filteredTransactions = computed(() => transactions.value);

// 显示充值模态框
const openRechargeModal = () => {
  showRechargeModal.value = true;
  rechargeAmount.value = 0;
  rechargeMethod.value = '';
};

// 显示提现模态框
const openWithdrawModal = () => {
  showWithdrawModal.value = true;
  withdrawAmount.value = 0;
  withdrawMethod.value = '';
  withdrawAccount.value = '';
};

// 提交充值
const submitRecharge = async () => {
  if (!rechargeAmount.value || rechargeAmount.value <= 0) {
    alert('请输入有效的充值金额');
    return;
  }

  if (!rechargeMethod.value) {
    alert('请选择充值方式');
    return;
  }

  try {
    const userId = getCurrentUserId();
    if (!userId) {
      alert('请先登录');
      return;
    }

    // 准备交易数据
    const transData = {
      user_id: userId,
      trans_date: new Date().toISOString().split('T')[0],
      trans_type: '充值',
      description: `${rechargeMethod.value === 'alipay' ? '支付宝' : rechargeMethod.value === 'wechat' ? '微信' : '银行卡'}充值`,
      amount: rechargeAmount.value.toString(),
      status: '成功'
    };

    // 调用API提交交易记录
    const transResponse = await apiService.insertTransInfo(transData);
    if (!transResponse.success) {
      throw new Error(transResponse.message || '提交交易记录失败');
    }

    // 更新账户余额
    const newBalance = totalAsset.value + rechargeAmount.value;
    const isNewAccount = !accountDataExists.value;
    const url = isNewAccount ? '/api/insertAccout' : '/api/updateAccout';
    const method = isNewAccount ? 'POST' : 'PUT';
    const response = await fetch(url, {
      method: method,
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        total_asset: newBalance,
        user_id: userId,
        accout_id: isNewAccount ? null : accoutId.value,
        last_updated: new Date().toISOString(),
        withdraw_method: withdrawMethod.value,
        withdraw_account: withdrawAccount.value
      })
    });

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    const result = await response.json();
    if (result && result.code === 200) {
      totalAsset.value = newBalance;
      // 重新加载交易列表
      loadTransactions();
      showRechargeModal.value = false;
      alert(`充值成功！已充值 ¥${rechargeAmount.value.toFixed(2)}`);
    } else {
      throw new Error(result.message || '充值失败');
    }
  } catch (error) {
    console.error('充值失败:', error);
    alert('充值失败，请重试');
  }
};

// 提交提现
const submitWithdraw = async () => {
  if (!withdrawAmount.value || withdrawAmount.value <= 0) {
    alert('请输入有效的提现金额');
    return;
  }

  if (withdrawAmount.value > totalAsset.value) {
    alert('提现金额不能超过总资产');
    return;
  }

  if (!withdrawMethod.value || !withdrawAccount.value) {
    alert('请选择提现方式并填写账号');
    return;
  }

  try {
    const userId = getCurrentUserId();
    if (!userId) {
      alert('请先登录');
      return;
    }

    // 准备交易数据
    const transData = {
      user_id: userId,
      trans_date: new Date().toISOString().split('T')[0],
      trans_type: '提现',
      description: `提现到${withdrawMethod.value === 'alipay' ? '支付宝' : withdrawMethod.value === 'wechat' ? '微信' : '银行卡'}`,
      amount: withdrawAmount.value.toString(),
      status: '成功'
    };

    // 调用API提交交易记录
    const transResponse = await apiService.insertTransInfo(transData);
    if (!transResponse.success) {
      throw new Error(transResponse.message || '提交交易记录失败');
    }

    // 更新账户余额
    const newBalance = totalAsset.value - withdrawAmount.value;
    const response = await fetch('/api/updateAccout', {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        total_asset: newBalance,
        user_id: userId,
        accout_id: accoutId.value,
        last_updated: new Date().toISOString(),
        withdraw_method: withdrawMethod.value,
        withdraw_account: withdrawAccount.value
      })
    });

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    const result = await response.json();
    if (result && result.code === 200) {
      totalAsset.value = newBalance;
      // 重新加载交易列表
      loadTransactions();
      showWithdrawModal.value = false;
      alert(`提现成功！已提现 ¥${withdrawAmount.value.toFixed(2)}`);
    } else {
      throw new Error(result.message || '提现失败');
    }
  } catch (error) {
    console.error('提现失败:', error);
    alert('提现失败，请重试');
  }
};

// 从localStorage获取登录用户信息
const getUserInfo = () => {
  const userInfoStr = localStorage.getItem('userInfo');
  if (userInfoStr) {
    return JSON.parse(userInfoStr);
  }
  return null;
};

// 获取当前登录用户ID
const getCurrentUserId = () => {
  const userInfo = getUserInfo();
  return userInfo ? userInfo.id : null;
};

// 加载资产数据
const loadAssetData = async () => {
  try {
    const userId = getCurrentUserId();
    if (!userId) {
      console.warn('用户未登录，无法加载账户信息');
      return;
    }
    
    const response = await fetch(`/api/selectAccout?user_id=${userId}`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
      }
    });
    
    const result = await response.json();
    if (result.code === 200) {
      accountDataExists.value = result.data !== null;
      if (result.data) {
        // 更新总资产
        totalAsset.value = result.data.total_asset || 0;
        accoutId.value = result.data.accout_id;
        lastUpdateTime.value = result.data.last_updated ? 
          new Date(result.data.last_updated).toLocaleString('zh-CN') : 
          new Date().toLocaleString('zh-CN');
        
        // 更新其他资产信息（如果有的话）
        pointsBalance.value = result.data.points_balance || 0;
        diamondBalance.value = result.data.diamond_balance || 0;
        couponCount.value = result.data.coupon_count || 0;
        maxCouponValue.value = result.data.max_coupon_value || 0;
        
        console.log('账户信息加载成功:', result.data);
      } else {
        // 账户数据为空，重置资产信息
        totalAsset.value = 0;
        lastUpdateTime.value = new Date().toLocaleString('zh-CN');
        pointsBalance.value = 0;
        diamondBalance.value = 0;
        couponCount.value = 0;
        maxCouponValue.value = 0;
        console.log('账户数据为空');
      }
    } else {
      console.warn('获取账户信息失败:', result.message || '未知错误');
    }
  } catch (error) {
    console.error('加载资产数据失败:', error);
  }
};

// 页面加载时获取数据
onMounted(() => {
  loadAssetData();
});
</script>

<style scoped>
.jd-account-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  min-height: 100vh;
}

.jd-page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  padding: 20px;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(10px);
}

.jd-page-title {
  font-size: 28px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.jd-header-actions {
  display: flex;
  gap: 10px;
}

.jd-btn {
  padding: 10px 20px;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 5px;
}

.jd-btn-primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.jd-btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.4);
}

.jd-btn-default {
  background: #f8f9fa;
  color: #666;
  border: 1px solid #e9ecef;
}

.jd-btn-default:hover {
  background: #e9ecef;
  color: #333;
}

.jd-btn-sm {
  padding: 6px 12px;
  font-size: 12px;
}

.jd-btn-icon {
  font-weight: bold;
}

.jd-card-shadow {
  background: rgba(255, 255, 255, 0.9);
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  margin-bottom: 30px;
}

.jd-asset-overview {
  padding: 30px;
  text-align: center;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%);
}

.jd-asset-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.jd-asset-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.jd-asset-update {
  font-size: 12px;
  color: #666;
}

.jd-asset-amount {
  margin: 20px 0;
}

.jd-currency {
  font-size: 24px;
  color: #667eea;
  font-weight: 600;
}

.jd-balance {
  font-size: 48px;
  font-weight: 700;
  color: #333;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.jd-asset-actions {
  display: flex;
  justify-content: center;
  gap: 15px;
}

.jd-asset-details {
  padding: 25px;
}

.jd-details-header {
  margin-bottom: 20px;
}

.jd-details-title {
  font-size: 20px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.jd-details-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
}

.jd-detail-item {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 15px;
  background: rgba(255, 255, 255, 0.7);
  border-radius: 12px;
  transition: all 0.3s ease;
}

.jd-detail-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
}

.jd-detail-icon {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 600;
  color: white;
}

.jd-points-icon {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.jd-diamond-icon {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.jd-coupon-icon {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.jd-max-icon {
  background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
}

.jd-detail-info {
  flex: 1;
}

.jd-detail-name {
  font-size: 14px;
  color: #666;
  margin-bottom: 4px;
}

.jd-detail-value {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.jd-transactions {
  padding: 25px;
}

.jd-transactions-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  flex-wrap: wrap;
  gap: 15px;
}

.jd-transactions-title {
  font-size: 20px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.jd-transactions-filters {
  display: flex;
  gap: 10px;
}

.jd-select, .jd-date-input {
  padding: 8px 12px;
  border: 1px solid #e9ecef;
  border-radius: 6px;
  font-size: 14px;
  background: white;
}

.jd-transactions-list {
  max-height: 400px;
  overflow-y: auto;
}

.jd-transaction-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
}

.jd-transaction-item:hover {
  background: rgba(102, 126, 234, 0.05);
}

.jd-transaction-info {
  flex: 1;
}

.jd-transaction-type {
  font-size: 14px;
  font-weight: 600;
  color: #333;
  margin-bottom: 4px;
}

.jd-transaction-description {
  font-size: 12px;
  color: #666;
  margin-bottom: 4px;
}

.jd-transaction-date {
  font-size: 11px;
  color: #999;
}

.jd-transaction-amount {
  font-size: 16px;
  font-weight: 600;
  margin: 0 15px;
}

.jd-positive {
  color: #28a745;
}

.jd-negative {
  color: #dc3545;
}

.jd-transaction-status {
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 11px;
  font-weight: 500;
}

.jd-status-success {
  background: #d4edda;
  color: #155724;
}

.jd-status-pending {
  background: #fff3cd;
  color: #856404;
}

.jd-empty-transactions {
  text-align: center;
  padding: 40px;
  color: #666;
}

.jd-empty-icon {
  font-size: 48px;
  margin-bottom: 10px;
}

.jd-empty-text {
  font-size: 16px;
  margin: 0;
}

/* 模态框样式 */
.jd-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 1000;
  display: flex;
  align-items: center;
  justify-content: center;
}

.jd-modal-backdrop {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(5px);
}

.jd-modal-content {
  position: relative;
  background: white;
  border-radius: 16px;
  max-width: 500px;
  width: 90%;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  z-index: 1001;
}

.jd-modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 25px;
  border-bottom: 1px solid #e9ecef;
}

.jd-modal-title {
  font-size: 20px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.jd-modal-close {
  background: none;
  border: none;
  font-size: 24px;
  color: #666;
  cursor: pointer;
  padding: 0;
  width: 30px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: all 0.3s ease;
}

.jd-modal-close:hover {
  background: #f8f9fa;
  color: #333;
}

.jd-modal-form {
  padding: 25px;
}

.jd-form-group {
  margin-bottom: 20px;
}

.jd-form-label {
  display: block;
  font-size: 14px;
  font-weight: 500;
  color: #333;
  margin-bottom: 8px;
}

.jd-required {
  color: #dc3545;
}

.jd-input-group {
  display: flex;
  align-items: center;
  border: 1px solid #e9ecef;
  border-radius: 8px;
  overflow: hidden;
}

.jd-input-prefix {
  padding: 12px 15px;
  background: #f8f9fa;
  color: #666;
  font-weight: 500;
  border-right: 1px solid #e9ecef;
}

.jd-form-input {
  flex: 1;
  padding: 12px 15px;
  border: none;
  font-size: 14px;
  outline: none;
}

.jd-form-hint {
  font-size: 12px;
  color: #666;
  margin-top: 5px;
}

.jd-info-hint {
  color: #667eea;
}

.jd-highlight {
  font-weight: 600;
  color: #667eea;
}

.jd-payment-methods {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
  gap: 10px;
}

.jd-payment-method {
  padding: 15px;
  border: 2px solid #e9ecef;
  border-radius: 8px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
}

.jd-payment-method:hover {
  border-color: #667eea;
  background: rgba(102, 126, 234, 0.05);
}

.jd-payment-method.jd-selected {
  border-color: #667eea;
  background: rgba(102, 126, 234, 0.1);
}

.jd-payment-icon {
  font-size: 12px;
  font-weight: 600;
  color: #666;
  margin-bottom: 5px;
}

.jd-payment-name {
  font-size: 14px;
  color: #333;
}

.jd-form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 25px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .jd-account-container {
    padding: 15px;
  }
  
  .jd-page-header {
    flex-direction: column;
    gap: 15px;
    text-align: center;
  }
  
  .jd-header-actions {
    justify-content: center;
  }
  
  .jd-details-grid {
    grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  }
  
  .jd-transactions-header {
    flex-direction: column;
    align-items: stretch;
  }
  
  .jd-transactions-filters {
    justify-content: center;
  }
  
  .jd-transaction-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .jd-transaction-amount {
    margin: 0;
  }
}
</style>