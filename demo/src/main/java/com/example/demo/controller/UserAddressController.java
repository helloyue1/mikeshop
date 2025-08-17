package com.example.demo.controller;


import com.example.demo.entity.User_address;

import com.example.demo.service.UserAddressService;
import com.example.demo.unitls.Response;
import jakarta.validation.Valid;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api")
public class UserAddressController {


    @Autowired
    private UserAddressService userAddressService;

        @PostMapping("/insertAddress")
     public    Response  insertUserAddress(@Valid @RequestBody User_address user_address){
            System.out.println("CONTROLLER"+user_address);
            return userAddressService.insertUserAddress(user_address);
        }
//获取地址信息
        @GetMapping("/getAddressinfo")
        public Response getAddressinfo(@Valid @RequestParam("user_id")  Integer user_id){

            return userAddressService.getAddressinfo(user_id);
        }

       @DeleteMapping("/deleteAddress")
         public Response deleteAddress(@Valid @RequestParam("user_id") Integer user_id,Integer address_id){

            return userAddressService.deleteUserAddress(user_id,address_id);
        }


        @PutMapping("/updateAddress")
         public  Response   updateUserAddress(@RequestBody@Valid  User_address  user_address)
        {
            return userAddressService.updateUserAddress(user_address);
        }


}
