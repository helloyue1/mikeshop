package com.example.demo.mapper;


import com.example.demo.entity.User_paymentMethod;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserPayMethodMapper {

//查询支付方式
    @Select("select * from payment_method where user_id=#{user_id}")
    List<User_paymentMethod> selectPayMethodByUserId(User_paymentMethod userPaymentMethod);


    @Select("select MAX(pay_id) from payment_method where user_id=#{user_id}")
    Integer selectPayMethodId(Integer user_id);
//重新设置默认值
    @Update("UPDATE payment_method set is_default =0 where user_id=#{user_id}")
    Integer resetPayMethodIsDfault(Integer user_id);

    @Insert("INSERT INTO payment_method (" +
            "user_id, " +
            "type, " +
            "account_info, " +
            "is_default, " +
            "create_time," +
            "update_time"+
            ") VALUES ("+
            "#{user_id}, " +
            "#{type}, " +
            "#{account_info}, " +
            "#{is_default}, " +
            "#{create_time},"+
            "#{update_time}"+
            ")")
    int insertPayMethod(User_paymentMethod userPaymentMethod);



    @Update("UPDATE payment_method " +
            "SET user_id = #{user_id}, " +
            "    type = #{type}, " +
            "    account_info = #{account_info}, " +
            "    is_default = #{is_default}, " +
            "    update_time = #{update_time} " +  // 移除了对pay_id和create_time的更新
            "WHERE pay_id = #{pay_id}")  // 移除了WHERE前多余的逗号和空格
    int updatePayMethod(User_paymentMethod userPaymentMethod);

    @Delete("delete  from payment_method  where  user_id=#{user_id} and pay_id=#{pay_id}")
    int  deletePayMeythod(User_paymentMethod userPaymentMethod);

}
