package com.example.demo.entity.Admin;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminProduct {
//    商品id
    private  Integer   product_id;
//    商品名称
    private String        product_name;
//    商品分类
    private  String        category;
//    商品价格
    private  Integer        price;
//    商品库存
    private   Integer   stock;
//    商品销量
    private   Integer  sales;
    private   String   image;
//    商品状态
    private   String    status;
//    商品描述
    private  String        description;
// 商品规格
    private   String  product_value;
    private   String        create_time;
    private   String     update_time;
    private   String       variants;
}
