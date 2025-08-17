import { ref } from 'vue';

// 创建一个API服务类
class ApiService {
  private baseUrl: string;

  constructor(baseUrl: string) {
    this.baseUrl = baseUrl;
  }

  // 通用请求方法
  private async request<T>(
    endpoint: string,
    method: 'GET' | 'POST' | 'PUT' | 'DELETE' = 'GET',
    data: any = null,
    requiresAuth: boolean = true
  ): Promise<T> {
    const url = `${this.baseUrl}/${endpoint}`;
    const headers: HeadersInit = {
      'Content-Type': 'application/json',
    };

    // 添加token到请求头
    const token = localStorage.getItem('token');
    if (token) {
      headers['Authorization'] = `Bearer ${token}`;
    }

    console.log('Request headers:', headers);
    const options: RequestInit = {
      method,
      headers,
      credentials: 'include', // 包含cookies
    };

    if (data && (method === 'POST' || method === 'PUT')) {
      options.body = JSON.stringify(data);
    }

    try {
      // 拦截/api/edit请求
      if (url.includes('/api/edit')) {
        console.log('拦截到/api/edit请求:', { url, options, requestData: data });
      }
      const response = await fetch(url, options);

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      return await response.json() as T;
    } catch (error) {
      console.error('API request failed:', error);
      throw error;
    }
  }

  // 登录API (用户)
  async login(credentials: { username: string; password: string }) {
    const response = await this.request<{ data: { token: string; username: string; id: number; redirectUrl?: string } }>('login', 'POST', credentials, false);
    
    // 存储token
    localStorage.setItem('token', response.data.token);
    
    // 存储包含登录状态的完整用户信息
    const userInfo = {
      id: response.data.id,
      username: response.data.username,
      isLoggedIn: true
    };
    localStorage.setItem('userInfo', JSON.stringify(userInfo));
    
    return response;
  }

  // 管理员登录API
  async adminLogin(credentials: { username: string; password: string }) {
    const response = await this.request<{ data: { token: string; username: string; name: string; role: string; redirectUrl?: string } }>('login', 'POST', credentials, false);
    
    // 存储管理员token和用户信息
    localStorage.setItem('adminToken', response.data.token);
    
    const adminUserInfo = {
      username: response.data.username,
      name: response.data.name,
      role: response.data.role,
      isLoggedIn: true
    };
    localStorage.setItem('adminUser', JSON.stringify(adminUserInfo));
    
    return response;
  }

  // 注册API
  async register(userData: { username: string; password: string; email?: string }) {
    return this.request<{ data: { token: string } }>('register', 'POST', userData, false);
  }

  // 获取用户信息API
  async getUserInfo() {
    const response = await this.request<{ data: { id: number; username: string; nickname: string; email?: string; phone?: string; gender?: number; birthday?: string; signature?: string; avatar?: string } }>('edit', 'POST');
    localStorage.setItem('id', response.data.id.toString());
    return response;
  }

  // 更新用户信息API
  async updateUserInfo(userData: { id: number; nickname?: string; email?: string; phone?: string; gender?: number; birthday?: string; signature?: string; avatar?: string }) {
    const requestData = { ...userData };
    return this.request<{ code: number; message: string; data: any; success: boolean }>('edit', 'POST', requestData);
  }

  // 地址管理API方法
  // 获取用户地址列表
  async getUserAddresses(userId: number) {
    return this.request<{ data: any[] }>(`getAddressinfo?user_id=${userId}`, 'GET');
  }

  // 创建新地址
  async createAddress(addressData: any) {
    return this.request<{ code: number; message: string; data: any; success: boolean }>('insertAddress', 'POST', addressData);
  }

  // 更新地址
  async updateAddress(addressData: any) {
    return this.request<{ code: number; message: string; data: any; success: boolean }>('updateAddress', 'PUT', addressData);
  }

  // 删除地址
  async deleteAddress(userId: number, addressId: number) {
    return this.request<{ code: number; message: string; data: any; success: boolean }>(`deleteAddress?user_id=${userId}&address_id=${addressId}`, 'DELETE');
  }

