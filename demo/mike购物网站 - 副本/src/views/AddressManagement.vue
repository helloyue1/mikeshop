<template>
  <div class="address-management-container">
    <!-- 返回按钮 -->
    <button class="back-button" @click="goBack">← 返回</button>

    <!-- 页面标题 -->
    <h1 class="page-title">地址管理</h1>

    <!-- 添加新地址按钮 -->
    <button class="add-address-btn" @click="showAddAddressForm">+ 添加新地址</button>

    <!-- 地址列表 -->
    <div class="address-list">
      <div v-for="address in addresses" :key="address.addressid" class="address-card">
        <div class="address-header">
          <h3 class="receiver-info">{{ address.receiver_name }} {{ address.receiver_phone }}</h3>
          <div class="address-actions">
            <button class="edit-btn" @click="editAddress(address)">编辑</button>
            <button class="delete-btn" @click="address.addressid && deleteAddress(address.addressid)">删除</button>
          </div>
        </div>
        <div class="address-body">
          <p class="address-text">
            {{ address.province }} {{ address.city }} {{ address.district }} {{ address.detail_address }}
            <span v-if="address.postal_code">({{ address.postal_code }})</span>
          </p>
          <span v-if="address.is_default" class="default-tag">默认地址</span>
        </div>
        <div class="address-footer">
          <button
            class="set-default-btn"
            @click="address.addressid && setDefaultAddress(address.addressid)"
            :disabled="address.is_default === 1"
          >
            {{ address.is_default ? '已设为默认' : '设为默认' }}
          </button>
        </div>
      </div>

      <!-- 空状态提示 -->
      <div v-if="addresses.length === 0" class="empty-state">
        <p>暂无收货地址，请添加新地址</p>
      </div>
    </div>

    <!-- 添加/编辑地址表单弹窗 -->
    <div v-if="showForm" class="form-modal">
      <div class="modal-content">
        <h2 class="modal-title">{{ currentAddress.addressid ? '编辑地址' : '添加新地址' }}</h2>
        <form @submit.prevent="saveAddress">
          <div class="form-group">
            <label class="form-label">收件人姓名 *</label>
            <input
              type="text"
              v-model="currentAddress.receiver_name"
              class="form-input"
              required
              maxlength="50"
            >
          </div>

          <div class="form-group">
            <label class="form-label">手机号码 *</label>
            <input
              type="tel"
              v-model="currentAddress.receiver_phone"
              class="form-input"
              required
              maxlength="11"
              pattern="^1[3-9]\d{9}$"
            >
          </div>

          <div class="form-row">
            <div class="form-group form-group-half">
              <label class="form-label">省份 *</label>
              <input
                type="text"
                v-model="currentAddress.province"
                class="form-input"
                required
                maxlength="50"
              >
            </div>

            <div class="form-group form-group-half">
              <label class="form-label">城市 *</label>
              <input
                type="text"
                v-model="currentAddress.city"
                class="form-input"
                required
                maxlength="50"
              >
            </div>
          </div>

          <div class="form-row">
            <div class="form-group form-group-half">
              <label class="form-label">区/县 *</label>
              <input
                type="text"
                v-model="currentAddress.district"
                class="form-input"
                required
                maxlength="50"
              >
            </div>

            <div class="form-group form-group-half">
              <label class="form-label">邮政编码 *</label>
              <input
                type="text"
                v-model="currentAddress.postal_code"
                class="form-input"
                required
                maxlength="20"
              >
            </div>
          </div>

          <div class="form-group">
            <label class="form-label">详细地址 *</label>
            <textarea
              v-model="currentAddress.detail_address"
              class="form-textarea"
              required
              maxlength="200"
            ></textarea>
          </div>

          <div class="form-checkbox">
            <input
              type="checkbox"
              id="isDefault"
              v-model="isDefaultCheckbox"
              class="form-checkbox-input"
            >
            <label for="isDefault" class="form-checkbox-label">设为默认地址</label>
          </div>

          <div class="form-actions">
            <button type="button" class="cancel-btn" @click="showForm = false">取消</button>
            <button type="submit" class="save-btn">{{ currentAddress.addressid ? '保存修改' : '添加地址' }}</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import apiService from '@/services/apiService';

// 定义地址数据类型，匹配user_address表结构
interface Address {
  addressid?: number;
  user_id: number;
  receiver_name: string;
  receiver_phone: string;
  province: string;
  city: string;
  district: string;
  detail_address: string;
  postal_code?: string;
  is_default: number;
  create_time?: string;
  update_time?: string;
}

