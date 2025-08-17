<template>
  <div class="orders-container">
    <!-- 顶部导航 -->
    <div class="orders-header">
      <div class="header-content">
        <div class="back-btn" @click="goBack">
          <span>←</span> 返回
        </div>
        <h1>我的订单</h1>
        <div class="header-right">
          <div class="search-btn" @click="showSearch = !showSearch">
            🔍
          </div>
        </div>
      </div>
    </div>

    <!-- 搜索框 -->
    <div class="search-section" v-if="showSearch">
      <div class="search-container">
        <input 
          type="text" 
          v-model="searchKeyword" 
          placeholder="搜索订单号、商品名称"
          class="search-input"
          @input="searchOrders"
        />
        <button class="search-cancel" @click="showSearch = false">取消</button>
      </div>
    </div>

    <!-- 订单状态筛选 -->
    <div class="status-filter">
      <div class="filter-tabs">
        <div 
          v-for="tab in statusTabs" 
          :key="tab.id"
          class="filter-tab"
          :class="{ active: activeTab === tab.id }"
          @click="switchTab(tab.id)"
        >
          <span class="tab-name">{{ tab.name }}</span>
          <span class="tab-count" v-if="tab.count > 0">{{ tab.count }}</span>
        </div>
      </div>
    </div>

    <!-- 订单列表 -->
    <div class="orders-list" v-if="filteredOrders.length > 0">
      <div 
        v-for="order in filteredOrders" 
        :key="order.orderNumber"
        class="order-item"
      >
        <!-- 订单头部 -->
        <div class="order-header">
          <div class="order-info">
            <span class="order-number">订单号：{{ order.orderNumber }}</span>
            <span class="order-time">{{ order.orderTime }}</span>
          </div>
          <div class="order-status" :class="order.status">
            {{ getStatusText(order.status) }}
          </div>
        </div>

        <!-- 商品列表 -->
        <div class="order-products">
          <div 
            v-for="product in order.products" 
            :key="product.id"
            class="product-item"
            @click="viewOrderDetail(order.orderNumber)"
          >
            <div class="product-image">
              <img :src="product.image" :alt="product.name" />
            </div>
            <div class="product-info">
              <div class="product-name">{{ product.name }}</div>
              <div class="product-spec" v-if="product.spec">{{ product.spec }}</div>
              <div class="product-price">
                <span class="price">¥{{ product.price.toFixed(2) }}</span>
                <span class="quantity">x{{ product.quantity }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 订单底部 -->
        <div class="order-footer">
          <div class="order-summary">
            共{{ getTotalQuantity(order.products) }}件商品 
            合计：<span class="total-amount">¥{{ order.totalAmount.toFixed(2) }}</span>
          </div>
          <div class="order-actions">
            <button 
              v-if="order.status === 'pending_payment'"
              class="action-btn primary"
              @click="goToPayment(order)"
            >
              立即支付
            </button>
            <button 
              v-if="order.status === 'pending_payment'"
              class="action-btn secondary"
              @click="cancelOrder(order.orderNumber)"
            >
              取消订单
            </button>
            <button 
              v-if="order.status === 'shipped'"
              class="action-btn primary"
              @click="confirmReceive(order.orderNumber)"
            >
              确认收货
            </button>
            <button 
              v-if="order.status === 'completed'"
              class="action-btn secondary"
              @click="deleteOrder(order.orderNumber)"
            >
              删除订单
            </button>
            <button 
              v-if="order.status === 'completed'"
              class="action-btn primary"
              @click="buyAgain(order.products)"
            >
              再次购买
            </button>
            <button 
              class="action-btn secondary"
              @click="viewOrderDetail(order.orderNumber)"
            >
              查看详情
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 空状态 -->
    <div class="empty-state" v-else>
      <div class="empty-icon">📦</div>
      <div class="empty-text">暂无{{ getTabName(activeTab) }}订单</div>
      <button class="go-shopping-btn" @click="goToHome">去逛逛</button>
    </div>

    <!-- 订单详情模态框 -->
    <div class="order-detail-modal" v-if="showOrderDetail" @click.self="closeOrderDetail">
      <div class="modal-content">
        <div class="modal-header">
          <h3>订单详情</h3>
          <div class="close-btn" @click="closeOrderDetail">×</div>
        </div>
        <div class="modal-body" v-if="selectedOrder">
          <div class="detail-section">
            <h4>订单信息</h4>
            <div class="detail-item">
              <span class="label">订单号：</span>
              <span class="value">{{ selectedOrder.orderNumber }}</span>
            </div>
            <div class="detail-item">
              <span class="label">下单时间：</span>
              <span class="value">{{ selectedOrder.orderTime }}</span>
            </div>
            <div class="detail-item">
              <span class="label">订单状态：</span>
              <span class="value" :class="selectedOrder.status">{{ getStatusText(selectedOrder.status) }}</span>
            </div>
            <div class="detail-item">
              <span class="label">支付方式：</span>
              <span class="value">{{ selectedOrder.payMethod || '未支付' }}</span>
            </div>
          </div>
          
          <div class="detail-section">
            <h4>商品信息</h4>
            <div class="detail-products">
              <div 
                v-for="product in selectedOrder.products" 
                :key="product.id"
                class="detail-product"
              >
                <div class="detail-product-image">
                  <img :src="product.image" :alt="product.name" />
                </div>
                <div class="detail-product-info">
                  <div class="detail-product-name">{{ product.name }}</div>
                  <div class="detail-product-spec" v-if="product.spec">{{ product.spec }}</div>
                  <div class="detail-product-price">
                    <span class="price">¥{{ product.price.toFixed(2) }}</span>
                    <span class="quantity">x{{ product.quantity }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
          
          <div class="detail-section" v-if="selectedOrder.orderAddress">
            <h4>收货地址</h4>
            <div class="detail-item">
              <span class="value">{{ selectedOrder.orderAddress }}</span>
            </div>
          </div>
          
          <div class="detail-section" v-if="selectedOrder.orderUserSay">
            <h4>订单备注</h4>
            <div class="detail-item">
              <span class="value">{{ selectedOrder.orderUserSay }}</span>
            </div>
          </div>
          
          <div class="detail-section">
            <h4>费用信息</h4>
            <div class="detail-item">
              <span class="label">商品总额：</span>
              <span class="value">¥{{ selectedOrder.totalAmount.toFixed(2) }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 底部导航 -->
    <div class="bottom-nav">
      <div class="nav-item" @click="goToHome">
        <div class="nav-icon">🏠</div>
        <div class="nav-text">首页</div>
      </div>
      <div class="nav-item active">
        <div class="nav-icon">📋</div>
        <div class="nav-text">订单</div>
      </div>
      <div class="nav-item" @click="goToProfile">
        <div class="nav-icon">👤</div>
        <div class="nav-text">我的</div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import apiService from '../services/apiService';

const router = useRouter();

// 搜索相关
const showSearch = ref(false);
const searchKeyword = ref('');

// 状态筛选
const activeTab = ref('all');
const statusTabs = ref([
  { id: 'all', name: '全部', count: 0 },
  { id: 'pending_payment', name: '待付款', count: 0 },
  { id: 'pending_shipment', name: '待发货', count: 0 },
  { id: 'shipped', name: '待收货', count: 0 },
  { id: 'completed', name: '已完成', count: 0 }
]);

// 订单数据 - 根据后端参数结构定义
const orders = ref<any[]>([]);
const loading = ref(false);
const error = ref('');

// 模态框相关状态
const showOrderDetail = ref(false);
const selectedOrder = ref<any>(null);

// 获取用户ID
const getUserId = () => {
  const userInfo = localStorage.getItem('userInfo');
  if (userInfo) {
    const parsedUserInfo = JSON.parse(userInfo);
    return parsedUserInfo.id;
  }
  return null;
};

// 获取用户订单
const fetchUserOrders = async () => {
  const userId = getUserId();
  if (!userId) {
    error.value = '请先登录';
    return;
  }

  loading.value = true;
  error.value = '';
  
  try {
    const response = await apiService.getUserOrders(userId);
    if (response.data && Array.isArray(response.data)) {
      // 转换后端数据格式为前端需要的格式
      orders.value = response.data.map((order: any) => ({
        orderNumber: order.order_id.toString(),
        orderTime: order.create_time,
        status: mapStatus(order.status),
        totalAmount: parseFloat(order.product_allPrice),
        products: [{
          id: order.order_id,
          name: order.product_name,
          image: order.image || '/images/default-product.png',
          price: parseFloat(order.product_price),
          quantity: 1,
          spec: ''
        }],
        orderAddress: order.order_address,
        orderUserSay: order.order_userSay,
        payMethod: order.pay_method,
        userId: order.user_id
      }));
      updateTabCounts();
    } else {
      orders.value = [];
    }
  } catch (err) {
    console.error('获取订单失败:', err);
    error.value = '获取订单失败，请稍后重试';
    orders.value = [];
  } finally {
    loading.value = false;
  }
};

// 映射状态
const mapStatus = (backendStatus: string) => {
  const statusMap: Record<string, string> = {
    '待处理': 'pending_payment',
    '已确认': 'pending_shipment',
    '已发货': 'shipped',
    '已完成': 'completed',
    '已取消': 'cancelled'
  };
  return statusMap[backendStatus] || 'pending_payment';
};

// 反向映射状态
const mapStatusToBackend = (frontendStatus: string) => {
  const statusMap: Record<string, string> = {
    'pending_payment': '待处理',
    'pending_shipment': '已确认',
    'shipped': '已发货',
    'completed': '已完成',
    'cancelled': '已取消'
  };
  return statusMap[frontendStatus] || '待处理';
};

// 筛选后的订单
const filteredOrders = computed(() => {
  let result = orders.value;
  
  // 按状态筛选
  if (activeTab.value !== 'all') {
    result = result.filter(order => order.status === activeTab.value);
  }
  
  // 按关键词搜索
  if (searchKeyword.value.trim()) {
    const keyword = searchKeyword.value.toLowerCase();
    result = result.filter(order => 
      order.orderNumber.toLowerCase().includes(keyword) ||
      order.products.some((product: any) =>
        product.name.toLowerCase().includes(keyword)
      )
    );
  }
  
  return result;
});

// 更新标签计数
const updateTabCounts = () => {
  statusTabs.value.forEach(tab => {
    if (tab.id === 'all') {
      tab.count = orders.value.length;
    } else {
      tab.count = orders.value.filter(order => order.status === tab.id).length;
    }
  });
};

// 切换标签
const switchTab = (tabId: string) => {
  activeTab.value = tabId;
};

// 搜索订单
const searchOrders = () => {
  // 搜索逻辑已经在computed中实现
};

// 获取状态文本
const getStatusText = (status: string) => {
  const statusMap: Record<string, string> = {
    'pending_payment': '待付款',
    'pending_shipment': '待发货',
    'shipped': '待收货',
    'completed': '已完成',
    'cancelled': '已取消'
  };
  return statusMap[status] || '未知状态';
};

// 获取标签名称
const getTabName = (tabId: string) => {
  const tab = statusTabs.value.find(t => t.id === tabId);
  return tab ? tab.name : '';
};

// 计算商品总数量
const getTotalQuantity = (products: any[]) => {
  return products.reduce((total, product) => total + product.quantity, 0);
};

// 查看订单详情
const viewOrderDetail = (orderNumber: string) => {
  const order = orders.value.find(o => o.orderNumber === orderNumber);
  if (order) {
    selectedOrder.value = order;
    showOrderDetail.value = true;
  }
};

// 关闭订单详情
const closeOrderDetail = () => {
  showOrderDetail.value = false;
  selectedOrder.value = null;
};

// 去支付
const goToPayment = async (order: any) => {
  try {
    // 跳转到支付页面，不提前更新订单状态
    // 订单状态将在支付成功后更新为'已确认'
    router.push({
      name: 'payment',
      query: {
        orderNumber: order.orderNumber,
        amount: order.totalAmount
      }
    });
  } catch (err) {
    console.error('跳转支付页面失败:', err);
    alert('跳转支付页面失败，请稍后重试');
  }
};

// 取消订单
const cancelOrder = async (orderNumber: string) => {
  if (!confirm('确定要取消该订单吗？')) {
    return;
  }
  
  try {
    const orderId = parseInt(orderNumber);
    if (isNaN(orderId)) {
      throw new Error('订单号格式无效');
    }
    
    // 分别传递status和order_id两个参数
    await apiService.updateOrder('已取消', { order_id: orderId });
    
    // 刷新订单列表
    await fetchUserOrders();
  } catch (err) {
    console.error('取消订单失败:', err);
    alert('取消订单失败，请稍后重试');
  }
};

// 确认收货
const confirmReceive = async (orderNumber: string) => {
  if (!confirm('确认已收到商品吗？')) {
    return;
  }
  
  try {
    const order = orders.value.find(o => o.orderNumber === orderNumber);
    if (order) {
      // 更新订单状态为已完成
      const orderId = parseInt(order.orderNumber);
      if (isNaN(orderId)) {
        throw new Error('订单号格式无效');
      }
      
      const backendOrder = {
        user_id: order.userId,
        order_id: orderId,
        image: order.products[0].image,
        product_name: order.products[0].name,
        product_allPrice: order.totalAmount.toString(),
        product_price: order.products[0].price.toString(),
        status: '已完成',
        create_time: order.orderTime,
        order_address: order.orderAddress,
        order_userSay: order.orderUserSay,
        pay_method: order.payMethod
      };
      
      await apiService.updateOrder('已完成', backendOrder);
      
      // 刷新订单列表
      await fetchUserOrders();
    }
  } catch (err) {
    console.error('确认收货失败:', err);
    alert('确认收货失败，请稍后重试');
  }
};

// 删除订单
const deleteOrder = async (orderNumber: string) => {
  if (!confirm('确定要删除该订单吗？')) {
    return;
  }
  
  try {
    const order = orders.value.find(o => o.orderNumber === orderNumber);
    if (order) {
      // 更新订单状态为已取消
      const orderId = parseInt(order.orderNumber);
      if (isNaN(orderId)) {
        throw new Error('订单号格式无效');
      }
      
      const backendOrder = {
        user_id: order.userId,
        order_id: orderId,
        image: order.products[0].image,
        product_name: order.products[0].name,
        product_allPrice: order.totalAmount.toString(),
        product_price: order.products[0].price.toString(),
        status: '已取消',
        create_time: order.orderTime,
        order_address: order.orderAddress,
        order_userSay: order.orderUserSay,
        pay_method: order.payMethod
      };
      
      await apiService.updateOrder('已取消', backendOrder);
      
      // 刷新订单列表
      await fetchUserOrders();
    }
  } catch (err) {
    console.error('删除订单失败:', err);
    alert('删除订单失败，请稍后重试');
  }
};

// 再次购买
const buyAgain = (products: any[]) => {
  // 将商品添加到购物车
  alert('商品已添加到购物车');
  router.push('/cart');
};

// 返回
const goBack = () => {
  router.back();
};

// 去首页
const goToHome = () => {
  router.push('/');
};

// 去个人中心
const goToProfile = () => {
  router.push('/profile');
};

// 页面加载时获取订单数据
onMounted(() => {
  fetchUserOrders();
});
</script>

<style scoped>
.orders-container {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 60px;
}

/* 顶部导航 */
.orders-header {
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

.search-btn {
  cursor: pointer;
  font-size: 18px;
  color: #666;
}

/* 搜索框 */
.search-section {
  background-color: #fff;
  padding: 12px 16px;
  border-bottom: 1px solid #f0f0f0;
}

.search-container {
  display: flex;
  align-items: center;
  gap: 12px;
}

.search-input {
  flex: 1;
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.search-cancel {
  padding: 8px 16px;
  background-color: #fff;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
}

/* 状态筛选 */
.status-filter {
  background-color: #fff;
  margin-bottom: 12px;
}

.filter-tabs {
  display: flex;
  border-bottom: 1px solid #f0f0f0;
}

.filter-tab {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 12px 8px;
  cursor: pointer;
  position: relative;
}

.filter-tab.active {
  color: #e4393c;
}

.filter-tab.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 2px;
  background-color: #e4393c;
}

.tab-name {
  font-size: 14px;
  margin-bottom: 2px;
}

.tab-count {
  font-size: 12px;
  color: #999;
}

.filter-tab.active .tab-count {
  color: #e4393c;
}

/* 订单列表 */
.orders-list {
  padding: 0 12px;
}

.order-item {
  background-color: #fff;
  border-radius: 8px;
  margin-bottom: 12px;
  overflow: hidden;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

/* 订单头部 */
.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  border-bottom: 1px solid #f0f0f0;
}

.order-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.order-number {
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

.order-time {
  font-size: 12px;
  color: #999;
}

.order-status {
  font-size: 14px;
  font-weight: 500;
}

.order-status.pending_payment {
  color: #e4393c;
}

.order-status.pending_shipment {
  color: #ff9900;
}

.order-status.shipped {
  color: #2196f3;
}

.order-status.completed {
  color: #52c41a;
}

/* 商品列表 */
.order-products {
  padding: 12px 16px;
}

.product-item {
  display: flex;
  align-items: center;
  padding: 8px 0;
  cursor: pointer;
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

/* 订单底部 */
.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  border-top: 1px solid #f0f0f0;
  background-color: #fafafa;
}

.order-summary {
  font-size: 14px;
  color: #666;
}

.total-amount {
  color: #e4393c;
  font-weight: 500;
  font-size: 16px;
}

.order-actions {
  display: flex;
  gap: 8px;
}

.action-btn {
  padding: 6px 12px;
  border-radius: 4px;
  font-size: 12px;
  cursor: pointer;
  border: 1px solid;
  background-color: #fff;
}

.action-btn.primary {
  color: #fff;
  background-color: #e4393c;
  border-color: #e4393c;
}

.action-btn.primary:hover {
  background-color: #c1272d;
}

.action-btn.secondary {
  color: #666;
  border-color: #ddd;
}

.action-btn.secondary:hover {
  background-color: #f5f5f5;
}

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  text-align: center;
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 16px;
  color: #ddd;
}

.empty-text {
  font-size: 16px;
  color: #999;
  margin-bottom: 24px;
}

.go-shopping-btn {
  padding: 10px 24px;
  background-color: #e4393c;
  color: #fff;
  border: none;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
}

.go-shopping-btn:hover {
  background-color: #c1272d;
}

/* 底部导航 */
.bottom-nav {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background-color: #fff;
  display: flex;
  box-shadow: 0 -2px 8px rgba(0,0,0,0.1);
  z-index: 100;
}

.nav-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 8px 0;
  cursor: pointer;
}

.nav-item.active {
  color: #e4393c;
}

.nav-icon {
  font-size: 20px;
  margin-bottom: 2px;
}

.nav-text {
  font-size: 12px;
}

/* 订单详情模态框 */
.order-detail-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background-color: #fff;
  border-radius: 8px;
  width: 90%;
  max-width: 500px;
  max-height: 80vh;
  overflow-y: auto;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid #f0f0f0;
  background-color: #fafafa;
  border-radius: 8px 8px 0 0;
}

.modal-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 500;
  color: #333;
}

.close-btn {
  font-size: 24px;
  color: #999;
  cursor: pointer;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: background-color 0.2s;
}

.close-btn:hover {
  background-color: #f0f0f0;
  color: #666;
}

.modal-body {
  padding: 20px;
}

.detail-section {
  margin-bottom: 20px;
}

.detail-section:last-child {
  margin-bottom: 0;
}

.detail-section h4 {
  margin: 0 0 12px 0;
  font-size: 16px;
  font-weight: 500;
  color: #333;
  padding-bottom: 8px;
  border-bottom: 1px solid #f0f0f0;
}

.detail-item {
  display: flex;
  align-items: flex-start;
  margin-bottom: 8px;
  font-size: 14px;
  line-height: 1.5;
}

.detail-item:last-child {
  margin-bottom: 0;
}

.detail-item .label {
  color: #666;
  min-width: 80px;
  margin-right: 8px;
}

.detail-item .value {
  color: #333;
  flex: 1;
}

.detail-products {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.detail-product {
  display: flex;
  align-items: center;
  padding: 12px;
  background-color: #f9f9f9;
  border-radius: 6px;
}

.detail-product-image {
  width: 60px;
  height: 60px;
  margin-right: 12px;
  flex-shrink: 0;
}

.detail-product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 4px;
}

.detail-product-info {
  flex: 1;
}

.detail-product-name {
  font-size: 14px;
  color: #333;
  margin-bottom: 4px;
  line-height: 1.3;
}

.detail-product-spec {
  font-size: 12px;
  color: #999;
  margin-bottom: 4px;
}

.detail-product-price {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.detail-product-price .price {
  color: #e4393c;
  font-weight: 500;
}

.detail-product-price .quantity {
  color: #666;
  font-size: 12px;
}

/* 响应式设计 */
@media (max-width: 480px) {
  .modal-content {
    width: 95%;
    max-height: 90vh;
  }
  
  .modal-header {
    padding: 12px 16px;
  }
  
  .modal-body {
    padding: 16px;
  }
  
  .detail-item .label {
    min-width: 70px;
  }
}
</style>