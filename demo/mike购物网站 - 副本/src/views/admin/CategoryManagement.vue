<template>
  <div class="category-management">
    <div class="page-header">
      <h1>分类管理</h1>
      <div class="header-actions">
        <button @click="showAddModal = true" class="add-btn">
          <span>➕</span>
          添加分类
        </button>
      </div>
    </div>
    
    <!-- 搜索区域 -->
    <div class="search-section">
      <div class="search-box">
        <input
          type="text"
          v-model="searchQuery"
          placeholder="搜索分类名称..."
          @input="filterCategories"
        />
        <span class="search-icon">🔍</span>
      </div>
    </div>
    
    <!-- 分类表格 -->
    <div class="categories-table-container">
      <table class="categories-table">
        <thead>
          <tr>
            <th>分类ID</th>
            <th>分类名称</th>
            <th>二级菜单</th>
            <th>商品数量</th>

            <th>状态</th>
            <th>创建时间</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr 
            v-for="category in filteredCategories" 
            :key="category.id"
          >
            <td>
              <span class="category-id">{{ category.id }}</span>
            </td>
            <td>
              <div class="category-name">
                <span class="name-text">{{ category.name }}</span>
              </div>
            </td>
            <td>
              <div class="submenu-items">
                <div v-for="sub in category.subcategories" :key="sub.id" class="submenu-item">
                  {{ sub.name }}
                </div>
              </div>
            </td>
            <td>
              <span class="product-count">{{ category.productCount }}</span>
            </td>

            <td>
              <span class="status-badge" :class="category.status">
                {{ category.status === 'active' ? '启用' : '禁用' }}
              </span>
            </td>
            <td>
              <div class="create-time">
                <div>{{ category.createDate }}</div>
                <div class="time">{{ category.createTime }}</div>
              </div>
            </td>
            <td>
              <div class="action-buttons">
                <button @click="editCategory(category)" class="edit-btn">编辑</button>
                <button @click="toggleCategoryStatus(category)" class="toggle-btn">
                  {{ category.status === 'active' ? '禁用' : '启用' }}
                </button>
                <button @click="deleteCategory(category)" class="delete-btn">删除</button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    
    <!-- 添加/编辑分类模态框 -->
    <div v-if="showAddModal || showEditModal" class="modal-overlay">
      <div class="modal">
        <div class="modal-header">
          <h2>{{ showAddModal ? '添加分类' : '编辑分类' }}</h2>
          <button @click="closeModal" class="close-btn">✕</button>
        </div>
        
        <form @submit.prevent="saveCategory" class="category-form">
          <div class="form-group">
            <label>分类名称</label>
            <input
              type="text"
              v-model="categoryForm.name"
              required
              placeholder="请输入分类名称"
            />
          </div>
          
          <div class="form-group">
            <label>二级菜单</label>
            <div class="submenu-editor">
              <div v-for="(sub, index) in categoryForm.subcategories" :key="index" class="submenu-item-editor">
                <input
                  type="text"
                  v-model="sub.name"


                  placeholder="二级分类名称"
                  required
                />
                <button type="button" @click="removeSubcategory(index)" class="remove-btn">✕</button>
              </div>
              <button type="button" @click="addSubcategory" class="add-sub-btn">+ 添加二级分类</button>
            </div>
          </div>
          

          
          <div class="form-group">
            <label>状态</label>
            <select v-model="categoryForm.status" required>
              <option value="active">启用</option>
              <option value="inactive">禁用</option>
            </select>
          </div>
          
          <div class="form-actions">
            <button type="button" @click="closeModal" class="cancel-btn">取消</button>
            <button type="submit" class="save-btn">保存</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import apiService from '@/services/apiService';

const router = useRouter();

// 搜索状态
const searchQuery = ref('');

// 模态框状态
const showAddModal = ref(false);
const showEditModal = ref(false);
const editingCategoryId = ref<number | null>(null);

// 拖拽相关状态



