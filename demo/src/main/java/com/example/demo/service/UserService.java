package com.example.demo.service;



import com.example.demo.entity.User;
import com.example.demo.entity.User_address;
import com.example.demo.unitls.Response;

public interface UserService {


    Response login(String username, String password);

    Response userRejister(String username, String password);



    //更新信息
    Response userUpdate(User user);

    Response userAllInfo();
}
