<template>
  <div class="jd-wallet-container">
    <!-- 页面头部 -->
    <div class="jd-wallet-header">
      <div class="jd-header-back" @click="goBack">
        <i class="jd-icon jd-icon-back"></i>
      </div>
      <div class="jd-header-title">我的钱包</div>
      <div class="jd-header-user-info">
        <span class="jd-user-id">用户ID: {{ userId }}</span>
      </div>
    </div>

    <!-- 资产概览卡片 -->
    <div class="jd-asset-overview">
      <div class="jd-asset-card">
        <div class="jd-amount-section">
          <div class="jd-amount-label">用户余额（元）</div>
          <div class="jd-amount-value">{{ formatAmount(totalAsset) }}</div>
          <div class="update-time">最后更新: {{ lastUpdated }}</div>
        </div>
        <div class="jd-asset-actions">
          <button class="jd-btn jd-btn-primary" @click="showRecharge">充值</button>
          <button class="jd-btn jd-btn-outline" @click="showWithdraw">提现</button>
        </div>
      </div>
    </div>

    <!-- 资产明细 -->
    <div class="jd-asset-details">
      <div class="jd-section-title">资产明细</div>
      <div class="jd-asset-grid">
        <div class="jd-asset-item">
          <div class="jd-asset-icon jd-asset-balance"></div>
          <div class="jd-asset-info">
            <div class="jd-asset-name">可用余额</div>
            <div class="jd-asset-value">{{ formatAmount(availableBalance) }}</div>
          </div>
        </div>
        <div class="jd-asset-item">
          <div class="jd-asset-icon jd-asset-frozen"></div>
          <div class="jd-asset-info">
            <div class="jd-asset-name">冻结金额</div>
            <div class="jd-asset-value">{{ formatAmount(frozenAmount) }}</div>
          </div>
        </div>
        <div class="jd-asset-item">
          <div class="jd-asset-icon jd-asset-points"></div>
          <div class="jd-asset-info">
            <div class="jd-asset-name">积分</div>
            <div class="jd-asset-value">{{ points }}</div>
          </div>
        </div>
        <div class="jd-asset-item">
          <div class="jd-asset-icon jd-asset-coupons"></div>
          <div class="jd-asset-info">
            <div class="jd-asset-name">优惠券</div>
            <div class="jd-asset-value">{{ coupons }}张</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 支付方式 -->
    <div class="jd-payment-methods-section">
      <div class="jd-section-title">我的支付方式</div>
      <div class="jd-payment-methods-list">
        <div v-if="paymentMethods.length === 0" class="jd-empty-state">
          <div class="jd-empty-icon"></div>
          <div class="jd-empty-text">暂无支付方式</div>
        </div>
        <div v-else class="jd-payment-methods-grid">
          <div v-for="method in paymentMethods" :key="method.pay_id" class="jd-payment-method-item">
            <div class="jd-payment-method-info">
              <div class="jd-payment-method-type">{{ method.type }}</div>
              <div class="jd-payment-method-account">{{ method?.account_info }}</div>
              <div class="jd-payment-method-default" v-if="method?.is_default === 1">默认</div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 交易记录 -->
    <div class="jd-transaction-history">
      <div class="jd-section-header">
        <div class="jd-section-title">交易记录</div>
        <div class="jd-section-more" @click="viewAllTransactions">查看全部</div>
      </div>
      <div class="jd-transaction-list">
        <div v-if="transactions.length === 0" class="jd-empty-state">
          <div class="jd-empty-icon"></div>
          <div class="jd-empty-text">暂无交易记录</div>
        </div>
        <div v-else class="jd-transaction-items">
          <div v-for="transaction in transactions" :key="transaction.id" class="jd-transaction-item">
            <div class="jd-transaction-left">
              <div class="jd-transaction-icon" :class="getTransactionIconClass(transaction.type)"></div>
              <div class="jd-transaction-info">
                <div class="jd-transaction-title">{{ transaction.title }}</div>
                <div class="jd-transaction-time">{{ transaction.time }}</div>
              </div>
            </div>
            <div class="jd-transaction-right">
              <div class="jd-transaction-amount" :class="getAmountClass(transaction.amount)">
                {{ formatAmount(transaction.amount) }}
              </div>
              <div class="jd-transaction-status" :class="getStatusClass(transaction.status)">
                {{ transaction.status }}
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 充值模态框 -->
    <div v-if="showRechargeModal" class="jd-modal" @click="closeRechargeModal">
      <div class="jd-modal-content" @click.stop>
        <div class="jd-modal-header">
          <div class="jd-modal-title">账户充值</div>
          <div class="jd-modal-close" @click="closeRechargeModal">×</div>
        </div>
        <div class="jd-modal-body">
          <div class="jd-form-group">
            <label class="jd-form-label">充值金额</label>
            <div class="jd-input-group">
              <span class="jd-input-prefix">¥</span>
              <input type="number" v-model="rechargeAmount" class="jd-input" placeholder="请输入充值金额">
            </div>
          </div>
          <div class="jd-form-group">
            <label class="jd-form-label">充值方式</label>
            <div class="jd-payment-methods">
              <div class="jd-payment-method" :class="{ active: selectedPayment === 'alipay' }" @click="selectPayment('alipay')">
                <div class="jd-payment-icon jd-payment-alipay"></div>
                <div class="jd-payment-name">支付宝</div>
              </div>
              <div class="jd-payment-method" :class="{ active: selectedPayment === 'wechat' }" @click="selectPayment('wechat')">
                <div class="jd-payment-icon jd-payment-wechat"></div>
                <div class="jd-payment-name">微信支付</div>
              </div>
              <div class="jd-payment-method" :class="{ active: selectedPayment === 'bank' }" @click="selectPayment('bank')">
                <div class="jd-payment-icon jd-payment-bank"></div>
                <div class="jd-payment-name">银行卡</div>
              </div>
            </div>
          </div>
        </div>
        <div class="jd-modal-footer">
          <button class="jd-btn jd-btn-cancel" @click="closeRechargeModal">取消</button>
          <button class="jd-btn jd-btn-primary" @click="confirmRecharge">确认充值</button>
        </div>
      </div>
    </div>

    <!-- 提现模态框 -->
    <div v-if="showWithdrawModal" class="jd-modal" @click="closeWithdrawModal">
      <div class="jd-modal-content" @click.stop>
        <div class="jd-modal-header">
          <div class="jd-modal-title">账户提现</div>
          <div class="jd-modal-close" @click="closeWithdrawModal">×</div>
        </div>
        <div class="jd-modal-body">
          <div class="jd-form-group">
            <label class="jd-form-label">提现金额</label>
            <div class="jd-input-group">
              <span class="jd-input-prefix">¥</span>
              <input type="number" v-model="withdrawAmount" class="jd-input" placeholder="请输入提现金额">
            </div>
            <div class="jd-form-hint">可用余额：{{ formatAmount(availableBalance) }}</div>
          </div>
          <div class="jd-form-group">
            <label class="jd-form-label">提现到</label>
            <div class="jd-bank-cards">
              <div class="jd-bank-card" :class="{ active: selectedBankCard === 'card1' }" @click="selectBankCard('card1')">
                <div class="jd-bank-icon jd-bank-abc"></div>
                <div class="jd-bank-info">
                  <div class="jd-bank-name">中国农业银行</div>
                  <div class="jd-bank-number">**** **** **** 8888</div>
                </div>
              </div>
              <div class="jd-bank-card" :class="{ active: selectedBankCard === 'card2' }" @click="selectBankCard('card2')">
                <div class="jd-bank-icon jd-bank-icbc"></div>
                <div class="jd-bank-info">
                  <div class="jd-bank-name">中国工商银行</div>
                  <div class="jd-bank-number">**** **** **** 6666</div>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="jd-modal-footer">
          <button class="jd-btn jd-btn-cancel" @click="closeWithdrawModal">取消</button>
          <button class="jd-btn jd-btn-primary" @click="confirmWithdraw">确认提现</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import apiService from '../services/apiService'

