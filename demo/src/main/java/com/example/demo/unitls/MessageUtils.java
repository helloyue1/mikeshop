package com.example.demo.unitls;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageUtils {
    private static final Logger logger = LoggerFactory.getLogger(MessageUtils.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 构建统一格式的消息JSON
     * @param isSystem 是否为系统消息（如在线列表更新）
     * @param toName 接收者用户名（系统消息可为null，群发为"all"）
     * @param content 消息内容（支持任意对象，会序列化为JSON）
     * @return 格式化的消息JSON字符串
     */
    public static String getMessage(boolean isSystem, String toName, Object content,String sendName) {
        try {
            // 构建消息对象
            MessageDTO message = new MessageDTO();
            message.setSystem(isSystem);
            message.setToName(toName);
            message.setContent(content);
            message.setSendName(sendName);

            // 序列化为JSON字符串
            return objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            logger.error("消息序列化失败", e);
            return "{\"error\":\"消息格式错误\"}";
        }
    }

    /**
     * 消息DTO（用于JSON序列化）
     */
    @Data
    private static class MessageDTO {
        private boolean isSystem;      // 是否为系统消息
        private String toName;         // 接收者
        private Object content;
        private String  sendName;
        // 消息内容

    }


}