<template>
  <div class="order-management">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <h1>订单管理</h1>
        <p>管理系统中的所有订单信息</p>
      </div>
      <div class="header-right">
        <el-button type="primary" @click="exportOrders">
          <el-icon><Download /></el-icon>
          导出订单
        </el-button>
        <el-button @click="refreshData">
          <el-icon><Refresh /></el-icon>
          刷新
        </el-button>
      </div>
    </div>

    <!-- 统计信息 -->
    <div class="stats-section">
      <el-row :gutter="20">
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-icon total">
              <el-icon><Document /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-number">{{ stats.total }}</div>
              <div class="stat-label">订单总数</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-icon pending">
              <el-icon><Clock /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-number">{{ stats.pending }}</div>
              <div class="stat-label">待处理</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-icon shipped">
              <el-icon><Van /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-number">{{ stats.shipped }}</div>
              <div class="stat-label">已发货</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-icon completed">
              <el-icon><CircleCheck /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-number">{{ stats.completed }}</div>
              <div class="stat-label">已完成</div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 搜索筛选区域 -->
    <div class="filter-section">
      <el-card>
        <el-form :model="filterForm" inline>
          <el-form-item label="订单号">
            <el-input v-model="filterForm.order_id" placeholder="请输入订单号" clearable />
          </el-form-item>
          <el-form-item label="用户ID">
            <el-input v-model="filterForm.user_id" placeholder="请输入用户ID" clearable />
          </el-form-item>
          <el-form-item label="商品名称">
            <el-input v-model="filterForm.product_name" placeholder="请输入商品名称" clearable />
          </el-form-item>
          <el-form-item label="订单状态">
            <el-select v-model="filterForm.status" placeholder="请选择状态" clearable>
              <el-option label="待处理" value="待处理" />
              <el-option label="已确认" value="已确认" />
              <el-option label="已发货" value="已发货" />
              <el-option label="已完成" value="已完成" />
              <el-option label="已取消" value="已取消" />
            </el-select>
          </el-form-item>
          <el-form-item label="支付方式">
            <el-select v-model="filterForm.pay_method" placeholder="请选择支付方式" clearable>
              <el-option label="支付宝" value="支付宝" />
              <el-option label="微信支付" value="微信支付" />
              <el-option label="银行卡" value="银行卡" />
              <el-option label="货到付款" value="货到付款" />
            </el-select>
          </el-form-item>
          <el-form-item label="创建时间">
            <el-date-picker
              v-model="filterForm.date_range"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="searchOrders">
              <el-icon><Search /></el-icon>
              搜索
            </el-button>
            <el-button @click="resetFilter">
              <el-icon><Refresh /></el-icon>
              重置
            </el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>

    <!-- 订单列表 -->
    <div class="table-section">
      <el-card>
        <el-table 
          :data="orderList" 
          style="width: 100%" 
          v-loading="loading"
          @selection-change="handleSelectionChange"
        >
          <el-table-column type="selection" width="55" />
          <el-table-column prop="order_id" label="订单号" width="100" />
          <el-table-column prop="user_id" label="用户ID" width="80" />
          <el-table-column label="商品信息" min-width="300">
            <template #default="{ row }">
              <div class="product-info">
                <el-image 
                  :src="row.image || 'http://localhost:3001/uploads/default-product.png'" 
                  :preview-src-list="[row.image || 'http://localhost:3001/uploads/default-product.png']"
                  fit="cover"
                  style="width: 60px; height: 60px; border-radius: 4px; margin-right: 10px;"
                />
                <div class="product-details">
                  <div class="product-name">{{ row.product_name }}</div>
                  <div class="product-price">单价: ¥{{ row.product_price }}</div>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="product_allPrice" label="订单金额" width="120">
            <template #default="{ row }">
              <span class="order-amount">¥{{ row.product_allPrice }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="订单状态" width="100">
            <template #default="{ row }">
              <el-tag :type="getStatusType(row.status)">
                {{ row.status }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="pay_method" label="支付方式" width="100" />
          <el-table-column prop="create_time" label="创建时间" width="160" />
          <el-table-column prop="order_address" label="收货地址" min-width="200" show-overflow-tooltip />
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="{ row }">
              <el-button size="small" @click="viewOrderDetail(row)">详情</el-button>
              <el-button 
                size="small" 
                type="primary" 
                @click="updateOrderStatus(row)"
                v-if="row.status !== '已完成' && row.status !== '已取消'"
              >
                更新状态
              </el-button>
              <el-button size="small" type="danger" @click="deleteOrder(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>

        <!-- 分页 -->
        <div class="pagination-section">
          <el-pagination
            v-model:current-page="pagination.page"
            v-model:page-size="pagination.size"
            :page-sizes="[10, 20, 50, 100]"
            :total="pagination.total"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </el-card>
    </div>

    <!-- 订单详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="订单详情"
      width="800px"
    >
      <div v-if="currentOrder" class="order-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="订单号">{{ currentOrder.order_id }}</el-descriptions-item>
          <el-descriptions-item label="用户ID">{{ currentOrder.user_id }}</el-descriptions-item>
          <el-descriptions-item label="商品名称">{{ currentOrder.product_name }}</el-descriptions-item>
          <el-descriptions-item label="商品单价">¥{{ currentOrder.product_price }}</el-descriptions-item>
          <el-descriptions-item label="订单总价">¥{{ currentOrder.product_allPrice }}</el-descriptions-item>
          <el-descriptions-item label="订单状态">
            <el-tag :type="getStatusType(currentOrder.status)">{{ currentOrder.status }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="支付方式">{{ currentOrder.pay_method }}</el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ currentOrder.create_time }}</el-descriptions-item>
          <el-descriptions-item label="收货地址" :span="2">{{ currentOrder.order_address }}</el-descriptions-item>
          <el-descriptions-item label="用户备注" :span="2">{{ currentOrder.order_userSay || '无' }}</el-descriptions-item>
        </el-descriptions>
        
        <div class="product-image-section" v-if="currentOrder.image">
          <h4>商品图片</h4>
          <el-image 
            :src="currentOrder.image" 
            :preview-src-list="[currentOrder.image]"
            fit="cover"
            style="width: 200px; height: 200px; border-radius: 4px;"
          />
        </div>
      </div>
    </el-dialog>

    <!-- 更新订单状态对话框 -->
    <el-dialog
      v-model="statusDialogVisible"
      title="更新订单状态"
      width="500px"
    >
      <el-form :model="statusForm" label-width="100px">
        <el-form-item label="订单号">
          <span>{{ statusForm.order_id }}</span>
        </el-form-item>
        <el-form-item label="当前状态">
          <el-tag :type="getStatusType(statusForm.current_status)">{{ statusForm.current_status }}</el-tag>
        </el-form-item>
        <el-form-item label="新状态">
          <el-select v-model="statusForm.new_status" placeholder="请选择新状态">
            <el-option label="待处理" value="待处理" />
            <el-option label="已确认" value="已确认" />
            <el-option label="已发货" value="已发货" />
            <el-option label="已完成" value="已完成" />
            <el-option label="已取消" value="已取消" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="statusDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmUpdateStatus">确认</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import apiService from '@/services/apiService';
import { ElMessage, ElMessageBox } from 'element-plus';
import { 
  Document, 
  Clock, 
  Van, 
  CircleCheck, 
  Download, 
  Refresh, 
  Search 
} from '@element-plus/icons-vue';

// 统计数据
const stats = reactive({
  total: 1256,
  pending: 89,
  shipped: 234,
  completed: 856
});

// 筛选表单
const filterForm = reactive({
  order_id: '',
  user_id: '',
  product_name: '',
  status: '',
  pay_method: '',
  date_range: []
});

// 订单列表数据
const orderList = ref([]);

// 分页信息
const pagination = reactive({
  page: 1,
  size: 10,
  total: 1256
});

// 加载状态
const loading = ref(false);

// 对话框状态
const detailDialogVisible = ref(false);
const statusDialogVisible = ref(false);
const currentOrder = ref<any>(null);

// 状态更新表单
const statusForm = reactive({
  order_id: '',
  current_status: '',
  new_status: ''
});

// 获取状态类型
const getStatusType = (status: string) => {
  const statusMap: Record<string, string> = {
    '待处理': 'warning',
    '已确认': 'primary',
    '已发货': 'success',
    '已完成': 'success',
    '已取消': 'danger'
  };
  return statusMap[status] || 'info';
};

// 搜索订单
const searchOrders = () => {
  loading.value = true;
  // 模拟搜索
  setTimeout(() => {
    loading.value = false;
    ElMessage.success('搜索完成');
  }, 1000);
};

// 重置筛选
const resetFilter = () => {
  Object.assign(filterForm, {
    order_id: '',
    user_id: '',
    product_name: '',
    status: '',
    pay_method: '',
    date_range: []
  });
  searchOrders();
};

// 刷新数据
const refreshData = () => {
  loading.value = true;
  setTimeout(() => {
    loading.value = false;
    ElMessage.success('数据已刷新');
  }, 1000);
};

// 导出订单
const exportOrders = () => {
  ElMessage.success('订单导出成功');
};

// 查看订单详情
const viewOrderDetail = (order: any) => {
  currentOrder.value = order;
  detailDialogVisible.value = true;
};

// 更新订单状态
const updateOrderStatus = (order: any) => {
  statusForm.order_id = order.order_id;
  statusForm.current_status = order.status;
  statusForm.new_status = order.status;
  statusDialogVisible.value = true;
};

// 确认更新状态
const confirmUpdateStatus = async () => {
  if (!statusForm.new_status) {
    ElMessage.warning('请选择新状态');
    return;
  }
  
  try {
    loading.value = true;
    const response = await apiService.updateOrderStatus(statusForm.order_id, statusForm.new_status);
    if (response.success) {
      const order = orderList.value.find(item => item.order_id === statusForm.order_id);
      if (order) {
        order.status = statusForm.new_status;
      }
      ElMessage.success('订单状态更新成功');
      statusDialogVisible.value = false;
    } else {
      ElMessage.error(`更新失败: ${response.message || '未知错误'}`);
    }
  } catch (error) {
    ElMessage.error('网络错误，无法更新订单状态');
  } finally {
    loading.value = false;
  }
};

// 删除订单
const deleteOrder = (order: any) => {
  ElMessageBox.confirm(
    `确定要删除订单 ${order.order_id} 吗？此操作不可恢复。`,
    '确认删除',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(() => {
    const index = orderList.value.findIndex(item => item.order_id === order.order_id);
    if (index > -1) {
      orderList.value.splice(index, 1);
      ElMessage.success('订单删除成功');
    }
  }).catch(() => {
    ElMessage.info('已取消删除');
  });
};

// 处理选择变化
const handleSelectionChange = (selection: any[]) => {
  console.log('选中的订单:', selection);
};

// 分页处理
const handleSizeChange = (size: number) => {
  pagination.size = size;
  searchOrders();
};

const handleCurrentChange = (page: number) => {
  pagination.page = page;
  searchOrders();
};

// 页面加载时初始化数据
onMounted(async () => {
  loading.value = true;
  try {
    const response = await apiService.getAllOrders();
    if (response.success && response.data) {
      orderList.value = response.data;
      // 更新统计数据
      stats.total = response.data.length;
      stats.pending = response.data.filter(order => order.status === '待处理').length;
      stats.shipped = response.data.filter(order => order.status === '已发货').length;
      stats.completed = response.data.filter(order => order.status === '已完成').length;
    } else {
      ElMessage.error('获取订单数据失败');
    }
  } catch (error) {
    ElMessage.error('网络错误，无法获取订单数据');
  } finally {
    loading.value = false;
  }
});
</script>

<style scoped>
.order-management {
  padding: 20px;
  background-color: #f5f5f5;
  min-height: 100vh;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.header-left h1 {
  margin: 0 0 5px 0;
  color: #333;
  font-size: 24px;
}

.header-left p {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.stats-section {
  margin-bottom: 20px;
}

.stat-card {
  background: white;
  border-radius: 8px;
  padding: 20px;
  display: flex;
  align-items: center;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  transition: transform 0.2s;
}

.stat-card:hover {
  transform: translateY(-2px);
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 20px;
  font-size: 24px;
  color: white;
}

.stat-icon.total {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.stat-icon.pending {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.stat-icon.shipped {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.stat-icon.completed {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.stat-content {
  flex: 1;
}

.stat-number {
  font-size: 28px;
  font-weight: bold;
  color: #333;
  margin-bottom: 5px;
}

.stat-label {
  color: #666;
  font-size: 14px;
}

.filter-section {
  margin-bottom: 20px;
}

.table-section {
  margin-bottom: 20px;
}

.product-info {
  display: flex;
  align-items: center;
}

.product-details {
  flex: 1;
}

.product-name {
  font-weight: bold;
  margin-bottom: 5px;
  color: #333;
}

.product-price {
  color: #666;
  font-size: 12px;
}

.order-amount {
  color: #e6a23c;
  font-weight: bold;
}

.pagination-section {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.order-detail {
  padding: 20px 0;
}

.product-image-section {
  margin-top: 20px;
  text-align: center;
}

.product-image-section h4 {
  margin-bottom: 10px;
  color: #333;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .header-right {
    margin-top: 10px;
  }
  
  .stat-card {
    margin-bottom: 10px;
  }
}
</style>