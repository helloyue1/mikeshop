<template>
  <div class="customer-service">
    <div class="service-header">
      <h1>客服聊天系统 - 管理员: {{ adminName }}</h1>
      <div class="service-stats">
        <div class="stat-item">
          <span class="stat-number">{{ onlineUsers.length }}</span>
          <span class="stat-label">在线用户</span>
        </div>
        <div class="stat-item">
          <span class="stat-number">{{ waitingChats }}</span>
          <span class="stat-label">等待回复</span>
        </div>
        <div class="stat-item">
          <span class="stat-number">{{ activeChats }}</span>
          <span class="stat-label">进行中</span>
        </div>
      </div>
      <div class="connection-status" :class="{ connected: wsConnected, disconnected: !wsConnected }">
        {{ wsConnected ? '已连接' : '未连接' }}
      </div>
    </div>

    <div class="service-content">
      <!-- 左侧用户列表 -->
      <div class="user-list">
        <div class="list-header">
          <h3>用户列表</h3>
          <div class="search-box">
            <input 
              type="text" 
              v-model="searchKeyword" 
              placeholder="搜索用户..."
              @input="searchUsers"
            >
            <span class="search-icon">🔍</span>
          </div>
        </div>
        
        <div class="user-tabs">
          <button 
            :class="['tab-btn', activeTab === 'all' ? 'active' : '']"
            @click="switchTab('all')"
          >
            全部用户
          </button>
          <button 
            :class="['tab-btn', activeTab === 'waiting' ? 'active' : '']"
            @click="switchTab('waiting')"
          >
            等待回复
          </button>
          <button 
            :class="['tab-btn', activeTab === 'active' ? 'active' : '']"
            @click="switchTab('active')"
          >
            进行中
          </button>
        </div>
        
        <div class="user-items">
          <div 
            v-for="user in filteredUsers" 
            :key="user.username"
            :class="['user-item', selectedUser?.username === user.username ? 'selected' : '']"
            @click="selectUser(user)"
          >
            <div class="user-avatar">{{ user.avatar }}</div>
            <div class="user-info">
              <div class="user-name">{{ user.name }}</div>
              <div class="user-message">{{ user.lastMessage }}</div>
            </div>
            <div class="user-meta">
              <div class="user-time">{{ user.lastTime }}</div>
              <div v-if="user.unreadCount > 0" class="unread-badge">{{ user.unreadCount }}</div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 右侧聊天区域 -->
      <div class="chat-area">
        <div v-if="!selectedUser" class="no-chat-selected">
          <div class="no-chat-icon">💬</div>
          <h3>选择一个用户开始聊天</h3>
          <p>从左侧列表中选择一个用户来查看聊天记录</p>
        </div>
        
        <div v-else class="chat-container">
          <!-- 聊天头部 -->
          <div class="chat-header">
            <div class="chat-user-info">
              <div class="chat-user-avatar">{{ selectedUser.avatar }}</div>
              <div class="chat-user-details">
                <div class="chat-user-name">{{ selectedUser.name }}</div>
                <div class="chat-user-status">{{ selectedUser.status }}</div>
              </div>
            </div>
            <div class="chat-actions">
              <button class="action-btn" @click="viewUserOrder(selectedUser)">
                📋 查看订单
              </button>
              <button class="action-btn" @click="viewUserInfo(selectedUser)">
                👤 用户信息
              </button>
            </div>
          </div>
          
          <!-- 聊天消息区域 -->
          <div class="chat-messages" ref="messagesContainer">
            <div 
              v-for="message in currentMessages" 
              :key="message.id"
              :class="['message', message.type]"
            >
              <div class="message-avatar">{{ message.type === 'sent' ? '👨‍💼' : selectedUser.avatar }}</div>
              <div class="message-content">
                <div class="message-text">{{ message.text }}</div>
                <div class="message-time">{{ message.time }}</div>
              </div>
            </div>
          </div>
          
          <!-- 聊天输入区域 -->
          <div class="chat-input">
            <div class="input-tools">
              <button class="tool-btn" @click="insertEmoji">😊</button>
              <button class="tool-btn" @click="sendQuickReply">📝 快捷回复</button>
              <button class="tool-btn" @click="sendImage">🖼️ 图片</button>
              <button class="tool-btn" @click="sendOrderInfo">📋 订单信息</button>
            </div>
            <div class="input-area">
              <textarea 
                v-model="messageInput"
                placeholder="输入消息..."
                @keydown.enter.prevent="sendMessage"
                @keydown.ctrl.enter.prevent="messageInput += '\n'"
                rows="3"
                class="chat-textarea"
              ></textarea>
              <button 
                class="send-btn" 
                @click="sendMessage"
                :disabled="!messageInput.trim()"
              >
                发送
              </button>
            </div>
          </div>


        </div>
      </div>
    </div>
    
    <!-- 快捷回复弹窗 -->
    <div v-if="showQuickReply" class="modal-overlay" @click="closeQuickReply">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>快捷回复</h3>
          <button class="close-btn" @click="closeQuickReply">×</button>
        </div>
        <div class="quick-reply-list">
          <div 
            v-for="reply in quickReplies" 
            :key="reply.id"
            class="reply-item"
            @click="useQuickReply(reply.text)"
          >
            <div class="reply-title">{{ reply.title }}</div>
            <div class="reply-content">{{ reply.text }}</div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 用户信息弹窗 -->
    <div v-if="showUserInfo" class="modal-overlay" @click="closeUserInfo">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>用户信息</h3>
          <button class="close-btn" @click="closeUserInfo">×</button>
        </div>
        <div class="user-detail-info">
          <div class="info-item">
            <span class="info-label">用户名：</span>
            <span class="info-value">{{ selectedUser?.name }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">用户ID：</span>
            <span class="info-value">{{ selectedUser?.id }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">注册时间：</span>
            <span class="info-value">{{ selectedUser?.registerTime }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">最后登录：</span>
            <span class="info-value">{{ selectedUser?.lastLogin }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">订单数量：</span>
            <span class="info-value">{{ selectedUser?.orderCount }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">消费金额：</span>
            <span class="info-value">¥{{ selectedUser?.totalSpent }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, nextTick, onUnmounted } from 'vue';

// 获取管理员信息
const adminUser = JSON.parse(localStorage.getItem('adminUser') || '{}');
const adminName = ref(adminUser.username || 'ceshi');

// WebSocket相关
const ws = ref(null);
const wsConnected = ref(false);
const reconnectInterval = ref(null);

// 统计数据
const onlineUsers = ref([]);
const waitingChats = ref(0);
const activeChats = ref(0);

// 搜索和筛选
const searchKeyword = ref('');
const activeTab = ref('all');
const selectedUser = ref(null);

// 消息相关
const messageInput = ref('');

const messagesContainer = ref(null);

// 弹窗状态
const showQuickReply = ref(false);
const showUserInfo = ref(false);

// 用户和消息数据
const users = ref([]);
const messages = reactive(new Map());

// 快捷回复模板
const quickReplies = ref([
  { id: 1, title: '订单查询', text: '您好！请问您的订单号是多少？我帮您查询一下。' },
  { id: 2, title: '发货时间', text: '您的订单已经打包完成，预计今天下午发货，明天就能到达。' },
  { id: 3, title: '退款流程', text: '您好！请问是什么原因需要退款呢？我帮您处理。' },
  { id: 4, title: '商品咨询', text: '您好！请问有什么可以帮助您的吗？' },
  { id: 5, title: '投诉处理', text: '很抱歉给您带来不便，我马上为您处理这个问题。' },
  { id: 6, title: '结束语', text: '不客气，如果您还有其他问题，随时联系我们！' }
]);

// 筛选后的用户列表
const filteredUsers = computed(() => {
  let result = users.value;
  
  // 按标签筛选
  if (activeTab.value === 'waiting') {
    result = result.filter(user => user.unreadCount > 0);
  } else if (activeTab.value === 'active') {
    result = result.filter(user => user.status === '在线');
  }
  
  // 按关键词搜索
  if (searchKeyword.value.trim()) {
    const keyword = searchKeyword.value.toLowerCase();
    result = result.filter(user => 
      user.name.toLowerCase().includes(keyword) ||
      user.lastMessage.toLowerCase().includes(keyword)
    );
  }
  
  return result;
});

// 当前用户的消息
const currentMessages = computed(() => {
  if (!selectedUser.value) return [];
  return messages.get(selectedUser.value.username) || [];
});

// WebSocket连接
const connectWebSocket = () => {
  try {
    // 确保管理员用户名已设置
    if (!adminName.value) {
      console.error('管理员用户名未设置，无法建立WebSocket连接');
      return;
    }
    // 在URL中携带管理员用户名
    const wsUrl = `ws://localhost:8080/chat?username=${encodeURIComponent(adminName.value)}`;
    ws.value = new WebSocket(wsUrl);
    
    ws.value.onopen = () => {
      console.log('客服WebSocket连接已建立');
      wsConnected.value = true;
      if (reconnectInterval.value) {
        clearInterval(reconnectInterval.value);
        reconnectInterval.value = null;
      }
    };
    
    ws.value.onmessage = (event) => {
      const data = JSON.parse(event.data);
      handleMessage(data);
    };
    
    ws.value.onclose = () => {
      console.log('客服WebSocket连接已关闭');
      wsConnected.value = false;
      // 自动重连
      if (!reconnectInterval.value) {
        reconnectInterval.value = setInterval(() => {
          connectWebSocket();
        }, 3000);
      }
    };
    
    ws.value.onerror = (error) => {
      console.error('客服WebSocket错误:', error);
      wsConnected.value = false;
    };
  } catch (error) {
    console.error('创建WebSocket连接失败:', error);
  }
};

// 处理接收到的消息
const handleMessage = (data) => {
  // 增加日志以便调试
  console.log('接收到WebSocket消息:', data);

  // 处理后端MessageUtils构建的消息格式
  // 后端格式: {isSystem: boolean, toName: string, content: object}
  
  // 如果是系统消息（isSystem为true或system为true）
  if (data.isSystem === true || data.system === true) {
    // 处理系统消息，如在线用户列表更新
    if (data.content) {
      // 如果content是数组，直接作为在线用户列表
      if (Array.isArray(data.content)) {
        updateOnlineUsers(data.content);
      }
      // 如果content是对象，检查是否有users字段
      else if (typeof data.content === 'object' && data.content.users) {
        // 更新在线用户列表
        updateOnlineUsers(data.content.users);
      }
    }
    return;
  }
  
  // 处理用户消息
  if (data.content !== undefined) {
    // 检查消息是否是发送给当前管理员的
    if (data.toName && data.toName !== adminName.value) {
      console.log('这是发送给其他用户的消息，跳过处理:', data);
      return;
    }
    
    // 获取消息内容
    let messageContent = data.content;
    let fromUser = null;
    
    // 优先检查sendName字段（新格式）
    if (data.sendName) {
      fromUser = data.sendName;
    }
    // 如果没有sendName，再检查content中的发送者信息
    else if (typeof messageContent === 'string') {
      // 尝试解析是否为JSON格式（包含发送者信息）
      try {
        const parsedContent = JSON.parse(messageContent);
        if (parsedContent.from || parsedContent.fromName) {
          fromUser = parsedContent.from || parsedContent.fromName;
          messageContent = parsedContent.message || parsedContent.content || messageContent;
        }
      } catch (e) {
        // 不是JSON格式，保持原样
      }
    } 
    // 如果content是对象，尝试从中获取发送者信息
    else if (typeof messageContent === 'object' && messageContent !== null) {
      fromUser = messageContent.from || messageContent.fromName || messageContent.session;
      messageContent = messageContent.message || messageContent.content || JSON.stringify(messageContent);
    }
    
    // 如果仍然没有发送者信息，但有toName是当前管理员，这可能是其他用户发送的消息
    if (!fromUser && data.toName === adminName.value) {
      // 这种情况下，我们无法确定发送者，需要后端提供发送者信息
      console.error('消息缺少发送者信息，无法确定是谁发送的:', data);
      return;
    }
    
    // 验证消息有效性
    if (!fromUser || !messageContent) {
      console.error('无效的消息格式:', data);
      return;
    }
    
    // 确保消息内容是字符串
    const messageText = typeof messageContent === 'string' ? messageContent : JSON.stringify(messageContent);
    
    // 确保用户存在
    let user = users.value.find(u => u.username === fromUser);
    if (!user) {
      user = {
        id: Date.now(),
        username: fromUser,
        name: fromUser,
        avatar: '👤',
        status: '在线',
        lastMessage: messageText,
        lastTime: new Date().toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' }),
        unreadCount: 0
      };
      users.value.push(user);
    }
    
    // 更新最后消息
    user.lastMessage = messageText;
    user.lastTime = new Date().toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' });
    
    // 添加消息到对话
    if (!messages.has(fromUser)) {
      messages.set(fromUser, []);
    }
    
    const newMessage = {
      id: Date.now(),
      type: 'received',
      text: messageText,
      time: new Date().toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
    };
    
    messages.get(fromUser).push(newMessage);
    
    // 如果当前选中的用户不是发送者，增加未读计数
    if (!selectedUser.value || selectedUser.value.username !== fromUser) {
      user.unreadCount = (user.unreadCount || 0) + 1;
    }
    
    // 滚动到底部
    nextTick(() => {
      scrollToBottom();
    });
  } else {
    console.log('未知消息格式，跳过处理:', data);
  }
};

// 更新在线用户
const updateOnlineUsers = (onlineUsernames) => {
  console.log('更新在线用户列表:', onlineUsernames);
  
  // 更新现有用户的在线状态
  users.value.forEach(user => {
    // 如果用户名在在线列表中，或者用户是ceshi，则设为在线
    if (onlineUsernames.includes(user.username) || user.username === 'ceshi') {
      user.status = '在线';
    } 
    // 如果用户不在在线列表中，且不是aaa和ceshi，则设为离线
    else if (user.username !== 'aaa' && user.username !== 'ceshi') {
      user.status = '离线';
    }
  });
  
  // 移除不在线的用户（除了aaa和ceshi）
  users.value = users.value.filter(user => 
    onlineUsernames.includes(user.username) || user.username === 'aaa' || user.username === 'ceshi'
  );
  
  // 添加新在线用户（除了ceshi，因为ceshi会特殊处理）
  onlineUsernames.forEach(username => {
    // 如果用户不存在且不是ceshi，则添加
    if (username !== 'ceshi' && !users.value.find(u => u.username === username)) {
      users.value.push({
        id: Date.now() + Math.random(),
        username: username,
        name: username,
        avatar: '👤',
        status: '在线',
        lastMessage: '开始聊天...',
        lastTime: new Date().toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' }),
        unreadCount: 0
      });
    }
  });
  
  // 确保ceshi用户始终存在且在线
  const ceshiUser = users.value.find(u => u.username === 'ceshi');
  if (!ceshiUser) {
    users.value.push({
      id: Date.now() + Math.random(),
      username: 'ceshi',
      name: 'ceshi',
      avatar: '👤',
      status: '在线',
      lastMessage: '开始聊天...',
      lastTime: new Date().toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' }),
      unreadCount: 0
    });
  } else {
    ceshiUser.status = '在线';
  }
  
  // 更新在线用户列表（排除aaa）
  onlineUsers.value = users.value.filter(u => u.username !== 'aaa');
  
  console.log('更新后的用户列表:', users.value.map(u => ({ username: u.username, status: u.status })));
};

// 切换标签
const switchTab = (tab) => {
  activeTab.value = tab;
};

// 搜索用户
const searchUsers = () => {
  // 搜索逻辑已经在computed中实现
};

// 选择用户
const selectUser = (user) => {
  selectedUser.value = user;
  // 清除未读消息
  user.unreadCount = 0;
  // 滚动到底部
  nextTick(() => {
    scrollToBottom();
  });
};

// 发送消息
const sendMessage = () => {
  if (!messageInput.value.trim() || !selectedUser.value || !wsConnected.value) return;
  
  const newMessage = {
    id: Date.now(),
    type: 'sent',
    text: messageInput.value,
    time: new Date().toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
  };
  
  // 发送到后端
  const messageObj = {
    toName: selectedUser.value.username,
    FromName: adminName.value,
    message: messageInput.value,
    isSystem: false
  };
  
  console.log('发送消息:', messageObj);
  ws.value.send(JSON.stringify(messageObj));
  
  // 立即添加到本地消息显示
  if (!messages.has(selectedUser.value.username)) {
    messages.set(selectedUser.value.username, []);
  }
  messages.get(selectedUser.value.username).push(newMessage);
  
  // 更新用户最后消息
  selectedUser.value.lastMessage = messageInput.value;
  selectedUser.value.lastTime = newMessage.time;
  
  messageInput.value = '';
  
  // 滚动到底部
  nextTick(() => {
    scrollToBottom();
  });
};

// 滚动到底部
const scrollToBottom = () => {
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight;
  }
};

// 其他功能
const insertEmoji = () => {
  messageInput.value += ' 😊 ';
};





const sendQuickReply = () => {
  showQuickReply.value = true;
};

const useQuickReply = (text) => {
  messageInput.value = text;
  showQuickReply.value = false;
};

const closeQuickReply = () => {
  showQuickReply.value = false;
};

const sendImage = () => {
  alert('图片发送功能开发中...');
};

const sendOrderInfo = () => {
  if (selectedUser.value) {
    messageInput.value = '您的订单信息如下：\n订单号：ORD001\n商品：iPhone 15 Pro\n金额：¥8999\n状态：待发货';
  }
};

const viewUserOrder = (user) => {
  alert(`查看用户 ${user.name} 的订单`);
};

const viewUserInfo = (user) => {
  selectedUser.value = user;
  showUserInfo.value = true;
};

const closeUserInfo = () => {
  showUserInfo.value = false;
};

// 页面加载时初始化
onMounted(() => {
  connectWebSocket();
  
  // 确保ceshi用户在初始化时就存在
  if (!users.value.find(u => u.username === 'ceshi')) {
    users.value.push({
      id: Date.now() + Math.random(),
      username: 'ceshi',
      name: 'ceshi',
      avatar: '👤',
      status: '在线',
      lastMessage: '开始聊天...',
      lastTime: new Date().toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' }),
      unreadCount: 0
    });
  }
});

// 页面卸载时清理
onUnmounted(() => {
  if (ws.value) {
    ws.value.close();
  }
  if (reconnectInterval.value) {
    clearInterval(reconnectInterval.value);
  }
});
</script>

<style scoped>
.customer-service {
  height: 100vh;
  background-color: #f5f5f5;
  display: flex;
  flex-direction: column;
}

.service-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.service-header h1 {
  margin: 0;
  font-size: 24px;
}

.service-stats {
  display: flex;
  gap: 30px;
}

.stat-item {
  text-align: center;
}

.stat-number {
  display: block;
  font-size: 24px;
  font-weight: bold;
}

.stat-label {
  font-size: 12px;
  opacity: 0.9;
}

.connection-status {
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 14px;
}

.connection-status.connected {
  background-color: #e6f7ee;
  color: #52c41a;
}

.connection-status.disconnected {
  background-color: #fff1f0;
  color: #f5222d;
}

.service-content {
  flex: 1;
  display: flex;
  overflow: hidden;
}

.user-list {
  width: 350px;
  background-color: white;
  border-right: 1px solid #e0e0e0;
  display: flex;
  flex-direction: column;
}

.list-header {
  padding: 20px;
  border-bottom: 1px solid #e0e0e0;
}

.list-header h3 {
  margin: 0 0 15px 0;
  font-size: 18px;
}

.search-box {
  position: relative;
}

.search-box input {
  width: 100%;
  padding: 10px 40px 10px 15px;
  border: 1px solid #ddd;
  border-radius: 20px;
  font-size: 14px;
}

.search-icon {
  position: absolute;
  right: 15px;
  top: 50%;
  transform: translateY(-50%);
}

.user-tabs {
  display: flex;
  border-bottom: 1px solid #e0e0e0;
}

.tab-btn {
  flex: 1;
  padding: 12px;
  border: none;
  background: none;
  cursor: pointer;
  font-size: 14px;
  border-bottom: 2px solid transparent;
}

.tab-btn.active {
  color: #667eea;
  border-bottom-color: #667eea;
}

.user-items {
  flex: 1;
  overflow-y: auto;
}

.user-item {
  display: flex;
  align-items: center;
  padding: 15px;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: background-color 0.2s;
}

.user-item:hover {
  background-color: #f8f9fa;
}

.user-item.selected {
  background-color: #e3f2fd;
}

.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background-color: #f0f0f0;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  margin-right: 12px;
}

.user-info {
  flex: 1;
}

.user-name {
  font-weight: bold;
  margin-bottom: 4px;
}

.user-message {
  font-size: 12px;
  color: #666;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 180px;
}

.user-meta {
  text-align: right;
}

.user-time {
  font-size: 11px;
  color: #999;
  margin-bottom: 4px;
}

.unread-badge {
  background-color: #ff4757;
  color: white;
  border-radius: 10px;
  padding: 2px 6px;
  font-size: 11px;
  min-width: 18px;
  text-align: center;
}

.chat-area {
  flex: 1;
  display: flex;
  flex-direction: column;
  background-color: white;
}

.no-chat-selected {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #666;
}

.no-chat-icon {
  font-size: 64px;
  margin-bottom: 20px;
  opacity: 0.5;
}

.chat-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
}

.chat-header {
  padding: 20px;
  border-bottom: 1px solid #e0e0e0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chat-user-info {
  display: flex;
  align-items: center;
}

.chat-user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background-color: #f0f0f0;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  margin-right: 12px;
}

.chat-user-details {
  flex: 1;
}

.chat-user-name {
  font-weight: bold;
  margin-bottom: 4px;
}

.chat-user-status {
  font-size: 12px;
  color: #666;
}

.chat-actions {
  display: flex;
  gap: 10px;
}

.action-btn {
  padding: 8px 12px;
  border: 1px solid #ddd;
  background: white;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
}

.action-btn:hover {
  background-color: #f8f9fa;
}

.chat-messages {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 16px;
  min-height: 0;
}

.message {
  display: flex;
  max-width: 70%;
  align-items: flex-start;
}

.message.sent {
  align-self: flex-end;
  flex-direction: row-reverse;
}

.message.received {
  align-self: flex-start;
}

.message-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background-color: #f0f0f0;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  margin-right: 8px;
}

