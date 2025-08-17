package com.example.demo.mapper;


import com.example.demo.entity.User_Collect;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserCollerctMapper {

    @Select("select * from collect where user_id=#{user_id}")
    List<User_Collect>  selectCollectProductInfo(Integer user_id);


    @Insert("insert into collect (product_id,user_id, collect_productName, collect_productPrice, collect_productImg, collect_description) VALUES " +
            "(#{product_id},#{user_id},#{collect_productName},#{collect_productPrice},#{collect_productImg},#{collect_description})")
    int insertCollect(User_Collect user_collect);


    @Delete("delete from collect where product_id=#{product_id} and user_id=#{user_id}")
    int deleteCollect(Integer product_id, Integer user_id);





}