// 定义分类接口
interface Subcategory {
  id: number;
  name: string;
}

interface CategoryForm {
  name: string;
  subcategories: Subcategory[];
  status: 'active' | 'inactive';
}

// 分类表单
const categoryForm = reactive<CategoryForm>({
  name: '',
  subcategories: [],
  status: 'active'
});

// 添加二级分类
const addSubcategory = () => {
  const newId = categoryForm.subcategories.length > 0 
  ? Math.max(...categoryForm.subcategories.map(s => s.id)) + 1 
  : 1;
  categoryForm.subcategories.push({ id: newId, name: '' });
};

// 删除二级分类
const removeSubcategory = (index: number) => {
  categoryForm.subcategories.splice(index, 1);
};

// 可用图标
const availableIcons = [
  '📱', '💻', '🎧', '📺', '❄️', '👕', '👟', '👜', '🍎', '🥩', '🥦', '🍞',
  '📚', '🎮', '⚽', '🏠', '🚗', '💄', '⌚', '🔧', '🎁', '🌟'
];

// 分类数据
const categories = ref<any[]>([]);

// 过滤后的分类
const filteredCategories = computed(() => {
  return categories.value.filter(category => {
    const matchesSearch = category.name.toLowerCase().includes(searchQuery.value.toLowerCase());
    return matchesSearch;
  });
});

// 筛选分类
const filterCategories = () => {
  // 计算属性会自动更新
};

// 编辑分类
const editCategory = (category: any) => {
  editingCategoryId.value = category.id;
  categoryForm.name = category.name;
  categoryForm.subcategories = [...category.subcategories];
  categoryForm.status = category.status;
  
  showEditModal.value = true;
};

// 切换分类状态
const toggleCategoryStatus = (category: any) => {
  const index = categories.value.findIndex(c => c.id === category.id);
  if (index !== -1) {
    categories.value[index].status = category.status === 'active' ? 'inactive' : 'active';
  }
};

// 删除分类
const deleteCategory = async (category: any) => {
  if (confirm('确定要删除这个分类吗？删除后该分类下的商品将失去分类。')) {
    try {
      const categoryData = {
        category_id: category.id,
        category_name: category.name,
        categorySon_name: category.subcategories?.[0]?.name || '',
        product_count: category.productCount || 0,
        status: category.status,
        create_time: category.createDate || ''
      };
      
      const response = await apiService.deleteCategory(categoryData);
      if (response.success) {
        // 从本地数据中移除
        const index = categories.value.findIndex(c => c.id === category.id);
        if (index !== -1) {
          categories.value.splice(index, 1);
        }
        alert('分类删除成功');
      } else {
        alert(response.message || '删除失败');
      }
    } catch (error) {
      console.error('删除分类失败:', error);
      alert('删除分类失败，请稍后重试');
    }
  }
};



