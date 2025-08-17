package com.example.demo.unitls;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Response<T> {
    private int code;
    private String message;
    private T data;
    private boolean success;
    private String url;
    private Integer order_id;


    public static <T> Response<T> success(T data) {
        Response<T> response = new Response<>();
        response.setCode(200);
        response.setData(data);
        response.setMessage("SUCCESS");
        response.setSuccess(true);
        return response;
    }

    public static <T> Response<T> fail(int  code,String msg) {
        Response<T> response = new Response<>();
        response.setCode(code);
        response.setMessage(msg);
        response.setSuccess(false);
        return response;
    }

    public static <T> Response<T> zifinfyiErro(Integer code,String msg) {
        Response<T> response =new Response<>();
        response.setCode(code);
        response.setMessage(msg);
        return response;
    }
    public static <T> Response<T> Mysuccess(Integer code,String msg) {
        Response<T> response = new Response<>();
        response.setCode(200);
        response.setMessage(msg);
        response.setSuccess(true);
        return response;
    }
    public static <T> Response<T> Loginsuccess(T data,String url) {
        Response<T> response = new Response<>();
        response.setCode(200);
        response.setData(data);
        response.setMessage("SUCCESS");
        response.setSuccess(true);
        response.setUrl(url);
        return response;
    }
    public static <T> Response<T> putOrderSuccess(T data,Integer  order_id) {
        Response<T> response = new Response<>();
        response.setCode(200);
        response.setData(data);
        response.setMessage("SUCCESS");
        response.setSuccess(true);
        response.setOrder_id(order_id);
        return response;
    }


}

