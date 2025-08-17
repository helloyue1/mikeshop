package com.example.demo.entity.Admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminOrder {
//用户id
    private int user_id;
//    订单号
    private int order_id;

    private int user_info;
//    照片地址
    private String image;
//商品名字
    private String product_name;
//    商品总价
    private String product_allPrice;
//  商品单价
    private String product_price;
//   订单状态（待处理，已确认，已发货。。。。）
    private String status;
//    创建时间
    private String create_time;
//    用户地址
    private String order_address;
//    用户备注
    private String order_userSay;
//    支付方式
    private String pay_method;

}
