<template>
  <div class="chat-room-container">
    <!-- 顶部导航 -->
    <div class="chat-header">
      <el-page-header @back="goBack" content="在线客服聊天" />
      <div class="connection-status" :class="{ 'connected': connected, 'disconnected': !connected }">{{ connectionStatus }}</div>
    </div>

    <!-- 聊天内容区域 -->
    <div class="chat-messages">
      <!-- 用户消息区域将通过v-for动态渲染 -->
      <!-- 用户消息区域将通过v-for动态渲染 -->
      <div v-for="(msg, index) in messages" :key="index" :class="['message', msg.sender]">
        <div class="avatar">{{ msg.avatar }}</div>
        <div class="message-content">
           <div class="message-text">{{ msg.content }}</div>
         </div>
      </div>
    </div>

    <!-- 消息输入区域 -->
    <div class="message-input-area">
      <el-input
        v-model="message"
        placeholder="请输入消息..."
        class="input-field"
        @keyup.enter="sendMessage"
        :disabled="!connected"
      ></el-input>
      <el-button type="primary" class="send-button" @click="sendMessage" :disabled="!connected">发送</el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import apiService from '@/services/apiService.ts'

const router = useRouter();
const message = ref('');
const messages = ref([]);
const ws = ref(null);
const connected = ref(false);
const connectionStatus = ref('未连接');
const username = ref('');
let shouldReconnect = true; // 控制是否应该重连

// 获取当前登录用户信息
const getCurrentUser = async () => {
  console.log('开始获取当前登录用户信息...');
  
  // 首先检查localStorage中的token
  const token = localStorage.getItem('token');
  console.log('localStorage中的token:', token);
  
  // 检查localStorage中的userInfo
  const userInfo = localStorage.getItem('userInfo');
  console.log('localStorage中的userInfo:', userInfo);
  
  // 优先从localStorage获取用户信息
  if (userInfo) {
    try {
      const parsedUserInfo = JSON.parse(userInfo);
      if (parsedUserInfo.username) {
        username.value = parsedUserInfo.username;
        console.log('从localStorage获取到用户名:', username.value);
        return;
      }
    } catch (error) {
      console.error('解析localStorage中的userInfo失败:', error);
    }
  }
  
  // 如果localStorage中没有，尝试从API获取
  try {
    console.log('调用apiService.getUserInfo()...');
    const response = await apiService.getUserInfo();
    console.log('apiService.getUserInfo()响应:', response);
    
    if (response && response.data && response.data.username) {
      username.value = response.data.username;
      console.log('从API获取到当前登录用户:', username.value);
      
      // 将获取到的用户信息存储到localStorage
      const userInfoToStore = {
        id: response.data.id,
        username: response.data.username,
        isLoggedIn: true
      };
      localStorage.setItem('userInfo', JSON.stringify(userInfoToStore));
      console.log('已将用户信息存储到localStorage:', userInfoToStore);
    } else {
      console.log('API响应中没有username字段');
      username.value = 'guest_' + Math.floor(Math.random() * 1000);
      console.log('使用默认用户名:', username.value);
    }
  } catch (error) {
    console.error('获取用户信息失败:', error);
    username.value = 'guest_' + Math.floor(Math.random() * 1000);
    console.log('使用默认用户名:', username.value);
  }
}

// 返回上一页
const goBack = () => {
  shouldReconnect = false; // 停止重连
  closeConnection(); // 关闭连接
  router.back();
};

