package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.entity.User_paymentMethod;
import com.example.demo.service.UserPayMethodService;
import com.example.demo.unitls.Response;
import jakarta.validation.Valid;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserPayMethodController {
    @Autowired
     private   UserPayMethodService userPayMethodService;

    @PostMapping("/insertPayMethod")
    public Response insertUserPayMethod(@RequestBody@Valid User_paymentMethod userPaymentMethod) {
        return  userPayMethodService.insertPayMethod(userPaymentMethod);
    }

    @GetMapping("/selectPayMethod")
    public Response selectUserPayMethodById(User_paymentMethod userPaymentMethod) {
        return userPayMethodService.selectPayMethodByUserId(userPaymentMethod);
    }

    @DeleteMapping("/deletePayMethod")
    public Response deleteUserPayMethodById(User_paymentMethod userPaymentMethod) {
        return  userPayMethodService.deletePayMethod(userPaymentMethod);
    }

    @PutMapping("/putPayMethod")
    public Response putUserPayMethod(@RequestBody@Valid User_paymentMethod userPaymentMethod) {
        return userPayMethodService.updatePayMethod(userPaymentMethod);
    }
}
