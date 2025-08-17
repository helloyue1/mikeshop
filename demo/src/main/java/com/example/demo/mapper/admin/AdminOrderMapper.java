package com.example.demo.mapper.admin;

import com.example.demo.entity.Admin.AdminOrder;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
@Mapper
public interface AdminOrderMapper {


        @Select("select * from orders  ")
        List<AdminOrder> selectAdminOderAll();


        @Select("select * from orders where  user_id=#{user_id}")
        List<AdminOrder> selectAdminOder(Integer user_id  );

        @Insert("insert into orders (user_info, user_id, image, product_name, product_allPrice, product_price, status, create_time, order_address, order_userSay, pay_method) VALUES " +
                "(#{user_info},#{user_id},#{image},#{product_name},#{product_allPrice},#{product_price},#{status},#{create_time},#{order_address},#{order_userSay},#{pay_method})")
        int  insertAdminOrder(AdminOrder adminOrder);


        @Update("update orders set status=#{status} where order_id=#{order_id} ")
        Integer updateAdminOrder(AdminOrder adminOrder);

        @Select("select order_id from orders where create_time = (select MAX(create_time) from orders) and user_id=#{user_id}")
        Integer findLatestOrderIds(AdminOrder adminOrder);

}
