package com.example.demo.entity;


import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User_trans {
    @Id
  private Integer   trans_id ;

  private Integer  user_id;
//交易时间
  private String  trans_date;
//交易类型（充值，提现，消费）
  private String  trans_type;
//交易描述
  private String  description;
//交易金额
  private String  amount;
//交易状态
  private String  status;

}