  // 设置默认地址
  async setDefaultAddress(userId: number, addressId: number) {
    return this.request<{ code: number; message: string; data: any; success: boolean }>(`address/setDefault`, 'POST', { user_id: userId, address_id: addressId });
  }

  // 支付方式管理API方法
  // 查询支付方式
  async selectPayMethod(userId: number) {
    const params = new URLSearchParams({ user_id: userId.toString() });
    const url = `${this.baseUrl}/selectPayMethod?${params.toString()}`;
    const headers: HeadersInit = {
      'Content-Type': 'application/json',
    };
    
    const token = localStorage.getItem('token');
    if (token) {
      headers['Authorization'] = `Bearer ${token}`;
    }
    
    const options: RequestInit = {
      method: 'GET',
      headers,
      credentials: 'include',
    };
    
    try {
      const response = await fetch(url, options);
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      return await response.json();
    } catch (error) {
      console.error('查询支付方式失败:', error);
      throw error;
    }
  }

  // 删除支付方式
  async deletePayMethod(userId: number, payId: number) {
    return this.request<{ code: number; message: string; data: any; success: boolean }>(`deletePayMethod?user_id=${userId}&pay_id=${payId}`, 'DELETE');
  }

  // 更新支付方式
  async putPayMethod(userPaymentMethod: any) {
    return this.request<{ code: number; message: string; data: any; success: boolean }>('putPayMethod', 'PUT', userPaymentMethod);
  }

  // 添加支付方式
  async insertPayMethod(userPaymentMethod: any) {
    return this.request<{ code: number; message: string; data: any; success: boolean }>('insertPayMethod', 'POST', userPaymentMethod);
  }

  // 设置默认支付方式
  async setDefaultPayMethod(payId: number, userId: number) {
    return this.request<{ code: number; message: string; data: any; success: boolean }>(`setDefaultPayMethod?pay_id=${payId}&user_id=${userId}&is_default=1`, 'PUT');
  }

  // 交易记录相关API
  // 提交交易记录
  async insertTransInfo(transData: any) {
    return this.request<{ code: number; message: string; data: any; success: boolean }>('insertTransInfo', 'POST', transData);
  }

  // 提交订单
  async insertOder(orderData: any) {
    return this.request<{ code: number; message: string; data: any; success: boolean }>('insertOder', 'POST', orderData);
  }

  // 按类型查询交易
  async getTransByType(transType: string) {
    const params = new URLSearchParams({ trans_type: transType });
    return this.request<{ data: any[] }>(`getTransByType?${params.toString()}`, 'GET');
  }

  // 按日期查询交易
  async getTransInfoByDate(transDate: string) {
    const params = new URLSearchParams({ trans_date: transDate });
    return this.request<{ data: any[] }>(`getTransInfoByDate?${params.toString()}`, 'GET');
  }

  // 按日期和类型查询交易
  async getTransInfoByDateAndType(transDate: string, transType: string) {
    const params = new URLSearchParams({ trans_date: transDate, trans_type: transType });
    return this.request<{ data: any[] }>(`getTransInfoByDateAndType?${params.toString()}`, 'GET');
  }

  // 查询用户账户余额
  async selectAccountInfoByid(userId: number) {
    return this.request<{ data: { accout_id: number; user_id: number; total_asset: number; last_updated: string } }>(`selectAccout?user_id=${userId}`, 'GET');
  }

  // 查询用户交易记录
  async selectTransByUserId(userId: number) {
  return this.request<{ data: any[] }>(`getTransInfo?user_id=${userId}`, 'GET');
}

  // 获取订单详情
  async getOrderDetail(orderId: string) {
    return this.request<{ data: any }>(`orders/${orderId}`, 'GET');
  }



  // 获取商品详情
  async getProductInfo(productId: number) {
    return this.request<{ data: any }>(`getProductInfo?product_id=${productId}`, 'GET', null, false);
  }

