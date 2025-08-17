<template>
  <div class="min-h-screen bg-gray-50">
    <!-- 顶部导航 -->
    <div class="bg-white shadow-sm">
      <div class="max-w-6xl mx-auto px-4 py-4">
        <div class="flex items-center justify-between">
          <div class="flex items-center space-x-4">
            <router-link to="/" class="text-2xl font-bold text-red-600">Mike购物</router-link>
            <span class="text-gray-400">|</span>
            <span class="text-lg font-medium">确认订单</span>
          </div>
          <div class="flex items-center space-x-8 text-sm">
            <div class="flex items-center space-x-2">
              <span class="w-6 h-6 bg-red-600 text-white rounded-full flex items-center justify-center text-xs">1</span>
              <span class="text-red-600">我的购物车</span>
            </div>
            <div class="flex items-center space-x-2">
              <span class="w-6 h-6 bg-red-600 text-white rounded-full flex items-center justify-center text-xs">2</span>
              <span class="text-red-600">填写核对订单信息</span>
            </div>
            <div class="flex items-center space-x-2">
              <span class="w-6 h-6 bg-gray-300 text-gray-600 rounded-full flex items-center justify-center text-xs">3</span>
              <span class="text-gray-600">成功提交订单</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 主要内容 -->
    <div class="max-w-6xl mx-auto px-4 py-8">
      <!-- 加载状态 -->
      <div v-if="loading" class="bg-white rounded-lg shadow-sm p-8 mb-6">
        <div class="text-center">
          <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-red-600 mx-auto mb-4"></div>
          <p class="text-gray-600">加载订单信息中...</p>
        </div>
      </div>

      <!-- 错误信息 -->
      <div v-if="error" class="bg-red-50 border border-red-200 rounded-lg p-4 mb-6">
        <div class="flex items-center">
          <div class="text-red-600">
            <p>{{ error }}</p>
            <button @click="fetchCheckoutData" class="text-red-600 hover:text-red-700 text-sm mt-2">
              重新加载
            </button>
          </div>
        </div>
      </div>

      <!-- 收货地址 -->
      <div v-if="!loading && !error" class="bg-white rounded-lg shadow-sm mb-6">
        <div class="p-6">
          <div class="flex items-center justify-between mb-4">
            <h3 class="text-lg font-semibold">收货人信息</h3>
            <button class="text-red-600 text-sm hover:text-red-700" @click="showAddressModal = true">
              新增收货地址
            </button>
          </div>
          
          <div v-if="addresses.length === 0" class="text-center py-8">
            <p class="text-gray-500 mb-4">暂无收货地址，请添加收货地址</p>
            <button class="bg-red-600 text-white px-4 py-2 rounded-lg text-sm hover:bg-red-700" @click="showAddressModal = true">
              添加收货地址
            </button>
          </div>
          
          <div v-else class="space-y-3">
            <div v-for="(address, index) in addresses" :key="index" 
                 class="border rounded-lg p-4 cursor-pointer transition-all"
                 :class="selectedAddress?.addressid === address.addressid ? 'border-red-600 bg-red-50' : 'border-gray-200 hover:border-gray-300'"
                 @click="selectedAddress = address">
              <div class="flex items-center justify-between">
                <div class="flex items-center space-x-4">
                  <input type="radio" :checked="selectedAddress?.addressid === address.addressid" class="text-red-600">
                  <div>
                    <span class="font-medium">{{ address.receiver_name }}</span>
                    <span class="ml-2 text-gray-600">{{ address.receiver_phone }}</span>
                  </div>
                </div>
                <span v-if="address.is_default" class="bg-red-100 text-red-600 px-2 py-1 rounded text-xs">默认</span>
              </div>
              <div class="mt-2 text-gray-700">
                {{ address.province }} {{ address.city }} {{ address.district }} {{ address.detail_address }}
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 商品信息 -->
      <div class="bg-white rounded-lg shadow-sm mb-6">
        <div class="p-6">
          <h3 class="text-lg font-semibold mb-4">商品信息</h3>
          <div v-if="cartItems.length === 0" class="text-center py-8">
            <p class="text-gray-500">暂无商品信息</p>
          </div>
          <div v-else class="border rounded-lg">
            <div class="bg-gray-50 px-4 py-3 text-sm text-gray-600 grid grid-cols-12 gap-4">
              <div class="col-span-6">商品</div>
              <div class="col-span-2 text-center">单价</div>
              <div class="col-span-2 text-center">数量</div>
              <div class="col-span-2 text-right">小计</div>
            </div>
            
            <div v-for="(item, index) in cartItems" :key="index" class="border-t px-4 py-4 grid grid-cols-12 gap-4 items-center">
              <div class="col-span-6 flex items-center space-x-4">
                <img :src="item.image || item.product_image" :alt="item.product_name || item.name" class="w-16 h-16 object-cover rounded">
                <div>
                  <h4 class="font-medium">{{ item.product_name || item.name }}</h4>
                  <p class="text-sm text-gray-500">{{ item.spec || item.product_spec || '默认规格' }}</p>
                </div>
              </div>
              <div class="col-span-2 text-center">¥{{ Number(item.product_price || item.price).toFixed(2) }}</div>
              <div class="col-span-2 text-center">{{ item.quantity || item.product_value || 1 }}</div>
              <div class="col-span-2 text-right font-medium">¥{{ (Number(item.product_price || item.price) * (item.quantity || item.product_value || 1)).toFixed(2) }}</div>
            </div>
          </div>
        </div>
      </div>

      <!-- 配送方式 -->
      <div class="bg-white rounded-lg shadow-sm mb-6">
        <div class="p-6">
          <h3 class="text-lg font-semibold mb-4">配送方式</h3>
          <div class="space-y-3">
            <label class="flex items-center space-x-3 cursor-pointer">
              <input type="radio" v-model="deliveryMethod" value="standard" class="text-red-600">
              <div>
                <div class="font-medium">京东快递</div>
                <div class="text-sm text-gray-500">预计 2024-12-15 送达</div>
              </div>
              <span class="ml-auto text-gray-600">免运费</span>
            </label>
          </div>
        </div>
      </div>

      <!-- 支付方式 -->
      <div class="bg-white rounded-lg shadow-sm mb-6">
        <div class="p-6">
          <h3 class="text-lg font-semibold mb-4">支付方式</h3>
          <div v-if="paymentMethods.length === 0" class="text-center py-8">
            <p class="text-gray-500">暂无支付方式</p>
          </div>
          <div v-else class="grid grid-cols-2 gap-4">
            <div v-for="(method, index) in paymentMethods" :key="index"
               class="border rounded-lg p-4 cursor-pointer transition-all"
               :class="selectedPayment?.pay_id === method.pay_id ? 'border-red-600 bg-red-50' : 'border-gray-200 hover:border-gray-300'"
               @click="selectedPayment = method">
              <div class="flex items-center space-x-3">
                <input type="radio" :checked="selectedPayment?.pay_id === method.pay_id" class="text-red-600">
                <span>{{ method.type }}<template v-if="method.account_info">({{ getAccountNumber(method.account_info) }})</template></span>
              </div>
            </div>
          </div>

        </div>
      </div>

      <!-- 订单备注 -->
      <div class="bg-white rounded-lg shadow-sm mb-6">
        <div class="p-6">
          <h3 class="text-lg font-semibold mb-4">订单备注</h3>
          <textarea 
            v-model="orderRemark"
            placeholder="选填，给商家留言（50字以内）"
            class="w-full border border-gray-300 rounded-lg px-3 py-2 resize-none"
            rows="3"
            maxlength="50"
          ></textarea>
        </div>
      </div>

      <!-- 订单汇总 -->
      <div class="bg-white rounded-lg shadow-sm mb-6">
        <div class="p-6">
          <h3 class="text-lg font-semibold mb-4">订单汇总</h3>
          
          <div class="space-y-2 text-right">
            <div class="flex justify-between">
              <span>商品总价：</span>
              <span>¥{{ parseFloat(totalAmount).toFixed(2) }}</span>
            </div>
            <div class="flex justify-between">
              <span>运费：</span>
              <span>¥{{ parseFloat(shippingFee).toFixed(2) }}</span>
            </div>
            <div class="flex justify-between text-lg font-semibold pt-2 border-t">
              <span>应付金额：</span>
              <span class="text-red-600">¥{{ (parseFloat(totalAmount) + parseFloat(shippingFee)).toFixed(2) }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 底部操作栏 -->
      <div class="fixed bottom-0 left-0 right-0 bg-white border-t px-4 py-4">
        <div class="max-w-6xl mx-auto flex items-center justify-between">
          <div>
            <span class="text-gray-600">返回</span>
            <router-link to="/cart" class="text-red-600 hover:text-red-700">购物车</router-link>
          </div>
          <div class="flex items-center space-x-4">
            <div class="text-right">
              <span class="text-sm text-gray-600">应付金额：</span>
              <span class="text-xl font-bold text-red-600 ml-2">¥{{ (parseFloat(totalAmount) + parseFloat(shippingFee)).toFixed(2) }}</span>
            </div>
            <button 
              @click="submitOrder"
              :disabled="!canSubmit"
              class="bg-red-600 text-white px-8 py-3 rounded-lg font-medium hover:bg-red-700 disabled:bg-gray-400 disabled:cursor-not-allowed transition-colors"
            >
              {{ loading ? '处理中...' : '提交订单' }}
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 地址选择弹窗 -->
    <div v-if="showAddressModal" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div class="bg-white rounded-lg p-6 w-full max-w-md">
        <h3 class="text-lg font-semibold mb-4">新增收货地址</h3>
        <form @submit.prevent="addAddress">
          <div class="space-y-4">
            <div>
              <label class="block text-sm font-medium mb-1">收货人</label>
              <input v-model="newAddress.name" type="text" class="w-full border rounded-lg px-3 py-2" required>
            </div>
            <div>
              <label class="block text-sm font-medium mb-1">手机号</label>
              <input v-model="newAddress.phone" type="tel" class="w-full border rounded-lg px-3 py-2" required>
            </div>
            <div>
              <label class="block text-sm font-medium mb-1">所在地区</label>
              <div class="grid grid-cols-3 gap-2">
                <select v-model="newAddress.province" class="border rounded-lg px-3 py-2" required>
                  <option value="">选择省份</option>
                  <option v-for="p in provinces" :key="p" :value="p">{{ p }}</option>
                </select>
                <select v-model="newAddress.city" class="border rounded-lg px-3 py-2" required>
                  <option value="">选择城市</option>
                  <option v-for="c in cities" :key="c" :value="c">{{ c }}</option>
                </select>
                <select v-model="newAddress.district" class="border rounded-lg px-3 py-2" required>
                  <option value="">选择区县</option>
                  <option v-for="d in districts" :key="d" :value="d">{{ d }}</option>
                </select>
              </div>
            </div>
            <div>
              <label class="block text-sm font-medium mb-1">详细地址</label>
              <input v-model="newAddress.detail" type="text" class="w-full border rounded-lg px-3 py-2" required>
            </div>
          </div>
          <div class="flex space-x-3 mt-6">
            <button type="button" @click="showAddressModal = false" 
                    class="flex-1 border border-gray-300 rounded-lg py-2 hover:bg-gray-50">
              取消
            </button>
            <button type="submit" 
                    class="flex-1 bg-red-600 text-white rounded-lg py-2 hover:bg-red-700">
              保存
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import apiService from '@/services/apiService'

const router = useRouter()
const route = useRoute()

// 状态管理
const loading = ref(false)
const error = ref('')

// 商品数据
const cartItems = ref<Array<any>>([])

// 地址数据
const addresses = ref<Array<any>>([])
const selectedAddress = ref<any>(null)

// 配送方式
const deliveryMethod = ref('standard')

// 支付方式
const paymentMethods = ref<Array<any>>([])
const selectedPayment = ref<any>(null)

// 支付联系电话


// 订单备注
const orderRemark = ref('')

// 计算属性
const totalAmount = computed(() => {
  return cartItems.value.reduce((sum, item) => {
    return sum + Number(item.product_price || item.price) * Number(item.quantity || item.product_value || 1)
  }, 0).toFixed(2)
})

const shippingFee = computed(() => {
  return parseFloat(totalAmount.value) >= 99 ? '0.00' : '10.00'
})

const canSubmit = computed(() => {
  return selectedAddress.value !== null && 
         selectedPayment.value && 
         cartItems.value.length > 0 &&
         !loading.value
})

// 获取用户信息
const getUserId = () => {
  const userInfo = localStorage.getItem('userInfo')
  if (userInfo) {
    const user = JSON.parse(userInfo)
    return user.id
  }
  router.push('/login')
  return null
}

// 获取地址列表
const fetchAddresses = async () => {
  try {
    const userId = getUserId()
    if (!userId) return
    
    const response = await apiService.getUserAddresses(userId)
    // 调整成功判断条件以匹配后端实际返回的状态码
    // 检查响应数据中的success字段或状态码
    if (response.data?.success || response.data?.code === 200 || response.code === 200) {
      addresses.value = response.data || []
      // 设置默认地址
      const defaultAddress = addresses.value.find(addr => addr.is_default)
      if (defaultAddress) {
        selectedAddress.value = defaultAddress
      } else if (addresses.value.length > 0) {
        selectedAddress.value = addresses.value[0]
      }
    } else {
      throw new Error(response.message || '获取地址失败')
    }
  } catch (err: any) {
    console.error('获取地址失败:', err)
    error.value = err.message || '获取地址失败'
  }
}

// 获取支付方式
const getAccountNumber = (accountInfo) => {
      try {
        const parsed = JSON.parse(accountInfo);
        return parsed.number || accountInfo;
      } catch (e) {
        return accountInfo;
      }
    };

    const fetchPaymentMethods = async () => {
      try {
        const userId = getUserId()
        if (!userId) return
        
        const response = await apiService.selectPayMethod(userId)
        // 调整成功判断条件以匹配后端实际返回的状态码
    // 检查响应数据中的success字段或状态码
    if (response.data?.success || response.data?.code === 200 || response.code === 200) {
          // 去重支付方式，避免重复选项
          const uniqueMethods = Array.from(new Map(response.data.map(item => [item.pay_id, item])).values());
          paymentMethods.value = uniqueMethods || []
          if (paymentMethods.value.length > 0) {
            selectedPayment.value = paymentMethods.value[0]
          }
        } else {
          throw new Error(response.message || '获取支付方式失败')
        }
      } catch (err: any) {
        console.error('获取支付方式失败:', err)
        error.value = err.message || '获取支付方式失败'
      }
    }

// 加载购物车商品
const loadCartItems = async () => {
  try {
    const userId = getUserId()
    if (!userId) return

    const productId = route.query.productId
    const quantity = route.query.quantity
    const productValue = route.query.productValue
    
    if (productId && quantity) {
      // 直接购买
      const productResponse = await apiService.getProductInfo(productId)
      const productData = productResponse.data?.[0]
      
      if (productData) {
        cartItems.value = [{
          product_id: productData.product_id || productData.id,
          product_name: productData.product_name || '',
          product_price: productData.price || 0,
          quantity: parseInt(quantity as string),
          image: productData.image || '',
          spec: productValue as string || ''
        }]
      }
    } else {
      // 优先从sessionStorage获取购物车页面传递的数据
      const checkoutItems = sessionStorage.getItem('checkoutItems')
      if (checkoutItems) {
        const items = JSON.parse(checkoutItems)
        cartItems.value = items.map(item => ({
          product_id: item.product_id || item.id,
          product_name: item.name || item.product_name,
          product_price: item.price || item.product_price,
          quantity: item.quantity || item.product_value || 1,
          image: item.image || item.product_image,
          spec: item.spec || item.product_spec || ''
        }))
        // 清除sessionStorage中的数据
        sessionStorage.removeItem('checkoutItems')
      } else {
        // 从localStorage获取（备用方案）
        const checkoutData = localStorage.getItem('checkoutData')
        if (checkoutData) {
          const data = JSON.parse(checkoutData)
          cartItems.value = data.items || []
        } else {
          // 从后端API获取购物车数据
          const cartResponse = await apiService.getCartItems(userId)
          if (cartResponse.code === 200) {
            cartItems.value = cartResponse.data || []
          }
        }
      }
    }
  } catch (err: any) {
    console.error('加载商品数据失败:', err)
    error.value = err.message || '加载商品数据失败'
  }
}

// 获取确认订单数据
const fetchCheckoutData = async () => {
  loading.value = true
  error.value = ''
  
  try {
    await Promise.all([
      fetchAddresses(),
      fetchPaymentMethods(),
      loadCartItems()
    ])
  } catch (err: any) {
    error.value = err.message || '加载订单信息失败'
  } finally {
    loading.value = false
  }
}

// 新地址表单
const newAddress = ref({
  name: '',
  phone: '',
  province: '',
  city: '',
  district: '',
  detail: '',
  is_default: false
})

// 省份数据（简化版）
const provinces = ['北京市', '上海市', '广东省', '江苏省', '浙江省']
const cities = ref(['北京市'])
const districts = ref(['朝阳区', '海淀区', '东城区', '西城区'])

// 添加地址
const addAddress = async () => {
  try {
    const userId = getUserId()
    if (!userId) return
    
    const addressData = {
      ...newAddress.value,
      user_id: userId
    }
    
    const response = await apiService.createAddress(addressData)
    // 调整成功判断条件以匹配后端实际返回的状态码
    // 检查订单提交响应中的success字段或状态码
    if (response.data?.success || response.data?.code === 200 || response.code === 200) {
      showAddressModal.value = false
      newAddress.value = {
        name: '',
        phone: '',
        province: '',
        city: '',
        district: '',
        detail: '',
        is_default: false
      }
      await fetchAddresses()
    } else {
      throw new Error(response.message || '添加地址失败')
    }
  } catch (err: any) {
    console.error('添加地址失败:', err)
    alert('添加地址失败: ' + (err.message || '请重试'))
  }
}

// 提交订单
const submitOrder = async () => {
  if (!canSubmit.value) {
    alert('请完善订单信息')
    return
  }

  loading.value = true
  
  try {
    const userId = getUserId()
    if (!userId) return
    
    const orderData = {
      user_id: userId,
      product_name: cartItems.value[0]?.product_name || '',
      product_allPrice: (parseFloat(totalAmount.value) + parseFloat(shippingFee.value)).toFixed(2),
      product_price: cartItems.value[0]?.product_price || '',
      image: cartItems.value[0]?.image || '',
      order_address: `${selectedAddress.value.province}${selectedAddress.value.city}${selectedAddress.value.district}${selectedAddress.value.detail}`,
      order_userSay: orderRemark.value,
      pay_method: selectedPayment.value.type || selectedPayment.value.pay_method,
      status: '待处理',
      create_time: new Date().toLocaleString()
    }

    const response = await apiService.insertOder(orderData)
      // 专门针对订单提交的成功判断条件
      if (response.data?.success || response.code === 200) {
      const orderId = response.data?.order_id || response.order_id
      if (!orderId) {
        throw new Error('订单创建成功但未返回订单ID')
      }
      
      // 保存订单ID到localStorage，供后续updateOrder使用
      localStorage.setItem('currentOrderId', orderId.toString())
      localStorage.removeItem('checkoutData')
      
      router.push({
        name: 'payment',
        query: { 
          orderNumber: orderId,
          amount: orderData.product_allPrice,
          accountInfo: selectedPayment.value.account_info || ''
        }
      })
    } else {
      throw new Error(response.message || '提交订单失败')
    }
  } catch (err: any) {
    console.error('提交订单失败:', err)
    alert('提交订单失败: ' + (err.message || '请重试'))
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchCheckoutData()
})
</script>

<style scoped>
/* 自定义样式 */
</style>