const router = useRouter()

// 账户信息
const accountId = ref<number>(1)
const userId = ref<number>(1)
const totalAsset = ref<number>(0)
const availableBalance = ref<number>(0)
const frozenAmount = ref<number>(0)
const points = ref<number>(0)
const coupons = ref<number>(0)
const lastUpdated = ref<string>('')
const transactions = ref([
  {
    id: 1,
    title: '在线购物支付',
    amount: -299.00,
    time: '2024-01-15 14:25',
    type: 'payment',
    status: '已完成'
  },
  {
    id: 2,
    title: '账户充值',
    amount: 1000.00,
    time: '2024-01-14 10:15',
    type: 'recharge',
    status: '已完成'
  },
  {
    id: 3,
    title: '退款到账',
    amount: 158.50,
    time: '2024-01-13 16:45',
    type: 'refund',
    status: '已完成'
  },
  {
    id: 4,
    title: '提现处理中',
    amount: -500.00,
    time: '2024-01-12 09:30',
    type: 'withdraw',
    status: '处理中'
  }
])

// 充值相关
const showRechargeModal = ref(false)
const rechargeAmount = ref('')
const selectedPayment = ref('alipay')

// 支付方式相关
const paymentMethods = ref([])

// 查询支付方式
const getPaymentMethods = async () => {
  try {
    console.log('开始查询支付方式，用户ID:', userId.value)
    const response = await apiService.selectPayMethod(userId.value)
    console.log('查询支付方式响应:', response)
    
    if (response.success && response.data) {
      paymentMethods.value = response.data
      console.log('支付方式数据:', paymentMethods.value)
    } else {
      console.log('未找到支付方式信息')
      paymentMethods.value = []
    }
  } catch (error) {
    console.error('查询支付方式失败:', error)
    paymentMethods.value = []
  }
}