// 保存分类
const saveCategory = async () => {
  try {
    if (showAddModal.value) {
      // 添加新分类
      const categoryData = {
        category_name: categoryForm.name,
        categorySon_name: categoryForm.subcategories.map((sub: any) => sub.name).filter(name => name.trim()).join('/') || '',
        product_count: 0,
        status: categoryForm.status,
        create_time: new Date().toISOString().split('T')[0]
      };
      
      const response = await apiService.insertCategory(categoryData);
      if (response.success) {
        // 重新获取分类列表并转换格式
        const categoriesResponse = await apiService.selectCategory();
        categories.value = (categoriesResponse.data || []).map((item: any) => ({
          id: item.category_id,
          name: item.category_name,
          subcategories: item.categorySon_name ? 
            item.categorySon_name.split('/').filter(name => name.trim()).map((name: string, index: number) => ({
              id: item.category_id + index + 1,
              name: name.trim()
            })) : [],
          productCount: item.product_count,
          status: item.status,
          createDate: item.create_time ? item.create_time.split(' ')[0] : '',
          createTime: item.create_time ? item.create_time.split(' ')[1] : ''
        }));
        alert('分类添加成功');
      } else {
        alert(response.message || '添加失败');
      }
    } else if (showEditModal.value && editingCategoryId.value) {
      // 编辑现有分类
const originalCategory = categories.value.find(c => c.id === editingCategoryId.value);
const categoryData = {
  category_id: editingCategoryId.value,
  category_name: categoryForm.name,
  categorySon_name: categoryForm.subcategories.map(sub => sub.name).filter(name => name.trim()).join('/') || '',
  product_count: originalCategory?.productCount || 0,
  status: categoryForm.status,
  create_time: originalCategory?.createDate || ''
};
      
      const response = await apiService.updateCategory(categoryData);
      if (response.success) {
        // 更新本地数据
        const index = categories.value.findIndex(c => c.id === editingCategoryId.value);
        if (index !== -1) {
          categories.value[index] = {
            ...categories.value[index],
            name: categoryForm.name,
            subcategories: categoryForm.subcategories,
            status: categoryForm.status
          };
        }
        alert('分类更新成功');
      } else {
        alert(response.message || '更新失败');
      }
    }
    
    closeModal();
  } catch (error) {
    console.error('保存分类失败:', error);
    alert('保存分类失败，请稍后重试');
  }
};





// 关闭模态框
const closeModal = () => {
  showAddModal.value = false;
  showEditModal.value = false;
  editingCategoryId.value = null;
  
  // 重置表单
  categoryForm.name = '';
  categoryForm.subcategories = [];

  categoryForm.status = 'active';
};

// 检查管理员登录状态并获取分类数据
onMounted(async () => {
  const adminToken = localStorage.getItem('adminToken');
  if (!adminToken) {
    router.push('/admin/login');
    return;
  }
  
  try {
    // 调用后端接口获取所有分类信息
    const response = await apiService.selectCategory();
    // 将后端数据格式转换为前端需要的格式
    categories.value = (response.data || []).map((item: any) => ({
      id: item.category_id,
      name: item.category_name,
      subcategories: item.categorySon_name ? 
        item.categorySon_name.split('/').filter(name => name.trim()).map((name: string, index: number) => ({
          id: item.category_id + index + 1,
          name: name.trim()
        })) : [],
      productCount: item.product_count,
      status: item.status,
      createDate: item.create_time ? item.create_time.split(' ')[0] : '',
      createTime: item.create_time ? item.create_time.split(' ')[1] : ''
    }));
    console.log('获取到的分类数据:', categories.value);
  } catch (error) {
    console.error('获取分类数据失败:', error);
    alert('获取分类数据失败，请稍后重试');
  }
});
</script>