  // 获取所有用户信息（管理员用）
  async getAllUsers() {
    const adminToken = localStorage.getItem('adminToken');
    const url = `${this.baseUrl}/AllUser`;
    const headers: HeadersInit = {
      'Content-Type': 'application/json',
    };
    
    if (adminToken) {
      headers['Authorization'] = `Bearer ${adminToken}`;
    }
    
    const options: RequestInit = {
      method: 'GET',
      headers,
      credentials: 'include',
    };
    
    try {
      const response = await fetch(url, options);
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      return await response.json() as { data: any[] };
    } catch (error) {
      console.error('获取所有用户信息失败:', error);
      throw error;
    }
  }

  // 获取所有分类信息（管理员用）
  async selectCategory() {
    const adminToken = localStorage.getItem('adminToken');
    const url = `${this.baseUrl}/selectCategory`;
    const headers: HeadersInit = {
      'Content-Type': 'application/json',
    };
    
    if (adminToken) {
      headers['Authorization'] = `Bearer ${adminToken}`;
    }
    
    const options: RequestInit = {
      method: 'GET',
      headers,
      credentials: 'include',
    };
    
    try {
      const response = await fetch(url, options);
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      return await response.json() as { data: any[] };
    } catch (error) {
      console.error('获取分类信息失败:', error);
      throw error;
    }
  }

  // 插入分类信息（管理员用）
  async insertCategory(categoryData: { category_id?: number; category_name: string; categorySon_name: string; product_count?: number; status: string; create_time?: string }) {
    const adminToken = localStorage.getItem('adminToken');
    const url = `${this.baseUrl}/insertCategory`;
    const headers: HeadersInit = {
      'Content-Type': 'application/json',
    };
    
    if (adminToken) {
      headers['Authorization'] = `Bearer ${adminToken}`;
    }
    
    const options: RequestInit = {
      method: 'POST',
      headers,
      credentials: 'include',
      body: JSON.stringify(categoryData),
    };
    
    try {
      const response = await fetch(url, options);
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      return await response.json() as { code: number; message: string; data: any; success: boolean };
    } catch (error) {
      console.error('插入分类信息失败:', error);
      throw error;
    }
  }

  // 更新分类信息（管理员用）
  async updateCategory(categoryData: { category_id: number; category_name: string; categorySon_name: string; product_count?: number; status: string; create_time?: string }) {
    const adminToken = localStorage.getItem('adminToken');
    const url = `${this.baseUrl}/updateCategory`;
    const headers: HeadersInit = {
      'Content-Type': 'application/json',
    };
    
    if (adminToken) {
      headers['Authorization'] = `Bearer ${adminToken}`;
    }
    
    const options: RequestInit = {
      method: 'PUT',
      headers,
      credentials: 'include',
      body: JSON.stringify(categoryData),
    };
    
    try {
      const response = await fetch(url, options);
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      return await response.json() as { code: number; message: string; data: any; success: boolean };
    } catch (error) {
      console.error('更新分类信息失败:', error);
      throw error;
    }
  }

  // 删除分类信息（管理员用）
  async deleteCategory(categoryData: { category_id: number; category_name: string; categorySon_name: string; product_count?: number; status: string; create_time?: string }) {
    const adminToken = localStorage.getItem('adminToken');
    const url = `${this.baseUrl}/deleteCategory`;
    const headers: HeadersInit = {
      'Content-Type': 'application/json',
    };
    
    if (adminToken) {
      headers['Authorization'] = `Bearer ${adminToken}`;
    }
    
    const options: RequestInit = {
      method: 'DELETE',
      headers,
      credentials: 'include',
      body: JSON.stringify(categoryData),
    };
    
    try {
      const response = await fetch(url, options);
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      return await response.json() as { code: number; message: string; data: any; success: boolean };
    } catch (error) {
      console.error('删除分类信息失败:', error);
      throw error;
    }
  }



  // 获取商品列表
  async getProductList() {
    const adminToken = localStorage.getItem('adminToken');
    const url = `${this.baseUrl}/getProductList`;
    const headers: HeadersInit = {
      'Content-Type': 'application/json',
    };
    
    if (adminToken) {
      headers['Authorization'] = `Bearer ${adminToken}`;
    }
    
    const options: RequestInit = {
      method: 'GET',
      headers,
      credentials: 'include',
    };
    
    try {
      const response = await fetch(url, options);
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      return await response.json();
    } catch (error) {
      console.error('获取商品列表失败:', error);
      throw error;
    }
  }

