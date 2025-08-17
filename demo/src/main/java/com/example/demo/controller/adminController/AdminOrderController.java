package com.example.demo.controller.adminController;


import com.example.demo.entity.Admin.AdminOrder;
import com.example.demo.service.AdminOrderService;
import com.example.demo.unitls.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AdminOrderController {
@Autowired
    private AdminOrderService adminOrderService;
//管理员调用的获取订单接口
@GetMapping("getAllOrder")
public Response<List<AdminOrder>> selectAdminOderAll() {
    return adminOrderService.selectAdminOderAll();
}

//用户的获取订单
@GetMapping("getUserOder")
  public   Response<List<AdminOrder>> selectAdminOder(@RequestParam Integer user_id  ){
    return adminOrderService.selectAdminOder(user_id);
  }

//用户
    @PostMapping("insertOder")
  public   Response  insertAdminOrder(@RequestBody  AdminOrder adminOrder){
        System.out.println("插进来的订单信息："+adminOrder);
    return adminOrderService.insertAdminOrder(adminOrder);
  }
//用户和管理员都得用
    @PutMapping("updateOrder")
    Response updateAdminOrder(@RequestBody AdminOrder adminOrder){
    System.out.println("status："+adminOrder.getStatus());
    System.out.println(adminOrder.getOrder_id());
    return adminOrderService.updateAdminOrder(adminOrder);
    }
}
