package com.example.demo.entity;


import lombok.Data;


@Data
public class Message {

//    接受者名字
    private String toName;
//发送者名字
    private String fromName;
//    发送的消息
    private String message;



}
