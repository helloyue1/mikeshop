package com.example.demo.mapper.admin;

import com.example.demo.entity.Admin.AdminProduct;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AdminProductMapper {

        @Select("select * from product  ")
        List<AdminProduct>  selectProduct();


        @Insert("insert into  product (variants, image,product_name, category, price, stock, description, product_value,sales,status, create_time, update_time) " +
                "VALUES (#{variants},#{image},#{product_name},#{category},#{price},#{stock},#{description},#{product_value},#{sales},#{status},#{create_time},#{update_time})")
        int  insertProduct(AdminProduct adminProduct);


        @Update("update product set product_name=#{product_name}," +
                "category=#{category},price=#{price},stock=#{stock}," +
                "description=#{description},product_value=#{product_value},sales=#{sales}," +
                "status=#{status},create_time=#{create_time},update_time=#{update_time},image=#{image},variants=#{variants} " +
                "where product_id=#{product_id}")
        int updateProduct(AdminProduct adminProduct);

        @Delete("delete from product where product_id=#{product_id}")
        int deleteProduct(Integer product_id);


        @Select("select * from product where product_id=#{product_id}")
        List<AdminProduct>  selectProductInfo(Integer product_id);
}
