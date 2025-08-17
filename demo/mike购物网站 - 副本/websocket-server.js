import { WebSocketServer } from 'ws';
import http from 'http';
import url from 'url';

// 创建HTTP服务器
const server = http.createServer();

// 创建WebSocket服务器
const wss = new WebSocketServer({ server });

// 存储在线用户
const onlineUsers = new Map();

// 存储离线消息
const offlineMessages = new Map();

// 处理WebSocket连接
wss.on('connection', (ws, req) => {
  // 解析URL获取用户名
  const query = url.parse(req.url, true).query;
  const username = query.username;
  
  if (!username) {
    ws.close(1008, '未登录用户不允许连接');
    return;
  }
  
  console.log(`用户[${username}]已经上线，当前在线人数${onlineUsers.size + 1}`);
  
  // 如果用户已经在线，关闭之前的连接
  if (onlineUsers.has(username)) {
    const existingUser = onlineUsers.get(username);
    if (existingUser.readyState === 1) { // WebSocket.OPEN
      existingUser.close(1000, '用户在其他地方登录');
    }
    onlineUsers.delete(username);
  }
  
  // 添加新用户
  onlineUsers.set(username, ws);
  
  // 发送离线消息
  if (offlineMessages.has(username)) {
    const messages = offlineMessages.get(username);
    messages.forEach(message => {
      ws.send(JSON.stringify(message));
    });
    offlineMessages.delete(username);
  }
  
  // 广播在线用户列表
  broadcastOnlineUsers();
  
  // 处理消息
  ws.on('message', (message) => {
    try {
      const data = JSON.parse(message);
      handleMessage(data, username);
    } catch (error) {
      console.error('解析消息失败:', error);
    }
  });
  
  // 处理连接关闭
  ws.on('close', () => {
    console.log(`用户[${username}]已经下线`);
    onlineUsers.delete(username);
    broadcastOnlineUsers();
  });
  
  // 处理错误
  ws.on('error', (error) => {
    console.error(`用户[${username}]的WebSocket连接发生错误:`, error);
    onlineUsers.delete(username);
    broadcastOnlineUsers();
  });
});

// 处理消息
function handleMessage(data, fromUsername) {
  // 处理后端MessageUtils构建的消息格式
  // 后端格式: {isSystem: boolean, toName: string, content: object}
  
  if (data.isSystem) {
    // 系统消息，广播给所有用户
    broadcastMessage(data);
  } else if (data.toName) {
    // 私聊消息
    const targetUser = onlineUsers.get(data.toName);
    if (targetUser) {
      targetUser.send(JSON.stringify(data));
    } else {
      // 用户不在线，存储离线消息
      if (!offlineMessages.has(data.toName)) {
        offlineMessages.set(data.toName, []);
      }
      offlineMessages.get(data.toName).push(data);
    }
  } else {
    // 广播消息
    broadcastMessage(data);
  }
}

// 广播消息给所有用户
function broadcastMessage(message) {
  onlineUsers.forEach((ws) => {
    if (ws.readyState === 1) { // WebSocket.OPEN
      ws.send(JSON.stringify(message));
    }
  });
}

// 广播在线用户列表
function broadcastOnlineUsers() {
  const userList = Array.from(onlineUsers.keys());
  const message = {
    isSystem: true,
    toName: null,
    content: userList
  };
  broadcastMessage(message);
}

// 启动服务器
const PORT = 8080;
server.listen(PORT, () => {
  console.log(`WebSocket服务器已启动，监听端口 ${PORT}`);
  console.log(`WebSocket连接地址: ws://localhost:${PORT}/chat`);
});

// 处理服务器错误
server.on('error', (error) => {
  console.error('服务器错误:', error);
});