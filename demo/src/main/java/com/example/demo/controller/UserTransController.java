package com.example.demo.controller;

import com.example.demo.entity.User_trans;
import com.example.demo.service.UserTransService;
import com.example.demo.unitls.Response;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserTransController {
    @Autowired
    private UserTransService userTransService;

//    查询信息
    @GetMapping("/getTransInfo")
    public Response  selectTransByUserId(Integer user_id){

        return  userTransService.selectTransInfo(user_id);
    }
//    充值，提现，消费信息存到数据库
    @PostMapping("/insertTransInfo")
    public Response insertTransInfo( @RequestBody@Valid User_trans user_trans){
        return userTransService.insertTransInfo(user_trans);
    }
//    通过交易类型查询交易信息
    @GetMapping("/getTransByType")
    public Response  selectUserTransByType(  String trans_type){
        return userTransService.selectUserTransByType(trans_type);
    }
//     通过交易时间查询交易信息
    @GetMapping("getTransInfoByDate")
    public  Response selectUserTransByDate(  String trans_date){
        return userTransService.selectUserTransByDate(trans_date);
    }
//通过交易时间和类型查询交易信息
    @GetMapping("/getTransInfoByDateAndType")
     public    Response selectUserTransByDateAndType(    String trans_date, String trans_type){
        return userTransService.selectUserTransByDateAndType(trans_date,trans_type);
    }


}