<style scoped>
.category-management {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h1 {
  margin: 0;
  color: #2c3e50;
}

.add-btn {
  background: linear-gradient(135deg, #3498db, #2980b9);
  color: white;
  border: none;
  border-radius: 6px;
  padding: 10px 20px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  transition: transform 0.2s;
}

.add-btn:hover {
  transform: translateY(-2px);
}

/* 搜索区域 */
.search-section {
  margin-bottom: 20px;
}

.search-box {
  position: relative;
  max-width: 300px;
}

.search-box input {
  width: 100%;
  padding: 10px 40px 10px 15px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
}

.search-icon {
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  color: #999;
}

/* 分类表格 */
.categories-table-container {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.categories-table {
  width: 100%;
  border-collapse: collapse;
}

.categories-table th,
.categories-table td {
  padding: 12px;
  text-align: left;
  border-bottom: 1px solid #ecf0f1;
}

.categories-table th {
  background-color: #f8f9fa;
  font-weight: 600;
  color: #2c3e50;
}

.category-id {
  font-family: monospace;
  font-weight: 600;
  color: #3498db;
}

.category-name {
  display: flex;
  align-items: center;
  gap: 10px;
}

.name-text {
  font-weight: 500;
  color: #2c3e50;
}

.category-icon {
  font-size: 20px;
}



.product-count {
  font-weight: 600;
  color: #e74c3c;
}



.status-badge {
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
}

.status-badge.active {
  background-color: #d4edda;
  color: #155724;
}

.status-badge.inactive {
  background-color: #f8d7da;
  color: #721c24;
}

.create-time {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.time {
  font-size: 12px;
  color: #7f8c8d;
}

.action-buttons {
  display: flex;
  gap: 5px;
  flex-wrap: wrap;
}

.action-buttons button {
  padding: 4px 8px;
  border: none;
  border-radius: 4px;
  font-size: 12px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.edit-btn {
  background-color: #3498db;
  color: white;
}

.edit-btn:hover {
  background-color: #2980b9;
}

.toggle-btn {
  background-color: #f39c12;
  color: white;
}

.toggle-btn:hover {
  background-color: #e67e22;
}

.delete-btn {
  background-color: #e74c3c;
  color: white;
}

.delete-btn:hover {
  background-color: #c0392b;
}

/* 模态框样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal {
  background: white;
  border-radius: 8px;
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
  border-bottom: 1px solid #ecf0f1;
}

.modal-header h2 {
  margin: 0;
  color: #2c3e50;
}

.close-btn {
  background: none;
  border: none;
  font-size: 20px;
  cursor: pointer;
  color: #999;
}

.close-btn:hover {
  color: #333;
}

.category-form {
  padding: 20px;
}

.form-group {
  margin-bottom: 15px;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
  color: #2c3e50;
  font-weight: 500;
  font-size: 14px;
}

.form-group input,
.form-group select,
.form-group textarea {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
}

.form-group textarea {
  resize: vertical;
}



.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 15px;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 20px;
}

.form-actions button {
  padding: 10px 20px;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  cursor: pointer;
}

.cancel-btn {
  background-color: #95a5a6;
  color: white;
}

.cancel-btn:hover {
  background-color: #7f8c8d;
}

.save-btn {
  background: linear-gradient(135deg, #3498db, #2980b9);
  color: white;
}

.save-btn:hover {
  background: linear-gradient(135deg, #2980b9, #21618c);
}

/* 头部操作区域 */
.header-actions {
  display: flex;
  gap: 10px;
  align-items: center;
}

.save-changes-btn {
  background: linear-gradient(135deg, #f39c12, #e67e22);
  color: white;
  border: none;
  border-radius: 6px;
  padding: 10px 20px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  transition: transform 0.2s;
}

.save-changes-btn:hover {
  transform: translateY(-2px);
}

/* 二级菜单样式 */
.submenu-items {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.submenu-item {
  background: linear-gradient(135deg, #3498db, #2980b9);
  color: white;
  border: none;
  border-radius: 6px;
  padding: 6px 12px;
  font-size: 12px;
  font-weight: 500;
  box-shadow: 0 2px 4px rgba(52, 152, 219, 0.3);
  transition: all 0.3s ease;
}

.submenu-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(52, 152, 219, 0.4);
}

/* 二级菜单编辑器 */
.submenu-editor {
  border: 1px solid #ddd;
  border-radius: 6px;
  padding: 10px;
  background-color: #f8f9fa;
}

.submenu-item-editor {
  display: flex;
  gap: 8px;
  margin-bottom: 8px;
  align-items: center;
}

.submenu-item-editor input {
  flex: 1;
  padding: 6px 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.submenu-item-editor .remove-btn {
  background: #e74c3c;
  color: white;
  border: none;
  border-radius: 4px;
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  font-size: 12px;
}

.submenu-item-editor .remove-btn:hover {
  background: #c0392b;
}

.add-sub-btn {
  background: #3498db;
  color: white;
  border: none;
  border-radius: 4px;
  padding: 6px 12px;
  font-size: 12px;
  cursor: pointer;
  width: 100%;
}

.add-sub-btn:hover {
  background: #2980b9;
}


</style>