  // 添加商品
  async insertProduct(productData: any) {
    console.log('添加商品 - 发送的数据:', productData);
    const adminToken = localStorage.getItem('adminToken');
    const url = `${this.baseUrl}/insertProduct`;
    const headers: HeadersInit = {
      'Content-Type': 'application/json',
    };
    
    if (adminToken) {
      headers['Authorization'] = `Bearer ${adminToken}`;
    }
    
    const requestBody = JSON.stringify(productData);
    console.log('添加商品 - 请求体:', requestBody);
    
    const options: RequestInit = {
      method: 'POST',
      headers,
      credentials: 'include',
      body: requestBody,
    };
    
    try {
      const response = await fetch(url, options);
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      return await response.json();
    } catch (error) {
      console.error('添加商品失败:', error);
      throw error;
    }
  }

  // 更新商品
  async updateProduct(productData: any) {
    console.log('更新商品 - 发送的数据:', productData);
    const adminToken = localStorage.getItem('adminToken');
    const url = `${this.baseUrl}/updateProduct`;
    const headers: HeadersInit = {
      'Content-Type': 'application/json',
    };
    
    if (adminToken) {
      headers['Authorization'] = `Bearer ${adminToken}`;
    }
    
    const requestBody = JSON.stringify(productData);
    console.log('更新商品 - 请求体:', requestBody);
    
    const options: RequestInit = {
      method: 'PUT',
      headers,
      credentials: 'include',
      body: requestBody,
    };
    
    try {
      const response = await fetch(url, options);
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      return await response.json();
    } catch (error) {
      console.error('更新商品失败:', error);
      throw error;
    }
  }

  // 删除商品
  async deleteProduct(productId: number) {
    console.log('删除商品 - productId:', productId);
    const adminToken = localStorage.getItem('adminToken');
    // 移除baseUrl末尾可能存在的斜杠，确保URL格式正确
    const url = `${this.baseUrl.replace(/\/$/, '')}/deleteProduct?product_id=${productId}`;
    const headers: HeadersInit = {
      'Content-Type': 'application/json',
    };
    
    if (adminToken) {
      headers['Authorization'] = `Bearer ${adminToken}`;
    }
    
    const options: RequestInit = {
      method: 'DELETE',
      headers,
      credentials: 'include',
    };
    
    try {
      const response = await fetch(url, options);
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      return await response.json();
    } catch (error) {
      console.error('删除商品失败:', error);
      throw error;
    }
  }





  // 删除图片
  async deleteImage(imageId: number) {
    console.log('删除图片 - imageId:', imageId);
    const adminToken = localStorage.getItem('adminToken');
    const url = `${this.baseUrl}/deleteImages`;
    const headers: HeadersInit = {
      'Content-Type': 'application/json',
    };
    
    if (adminToken) {
      headers['Authorization'] = `Bearer ${adminToken}`;
    }
    
    const options: RequestInit = {
      method: 'DELETE',
      headers,
      credentials: 'include',
      body: JSON.stringify(imageId),
    };
    
    try {
      const response = await fetch(url, options);
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      return await response.json() as { code: number; message: string; data: number; success: boolean };
    } catch (error) {
      console.error('删除图片失败:', error);
      throw error;
    }
  }

  // 购物车相关API方法
  // 获取购物车列表
  async getBuyCar(userId: number) {
    const url = `${this.baseUrl}/getBuyCar?user_id=${userId}`;
    const headers: HeadersInit = {
      'Content-Type': 'application/json',
    };
    
    const token = localStorage.getItem('token');
    if (token) {
      headers['Authorization'] = `Bearer ${token}`;
    }
    
    const options: RequestInit = {
      method: 'GET',
      headers,
      credentials: 'include',
    };
    
    try {
      const response = await fetch(url, options);
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      return await response.json();
    } catch (error) {
      console.error('获取购物车失败:', error);
      throw error;
    }
  }

