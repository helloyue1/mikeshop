package com.example.demo.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User_Collect {
//    收藏自增id
    private Integer collect_id;
//    用户id
    private Integer user_id;

    private Integer product_id;
//    收藏商品名称
    private String collect_productName;
//    收藏商品价格
    private String collect_productPrice;
//    收藏商品图片地址
    private String collect_productImg;
//    收藏商品描述
    private  String  collect_description;

}
