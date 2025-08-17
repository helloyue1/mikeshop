package com.example.demo.entity;


import com.alibaba.fastjson.JSON;
import com.example.demo.config.GetHttpSessionConfig;

import com.example.demo.unitls.MessageUtils;
import jakarta.servlet.http.HttpSession;
import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


import java.io.IOException;
import java.util.HashSet;
import java.util.Map;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;


@Component
@ServerEndpoint(value = "/chat", configurator = GetHttpSessionConfig.class)
public class ChatEndpoint {
//    创建日志
    private static final Logger logger= LoggerFactory.getLogger(ChatEndpoint.class);


    private Session session;
//    创建在线用户，key是用户名字，value是session对象
    private static final Map<String, Session> OnlineUsers= new ConcurrentHashMap<>();
//创建离线信息
    private static final Map<String, Set<Message>> offlineMessage= new ConcurrentHashMap<>();

    private HttpSession httpSession;

    private  String username ;

    @OnOpen
    public void onOpen(Session session, EndpointConfig config) {

        System.out.println("到Onoppen了");
        try {

            this.session = session;

//        通过 WebSocket 配置（config），从提前存入的用户属性中，提取出当前用户的 HTTP 会话（HttpSession），
//        目的是关联用户的登录状态，后续用于获取用户名（currentUser）、判断用户是否登录等操作。
        this.httpSession= (HttpSession) config.getUserProperties().get(HttpSession.class.getName());
//        从httpsession中提取在线用户
        this.username= (String) this.httpSession.getAttribute("username" );

//如果session里面没有在线用户提示
        if (username==null){
            logger.error("匿名用户正在尝试链接WebSocket,已拒绝");
            session.close(new CloseReason(CloseReason.CloseCodes.getCloseCode(1008),"未登录用户不允许链接"));
        }
//如果在线用户在线，关闭之前的链接
            if (OnlineUsers.containsKey(username)){
                Session existingUser = OnlineUsers.get(username);
                if (existingUser.isOpen()){
                    existingUser.close(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE,"用户在其他地方登录"));
                }
                OnlineUsers.remove(username);
            }

            OnlineUsers.put(username, session);
            logger.info("用户[{}]已经上线，当前在线人数{}",username,OnlineUsers.size());

//            发送离线消息
            sendOfflineMessages();

            String message= MessageUtils.getMessage(true,null,getNames(),null);
            broadcastAllUser(message);

        }catch (Exception e){
            logger.error("WebSocket链接失败"+e.getMessage(),e);
        }
    }
    private Set<String> getNames() {
        return OnlineUsers.keySet();
    }

//广播发送给所有在线用户
    private void broadcastAllUser(String message){
        System.out.println("广播发的消息"+message);
        if (message==null || message.isEmpty()){
            logger.warn("尝试发送空消息给所有用户");
        }
try {

//遍历整个地图集合
    for (Map.Entry<String, Session> entry : OnlineUsers.entrySet()){
        Session session = entry.getValue();
        if (session.isOpen()){
//            异步发送消息
            session.getAsyncRemote().sendText(message);
        }else{
            logger.warn("用户{}的会话已关闭",entry.getKey());
        }
    }
}catch (Exception e){
    logger.error("广播失败"+e.getMessage(),e);
}
    }

@OnMessage
    public void onMessage(String message){
    System.out.println("到OnMessage了");

        if (message==null || message.isEmpty()){
            logger.warn("消息格式不对");
            return ;
        }
try {


    Message msg = JSON.parseObject(message, Message.class);
    String ToName = msg.getToName();
    String content = msg.getMessage();
    String  sender = msg.getFromName();

    if (ToName == null || ToName.isEmpty() || content == null || content.isEmpty()) {
        logger.warn("消息格式不正确: {}", message);
        return;
    }
        System.out.println("获取的发给谁名字"+ToName);
        System.out.println("获取的发de信息"+content);
    Session session = OnlineUsers.get(ToName);
    System.out.println("获取的Session"+content);
    String messageToSend = MessageUtils.getMessage(false, ToName, content,sender);
    System.out.println("获取的messageToSend"+messageToSend);

    if (session != null && session.isOpen()) {
        session.getAsyncRemote().sendText(messageToSend);

    } else {
        saveOfflineMessage(msg);
        logger.info("用户[{}]不在线，已保存离线信息", username);
    }
}catch (Exception e){
    logger.info(e.getMessage(),e);
}
    }
//存储离线消息
// computeIfAbsent(key, k -> 新集合)：如果 key 不存在，会先执行第二个参数的逻辑（创建一个线程安全的 Set），并将 key 和新集合存入 Map；如果 key 已存在，直接返回对应的 Set。
//之后调用 .add(message) 时，Set 一定非空，避免空指针异常。
private void  saveOfflineMessage(Message message){
        offlineMessage.computeIfAbsent(message.getToName(), k -> ConcurrentHashMap.newKeySet()).add(message);
    }

    private void sendOfflineMessages() {
        Set<Message> messages = offlineMessage.remove(username);
        if (messages != null && !messages.isEmpty()) {
            try {
                for (Message msg : messages) {
                    String messageToSend = MessageUtils.getMessage(false, msg.getFromName(), msg.getMessage(),msg.getToName());
                    Session session = OnlineUsers.get(username);
                    if (session != null && session.isOpen()) {
                        session.getBasicRemote().sendText(messageToSend);
                        // 简单的限流，避免消息发送过快
                        TimeUnit.MILLISECONDS.sleep(10);
                    }
                }
                logger.info("已向用户[{}]发送{}条离线消息", username, messages.size());
            } catch (Exception e) {
                logger.error("发送离线消息失败", e);
            }
        }
    }

    /**
     * 断开 websocket 连接时被调用
     */
    @OnClose
    public void onClose(Session session, CloseReason reason) {
        System.out.println("到Onclose了");
        try {
            if (username != null) {
                Session removedSession = OnlineUsers.remove(username);
                if (removedSession != null && removedSession.isOpen()) {
                    removedSession.close();
                }
                logger.info("用户[{}]已下线，原因: {}, 当前在线人数: {}",
                        username, reason.getReasonPhrase(), OnlineUsers.size());

                // 通知其他用户，更新在线列表
                String message = MessageUtils.getMessage(true, null, getNames(),null);
                broadcastAllUser(message);
            }
        } catch (Exception e) {
            logger.error("处理WebSocket关闭事件失败", e);
        }
    }

    /**
     * 处理WebSocket错误
     */
    @OnError
    public void onError(Session session, Throwable error) {
        logger.error("用户[{}]的WebSocket连接发生错误", username, error);

        // 发生严重错误时关闭连接
        if (session.isOpen()) {
            try {

                session.close(new CloseReason(CloseReason.CloseCodes.UNEXPECTED_CONDITION,
                        "发生错误: " + error.getMessage()));
            } catch (IOException e) {
                logger.error("关闭错误连接失败", e);
            }
        }
    }


}