  // 获取购物车商品（用于确认订单页面）
  async getCartItems(userId: number) {
    const url = `${this.baseUrl}/getBuyCar?user_id=${userId}`;
    const headers: HeadersInit = {
      'Content-Type': 'application/json',
    };
    
    const token = localStorage.getItem('token');
    if (token) {
      headers['Authorization'] = `Bearer ${token}`;
    }
    
    const options: RequestInit = {
      method: 'GET',
      headers,
      credentials: 'include',
    };
    
    try {
      const response = await fetch(url, options);
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      return await response.json();
    } catch (error) {
      console.error('获取购物车商品失败:', error);
      throw error;
    }
  }

  // 添加商品到购物车
  async insertBuyCar(cartData: { user_id: number; product_id: number; product_value: string; product_name: string; image: string; price: number }) {
    const url = `${this.baseUrl}/insertBuyCar`;
    const headers: HeadersInit = {
      'Content-Type': 'application/json',
    };
    
    const token = localStorage.getItem('token');
    if (token) {
      headers['Authorization'] = `Bearer ${token}`;
    }
    
    const options: RequestInit = {
      method: 'POST',
      headers,
      credentials: 'include',
      body: JSON.stringify(cartData),
    };
    
    try {
      const response = await fetch(url, options);
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      return await response.json();
    } catch (error) {
      console.error('添加购物车失败:', error);
      throw error;
    }
  }

  // 删除购物车商品
  async deleteBuyCar(userId: number, productId: number) {
    const url = `${this.baseUrl}/deleteBuyCar?user_id=${userId}&product_id=${productId}`;
    const headers: HeadersInit = {
      'Content-Type': 'application/json',
    };
    
    const token = localStorage.getItem('token');
    if (token) {
      headers['Authorization'] = `Bearer ${token}`;
    }
    
    const options: RequestInit = {
      method: 'DELETE',
      headers,
      credentials: 'include',
    };
    
    try {
      const response = await fetch(url, options);
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      return await response.json();
    } catch (error) {
      console.error('删除购物车商品失败:', error);
      throw error;
    }
  }

  // 订单相关API方法
  // 获取用户订单
  async getUserOrders(userId: number) {
    const url = `${this.baseUrl}/getUserOder?user_id=${userId}`;
    const headers: HeadersInit = {
      'Content-Type': 'application/json',
    };
    
    const token = localStorage.getItem('token');
    if (token) {
      headers['Authorization'] = `Bearer ${token}`;
    }
    
    const options: RequestInit = {
      method: 'GET',
      headers,
      credentials: 'include',
    };
    
    try {
      const response = await fetch(url, options);
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      return await response.json();
    } catch (error) {
      console.error('获取用户订单失败:', error);
      throw error;
    }
  }

  // 插入订单
  async insertOrder(orderData: any) {
    const url = `${this.baseUrl}/insertOder`;
    const headers: HeadersInit = {
      'Content-Type': 'application/json',
    };
    
    const token = localStorage.getItem('token');
    if (token) {
      headers['Authorization'] = `Bearer ${token}`;
    }
    
    const options: RequestInit = {
      method: 'POST',
      headers,
      credentials: 'include',
      body: JSON.stringify(orderData),
    };
    
    try {
      const response = await fetch(url, options);
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      return await response.json();
    } catch (error) {
      console.error('插入订单失败:', error);
      throw error;
    }
  }

  // 更新订单
  async updateOrder(status: string, orderData: any) {
    const url = `${this.baseUrl}/updateOrder`;
    const headers: HeadersInit = {
      'Content-Type': 'application/json',
    };
    
    const token = localStorage.getItem('token');
    if (token) {
      headers['Authorization'] = `Bearer ${token}`;
    }
    
    const requestBody = {
      status: status,
      order_id: orderData.order_id
    };
    
    const options: RequestInit = {
      method: 'PUT',
      headers,
      credentials: 'include',
      body: JSON.stringify(requestBody),
    };
    
    try {
      const response = await fetch(url, options);
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      return await response.json();
    } catch (error) {
      console.error('更新订单失败:', error);
      throw error;
    }
  }

