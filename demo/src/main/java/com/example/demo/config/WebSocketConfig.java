package com.example.demo.config;


import jakarta.websocket.server.ServerEndpointConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

//用于扫描和注册带有@ServerEnpoint注解的类的组件
@Configuration
public class WebSocketConfig {

    @Bean
   public ServerEndpointExporter serverEndpointExporter() {
       return new ServerEndpointExporter();
   }
}