// 建立WebSocket连接
const connect = () => {
  // 确保用户名已设置
  if (!username.value) {
    console.error('用户名未设置，无法建立WebSocket连接');
    ElMessage.error('用户未登录，无法连接到客服系统');
    return;
  }

  // 连接到后端WebSocket服务，添加用户名为查询参数
  const wsUrl = `ws://localhost:8080/chat?username=${encodeURIComponent(username.value)}`;
  ws.value = new WebSocket(wsUrl);

  ws.value.onopen = () => {
    console.log('WebSocket连接已建立');
    connected.value = true;
    connectionStatus.value = '已连接';
    ElMessage.success('已连接到客服系统');
  };

  ws.value.onmessage = (event) => {
    try {
      // 解析接收到的消息
      const messageData = JSON.parse(event.data);
      
      // 根据后端MessageUtils.getMessage的格式处理消息
      if (messageData.type === 'online') {
        // 在线用户列表更新，可以忽略
      } else if (messageData.system === true || messageData.isSystem === true) {
        // 系统广播消息，不在聊天框中显示
        console.log('收到系统广播消息，不在聊天框中显示:', messageData);
      } else {
        // 添加客服消息
        messages.value.push({
          sender: 'aaa',
          avatar: '👨‍💼',
          content: messageData.content
        });
        scrollToBottom();
      }
    } catch (error) {
      console.error('解析WebSocket消息失败:', error);
    }
  };

  ws.value.onclose = () => {
    console.log('WebSocket连接已关闭');
    connected.value = false;
    connectionStatus.value = '已断开';
    ElMessage.warning('与客服系统的连接已断开');
    // 只有在应该重连时才尝试重连
    if (shouldReconnect) {
      setTimeout(connect, 3000);
    }
  };

  ws.value.onerror = (error) => {
    console.error('WebSocket错误:', error);
    connected.value = false;
    connectionStatus.value = '连接错误';
    ElMessage.error('连接客服系统时发生错误');
  };
};

// 关闭WebSocket连接
const closeConnection = () => {
  if (ws.value && ws.value.readyState === WebSocket.OPEN) {
    ws.value.close();
  }
};

// 发送消息
const sendMessage = async () => {
  if (!message.value.trim()) {
    ElMessage.warning('请输入消息内容');
    return;
  }

  if (!connected.value) {
    ElMessage.warning('未连接到客服系统，请稍后再试');
    return;
  }

  // 确保用户名已设置
  if (!username.value) {
    await getCurrentUser();
  }

  // 构建消息对象，后端需要toName、FromName和message字段
  const messageObj = {
    toName: 'aaa', // 客服用户名是aaa
    FromName: username.value, // 发送者用户名
    message: message.value
  };

  // 发送消息
  ws.value.send(JSON.stringify(messageObj));

  // 添加用户消息
  messages.value.push({
    sender: 'user',
    avatar: '👤',
    content: message.value
  });

  // 清空输入框
  message.value = '';

  // 滚动到底部
  scrollToBottom();
};

// 滚动到消息底部
const scrollToBottom = () => {
  const container = document.querySelector('.chat-messages');
  if (container) {
    container.scrollTop = container.scrollHeight;
  }
};

// 组件挂载时建立连接
onMounted(async () => {
  await getCurrentUser();
  connect();
});

// 组件卸载时关闭连接
onUnmounted(() => {
  shouldReconnect = false; // 停止重连
  closeConnection();
});
</script>

<style scoped>
.chat-room-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: #f5f5f5;
}

.chat-header {
  position: sticky;
  top: 0;
  z-index: 100;
  background-color: #fff;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 16px;
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

.chat-messages {
  flex: 1;
  padding: 16px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.system-message {
  align-self: center;
  padding: 8px 16px;
  background-color: #e9ecef;
  border-radius: 16px;
  font-size: 14px;
  color: #495057;
}

.message {
  display: flex;
  max-width: 70%;
}

.message.aaa {
  align-self: flex-start;
}

.message.user {
  align-self: flex-end;
  flex-direction: row-reverse;
}

.avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background-color: #e9ecef;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  margin-right: 8px;
}

.message.user .avatar {
  margin-right: 0;
  margin-left: 8px;
}

.message-content {
  padding: 12px 16px;
  border-radius: 18px;
  line-height: 1.5;
}

.message.aaa .message-content {
  background-color: #fff;
  border: 1px solid #e9ecef;
}

.message.user .message-content {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.message-input-area {
  display: flex;
  padding: 16px;
  background-color: #fff;
  border-top: 1px solid #e9ecef;
}

.input-field {
  flex: 1;
  margin-right: 12px;
}

.send-button {
  min-width: 100px;
}
</style>