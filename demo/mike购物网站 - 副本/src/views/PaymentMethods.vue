<template>
  <div class="payment-methods-container">
    <!-- 页面头部 -->
    <div class="header">
      <div class="header-content">
        <div class="header-top">
          <button class="back-btn" @click="goBack">
            <i class="back-icon">‹</i>
            返回
          </button>
          <h1>支付方式</h1>
        </div>
        <p>管理您的支付方式，安全便捷的支付体验</p>
      </div>
    </div>

    <!-- 主要内容区域 -->
    <div class="main-content">
      <!-- 添加支付方式按钮 -->
      <div class="add-payment-section">
        <button class="add-payment-btn" @click="showAddModal = true">
          <i class="add-icon">+</i>
          添加支付方式
        </button>
      </div>

      <!-- 支付方式列表 -->
      <div class="payment-methods-list">
        <div v-for="method in paymentMethods" :key="method.pay_id" class="payment-method-card">
          <div class="card-header">
            <div class="payment-type">
              <img :src="getPaymentIcon(method.type)" :alt="method.type" class="payment-icon">
              <span class="type-text">{{ method.type }}</span>
            </div>
            <div class="default-badge" v-if="method.is_default === 1">
              默认
            </div>
          </div>
          
          <div class="card-content">
            <div class="account-info">
              <span class="label">支付账号：</span>
              <span class="value">{{ maskAccountInfo(method.account_info) }}</span>
            </div>
            <div class="time-info">
              <span class="label">添加时间：</span>
              <span class="value">{{ method.create_time }}</span>
            </div>
          </div>
          
          <div class="card-actions">
            <button class="action-btn set-default-btn" 
                    @click="setDefaultMethod(method.pay_id)"
                    :disabled="method.is_default === 1">
              {{ method.is_default === 1 ? '已是默认' : '设为默认' }}
            </button>
            <button class="action-btn edit-btn" @click="editMethod(method)">
              编辑
            </button>
            <button class="action-btn delete-btn" @click="deleteMethod(method.pay_id)">
              删除
            </button>
          </div>
        </div>

        <!-- 空状态 -->
        <div v-if="paymentMethods.length === 0" class="empty-state">
          <img src="/images/支付方式.png" alt="暂无支付方式" class="empty-icon">
          <p>暂无支付方式</p>
          <p>添加您的第一个支付方式，享受便捷支付体验</p>
        </div>
      </div>
    </div>

    <!-- 添加/编辑支付方式模态框 -->
    <div v-if="showAddModal || showEditModal" class="modal-overlay" @click="closeModals">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>{{ showAddModal ? '添加支付方式' : '编辑支付方式' }}</h3>
          <button class="close-btn" @click="closeModals">&times;</button>
        </div>
        
        <div class="modal-body">
          <div class="form-group">
            <label>支付类型</label>
            <select v-model="currentMethod.type" class="form-control">
              <option value="支付宝">支付宝</option>
              <option value="微信支付">微信支付</option>
              <option value="银行卡">银行卡</option>
              <option value="信用卡">信用卡</option>
            </select>
          </div>
          
          <div class="form-group">
            <label>支付账号</label>
            <input 
              type="text" 
              v-model="currentMethod.account_info" 
              class="form-control"
              :placeholder="getAccountPlaceholder(currentMethod.type)"
            >
          </div>
          
          <div class="form-group">
            <label>
              <input type="checkbox" v-model="currentMethod.is_default">
              设为默认支付方式
            </label>
          </div>
        </div>
        
        <div class="modal-footer">
          <button class="cancel-btn" @click="closeModals">取消</button>
          <button class="confirm-btn" @click="saveMethod">确认</button>
        </div>
      </div>
    </div>

    <!-- 删除确认模态框 -->
    <div v-if="showDeleteModal" class="modal-overlay" @click="closeModals">
      <div class="modal-content delete-modal" @click.stop>
        <div class="modal-header">
          <h3>确认删除</h3>
          <button class="close-btn" @click="closeModals">&times;</button>
        </div>
        
        <div class="modal-body">
          <p>确定要删除这个支付方式吗？此操作无法撤销。</p>
        </div>
        
        <div class="modal-footer">
          <button class="cancel-btn" @click="closeModals">取消</button>
          <button class="delete-confirm-btn" @click="confirmDelete">确认删除</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import apiService from '@/services/apiService'

