package com.example.demo.service;

import com.example.demo.entity.User_Accout;
import com.example.demo.unitls.Response;

public interface UserAccountService {
    Response insertAccountInfoByid(User_Accout user_accout);

    Response selectAccountinfoByid(Integer user_id,Integer  total_asset);

    Response updateAccountInfoByid(User_Accout user_accout);
}
