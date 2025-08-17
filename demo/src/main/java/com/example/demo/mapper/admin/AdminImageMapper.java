package com.example.demo.mapper.admin;


import com.example.demo.entity.Admin.AdminImage;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AdminImageMapper {
    @Insert("insert into image (image_url, image_category,   create_time) VALUES " +
            "(#{image_url},#{image_category},#{create_time})")
    Integer insertImage(AdminImage adminImage);

    @Delete("delete from image where image_id=#{image_id}")
    Integer deleteByPrimaryKey(Integer image_id);


    @Update("update image set image_url=#{image_url}," +
            "image_category=#{image_category}" +
            "create_time=#{create_time}")
    Integer updateByPrimaryKey(AdminImage adminImage);


    @Select("select * from image ")
    List<AdminImage>  selectAllimage();
}