// 获取当前登录用户ID的工具函数
const getCurrentUserId = (): number => {
  // 尝试从localStorage获取id
  const idStr = localStorage.getItem('id');
  if (idStr) {
    const id = parseInt(idStr);
    if (!isNaN(id) && id > 0) return id;
  }
  
  // 从userInfo获取id
  const userInfoStr = localStorage.getItem('userInfo');
  if (userInfoStr) {
    try {
      const userInfo = JSON.parse(userInfoStr);
      if (userInfo.id) {
        const id = parseInt(userInfo.id);
        if (!isNaN(id) && id > 0) return id;
      }
    } catch (e) {
      console.error('解析userInfo失败:', e);
    }
  }
  
  // 如果都获取失败，重定向到登录页
  router.push('/login');
  return 0;
};

// 声明地址相关响应式变量 - 移至顶部
const addresses = ref<Address[]>([]);
const currentAddress = ref<Address>({ 
  user_id: 0, 
  receiver_name: '', 
  receiver_phone: '', 
  province: '', 
  city: '', 
  district: '', 
  detail_address: '', 
  is_default: 0 
});
const showForm = ref(false);
const isDefaultCheckbox = ref(false);

const router = useRouter();

// 添加用户登录状态检查
// 修正用户信息获取来源（从userInfo获取）
// 修复用户信息初始化，提供默认对象而非null
const user = ref(JSON.parse(localStorage.getItem('userInfo') || '{"isLoggedIn": false, "id": null}'));

// ... 其他代码保持不变 ...

const saveAddress = async () => {
  if (!user.value?.isLoggedIn) {
    alert('用户未登录，请先登录');
    router.push('/login');
    return
  }

  // 获取当前用户ID
  const userId = getCurrentUserId();
  if (!userId) return;

  // 验证手机号格式
  const phoneRegex = /^1[3-9]\d{9}$/;
  if (!phoneRegex.test(currentAddress.value.receiver_phone)) {
    alert('请输入有效的手机号码');
    return;
  }

  try {
    // 准备发送给后端的数据，严格匹配User_address实体类
    const addressData = {
      user_id: userId,
      receiver_name: currentAddress.value.receiver_name,
      receiver_phone: currentAddress.value.receiver_phone,
      province: currentAddress.value.province,
      city: currentAddress.value.city,
      district: currentAddress.value.district,
      detail_address: currentAddress.value.detail_address,
      postal_code: currentAddress.value.postal_code || '',
      is_default: isDefaultCheckbox.value ? 1 : 0
    };

    if (currentAddress.value.addressid) {
      // 编辑现有地址
      const response = await apiService.updateAddress({ ...addressData, addressid: currentAddress.value.addressid });
      if (response.code === 200) {
        alert('地址更新成功！');
        await loadAddresses();
      } else {
        alert(response.message || '更新地址失败');
      }
    } else {
      // 添加新地址 - 调用insertAddress接口
      const response = await apiService.createAddress(addressData);
      if (response.code === 200) {
        alert('地址添加成功！');
        await loadAddresses();
      } else {
        alert(response.message || '添加地址失败');
      }
    }
    
    showForm.value = false;
  } catch (error) {
    console.error('保存地址失败:', error);
    alert('保存地址失败，请重试');
  }
};

// 获取地址列表 - 从后端接口获取
const loadAddresses = async () => {
  const userId = getCurrentUserId();
  if (!userId) return;

  try {
    const response = await apiService.getUserAddresses(userId);
    if (response && (response as unknown as { code: number }).code === 200) {
      addresses.value = response.data || [];
    } else {
      alert((response as { data: { message?: string } })?.data?.message || '获取地址列表失败');
      addresses.value = [];
    }
  } catch (error) {
    console.error('获取地址列表失败:', error);
    addresses.value = [];
  }
};

// 删除地址
const deleteAddress = async (addressId: number) => {
  const userId = getCurrentUserId();
  if (!userId) return;

  if (!confirm('确定要删除这个地址吗？')) {
    return;
  }

  try {
    const response = await apiService.deleteAddress(userId, addressId);
    if (response.code === 200) {
      alert('地址删除成功！');
      await loadAddresses(); // 重新加载地址列表
    } else {
      alert(response.message || '删除地址失败');
    }
  } catch (error) {
    console.error('删除地址失败:', error);
    alert('删除地址失败，请重试');
  }
};

// 设置默认地址
const setDefaultAddress = async (addressId: number) => {
  const userId = getCurrentUserId();
  if (!userId) return;

  try {
    const response = await apiService.setDefaultAddress(userId, addressId);
    if (response.code === 200) {
      alert('默认地址设置成功！');
      await loadAddresses(); // 重新加载地址列表
    } else {
      alert(response.message || '设置默认地址失败');
    }
  } catch (error) {
    console.error('设置默认地址失败:', error);
    alert('设置默认地址失败，请重试');
  }
};