// 提现相关
const showWithdrawModal = ref(false)
const withdrawAmount = ref('')
const selectedBankCard = ref('card1')

// 格式化金额
const formatAmount = (amount: number): string => {
  return amount.toFixed(2)
}

// 获取交易图标类名
const getTransactionIconClass = (type: string): string => {
  const iconMap: Record<string, string> = {
    payment: 'jd-icon-payment',
    recharge: 'jd-icon-recharge',
    refund: 'jd-icon-refund',
    withdraw: 'jd-icon-withdraw'
  }
  return iconMap[type] || 'jd-icon-default'
}

// 获取金额样式类名
const getAmountClass = (amount: number): string => {
  return amount >= 0 ? 'jd-amount-positive' : 'jd-amount-negative'
}

// 获取状态样式类名
const getStatusClass = (status: string): string => {
  const statusMap: Record<string, string> = {
    '已完成': 'jd-status-success',
    '处理中': 'jd-status-pending',
    '失败': 'jd-status-failed'
  }
  return statusMap[status] || 'jd-status-default'
}

// 返回上一页
const goBack = () => {
  router.back()
}

// 显示充值模态框
const showRecharge = () => {
  showRechargeModal.value = true
}

// 关闭充值模态框
const closeRechargeModal = () => {
  showRechargeModal.value = false
  rechargeAmount.value = ''
}

// 选择支付方式
const selectPayment = (method: string) => {
  selectedPayment.value = method
}

