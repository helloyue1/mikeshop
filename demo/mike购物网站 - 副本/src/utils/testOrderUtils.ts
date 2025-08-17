// 测试订单工具函数
import { saveOrderId, getOrderId, clearOrderId } from './orderUtils'

// 测试订单ID存储和获取
export const testOrderIdFlow = () => {
  console.log('开始测试订单ID流程...')
  
  // 模拟从insertOder接口返回的数据
  const mockResponse = {
    code: 200,
    message: 'SUCCESS',
    data: 1,
    success: true,
    url: null,
    order_id: 16
  }
  
  // 提取并保存订单ID
  const orderId = mockResponse.data?.order_id || mockResponse.order_id
  console.log('提取到的订单ID:', orderId)
  
  saveOrderId(orderId)
  console.log('订单ID已保存到localStorage')
  
  // 验证获取
  const savedOrderId = getOrderId()
  console.log('从localStorage获取的订单ID:', savedOrderId)
  
  if (savedOrderId === orderId.toString()) {
    console.log('✅ 订单ID存储和获取测试通过')
  } else {
    console.error('❌ 订单ID存储和获取测试失败')
  }
  
  // 清理测试数据
  clearOrderId()
  console.log('测试数据已清理')
}

// 运行测试
if (typeof window !== 'undefined') {
  // @ts-ignore
  window.testOrderIdFlow = testOrderIdFlow
}