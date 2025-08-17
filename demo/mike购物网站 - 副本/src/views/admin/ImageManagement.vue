<template>
  <div class="image-management">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <h1>图片管理</h1>
        <p>管理系统中的所有图片信息</p>
      </div>
      <div class="header-right">
        <el-button type="primary" @click="showAddDialog">
          <el-icon><Plus /></el-icon>
          添加图片
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
              <el-icon><Picture /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-number">{{ stats.total }}</div>
              <div class="stat-label">图片总数</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-icon product">
              <el-icon><Goods /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-number">{{ stats.product }}</div>
              <div class="stat-label">商品图片</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-icon banner">
              <el-icon><Monitor /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-number">{{ stats.banner }}</div>
              <div class="stat-label">轮播图片</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-icon other">
              <el-icon><Files /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-number">{{ stats.other }}</div>
              <div class="stat-label">其他图片</div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 搜索筛选区域 -->
    <div class="filter-section">
      <el-card>
        <el-form :model="filterForm" inline>
          <el-form-item label="图片分类">
            <el-select v-model="filterForm.image_category" placeholder="请选择分类" clearable>
              <el-option label="商品图片" value="商品图片" />
              <el-option label="轮播图片" value="轮播图片" />
              <el-option label="分类图片" value="分类图片" />
              <el-option label="其他" value="其他" />
            </el-select>
          </el-form-item>
          <el-form-item label="创建时间">
            <el-date-picker
              v-model="filterForm.dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="searchImages">
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

    <!-- 图片列表 -->
    <div class="table-section">
      <el-card>
        <el-table 
          :data="imageList" 
          style="width: 100%" 
          v-loading="loading"
          @selection-change="handleSelectionChange"
        >
          <el-table-column type="selection" width="55" />
          <el-table-column prop="image_id" label="图片ID" width="80" />
          <el-table-column label="图片预览" width="120">
            <template #default="{ row }">
              <el-image 
                :src="row.image_url" 
                :preview-src-list="[row.image_url]"
                fit="cover"
                style="width: 80px; height: 80px; border-radius: 4px;"
              />
            </template>
          </el-table-column>
          <el-table-column prop="image_url" label="图片URL" min-width="300" show-overflow-tooltip />
          <el-table-column prop="image_category" label="图片分类" width="120">
            <template #default="{ row }">
              <el-tag :type="getCategoryType(row.image_category)">
                {{ row.image_category }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="create_time" label="创建时间" width="160" />
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="{ row }">
              <el-button size="small" @click="editImage(row)">编辑</el-button>
              <el-button size="small" type="danger" @click="deleteImage(row)">删除</el-button>
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

    <!-- 添加/编辑图片对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑图片' : '添加图片'"
      width="600px"
      @close="resetForm"
    >
      <el-form 
        ref="imageFormRef" 
        :model="imageForm" 
        :rules="formRules" 
        label-width="120px"
      >
        <el-form-item label="添加方式">
          <el-radio-group v-model="uploadMethod">
            <el-radio value="url">URL链接</el-radio>
            <el-radio value="local">本地上传</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <el-form-item v-if="uploadMethod === 'url'" label="图片URL" prop="image_url">
          <el-input v-model="imageForm.image_url" placeholder="请输入图片URL" />
        </el-form-item>
        
        <el-form-item v-if="uploadMethod === 'local'" label="上传图片">
          <el-upload
            class="upload-demo"
            :action="uploadUrl"
            :headers="uploadHeaders"
            :on-success="handleUploadSuccess"
            :on-error="handleUploadError"
            :before-upload="beforeUpload"
            :show-file-list="false"
            accept="image/*"
            name="file"
            :on-progress="handleUploadProgress"
          >
            <el-button type="primary" :loading="uploading">
              <el-icon><Upload /></el-icon>
              {{ uploading ? '上传中...' : '选择图片' }}
            </el-button>
            <template #tip>
              <div class="el-upload__tip">
                支持 JPG、PNG、GIF 格式，大小不超过 5MB<br>
                <span style="color: #67C23A;">上传完成后请选择分类并点击确定</span>
              </div>
            </template>
          </el-upload>
          <div v-if="uploading" class="upload-progress">
            <el-progress :percentage="uploadProgress" />
          </div>
        </el-form-item>
        
        <el-form-item label="图片分类" prop="image_category">
          <el-select v-model="imageForm.image_category" placeholder="请选择图片分类">
            <el-option label="商品图片" value="商品图片" />
            <el-option label="轮播图片" value="轮播图片" />
            <el-option label="分类图片" value="分类图片" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="图片预览" v-if="imageForm.image_url">
          <el-image 
            :src="imageForm.image_url" 
            :preview-src-list="[imageForm.image_url]"
            fit="cover"
            style="width: 200px; height: 200px; border-radius: 4px;"
          />
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
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Refresh, Search, Picture, Goods, Monitor, Files, Upload } from '@element-plus/icons-vue'
import apiService from '@/services/apiService'