// 支付方式数据类型
interface PaymentMethod {
  pay_id?: number
  user_id?: number
  type: string
  account_info: string
  is_default: number
  create_time?: string
  update_time?: string
}

const router = useRouter()

// 响应式数据
const paymentMethods = ref<PaymentMethod[]>([])
const showAddModal = ref(false)
const showEditModal = ref(false)
const showDeleteModal = ref(false)
const deleteMethodId = ref<number | null>(null)

const currentMethod = reactive<PaymentMethod>({
  type: '支付宝',
  account_info: '',
  is_default: 0
})

// 返回上一页
const goBack = () => {
  router.go(-1)
}

// 获取支付图标
const getPaymentIcon = (type: string) => {
  const iconMap: Record<string, string> = {
    '支付宝': '/images/支付宝.png',
    '微信支付': '/images/支付方式.png',
    '银行卡': '/images/银行卡.png',
    '信用卡': '/images/银行卡.png'
  }
  return iconMap[type] || '/images/支付方式.png'
}

// 账号信息掩码
const maskAccountInfo = (accountInfo: string) => {
  if (!accountInfo) return ''
  
  // 根据不同类型进行掩码处理
  if (accountInfo.includes('@')) {
    // 邮箱格式
    const [username, domain] = accountInfo.split('@')
    const maskedUsername = username.length > 3 
      ? username.substring(0, 2) + '***' + username.substring(username.length - 1)
      : username
    return `${maskedUsername}@${domain}`
  } else if (/^\d+$/.test(accountInfo)) {
    // 纯数字格式（银行卡号、手机号）
    if (accountInfo.length >= 11) {
      return accountInfo.substring(0, 3) + '****' + accountInfo.substring(accountInfo.length - 4)
    } else {
      return accountInfo.substring(0, 2) + '****' + accountInfo.substring(accountInfo.length - 2)
    }
  }
  
  // 其他格式
  return accountInfo.substring(0, 2) + '****' + accountInfo.substring(accountInfo.length - 2)
}

// 获取账号输入框占位符
const getAccountPlaceholder = (type: string) => {
  const placeholderMap: Record<string, string> = {
    '支付宝': '请输入支付宝账号或手机号',
    '微信支付': '请输入微信号或手机号',
    '银行卡': '请输入银行卡号',
    '信用卡': '请输入信用卡号'
  }
  return placeholderMap[type] || '请输入支付账号'
}

// 关闭所有模态框
const closeModals = () => {
  showAddModal.value = false
  showEditModal.value = false
  showDeleteModal.value = false
  resetCurrentMethod()
}

// 重置当前编辑的支付方式
const resetCurrentMethod = () => {
  Object.assign(currentMethod, {
    pay_id: undefined,
    type: '支付宝',
    account_info: '',
    is_default: 0
  })
}

// 编辑支付方式
const editMethod = (method: PaymentMethod) => {
  Object.assign(currentMethod, method)
  showEditModal.value = true
}

// 删除支付方式
const deleteMethod = (payId: number) => {
  deleteMethodId.value = payId
  showDeleteModal.value = true
}

// 确认删除
const confirmDelete = async () => {
  try {
    if (deleteMethodId.value) {
      const user = getUserInfo()
      if (!user || !user.id) {
        alert('用户未登录')
        return
      }
      
      console.log('删除支付方式ID:', deleteMethodId.value, '用户ID:', user.id)
      const response = await apiService.deletePayMethod(user.id, deleteMethodId.value)
      console.log('删除支付方式响应:', response)

      if (response.success) {
        alert('删除成功')
        await loadPaymentMethods() // 重新加载支付方式列表
      } else {
        alert(response.message || '删除失败')
      }
    }
  } catch (error) {
    console.error('删除支付方式时发生错误:', error)
    alert('删除失败，请稍后重试')
  }
  closeModals()
}