// 确认充值
const confirmRecharge = async () => {
  if (!rechargeAmount.value || parseFloat(rechargeAmount.value) <= 0) {
    alert('请输入有效的充值金额')
    return
  }
  
  try {
    // 计算充值后的总资产
    const amount = parseFloat(rechargeAmount.value)
    const newTotalAsset = totalAsset.value + amount
    
    // 调用更新接口
    const updateData = {
      accout_id: accountId.value,
      user_id: userId.value,
      total_asset: newTotalAsset,
      last_updated: new Date().toLocaleString('zh-CN')
    }
    
    console.log('调用更新接口，数据:', updateData)
    const response = await apiService.updateAccountInfo(updateData)
    console.log('更新接口响应:', response)
    
    if (response.success) {
      // 更新页面显示
      totalAsset.value = newTotalAsset
      availableBalance.value = newTotalAsset
      lastUpdated.value = updateData.last_updated
      
      // 添加交易记录
      transactions.value.unshift({
        id: transactions.value.length + 1,
        title: '账户充值',
        amount: amount,
        time: new Date().toLocaleString('zh-CN', { 
          year: 'numeric', 
          month: '2-digit', 
          day: '2-digit', 
          hour: '2-digit', 
          minute: '2-digit' 
        }).replace(/\//g, '-'),
        type: 'recharge',
        status: '已完成'
      })
      
      closeRechargeModal()
      alert('充值成功！')
    } else {
      alert('充值失败，请稍后重试')
    }
  } catch (error) {
    console.error('充值失败:', error)
    alert('充值失败，请稍后重试')
  }
}

// 显示提现模态框
const showWithdraw = () => {
  showWithdrawModal.value = true
}

// 关闭提现模态框
const closeWithdrawModal = () => {
  showWithdrawModal.value = false
  withdrawAmount.value = ''
}

// 选择银行卡
const selectBankCard = (card: string) => {
  selectedBankCard.value = card
}

// 确认提现
const confirmWithdraw = () => {
  if (!withdrawAmount.value || parseFloat(withdrawAmount.value) <= 0) {
    alert('请输入有效的提现金额')
    return
  }
  
  const amount = parseFloat(withdrawAmount.value)
  if (amount > availableBalance.value) {
    alert('提现金额不能超过可用余额')
    return
  }
  
  // 模拟提现处理
  totalAsset.value -= amount
  availableBalance.value -= amount
  
  // 添加交易记录
  transactions.value.unshift({
    id: transactions.value.length + 1,
    title: '账户提现',
    amount: -amount,
    time: new Date().toLocaleString('zh-CN', { 
      year: 'numeric', 
      month: '2-digit', 
      day: '2-digit', 
      hour: '2-digit', 
      minute: '2-digit' 
    }).replace(/\//g, '-'),
    type: 'withdraw',
    status: '处理中'
  })
  
  closeWithdrawModal()
  alert('提现申请已提交，请等待处理！')
}

// 查看所有交易记录
const viewAllTransactions = () => {
  // 这里可以跳转到交易记录详情页
  alert('查看全部交易记录功能开发中...')
}

// 获取当前用户ID
const getCurrentUserId = () => {
  const userStr = localStorage.getItem('user')
  if (userStr) {
    try {
      const userInfo = JSON.parse(userStr)
      return userInfo.id || 1
    } catch (error) {
      console.error('解析用户信息失败:', error)
      return 1
    }
  }
  return 1
}

// 监听用户变化并重新加载账户信息
const watchUserChange = () => {
  const currentUserId = getCurrentUserId()
  if (currentUserId !== userId.value) {
    userId.value = currentUserId
    // 重置账户数据
    totalAsset.value = 0
    availableBalance.value = 0
    frozenAmount.value = 0
    points.value = 0
    coupons.value = 0
    lastUpdated.value = ''
    // 重新加载账户信息
    getAccountInfo()
    getPaymentMethods()
  }
}

// 获取账户信息
const getAccountInfo = async () => {
  try {
    console.log('开始查询账户信息，用户ID:', userId.value)
    
    // 查询账户信息
    const response = await apiService.selectAccountInfo(userId.value)
    console.log('查询账户信息响应:', response)
    
    if (response.data && response.success) {
      // 直接使用API返回的数据更新页面显示
      const accountData = response.data
      console.log('账户数据:', accountData)
      
      // 更新页面显示的账户信息
      totalAsset.value = accountData.total_asset || 0
      availableBalance.value = accountData.total_asset || 0
      accountId.value = accountData.accout_id || 1
      lastUpdated.value = accountData.last_updated || new Date().toLocaleString('zh-CN')
      
      console.log('已用API返回数据更新页面显示:', {
        totalAsset: totalAsset.value,
        availableBalance: availableBalance.value,
        accountId: accountId.value,
        lastUpdated: lastUpdated.value
      })
    } else {
      // 账户信息不存在，执行插入操作
      console.log('账户信息不存在，准备插入新账户数据')
      const newAccountData = {
        user_id: userId.value,
        balance: 0,
        total_asset: 0,
        frozen_amount: 0,
        points: 0,
        coupons: 0
      }
      
      console.log('插入新账户数据:', newAccountData)
      const insertResponse = await apiService.insertAccountInfo(newAccountData)
      console.log('插入账户信息响应:', insertResponse)
      
      // 如果插入成功，重新查询
      if (insertResponse.success) {
        await getAccountInfo()
      }
    }
  } catch (error) {
    console.error('获取账户信息失败:', error)
    alert('获取账户信息失败，请稍后重试')
  }
}

// 页面加载时获取账户信息
onMounted(async () => {
  // 获取当前用户ID
  userId.value = getCurrentUserId()
  console.log('从localStorage获取到用户ID:', userId.value)
  
  console.log('账户ID:', accountId.value)
  console.log('用户ID:', userId.value)
  console.log('总资产:', totalAsset.value)
  console.log('最后更新时间:', lastUpdated.value)
  
  // 调用获取账户信息函数
  await getAccountInfo()
  
  // 调用查询支付方式函数
  await getPaymentMethods()
  
  // 监听localStorage变化，检测用户切换
  window.addEventListener('storage', (event) => {
    if (event.key === 'user') {
      console.log('检测到用户信息变化，重新加载账户信息')
      watchUserChange()
    }
  })
  
  // 定时检查用户变化（每5秒）
  const userCheckInterval = setInterval(() => {
    watchUserChange()
  }, 5000)
  
  // 组件卸载时清除定时器
  onUnmounted(() => {
    clearInterval(userCheckInterval)
    window.removeEventListener('storage', watchUserChange)
  })
})
</script>

<style scoped>
.jd-wallet-container {
  min-height: 100vh;
  background-color: #f5f5f5;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', sans-serif;
}

/* 页面头部 */
.jd-wallet-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 44px;
  background: linear-gradient(135deg, #e1251b 0%, #ff6b6b 100%);
  color: white;
  padding: 0 15px;
  position: sticky;
  top: 0;
  z-index: 100;
}

.jd-header-back {
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}

.jd-icon-back {
  width: 12px;
  height: 12px;
  border-left: 2px solid white;
  border-bottom: 2px solid white;
  transform: rotate(45deg);
}

.jd-header-title {
  font-size: 17px;
  font-weight: 500;
}

.jd-header-right {
  width: 24px;
}

.jd-header-user-info {
  display: flex;
  align-items: center;
}

.jd-user-id {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.9);
  background: rgba(255, 255, 255, 0.1);
  padding: 4px 8px;
  border-radius: 12px;
  font-weight: 400;
}

/* 资产概览 */
.jd-asset-overview {
  padding: 15px;
}

.jd-asset-card {
  background: linear-gradient(135deg, #e1251b 0%, #ff6b6b 100%);
  border-radius: 12px;
  padding: 20px;
  color: white;
  box-shadow: 0 4px 12px rgba(225, 37, 27, 0.3);
}

.jd-amount-section {
  margin-bottom: 20px;
}

.jd-amount-label {
  font-size: 14px;
  opacity: 0.9;
  margin-bottom: 8px;
}

.jd-amount-value {
  font-size: 32px;
  font-weight: bold;
  margin-bottom: 8px;
}

.jd-amount-update {
  font-size: 12px;
  opacity: 0.8;
}

.jd-asset-actions {
  display: flex;
  gap: 10px;
}

.jd-btn {
  flex: 1;
  height: 40px;
  border: none;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.jd-btn-primary {
  background: white;
  color: #e1251b;
}

.jd-btn-primary:hover {
  background: #f8f8f8;
}

.jd-btn-outline {
  background: transparent;
  color: white;
  border: 1px solid rgba(255, 255, 255, 0.8);
}

.jd-btn-outline:hover {
  background: rgba(255, 255, 255, 0.1);
}

/* 资产明细 */
.jd-asset-details {
  padding: 0 15px 15px;
}

.jd-section-title {
  font-size: 16px;
  font-weight: 500;
  color: #333;
  margin-bottom: 15px;
}

.jd-asset-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.jd-asset-item {
  background: white;
  border-radius: 8px;
  padding: 15px;
  display: flex;
  align-items: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.jd-asset-icon {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  margin-right: 12px;
}

.jd-asset-balance {
  background: linear-gradient(135deg, #4CAF50, #8BC34A);
}

.jd-asset-frozen {
  background: linear-gradient(135deg, #FF9800, #FFC107);
}

.jd-asset-points {
  background: linear-gradient(135deg, #2196F3, #03A9F4);
}

.jd-asset-coupons {
  background: linear-gradient(135deg, #9C27B0, #E91E63);
}

.jd-asset-info {
  flex: 1;
}

.jd-asset-name {
  font-size: 13px;
  color: #666;
  margin-bottom: 4px;
}

.jd-asset-value {
  font-size: 16px;
  font-weight: 500;
  color: #333;
}

/* 支付方式 */
.jd-payment-methods-section {
  margin: 12px 15px;
  background-color: #fff;
  border-radius: 8px;
  padding: 15px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.jd-payment-methods-list {
  margin-top: 10px;
}

.jd-payment-methods-grid {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.jd-payment-method-item {
  display: flex;
  align-items: center;
  padding: 12px;
  border: 1px solid #e8e8e8;
  border-radius: 8px;
  background-color: #fafafa;
  transition: all 0.3s ease;
}

.jd-payment-method-item:hover {
  border-color: #e1251b;
  background-color: #fff5f5;
}

.jd-payment-method-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.jd-payment-method-type {
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

.jd-payment-method-account {
  font-size: 12px;
  color: #666;
}

.jd-payment-method-default {
  display: inline-block;
  padding: 2px 6px;
  background-color: #e1251b;
  color: white;
  font-size: 10px;
  border-radius: 4px;
  font-weight: 500;
}

/* 交易记录 */
.jd-transaction-history {
  padding: 0 15px 15px;
}

.jd-section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 15px;
}

.jd-section-more {
  font-size: 13px;
  color: #e1251b;
  cursor: pointer;
}

.jd-transaction-list {
  background: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.jd-empty-state {
  text-align: center;
  padding: 40px 20px;
  color: #999;
}

.jd-empty-icon {
  width: 60px;
  height: 60px;
  margin: 0 auto 15px;
  background: #f5f5f5;
  border-radius: 50%;
}

.jd-empty-text {
  font-size: 14px;
}

.jd-transaction-items {
  max-height: 300px;
  overflow-y: auto;
}

.jd-transaction-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 15px;
  border-bottom: 1px solid #f5f5f5;
}

.jd-transaction-item:last-child {
  border-bottom: none;
}

.jd-transaction-left {
  display: flex;
  align-items: center;
  flex: 1;
}

.jd-transaction-icon {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  margin-right: 12px;
  background: #f5f5f5;
}

.jd-icon-payment {
  background: linear-gradient(135deg, #FF5722, #FF9800);
}

.jd-icon-recharge {
  background: linear-gradient(135deg, #4CAF50, #8BC34A);
}

.jd-icon-refund {
  background: linear-gradient(135deg, #2196F3, #03A9F4);
}

.jd-icon-withdraw {
  background: linear-gradient(135deg, #9C27B0, #E91E63);
}

.jd-transaction-info {
  flex: 1;
}

.jd-transaction-title {
  font-size: 14px;
  color: #333;
  margin-bottom: 4px;
}

.jd-transaction-time {
  font-size: 12px;
  color: #999;
}

.jd-transaction-right {
  text-align: right;
}

.jd-transaction-amount {
  font-size: 16px;
  font-weight: 500;
  margin-bottom: 4px;
}

.jd-amount-positive {
  color: #4CAF50;
}

.jd-amount-negative {
  color: #FF5722;
}

.jd-transaction-status {
  font-size: 12px;
  padding: 2px 6px;
  border-radius: 10px;
}

.jd-status-success {
  background: #E8F5E8;
  color: #4CAF50;
}

.jd-status-pending {
  background: #FFF3E0;
  color: #FF9800;
}

.jd-status-failed {
  background: #FFEBEE;
  color: #F44336;
}

/* 模态框 */
.jd-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.jd-modal-content {
  background: white;
  border-radius: 12px;
  width: 90%;
  max-width: 400px;
  max-height: 80vh;
  overflow-y: auto;
}

.jd-modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 15px 20px;
  border-bottom: 1px solid #f5f5f5;
}

.jd-modal-title {
  font-size: 16px;
  font-weight: 500;
  color: #333;
}

.jd-modal-close {
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  color: #999;
  cursor: pointer;
  border-radius: 50%;
  transition: background 0.3s ease;
}

.jd-modal-close:hover {
  background: #f5f5f5;
}

.jd-modal-body {
  padding: 20px;
}

.jd-form-group {
  margin-bottom: 20px;
}

.jd-form-label {
  display: block;
  font-size: 14px;
  color: #333;
  margin-bottom: 8px;
}

.jd-input-group {
  display: flex;
  align-items: center;
  border: 1px solid #ddd;
  border-radius: 8px;
  overflow: hidden;
}

.jd-input-prefix {
  padding: 0 12px;
  font-size: 14px;
  color: #666;
  background: #f5f5f5;
  border-right: 1px solid #ddd;
}

.jd-input {
  flex: 1;
  padding: 12px;
  border: none;
  font-size: 14px;
  outline: none;
}

.jd-form-hint {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}

.jd-payment-methods {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 10px;
}

.jd-payment-method {
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 15px 10px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
}

.jd-payment-method.active {
  border-color: #e1251b;
  background: #FFF0F0;
}

.jd-payment-icon {
  width: 32px;
  height: 32px;
  margin: 0 auto 8px;
  border-radius: 4px;
}

.jd-payment-alipay {
  background: linear-gradient(135deg, #1677FF, #40A9FF);
}

.jd-payment-wechat {
  background: linear-gradient(135deg, #07C160, #52C41A);
}

.jd-payment-bank {
  background: linear-gradient(135deg, #722ED1, #9254DE);
}

.jd-payment-name {
  font-size: 12px;
  color: #333;
}

.jd-bank-cards {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.jd-bank-card {
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 15px;
  display: flex;
  align-items: center;
  cursor: pointer;
  transition: all 0.3s ease;
}

.jd-bank-card.active {
  border-color: #e1251b;
  background: #FFF0F0;
}

.jd-bank-icon {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  margin-right: 12px;
}

.jd-bank-abc {
  background: linear-gradient(135deg, #52C41A, #73D13D);
}

.jd-bank-icbc {
  background: linear-gradient(135deg, #FA8C16, #FAAD14);
}

.jd-bank-info {
  flex: 1;
}

.jd-bank-name {
  font-size: 14px;
  color: #333;
  margin-bottom: 4px;
}

.jd-bank-number {
  font-size: 12px;
  color: #666;
}

.jd-modal-footer {
  display: flex;
  gap: 10px;
  padding: 15px 20px;
  border-top: 1px solid #f5f5f5;
}

.jd-btn-cancel {
  background: #f5f5f5;
  color: #666;
}

.jd-btn-cancel:hover {
  background: #e8e8e8;
}

/* 响应式设计 */
@media (max-width: 375px) {
  .jd-asset-grid {
    grid-template-columns: 1fr;
  }
  
  .jd-payment-methods {
    grid-template-columns: 1fr;
  }
}
</style>