// 响应式数据
const loading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const imageList = ref([])
const selectedImages = ref([])
const imageFormRef = ref()

// 统计信息
const stats = reactive({
  total: 0,
  product: 0,
  banner: 0,
  other: 0
})

// 筛选表单
const filterForm = reactive({
  image_category: '',
  dateRange: []
})

// 分页信息
const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

// 上传方式选择
const uploadMethod = ref('url')
const uploadUrl = ref('http://localhost:3001/api/upload')
const uploadHeaders = reactive({
  'Authorization': `Bearer ${localStorage.getItem('adminToken') || ''}`
})
const uploading = ref(false)
const uploadProgress = ref(0)

// 图片表单
const imageForm = reactive({
  image_id: null,
  image_url: '',
  image_category: ''
})

// 表单验证规则 - 根据上传方式动态调整
const formRules = computed(() => ({
  image_url: uploadMethod.value === 'url' 
    ? [
        { required: true, message: '请输入图片URL', trigger: 'blur' },
        { type: 'string', pattern: /^https?:\/\/.+/, message: '请输入有效的URL地址', trigger: 'blur' }
      ]
    : [], // 本地上传时不验证URL
  image_category: [
    { required: true, message: '请选择图片分类', trigger: 'change' }
  ]
}))

// 获取分类类型
const getCategoryType = (category: string) => {
  const typeMap: Record<string, string> = {
    '商品图片': 'success',
    '轮播图片': 'warning',
    '分类图片': 'info',
    '其他': 'default'
  }
  return typeMap[category] || 'default'
}

// 获取图片列表
const fetchImageList = async () => {
  loading.value = true
  try {
    const response = await apiService.getImageList()
    if (response.success) {
      imageList.value = response.data || []
      pagination.total = imageList.value.length
      updateStats()
    } else {
      ElMessage.error(response.message || '获取图片列表失败')
    }
  } catch (error) {
    console.error('获取图片列表失败:', error)
    ElMessage.error('获取图片列表失败')
  } finally {
    loading.value = false
  }
}

// 更新统计信息
const updateStats = () => {
  stats.total = imageList.value.length
  stats.product = imageList.value.filter(img => img.image_category === '商品图片').length
  stats.banner = imageList.value.filter(img => img.image_category === '轮播图片').length
  stats.other = imageList.value.filter(img => !['商品图片', '轮播图片'].includes(img.image_category)).length
}

// 显示添加对话框
const showAddDialog = () => {
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

// 编辑图片
const editImage = (row: any) => {
  isEdit.value = true
  Object.assign(imageForm, row)
  dialogVisible.value = true
}

// 删除图片
const deleteImage = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定要删除该图片吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const response = await apiService.deleteImage(row.image_id)
    if (response.success) {
      ElMessage.success('删除成功')
      fetchImageList()
    } else {
      ElMessage.error(response.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除图片失败:', error)
      ElMessage.error('删除图片失败')
    }
  }
}

