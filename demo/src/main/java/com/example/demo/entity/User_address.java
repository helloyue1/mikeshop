package com.example.demo.entity;


import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
@AllArgsConstructor
@Table(name = "user_address")
@NoArgsConstructor
public class User_address {
//    地址ID
    @TableId(type = IdType.AUTO)
    @Id
    private    Integer  addressid;
//    关联用户ID
    private        Integer   user_id;
//收件人名字
    private                 String   receiver_name;
//    收件人电话
    private                String  receiver_phone;
//    省
    private             String  province;
//    城市
    private               String city ;
//    区/县
    private               String district;
//    详细地址
    private                String detail_address ;
//    邮件编号
    private               Integer  postal_code  ;
//    是不是默认地址
    private                Integer   is_default  ;
//    创建时间
    private              String  create_time;
//    更新时间
    private                  String    update_time;

}