// 设为默认支付方式
const setDefaultMethod = async (payId: number) => {
  try {
    const user = getUserInfo()
    if (!user || !user.id) {
      alert('用户未登录')
      return
    }

    // 找到要设为默认的支付方式
    const methodToUpdate = paymentMethods.value.find(m => m.pay_id === payId)
    if (!methodToUpdate) {
      alert('支付方式不存在')
      return
    }

    // 更新支付方式为默认
    const updatedMethod = {
      ...methodToUpdate,
      is_default: 1
    }

    console.log('设为默认支付方式:', updatedMethod)
    const response = await apiService.putPayMethod(updatedMethod)
    console.log('设为默认支付方式响应:', response)

    if (response.success) {
      alert('设置成功')
      await loadPaymentMethods() // 重新加载支付方式列表
    } else {
      alert(response.message || '设置失败')
    }
  } catch (error) {
    console.error('设为默认支付方式时发生错误:', error)
    alert('设置失败，请稍后重试')
  }
}

// 保存支付方式
const saveMethod = async () => {
  if (!currentMethod.account_info.trim()) {
    alert('请输入支付账号')
    return
  }

  try {
    const user = getUserInfo()
    if (!user || !user.id) {
      alert('用户未登录')
      return
    }

    currentMethod.user_id = user.id
    
    let response
    if (currentMethod.pay_id) {
      // 更新现有支付方式 - 使用putPayMethod接口
      console.log('更新支付方式:', currentMethod)
      response = await apiService.putPayMethod(currentMethod)
    } else {
      // 添加新支付方式 - 使用insertPayMethod接口
      console.log('添加新支付方式:', currentMethod)
      response = await apiService.insertPayMethod(currentMethod)
    }

    console.log('保存支付方式响应:', response)

    if (response.success) {
      alert(currentMethod.pay_id ? '更新成功' : '添加成功')
      closeModals()
      await loadPaymentMethods() // 重新加载支付方式列表
    } else {
      alert(response.message || '操作失败')
    }
  } catch (error) {
    console.error('保存支付方式时发生错误:', error)
    alert('操作失败，请稍后重试')
  }
}

// 从localStorage获取用户信息
const getUserInfo = () => {
  const userStr = localStorage.getItem('user')
  if (userStr) {
    try {
      return JSON.parse(userStr)
    } catch (error) {
      console.error('解析用户信息失败:', error)
      return null
    }
  }
  return null
}

// 加载支付方式数据 - 调用真实API
const loadPaymentMethods = async () => {
  try {
    const user = getUserInfo()
    if (!user || !user.id) {
      console.error('用户未登录或用户信息不完整')
      return
    }

    console.log('开始查询支付方式，用户ID:', user.id)
    
    // 调用查询支付方式的API
    const response = await apiService.selectPayMethod(user.id)
    console.log('查询支付方式响应:', response)

    if (response.success && response.data) {
      paymentMethods.value = response.data
      console.log('成功获取支付方式列表，数量:', response.data.length)
    } else {
      console.error('获取支付方式失败:', response.message)
      paymentMethods.value = []
    }
  } catch (error) {
    console.error('查询支付方式时发生错误:', error)
    paymentMethods.value = []
  }
}

// 组件挂载时加载数据
onMounted(() => {
  loadPaymentMethods()
})
</script>

<style scoped>
.payment-methods-container {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.header {
  background: linear-gradient(135deg, #e4393c 0%, #c1272d 100%);
  color: white;
  padding: 40px 0;
  text-align: center;
}

.header-content h1 {
  font-size: 32px;
  margin-bottom: 10px;
  font-weight: 600;
}

.header-top {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 10px;
}

.back-btn {
  background: rgba(255, 255, 255, 0.2);
  color: white;
  border: 1px solid rgba(255, 255, 255, 0.3);
  padding: 8px 16px;
  border-radius: 6px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  transition: all 0.3s ease;
}

.back-btn:hover {
  background: rgba(255, 255, 255, 0.3);
  transform: translateY(-1px);
}

.back-icon {
  font-size: 18px;
  font-weight: bold;
}

.header-content p {
  font-size: 16px;
  opacity: 0.9;
}

.main-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 30px 20px;
}

.add-payment-section {
  margin-bottom: 30px;
}

.add-payment-btn {
  background: #e4393c;
  color: white;
  border: none;
  padding: 12px 24px;
  border-radius: 6px;
  font-size: 16px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: all 0.3s ease;
}

.add-payment-btn:hover {
  background: #c1272d;
  transform: translateY(-2px);
}

.add-icon {
  font-size: 20px;
  font-weight: bold;
}

.payment-methods-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 20px;
}

