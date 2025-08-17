package com.example.demo.controller;


import com.example.demo.entity.User_Accout;
import com.example.demo.service.UserAccountService;
import com.example.demo.unitls.Response;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserAccountController {

    @Autowired
    private UserAccountService userAccountService;

    @PostMapping("/insertAccout")
    public Response insertAccountInfoByid(@Valid@RequestBody User_Accout user_accout){
        return userAccountService.insertAccountInfoByid(user_accout);
    }

    @GetMapping("/selectAccout")
    public Response selectAccountInfoByid(@RequestParam("user_id")   Integer user_id,Integer  total_asset ){
        return userAccountService.selectAccountinfoByid(user_id,total_asset);
    }


    @PutMapping ("/updateAccout")
    public Response updateAccountInfoByid(@Valid@RequestBody User_Accout user_accout){
        return userAccountService.updateAccountInfoByid(user_accout);
    }



}