.message.sent .message-avatar {
  margin-right: 0;
  margin-left: 8px;
}

.message-content {
  padding: 12px 16px;
  border-radius: 18px;
  line-height: 1.5;
  max-width: calc(100% - 60px);
}

.message.sent .message-content {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.message.received .message-content {
  background-color: #fff;
  border: 1px solid #e9ecef;
}

.message-time {
  font-size: 11px;
  color: #999;
  margin-top: 4px;
}

.chat-input {
  padding: 20px;
  border-top: 1px solid #e0e0e0;
  background-color: white;
}

.input-tools {
  display: flex;
  gap: 10px;
  margin-bottom: 10px;
}

.tool-btn {
  padding: 8px 12px;
  border: 1px solid #ddd;
  background: white;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
}

.tool-btn:hover {
  background-color: #f8f9fa;
}

.input-area {
  display: flex;
  gap: 10px;
}

.input-area textarea {
  flex: 1;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  resize: none;
  font-family: inherit;
  font-size: 14px;
  min-height: 80px;
  max-height: 120px;
}

.chat-textarea {
  box-shadow: 0 0 10px rgba(0,0,0,0.05);
  transition: all 0.3s ease;
}

.chat-textarea:focus {
  border-color: #667eea;
  outline: none;
  box-shadow: 0 0 10px rgba(102, 126, 234, 0.2);
}

/* ceshi聊天框样式 */
.ceshi-chat {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #e0e0e0;
}

.ceshi-chat h4 {
  margin-bottom: 15px;
  color: #667eea;
  font-size: 16px;
}

.send-btn {
  padding: 10px 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.send-btn:hover:not(:disabled) {
  opacity: 0.9;
}

.send-btn:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

.modal-overlay {
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
  background: white;
  border-radius: 8px;
  width: 90%;
  max-width: 500px;
  max-height: 80vh;
  overflow-y: auto;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #e0e0e0;
}

.modal-header h3 {
  margin: 0;
}

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #666;
}

.quick-reply-list {
  padding: 20px;
}

.reply-item {
  padding: 15px;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
}

.reply-item:hover {
  background-color: #f8f9fa;
}

.reply-title {
  font-weight: bold;
  margin-bottom: 5px;
}

.reply-content {
  font-size: 14px;
  color: #666;
}

.user-detail-info {
  padding: 20px;
}

.info-item {
  display: flex;
  justify-content: space-between;
  padding: 10px 0;
  border-bottom: 1px solid #f0f0f0;
}

.info-label {
  font-weight: bold;
}

.info-value {
  color: #666;
}
</style>