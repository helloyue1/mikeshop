package com.example.demo.service;

import com.example.demo.entity.Admin.AdminImage;
import com.example.demo.unitls.Response;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface AdminImageService {

    Response insertImage(AdminImage adminImage);


    Response deleteByPrimaryKey(Integer image_id);



    Response updateByPrimaryKey(AdminImage adminImage);

    Response<List<AdminImage>> selectAllimage();
}
