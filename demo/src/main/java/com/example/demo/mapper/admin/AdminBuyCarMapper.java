package com.example.demo.mapper.admin;


import com.example.demo.entity.Admin.AdminBuyCar;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AdminBuyCarMapper {
    @Insert("insert into user_buycar (user_id, product_id, product_value, product_name, image, price)" +
            "values (#{user_id},#{product_id},#{product_value},#{product_name},#{image},#{price})")
    int  insertAdminBuyCar(AdminBuyCar adminBuyCar);

    @Delete("delete from  user_buycar  where product_id=#{product_id}")
    int deleteAdminBuyCar(Integer product_id);


    @Select("select * from user_buycar where  user_id=#{user_id}")
    List<AdminBuyCar> selectAllAdminBuyCar(Integer user_id);
}