  // 账户相关API方法
  // 查询账户信息
  async selectAccountInfo(userId: number) {
    const url = `${this.baseUrl}/selectAccout?user_id=${userId}`;
    const headers: HeadersInit = {
      'Content-Type': 'application/json',
    };
    
    const token = localStorage.getItem('token');
    if (token) {
      headers['Authorization'] = `Bearer ${token}`;
    }
    
    const options: RequestInit = {
      method: 'GET',
      headers,
      credentials: 'include',
    };
    
    try {
      const response = await fetch(url, options);
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      return await response.json();
    } catch (error) {
      console.error('查询账户信息失败:', error);
      throw error;
    }
  }

  // 插入账户信息
  async insertAccountInfo(accountData: any) {
    const url = `${this.baseUrl}/insertAccout`;
    const headers: HeadersInit = {
      'Content-Type': 'application/json',
    };
    
    const token = localStorage.getItem('token');
    if (token) {
      headers['Authorization'] = `Bearer ${token}`;
    }
    
    const options: RequestInit = {
      method: 'POST',
      headers,
      credentials: 'include',
      body: JSON.stringify(accountData),
    };
    
    try {
      const response = await fetch(url, options);
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      return await response.json();
    } catch (error) {
      console.error('插入账户信息失败:', error);
      throw error;
    }
  }

  // 更新账户信息
  async updateAccountInfo(accountData: any) {
    const url = `${this.baseUrl}/updateAccout`;
    const headers: HeadersInit = {
      'Content-Type': 'application/json',
    };
    
    const token = localStorage.getItem('token');
    if (token) {
      headers['Authorization'] = `Bearer ${token}`;
    }
    
    const options: RequestInit = {
      method: 'PUT',
      headers,
      credentials: 'include',
      body: JSON.stringify(accountData),
    };
    
    try {
      const response = await fetch(url, options);
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      return await response.json();
    } catch (error) {
      console.error('更新账户信息失败:', error);
      throw error;
    }
  }

  // 获取所有订单（管理员用）
  async getAllOrders() {
    const url = `${this.baseUrl}/getAllOrder`;
    const headers: HeadersInit = {
      'Content-Type': 'application/json',
    };
    
    const adminToken = localStorage.getItem('adminToken');
    if (adminToken) {
      headers['Authorization'] = `Bearer ${adminToken}`;
    }
    
    const options: RequestInit = {
      method: 'GET',
      headers,
      credentials: 'include',
    };
    
    try {
      const response = await fetch(url, options);
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      return await response.json();
    } catch (error) {
      console.error('获取所有订单失败:', error);
      throw error;
    }
  }





  // 更新订单状态
  async updateOrderStatus(orderId: string, status: string) {
    const url = `${this.baseUrl}/updateOrder`;
    const headers: HeadersInit = {
      'Content-Type': 'application/json',
    };
    
    const adminToken = localStorage.getItem('adminToken');
    if (adminToken) {
      headers['Authorization'] = `Bearer ${adminToken}`;
    }
    
    const requestBody = {
      order_id: Number(orderId),
      status: status
    };
    
    const options: RequestInit = {
      method: 'PUT',
      headers,
      credentials: 'include',
      body: JSON.stringify(requestBody),
    };
    
    try {
      const response = await fetch(url, options);
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      return await response.json();
    } catch (error) {
      console.error('更新订单状态失败:', error);
      throw error;
    }
  }

  // 图片管理API方法
  // 获取图片列表
  async getImageList() {
    const adminToken = localStorage.getItem('adminToken');
    const url = `${this.baseUrl}/getImages`;
    const headers: HeadersInit = {
      'Content-Type': 'application/json',
    };
    
    if (adminToken) {
      headers['Authorization'] = `Bearer ${adminToken}`;
    }
    
    const options: RequestInit = {
      method: 'GET',
      headers,
      credentials: 'include',
    };
    
    try {
      const response = await fetch(url, options);
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      return await response.json();
    } catch (error) {
      console.error('获取图片列表失败:', error);
      throw error;
    }
  }

