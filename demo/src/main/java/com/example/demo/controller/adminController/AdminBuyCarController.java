package com.example.demo.controller.adminController;


import com.example.demo.entity.Admin.AdminBuyCar;
import com.example.demo.service.AdminBuyCarService;
import com.example.demo.unitls.Response;
import org.apache.ibatis.annotations.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AdminBuyCarController {
@Autowired
    private AdminBuyCarService adminBuyCarService;

@PostMapping("insertBuyCar")
public Response  insertBuyCar(@RequestBody  AdminBuyCar adminBuyCar) {
    System.out.println("购物车："+adminBuyCar);
    return adminBuyCarService.insertAdminBuyCar(adminBuyCar);
}


@GetMapping("getBuyCar")
public   Response<List<AdminBuyCar>>   selectAllBuyCar(@RequestParam Integer user_id) {
    return adminBuyCarService.selectAllAdminBuyCar(user_id);
}

@DeleteMapping("deleteBuyCar")
public Response  deleteBuyCar(@RequestParam  Integer  product_id) {
    return adminBuyCarService.deleteAdminBuyCar(product_id);
}


}
