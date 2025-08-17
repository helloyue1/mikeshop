<template>
  <div class="product-management">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <h1>商品管理</h1>
        <p>管理系统中的所有商品信息</p>
      </div>
      <div class="header-right">
        <el-button type="primary" @click="showAddDialog">
          <el-icon><Plus /></el-icon>
          添加商品
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
              <el-icon><Goods /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-number">{{ stats.total }}</div>
              <div class="stat-label">商品总数</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-icon online">
              <el-icon><Check /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-number">{{ stats.online }}</div>
              <div class="stat-label">上架商品</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-icon offline">
              <el-icon><Close /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-number">{{ stats.offline }}</div>
              <div class="stat-label">下架商品</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-icon low-stock">
              <el-icon><Warning /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-number">{{ stats.lowStock }}</div>
              <div class="stat-label">库存预警</div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 搜索筛选区域 -->
    <div class="filter-section">
      <el-card>
        <el-form :model="filterForm" inline>
          <el-form-item label="商品名称">
            <el-input v-model="filterForm.product_name" placeholder="请输入商品名称" clearable />
          </el-form-item>
          <el-form-item label="商品分类">
            <el-select v-model="filterForm.category" placeholder="请选择分类" clearable>
              <el-option 
                v-for="category in categoryList" 
                :key="category" 
                :label="category" 
                :value="category" 
              />
            </el-select>
          </el-form-item>
          <el-form-item label="商品状态">
            <el-select v-model="filterForm.status" placeholder="请选择状态" clearable>
              <el-option label="上架" value="上架" />
              <el-option label="下架" value="下架" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="searchProducts">
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

    <!-- 商品列表 -->
    <div class="table-section">
      <el-card>
        <el-table 
          :data="productList" 
          style="width: 100%" 
          v-loading="loading"
          @selection-change="handleSelectionChange"
        >
          <el-table-column type="selection" width="55" />
          <el-table-column prop="product_id" label="商品ID" width="80" />
          <el-table-column label="商品图片" width="100">
            <template #default="{ row }">
              <el-image 
                :src="row.image || 'http://localhost:3001/uploads/default-product.png'" 
                :preview-src-list="[row.image || 'http://localhost:3001/uploads/default-product.png']"
                fit="cover"
                style="width: 60px; height: 60px; border-radius: 4px;"
              />
            </template>
          </el-table-column>
          <el-table-column prop="product_name" label="商品名称" min-width="200" show-overflow-tooltip />
          <el-table-column label="商品规格" width="200" show-overflow-tooltip>
            <template #default="{ row }">
              <span class="spec-display">{{ formatSpecifications(row.product_value) }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="category" label="商品分类" width="120" />
          <el-table-column prop="price" label="价格" width="100">
            <template #default="{ row }">
              <span class="price">¥{{ row.price }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="stock" label="库存" width="80">
            <template #default="{ row }">
              <span :class="{ 'low-stock': row.stock < 10 }">{{ row.stock }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="sales" label="销量" width="80" />
          <el-table-column prop="status" label="状态" width="80">
            <template #default="{ row }">
              <el-tag :type="row.status === '上架' ? 'success' : 'danger'">
                {{ row.status }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="create_time" label="创建时间" width="160" />
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="{ row }">
              <el-button size="small" @click="editProduct(row)">编辑</el-button>
              <el-button 
                size="small" 
                :type="row.status === '上架' ? 'danger' : 'success'"
                @click="toggleStatus(row)"
              >
                {{ row.status === '上架' ? '下架' : '上架' }}
              </el-button>
              <el-button size="small" type="danger" @click="deleteProduct(row)">删除</el-button>
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

    <!-- 添加/编辑商品对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑商品' : '添加商品'"
      width="800px"
      @close="resetForm"
      @open="() => console.log('对话框打开')"
    >
      <el-form 
        ref="productFormRef" 
        :model="productForm" 
        :rules="formRules" 
        label-width="120px"
      >
        <el-form-item label="商品名称" prop="product_name">
          <el-input v-model="productForm.product_name" placeholder="请输入商品名称" />
        </el-form-item>
        <el-form-item label="商品分类" prop="category">
          <el-select v-model="productForm.category" placeholder="请选择分类">
            <el-option 
              v-for="category in categoryList" 
              :key="category" 
              :label="category" 
              :value="category" 
            />
          </el-select>
        </el-form-item>
        <el-form-item label="商品价格" prop="price">
          <el-input-number 
            v-model="productForm.price" 
            :min="0" 
            :precision="2" 
            :step="0.01"
          />
        </el-form-item>
        <el-form-item label="商品库存" prop="stock">
          <el-input-number v-model="productForm.stock" :min="0" />
        </el-form-item>
        <el-form-item label="商品图片" prop="image">
          <el-upload
            class="avatar-uploader"
            action="http://localhost:3001/api/upload"
            :show-file-list="false"
            :on-success="handleImageSuccess"
            :on-error="handleImageError"
            :before-upload="beforeImageUpload"
            :data="{ productId: productForm.product_id || 'new' }"
            :with-credentials="true"
            name="file"
          >
            <img v-if="productForm.image" :src="productForm.image" class="avatar" @error="handleImgError" />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>
        <el-form-item label="商品状态" prop="status">
          <el-radio-group v-model="productForm.status">
            <el-radio label="上架">上架</el-radio>
            <el-radio label="下架">下架</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="商品描述" prop="description">
          <el-input 
            v-model="productForm.description" 
            type="textarea" 
            :rows="4" 
            placeholder="请输入商品描述"
          />
        </el-form-item>
        <el-form-item label="商品规格" prop="specifications">
          <div v-for="(spec, index) in productForm.specifications" :key="index" class="spec-item">
            <el-input 
              v-model="spec.attribute"
              placeholder="属性名称（如：颜色）"
              class="spec-attribute"
              style="width: 120px; margin-right: 10px;"
            />
            <div class="values-container">
              <div v-for="(value, valueIndex) in spec.values" :key="valueIndex" class="value-item">
                <el-input 
                  v-model="spec.values[valueIndex]"
                  placeholder="属性值"
                  class="spec-value"
                  style="width: 100px; margin-right: 5px;"
                />
                <el-button 
                  type="danger" 
                  icon="Delete"
                  size="small"
                  @click="removeSpecValue(index, valueIndex)"
                  v-if="spec.values.length > 1"
                />
              </div>
              <el-button 
                type="primary"
                icon="Plus"
                size="small"
                @click="addSpecValue(index)"
                style="margin-top: 5px;"
              >
                添加
              </el-button>
            </div>
            <el-button 
              type="danger" 
              icon="Delete"
              size="small"
              @click="removeSpec(index)"
              v-if="productForm.specifications.length > 1"
            />
          </div>
          <el-button 
            type="primary"
            icon="Plus"
            size="small"
            @click="addSpec"
            style="margin-top: 10px;"
          >
            添加规格
          </el-button>
        </el-form-item>
        
        <!-- 规格组合变体 -->
        <el-form-item label="规格组合" v-if="productForm.variants.length > 0">
          <div class="variants-container">
            <div v-for="(variant, index) in productForm.variants" :key="variant.id" class="variant-item">
              <div class="variant-info">
                <span class="variant-combination">
                  {{ Object.entries(variant.combination).map(([attr, val]) => `${attr}:${val}`).join(', ') }}
                </span>
              </div>
              <div class="variant-inputs">
                <el-input-number 
                  v-model="variant.price"
                  :min="0"
                  :precision="2"
                  :step="0.01"
                  placeholder="价格"
                  style="width: 120px; margin-right: 15px;"
                />
                <el-input-number 
                  v-model="variant.stock"
                  :min="0"
                  placeholder="库存"
                  style="width: 100px;"
                />
              </div>
            </div>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Plus, Refresh, Search, Goods, Check, Close, Warning
} from '@element-plus/icons-vue'
import axios from 'axios'
import apiService from '@/services/apiService'

// 商品规格组合接口
interface ProductVariant {
  id: string
  combination: Record<string, string>
  price: number
  stock: number
}

// 商品接口定义
interface Product {
  product_value: any
  product_id: number
  product_name: string
  category: string
  price: number
  stock: number
  sales: number
  image: string
  status: string
  description: string
  specifications: Array<{ attribute: string; values: string[] }>
  variants: ProductVariant[]
  create_time: string
  update_time: string
}

// 响应式数据
const loading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const selectedProducts = ref<Product[]>([])
const productFormRef = ref<any>()

// 统计数据
const stats = reactive({
  total: 0,
  online: 0,
  offline: 0,
  lowStock: 0
})

// 筛选表单
const filterForm = reactive({
  product_name: '',
  category: '',
  status: ''
})

// 商品表单
const productForm = reactive<Product>({
  product_id: 0,
  product_name: '',
  category: '',
  price: 0,
  stock: 0,
  sales: 0,
  image: '',
  status: '下架',
  description: '',
  specifications: [{ attribute: '', values: [''] }],
  variants: [],
  create_time: '',
  update_time: ''
})

// 分页数据
const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

// 商品列表数据
const productList = ref<Product[]>([])

// 分类数据
const categoryList = ref<string[]>([])

// 加载分类数据
const loadCategories = async () => {
  try {
    const response = await axios.get('http://localhost:8080/api/selectCategory')
    if (response.data && response.data.data) {
      // 提取category_name字段
      categoryList.value = response.data.data.map((item: any) => item.category_name)
    } else {
      console.warn('分类数据格式不正确:', response.data)
      categoryList.value = []
    }
  } catch (error) {
    console.error('获取分类数据失败:', error)
    ElMessage.error('获取分类数据失败')
    categoryList.value = []
  }
}

// 规格验证函数
const validateSpecifications = (rule: any, value: any, callback: any) => {
  const allFilled = productForm.specifications.every(
    spec => spec.attribute?.trim() && spec.values?.some(v => v?.trim())
  )
  allFilled ? callback() : callback(new Error('请填写所有规格属性和值'))
}

// 表单验证规则
const formRules = {
  product_name: [
    { required: true, message: '请输入商品名称', trigger: 'blur' }
  ],
  category: [
    { required: true, message: '请选择商品分类', trigger: 'change' }
  ],
  price: [
    { required: true, message: '请输入商品价格', trigger: 'blur' }
  ],
  stock: [
    { required: true, message: '请输入商品库存', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择商品状态', trigger: 'change' }
  ],
  specifications: [
    { required: true, validator: validateSpecifications, trigger: ['submit', 'change'] }
  ]
}

// 模拟数据


// 清理本地存储中的错误图片路径
if (typeof window !== 'undefined') {
  const savedProducts = localStorage.getItem('products')
  if (savedProducts) {
    try {
      const parsed = JSON.parse(savedProducts)
      const cleaned = parsed.map((p: any) => ({
        ...p,
        image: p.image && p.image.includes('images-1754822195337-81953518.jpg')
          ? 'http://localhost:3001/uploads/default-product.png'
          : p.image || 'http://localhost:3001/uploads/default-product.png'
      }))
      localStorage.setItem('products', JSON.stringify(cleaned))
    } catch (e) {
      console.error('清理本地存储失败:', e)
    }
  }
}

// 初始化数据
onMounted(() => {
  console.log('ProductManagement 组件挂载')
  loadProducts()
  loadCategories()
})

// 加载商品数据
const loadProducts = async () => {
  loading.value = true
  try {
    const response = await axios.get('/api/getProductList')
    let filteredProducts = response.data.data || []
    
    // 应用筛选
    if (filterForm.product_name) {
      filteredProducts = filteredProducts.filter((p: Product) =>
        p.product_name.includes(filterForm.product_name)
      )
    }
    if (filterForm.category) {
      filteredProducts = filteredProducts.filter((p: Product) =>
        p.category === filterForm.category
      )
    }
    if (filterForm.status) {
      filteredProducts = filteredProducts.filter((p: Product) =>
        p.status === filterForm.status
      )
    }
    
    // 分页处理
    const start = (pagination.page - 1) * pagination.size
    const end = start + pagination.size
    productList.value = filteredProducts.slice(start, end)
    pagination.total = filteredProducts.length
    
    // 更新统计数据
    updateStats(filteredProducts)
    
  } catch (error) {
    console.error('获取商品列表失败:', error)
    ElMessage.error('获取商品列表失败')
  } finally {
    loading.value = false
  }
}

// 更新统计数据
const updateStats = (products: Product[] = []) => {
  const safeProducts = Array.isArray(products) ? products : []
  stats.total = safeProducts.length
  stats.online = safeProducts.filter(p => p.status === '上架').length
  stats.offline = safeProducts.filter(p => p.status === '下架').length
  stats.lowStock = safeProducts.filter(p => p.stock < 10).length
}

// 搜索商品
const searchProducts = () => {
  pagination.page = 1
  loadProducts()
}

// 重置筛选
const resetFilter = () => {
  Object.assign(filterForm, {
    product_name: '',
    category: '',
    status: ''
  })
  searchProducts()
}

// 刷新数据
const refreshData = () => {
  loadProducts()
  ElMessage.success('数据已刷新')
}

// 显示添加对话框
const showAddDialog = () => {
  console.log('showAddDialog 被调用')
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
  console.log('dialogVisible 设置为:', dialogVisible.value)
}

// 编辑商品
const editProduct = (product: Product) => {
  isEdit.value = true;
  dialogVisible.value = true;
  
  // 解析规格字符串为新的数据结构
  const specs = product.product_value ? product.product_value.split(';').map((item: string) => {
    const [attribute, value] = item.split('=');
    return { 
      attribute: attribute || '', 
      values: value ? value.split(',').map(v => v.trim()) : ['']
    };
  }) : [{ attribute: '', values: [''] }];
  
  Object.assign(productForm, {
    ...product,
    specifications: specs,
    variants: product.variants || []
  });
  
  // 如果没有变体，生成变体
  if (!product.variants || product.variants.length === 0) {
    generateVariants();
  }
};

// 切换商品状态
const toggleStatus = async (row: Product) => {
  const newStatus = row.status === '上架' ? '下架' : '上架'
  ElMessageBox.confirm(
    `确定要${newStatus}该商品吗？`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      const updateData = {
        ...row,
        status: newStatus,
        update_time: new Date().toLocaleString()
      }
      await apiService.updateProduct(updateData)
      loadProducts()
      updateStats()
      ElMessage.success(`商品已${newStatus}`)
    } catch (error) {
      console.error('切换商品状态失败:', error)
      ElMessage.error('切换商品状态失败：' + (error as Error).message || '网络错误')
    }
  }).catch(() => {
    ElMessage.info('已取消操作')
  })
}

// 删除商品
const deleteProduct = async (row: Product) => {
  ElMessageBox.confirm(
    '确定要删除该商品吗？此操作不可恢复！',
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'error'
    }
  ).then(async () => {
    try {
      await apiService.deleteProduct(row.product_id)
      loadProducts()
      updateStats()
      ElMessage.success('商品已删除')
    } catch (error) {
      console.error('删除商品失败:', error)
      ElMessage.error('删除商品失败：' + (error as Error).message || '网络错误')
    }
  }).catch(() => {
    ElMessage.info('已取消删除')
  })
}

// 表格选择变化
const handleSelectionChange = (selection: Product[]) => {
  selectedProducts.value = selection
}

// 分页大小变化
const handleSizeChange = (size: number) => {
  pagination.size = size
  pagination.page = 1
  loadProducts()
}

// 页码变化
const handleCurrentChange = (page: number) => {
  pagination.page = page
  loadProducts()
}

// 添加规格
const addSpec = () => {
  productForm.specifications.push({ attribute: '', values: [''] })
}

const removeSpec = (index: number) => {
  productForm.specifications.splice(index, 1)
}

// 添加规格值
const addSpecValue = (specIndex: number) => {
  productForm.specifications[specIndex].values.push('')
}

// 移除规格值
const removeSpecValue = (specIndex: number, valueIndex: number) => {
  const spec = productForm.specifications[specIndex]
  if (spec.values.length > 1) {
    spec.values.splice(valueIndex, 1)
  }
}

// 生成所有可能的规格组合
const generateVariants = () => {
  const validSpecs = productForm.specifications.filter(
    spec => spec.attribute.trim() && spec.values.some(v => v.trim())
  )
  
  if (validSpecs.length === 0) {
    productForm.variants = []
    return
  }
  
  const combinations = cartesianProduct(
    validSpecs.map(spec => 
      spec.values.filter(v => v.trim()).map(value => ({
        attribute: spec.attribute,
        value: value
      }))
    )
  )
  
  productForm.variants = combinations.map((combo, index) => ({
    id: `variant_${Date.now()}_${index}`,
    combination: combo.reduce((acc, item) => {
      acc[item.attribute] = item.value
      return acc
    }, {} as Record<string, string>),
    price: productForm.price,
    stock: 0
  }))
}

// 计算笛卡尔积
const cartesianProduct = (arrays: any[][]): any[][] => {
  if (arrays.length === 0) return [[]]
  if (arrays.length === 1) return arrays[0].map(item => [item])
  
  const result: any[][] = []
  const rest = cartesianProduct(arrays.slice(1))
  
  for (const item of arrays[0]) {
    for (const combo of rest) {
      result.push([item, ...combo])
    }
  }
  
  return result
}

// 监听规格变化，自动生成变体
watch(
  () => productForm.specifications,
  () => {
    generateVariants()
  },
  { deep: true }
)

const submitForm = async () => {
  if (!productFormRef.value) {
    ElMessage.error('表单引用不存在')
    return
  }
  
  // 将规格数组转换为字符串格式
  const product_value = productForm.specifications.map(spec => 
    `${spec.attribute}=${spec.values.filter(v => v.trim()).join(',')}`
  ).join(';')
  
  try {
    const valid = await productFormRef.value.validate()
    if (valid) {
      // 计算总库存
      const totalStock = productForm.variants.reduce((sum, variant) => sum + variant.stock, 0)
      
      // 将variants数组转换为字符串格式
      const variants = JSON.stringify(productForm.variants.map(variant => ({
        combination: Object.entries(variant.combination)
          .map(([attr, val]) => `${attr}:${val}`)
          .join(';'),
        price: variant.price,
        stock: variant.stock
      })))
      
      const productData = {
        ...productForm,
        stock: totalStock,
        product_value,
        variants, // 使用字符串格式传输
        update_time: new Date().toLocaleString()
      }
      
      // 移除variants字段，避免后端报错
      delete productData.variants
      
      if (isEdit.value) {
        await apiService.updateProduct(productData)
        ElMessage.success('商品修改成功')
      } else {
        const newProduct = {
          ...productData,
          product_id: 0,
          sales: 0,
          create_time: new Date().toLocaleString()
        }
        await apiService.insertProduct(newProduct)
        ElMessage.success('商品添加成功')
      }
      
      dialogVisible.value = false
      loadProducts()
      updateStats()
    }
  } catch (error) {
    console.error('操作失败:', error)
    ElMessage.error('操作失败：' + ((error as Error).message || '网络错误'))
  }
}

// 重置表单
const resetForm = () => {
  if (productFormRef.value) {
    productFormRef.value.resetFields()
  }
  Object.assign(productForm, {
    product_id: 0,
    product_name: '',
    category: '',
    price: 0,
    stock: 0,
    sales: 0,
    image: '',
    status: '下架',
    description: '',
    specifications: [{ attribute: '', values: [''] }],
    variants: [],
    create_time: '',
    update_time: ''
  })
}

// 图片上传成功
const handleImageSuccess = (response: any) => {
  console.log('上传响应:', response)
  if (response && response.success && response.data && response.data.filePath) {
    productForm.image = 'http://localhost:3001' + response.data.filePath
    ElMessage.success('图片上传成功')
  } else {
    ElMessage.error('图片上传失败：响应格式错误')
    console.error('上传响应格式错误:', response)
  }
}

// 图片上传失败
const handleImgError = (e: Event) => {
  console.error('图片加载失败:', e.target)
  ElMessage.error('图片加载失败，请检查URL是否正确')
}

const handleImageError = (error: any) => {
  console.error('图片上传失败:', error)
  ElMessage.error('图片上传失败：' + (error.message || '网络错误'))
}

// 格式化商品规格显示
const formatSpecifications = (product_value: string) => {
  if (!product_value) return '暂无规格'
  
  try {
    const specs = product_value.split(';').filter(item => item.trim())
    if (specs.length === 0) return '暂无规格'
    
    return specs.map(spec => {
      const [attribute, value] = spec.split('=')
      if (attribute && value) {
        const values = value.split(',').filter(v => v.trim())
        return `${attribute.trim()}: ${values.join('、')}`
      }
      return spec.trim()
    }).join(' | ')
  } catch (error) {
    console.error('格式化规格失败:', error)
    return product_value
  }
}

// 图片上传前验证
const beforeImageUpload = (file: File) => {
  console.log('开始上传图片:', file.name, file.type, file.size)
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2
  
  if (!isImage) {
    ElMessage.error('只能上传图片文件！')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB！')
    return false
  }
  console.log('图片验证通过，开始上传')
  return true
}
</script>

<style scoped>
.product-management {
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

.values-container {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.value-item {
  display: flex;
  align-items: center;
  gap: 5px;
}

.variants-container {
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  padding: 10px;
  max-height: 300px;
  overflow-y: auto;
}

.variant-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px;
  border-bottom: 1px solid #e4e7ed;
}

.variant-item:last-child {
  border-bottom: none;
}

.variant-info {
  flex: 1;
}

.variant-combination {
  font-weight: 500;
  color: #303133;
}

.variant-inputs {
  display: flex;
  align-items: center;
  gap: 10px;
}

.spec-item {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  margin-bottom: 10px;
  padding: 10px;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
}

.stat-card {
  display: flex;
  align-items: center;
  padding: 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
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

.stat-icon.online {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.stat-icon.offline {
  background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
}

.stat-icon.low-stock {
  background: linear-gradient(135deg, #ff9a9e 0%, #fecfef 100%);
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
  font-size: 14px;
  color: #666;
}

.filter-section {
  margin-bottom: 20px;
}

.table-section {
  margin-bottom: 20px;
}

.price {
  color: #e6a23c;
  font-weight: bold;
}

.low-stock {
  color: #f56c6c;
  font-weight: bold;
}

.pagination-section {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.avatar-uploader {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  width: 100px;
  height: 100px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar-uploader:hover {
  border-color: #409eff;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
}

.avatar {
  width: 100px;
  height: 100px;
  object-fit: cover;
  display: block;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.spec-item {
  display: flex;
  align-items: flex-start;
  margin-bottom: 10px;
  gap: 10px;
}
.spec-attribute, .spec-value {
  margin-right: 10px;
}
.add-spec-btn {
  margin-top: 10px;
}

/* 多值属性输入样式 */
.multi-value-container {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.multi-value-item {
  display: flex;
  align-items: center;
  gap: 5px;
}

.multi-value-item .el-button {
  padding: 4px 8px;
  min-height: auto;
}

.spec-display {
  font-size: 12px;
  color: #666;
  line-height: 1.4;
  display: inline-block;
  max-width: 100%;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.spec-display:hover {
  white-space: normal;
  word-break: break-all;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .product-management {
    padding: 10px;
  }
  
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .header-right {
    width: 100%;
    display: flex;
    gap: 10px;
  }
  
  .header-right .el-button {
    flex: 1;
  }
  
  .stat-card {
    padding: 15px;
  }
  
  .stat-icon {
    width: 50px;
    height: 50px;
    font-size: 20px;
  }
  
  .stat-number {
    font-size: 24px;
  }
  
  .filter-section .el-form-item {
    width: 100%;
    margin-right: 0;
  }
  
  .filter-section .el-form-item .el-input,
  .filter-section .el-form-item .el-select {
    width: 100%;
  }
}
</style>
