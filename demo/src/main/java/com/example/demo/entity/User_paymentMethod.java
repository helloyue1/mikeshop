package com.example.demo.entity;


import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User_paymentMethod {
//    支付id
    @Id
    @TableId(type = IdType.AUTO)
  private Integer  pay_id  ;
//    用户id
  private  Integer  user_id;
//  支付类型（中文）
  private String  type;
//  支付号码
  private String  account_info ;
//  是不是默认值
  private Integer  is_default ;
//  创建时间
    private  String  create_time;
//  更新时间
    private  String  update_time;
}
