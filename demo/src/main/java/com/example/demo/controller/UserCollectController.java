package com.example.demo.controller;

import com.example.demo.entity.User_Collect;
import com.example.demo.service.UserCollectService;
import com.example.demo.unitls.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UserCollectController {

@Autowired
    private UserCollectService userCollectService;

@GetMapping("getUserCollect")
public Response<List<User_Collect>> getUserCollect(@RequestParam Integer user_id) {
    return userCollectService.selectCollectProductInfo(user_id);
}
@PostMapping("insertUserCollect")
public Response insertUserCollect(@RequestBody User_Collect user_collect) {
    System.out.println("收藏信息111："+user_collect);
    return userCollectService.insertCollect(user_collect);
}

@DeleteMapping("deleteUserCollect")
public Response deleteUserCollect(@RequestParam Integer product_id, Integer user_id) {
    return userCollectService.deleteCollect(product_id,user_id);
}


}
