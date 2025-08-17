package com.example.demo.config;

import jakarta.servlet.http.HttpSession;
import jakarta.websocket.HandshakeResponse;
import jakarta.websocket.server.HandshakeRequest;
import jakarta.websocket.server.ServerEndpointConfig;

public class GetHttpSessionConfig extends ServerEndpointConfig.Configurator {
//重写配置类方法
    @Override
    public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
//             返回类型是对象得强制转换类型
               HttpSession httpSession= (HttpSession) request.getHttpSession();

               System.out.println("这个是"+httpSession);
//将名字存到websocet配置里面，方便onOpen提取
               sec.getUserProperties().put(HttpSession.class.getName(), httpSession);

    }
}
