package com.example.demo.controller;

import com.example.demo.Dto.UserRequest;
import com.example.demo.Dto.UserDTO;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import com.example.demo.unitls.Response;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@CrossOrigin
@RestController
@RequestMapping("/api")
public class UserController {
    @Autowired
    private UserService userService;
//登录
    @PostMapping("/login")
    public Response<UserDTO> login(@Valid @RequestBody UserRequest userRequest, HttpSession session) {

        System.out.println("登录接口 - Session ID: " + session.getId());
        System.out.println("登录接口 - 存入的username: " + userRequest.getUsername());
        session.setAttribute("username", userRequest.getUsername());
        return userService.login( userRequest.getUsername(), userRequest.getPassword());

    }
    // 注册
    @PostMapping("/register")
    public Response userRejister(@Valid @RequestBody UserRequest userRequest){

        return userService.userRejister(userRequest.getUsername(), userRequest.getPassword());
    }


    @PostMapping("/edit")
    public Response userUpdate(@Valid @RequestBody User user)
    {

        return userService.userUpdate(user);

    }


    @GetMapping("AllUser")
    public Response allUser(){
        return userService.userAllInfo();
    }


}
