package com.example.demo.mapper;

import com.example.demo.entity.User_address;
import com.example.demo.unitls.Response;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;


@Mapper
public interface UserAddressMapper {
//插入地址信息

@Insert("insert into user_address (" +
        " user_id, receiver_name, receiver_phone, province, city, district, " +
        "detail_address, postal_code, is_default, create_time, update_time" +
        ") values (" +
        " #{user_id}, #{receiver_name}, #{receiver_phone}, #{province}, #{city}, #{district}, " +
        "#{detail_address}, #{postal_code}, #{is_default}, " +
        "#{create_time}, " +  // 转换为 MySQL 支持的格式
        "#{update_time}" +
        ")")
int  insertUserAddress(User_address userAddress);


    @Select("select  MAX(addressid)  from user_address where user_id = #{user_id}")
    Integer selectUserAddressId(Integer user_id);

    @Update("update user_address set is_default =0 where is_default=#{is_default}")
    int updateAddressisDefultByaddressid(Integer is_default);

    // 1. 将用户的所有地址设为非默认
    @Update("UPDATE user_address SET is_default = 0 WHERE user_id = #{user_id}")
    int resetAllDefaultByUserId(@Param("user_id") Integer user_id);

    // 2. 将指定地址设为默认
    @Update("UPDATE user_address SET is_default = 1 WHERE addressid = #{addressid} AND user_id = #{user_id}")
    int setDefaultByAddressId(@Param("addressid") Integer addressid, @Param("user_id") Integer user_id);
//获取地址信息
    @Select("select * from user_address where user_id =#{user_id}")
    List<User_address> getAddressinfo(Integer user_id);
//    删除地址信息
    @Delete("delete from user_address where user_id=#{user_id} and addressid=#{addressid} ")
    int deleteUserAddress(Integer user_id, Integer addressid);

    @Update("UPDATE user_address " +
            "  SET  receiver_name = #{receiver_name}, " +
            "    receiver_phone = #{receiver_phone}, " +
            "    province = #{province}, " +
            "    city = #{city}, " +
            "    district = #{district}, " +
            "    detail_address = #{detail_address}, " +
            "    postal_code = #{postal_code}, " +
            "    is_default = #{is_default}, " +
            "    create_time = #{create_time}, " +
            "    update_time = #{update_time} " +
            "WHERE addressid = #{addressid}")  // 必须加 WHERE 条件，否则会更新全表！
    int updateUserAddress(User_address user_address);


}
