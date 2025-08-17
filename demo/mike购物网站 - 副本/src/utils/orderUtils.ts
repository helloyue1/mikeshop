// 订单工具函数

/**
 * 保存订单ID到本地存储
 * @param orderId 订单ID
 */
export const saveOrderId = (orderId: string | number): void => {
  localStorage.setItem('currentOrderId', orderId.toString())
}

/**
 * 从本地存储获取订单ID
 * @returns 订单ID或null
 */
export const getOrderId = (): string | null => {
  return localStorage.getItem('currentOrderId')
}

/**
 * 清除本地存储的订单ID
 */
export const clearOrderId = (): void => {
  localStorage.removeItem('currentOrderId')
}

/**
 * 从响应数据中提取订单ID
 * @param response 响应数据
 * @returns 订单ID
 */
export const extractOrderId = (response: any): string | number | null => {
  return response?.data?.order_id || response?.order_id || null
}

/**
 * 安全地解析订单ID为数字
 * @param orderId 订单ID
 * @returns 数字类型的订单ID
 */
export const parseOrderId = (orderId: string | number): number => {
  const parsed = parseInt(orderId.toString())
  if (isNaN(parsed)) {
    throw new Error('无效的订单ID格式')
  }
  return parsed
}