.payment-method-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  border: 1px solid #eee;
}

.payment-method-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
  transform: translateY(-2px);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  padding-bottom: 15px;
  border-bottom: 1px solid #f0f0f0;
}

.payment-type {
  display: flex;
  align-items: center;
  gap: 10px;
}

.payment-icon {
  width: 32px;
  height: 32px;
  object-fit: contain;
}

.type-text {
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.default-badge {
  background: #e4393c;
  color: white;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
}

.card-content {
  margin-bottom: 20px;
}

.account-info, .time-info {
  display: flex;
  margin-bottom: 8px;
  font-size: 14px;
}

.label {
  color: #666;
  min-width: 80px;
}

.value {
  color: #333;
  font-weight: 500;
}

.card-actions {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.action-btn {
  padding: 8px 16px;
  border: 1px solid #ddd;
  border-radius: 6px;
  background: white;
  color: #666;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s ease;
  flex: 1;
  min-width: 80px;
  text-align: center;
}

.action-btn:hover:not(:disabled) {
  border-color: #e4393c;
  color: #e4393c;
}

.action-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.set-default-btn {
  background: #f8f8f8;
  border-color: #e4393c;
  color: #e4393c;
}

.edit-btn {
  background: #f0f8ff;
  border-color: #4a90e2;
  color: #4a90e2;
}

.delete-btn {
  background: #fff0f0;
  border-color: #ff4444;
  color: #ff4444;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  grid-column: 1 / -1;
}

.empty-icon {
  width: 120px;
  height: 120px;
  margin-bottom: 20px;
  opacity: 0.6;
}

.empty-state p {
  color: #666;
  margin-bottom: 10px;
  font-size: 16px;
}

.empty-state p:last-child {
  font-size: 14px;
}

/* 模态框样式 */
.modal-overlay {
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

.modal-content {
  background: white;
  border-radius: 12px;
  width: 90%;
  max-width: 500px;
  max-height: 90vh;
  overflow-y: auto;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #eee;
}

.modal-header h3 {
  margin: 0;
  color: #333;
  font-size: 18px;
}

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #999;
  padding: 0;
  width: 30px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.close-btn:hover {
  color: #666;
}

.modal-body {
  padding: 20px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  color: #333;
  font-weight: 500;
}

.form-control {
  width: 100%;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
  box-sizing: border-box;
}

.form-control:focus {
  outline: none;
  border-color: #e4393c;
  box-shadow: 0 0 0 3px rgba(228, 57, 60, 0.1);
}

.modal-footer {
  display: flex;
  gap: 12px;
  padding: 20px;
  border-top: 1px solid #eee;
  justify-content: flex-end;
}

.cancel-btn, .confirm-btn, .delete-confirm-btn {
  padding: 10px 20px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s ease;
}

.cancel-btn {
  background: #f5f5f5;
  color: #666;
}

.cancel-btn:hover {
  background: #e8e8e8;
}

.confirm-btn {
  background: #e4393c;
  color: white;
}

.confirm-btn:hover {
  background: #c1272d;
}

.delete-confirm-btn {
  background: #ff4444;
  color: white;
}

.delete-confirm-btn:hover {
  background: #cc0000;
}

.delete-modal .modal-body {
  text-align: center;
  padding: 30px 20px;
}

.delete-modal .modal-body p {
  color: #666;
  font-size: 16px;
  margin: 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .header {
    padding: 30px 0;
  }
  
  .header-content h1 {
    font-size: 24px;
  }
  
  .main-content {
    padding: 20px 15px;
  }
  
  .payment-methods-list {
    grid-template-columns: 1fr;
  }
  
  .card-actions {
    flex-direction: column;
  }
  
  .action-btn {
    flex: none;
  }
}
</style>