// 提交表单
const submitForm = async () => {
  if (!imageFormRef.value) return
  
  await imageFormRef.value.validate(async (valid: boolean) => {
    if (valid) {
      try {
        let response
        if (isEdit.value) {
          response = await apiService.updateImage(imageForm)
        } else {
          response = await apiService.insertImage(imageForm)
        }
        
        if (response.success) {
          ElMessage.success(isEdit.value ? '更新成功' : '添加成功')
          dialogVisible.value = false
          fetchImageList()
        } else {
          ElMessage.error(response.message || (isEdit.value ? '更新失败' : '添加失败'))
        }
      } catch (error) {
        console.error(isEdit.value ? '更新图片失败:' : '添加图片失败:', error)
        ElMessage.error(isEdit.value ? '更新图片失败' : '添加图片失败')
      }
    }
  })
}

// 文件上传处理 - 仅保存URL，不立即调用接口
const handleUploadSuccess = (response: any, file: any) => {
  uploading.value = false
  if (response.success) {
    imageForm.image_url = response.data.filePath || response.data.url || response.url
    ElMessage.success('图片上传成功，请选择分类后点击确定')
  } else {
    ElMessage.error(response.message || '上传失败')
  }
}

const handleUploadError = (error: any) => {
  uploading.value = false
  console.error('上传失败:', error)
  ElMessage.error('图片上传失败')
}

const beforeUpload = (file: File) => {
  const isImage = file.type.startsWith('image/')
  const isLt5M = file.size / 1024 / 1024 < 5

  if (!isImage) {
    ElMessage.error('只能上传图片文件！')
    return false
  }
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过 5MB！')
    return false
  }
  
  uploading.value = true
  uploadProgress.value = 0
  return true
}

const handleUploadProgress = (event: any) => {
  uploadProgress.value = Math.round((event.loaded / event.total) * 100)
}

// 重置表单
const resetForm = () => {
  if (imageFormRef.value) {
    imageFormRef.value.resetFields()
  }
  Object.assign(imageForm, {
    image_id: null,
    image_url: '',
    image_category: ''
  })
  uploadMethod.value = 'url'
  uploading.value = false
  uploadProgress.value = 0
}

// 搜索图片
const searchImages = () => {
  pagination.page = 1
  fetchImageList()
}

// 重置筛选
const resetFilter = () => {
  Object.assign(filterForm, {
    image_category: '',
    dateRange: []
  })
  searchImages()
}

// 刷新数据
const refreshData = () => {
  fetchImageList()
}

// 表格选择变化
const handleSelectionChange = (selection: any[]) => {
  selectedImages.value = selection
}

// 分页大小变化
const handleSizeChange = (size: number) => {
  pagination.size = size
  fetchImageList()
}

// 页码变化
const handleCurrentChange = (page: number) => {
  pagination.page = page
  fetchImageList()
}

// 页面加载时获取数据
onMounted(() => {
  fetchImageList()
})
</script>

<style scoped>
.image-management {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.header-left h1 {
  margin: 0 0 8px 0;
  font-size: 24px;
  color: #303133;
}

.header-left p {
  margin: 0;
  color: #909399;
  font-size: 14px;
}

.stats-section {
  margin-bottom: 30px;
}

.stat-card {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
  font-size: 24px;
  color: white;
}

.stat-icon.total {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.stat-icon.product {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.stat-icon.banner {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.stat-icon.other {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.stat-content {
  flex: 1;
}

.stat-number {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 4px;
}

.stat-label {
  color: #909399;
  font-size: 14px;
}

.filter-section {
  margin-bottom: 20px;
}

.table-section {
  margin-bottom: 20px;
}

.pagination-section {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.upload-demo {
  width: 100%;
}

.upload-progress {
  margin-top: 10px;
  width: 200px;
}

.el-upload__tip {
  color: #909399;
  font-size: 12px;
  margin-top: 5px;
}
</style>