  // 添加图片
  async insertImage(imageData: any) {
    console.log('添加图片 - 发送的数据:', imageData);
    const adminToken = localStorage.getItem('adminToken');
    const url = `${this.baseUrl}/insertImages`;
    const headers: HeadersInit = {
      'Content-Type': 'application/json',
    };
    
    if (adminToken) {
      headers['Authorization'] = `Bearer ${adminToken}`;
    }
    
    const requestBody = JSON.stringify(imageData);
    console.log('添加图片 - 请求体:', requestBody);
    
    const options: RequestInit = {
      method: 'POST',
      headers,
      credentials: 'include',
      body: requestBody,
    };
    
    try {
      const response = await fetch(url, options);
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      return await response.json();
    } catch (error) {
      console.error('添加图片失败:', error);
      throw error;
    }
  }

  // 更新图片信息
  async updateImage(imageData: any) {
    console.log('更新图片 - 发送的数据:', imageData);
    const adminToken = localStorage.getItem('adminToken');
    const url = `${this.baseUrl}/putImages`;
    const headers: HeadersInit = {
      'Content-Type': 'application/json',
    };
    
    if (adminToken) {
      headers['Authorization'] = `Bearer ${adminToken}`;
    }
    
    const requestBody = JSON.stringify(imageData);
    console.log('更新图片 - 请求体:', requestBody);
    
    const options: RequestInit = {
      method: 'PUT',
      headers,
      credentials: 'include',
      body: requestBody,
    };
    
    try {
      const response = await fetch(url, options);
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      return await response.json();
    } catch (error) {
      console.error('更新图片失败:', error);
      throw error;
    }
  }

  // 获取轮播图图片
  async getCarouselImages() {
    const url = `${this.baseUrl}/getImages`;
    const headers: HeadersInit = {
      'Content-Type': 'application/json',
    };
    
    const options: RequestInit = {
      method: 'GET',
      headers,
      credentials: 'include',
    };
    
    try {
      const response = await fetch(url, options);
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      return await response.json();
    } catch (error) {
      console.error('获取轮播图失败:', error);
      throw error;
    }
  }

  // 收藏功能相关API方法
  // 获取用户收藏列表
  async getUserCollect(userId: number) {
    const url = `${this.baseUrl}/getUserCollect?user_id=${userId}`;
    const headers: HeadersInit = {
      'Content-Type': 'application/json',
    };
    
    const token = localStorage.getItem('token');
    if (token) {
      headers['Authorization'] = `Bearer ${token}`;
    }
    
    const options: RequestInit = {
      method: 'GET',
      headers,
      credentials: 'include',
    };
    
    try {
      const response = await fetch(url, options);
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      return await response.json();
    } catch (error) {
      console.error('获取用户收藏列表失败:', error);
      throw error;
    }
  }

  // 添加收藏
  async insertUserCollect(collectData: {
    user_id: number;
    product_id: number;
    collect_productName: string;
    collect_productPrice: string;
    collect_productImg: string;
    collect_description: string;
  }) {
    const url = `${this.baseUrl}/insertUserCollect`;
    const headers: HeadersInit = {
      'Content-Type': 'application/json',
    };
    
    const token = localStorage.getItem('token');
    if (token) {
      headers['Authorization'] = `Bearer ${token}`;
    }
    
    const options: RequestInit = {
      method: 'POST',
      headers,
      credentials: 'include',
      body: JSON.stringify(collectData),
    };
    
    try {
      const response = await fetch(url, options);
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      return await response.json();
    } catch (error) {
      console.error('添加收藏失败:', error);
      throw error;
    }
  }

  // 删除收藏
  async deleteUserCollect(userId: number, productId: number) {
    const url = `${this.baseUrl}/deleteUserCollect?user_id=${userId}&product_id=${productId}`;
    const headers: HeadersInit = {
      'Content-Type': 'application/json',
    };
    
    const token = localStorage.getItem('token');
    if (token) {
      headers['Authorization'] = `Bearer ${token}`;
    }
    
    const options: RequestInit = {
      method: 'DELETE',
      headers,
      credentials: 'include',
    };
    
    try {
      const response = await fetch(url, options);
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      return await response.json();
    } catch (error) {
      console.error('删除收藏失败:', error);
      throw error;
    }
  }
}

// 创建API服务实例
const apiService = new ApiService('http://localhost:8080/api'); // 假设后端API地址是localhost:8080

export default apiService;