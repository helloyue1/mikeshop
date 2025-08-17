package com.example.demo.service;

import com.example.demo.entity.User_Collect;
import com.example.demo.unitls.Response;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface UserCollectService {

    Response<List<User_Collect>> selectCollectProductInfo(Integer user_id);



    Response insertCollect(User_Collect user_collect);



    Response deleteCollect(Integer product_id, Integer user_id);
}
