package com.example.demo.mapper.admin;


import com.example.demo.entity.Admin.AdminCategory;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;


@Mapper
public interface AdminCategoryMapper {

    @Select("select * from user_category ")
    List<AdminCategory> selectCategeryAll();

    @Insert("INSERT INTO user_category " +
            "( category_name, categorySon_name, product_count, status, create_time) " +
            "VALUES ( #{category_name}, #{categorySon_name}, #{product_count}, #{status}, #{create_time})")
    Integer insertCategory(AdminCategory adminCategory);

    @Update("update user_category set " +
            "category_name=#{category_name},categorySon_name=#{categorySon_name},product_count=#{product_count}," +
            "status=#{status},create_time=#{create_time}")
   Integer updateCategory(AdminCategory adminCategory);

    @Delete("delete from user_category where category_id=#{category_id}")
    Integer deleteCategory(AdminCategory adminCategory);


}