// 返回上一页
const goBack = () => {
  router.go(-1);
};

// 显示添加地址表单
const showAddAddressForm = () => {
  currentAddress.value = { 
    user_id: user.value.id, 
    receiver_name: '', 
    receiver_phone: '', 
    province: '', 
    city: '', 
    district: '', 
    detail_address: '', 
    is_default: 0 
  };
  isDefaultCheckbox.value = false;
  showForm.value = true;
};

// 编辑地址
const editAddress = (address: Address) => {
  currentAddress.value = { ...address };
  isDefaultCheckbox.value = address.is_default === 1;
  showForm.value = true;
};

// 页面加载时获取地址列表
onMounted(() => {
  loadAddresses();
});
</script>
<style scoped>
/* 基础样式 */
.address-management-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

/* 返回按钮 */
.back-button {
  background: none;
  border: none;
  font-size: 16px;
  color: #333;
  cursor: pointer;
  padding: 10px 0;
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

/* 页面标题 */
.page-title {
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 20px;
  color: #333;
}

/* 添加地址按钮 */
.add-address-btn {
  background-color: #000;
  color: white;
  border: none;
  border-radius: 4px;
  padding: 10px 16px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  margin-bottom: 20px;
  display: inline-flex;
  align-items: center;
  gap: 6px;
  transition: background-color 0.2s;
}

.add-address-btn:hover {
  background-color: #333;
}

/* 地址列表 */
.address-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

/* 地址卡片 */
.address-card {
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  padding: 16px;
  position: relative;
  transition: box-shadow 0.2s;
}

.address-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.address-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.receiver-info {
  font-size: 16px;
  font-weight: 500;
  color: #333;
  margin: 0;
}

.address-actions {
  display: flex;
  gap: 8px;
}

.edit-btn, .delete-btn {
  background: none;
  border: none;
  font-size: 14px;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 4px;
}

.edit-btn {
  color: #0066cc;
}

.edit-btn:hover {
  background-color: #f0f7ff;
}

.delete-btn {
  color: #ff4d4f;
}

.delete-btn:hover {
  background-color: #fff2f0;
}

.address-body {
  margin-bottom: 16px;
}

.address-text {
  font-size: 14px;
  color: #666;
  line-height: 1.5;
  margin: 0;
}

.default-tag {
  display: inline-block;
  background-color: #000;
  color: white;
  font-size: 12px;
  padding: 2px 6px;
  border-radius: 2px;
  margin-left: 8px;
}

.address-footer {
  border-top: 1px dashed #f0f0f0;
  padding-top: 12px;
}

.set-default-btn {
  background-color: #f5f5f5;
  color: #333;
  border: none;
  border-radius: 4px;
  padding: 6px 12px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;
}

.set-default-btn:disabled {
  background-color: #000;
  color: white;
  cursor: not-allowed;
}

.set-default-btn:not(:disabled):hover {
  background-color: #e5e5e5;
}

/* 空状态 */
.empty-state {
  grid-column: 1 / -1;
  text-align: center;
  padding: 60px 0;
  color: #999;
  font-size: 16px;
  border: 1px dashed #e0e0e0;
  border-radius: 8px;
}

/* 表单弹窗 */
.form-modal {
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
  background-color: white;
  border-radius: 8px;
  width: 100%;
  max-width: 600px;
  max-height: 90vh;
  overflow-y: auto;
  padding: 24px;
}

.modal-title {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 20px;
  color: #333;
}

/* 表单样式 */
.form-group {
  margin-bottom: 16px;
}

.form-label {
  display: block;
  margin-bottom: 8px;
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

.form-input, .form-textarea {
  width: 100%;
  padding: 10px;
  border: 1px solid #e0e0e0;
  border-radius: 4px;
  font-size: 14px;
}

.form-input:focus, .form-textarea:focus {
  outline: none;
  border-color: #000;
}

.form-textarea {
  min-height: 80px;
  resize: vertical;
}

.form-row {
  display: flex;
  gap: 16px;
}

.form-group-half {
  flex: 1;
}

.form-checkbox {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.form-checkbox-input {
  margin-right: 8px;
}

.form-checkbox-label {
  font-size: 14px;
  color: #333;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 24px;
}

.cancel-btn, .save-btn {
  padding: 10px 16px;
  border-radius: 4px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
}

.cancel-btn {
  background-color: #f5f5f5;
  color: #333;
  border: none;
}

.cancel-btn:hover {
  background-color: #e5e5e5;
}

.save-btn {
  background-color: #000;
  color: white;
  border: none;
}

.save-btn:hover {
  background-color: #333;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .form-row {
    flex-direction: column;
    gap: 0;
  }
}
</style>