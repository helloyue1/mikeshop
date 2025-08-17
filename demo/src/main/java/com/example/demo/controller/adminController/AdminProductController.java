package com.example.demo.controller.adminController;


import com.example.demo.entity.Admin.AdminProduct;
import com.example.demo.service.AdminProductService;
import com.example.demo.unitls.Response;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AdminProductController {

    @Autowired
    private AdminProductService adminProductService;

    @GetMapping("getProductList")
    public Response getProductList(){
        return adminProductService.searchProduct();
    }

    @PutMapping("updateProduct")
    public Response updateProduct(@RequestBody@Valid  AdminProduct adminProduct){
        System.out.println("更新的信息："+adminProduct);
        return adminProductService.updateProduct(adminProduct);
    }

    @DeleteMapping("deleteProduct")
    public Response deleteProduct(@RequestParam Integer  product_id){
        System.out.println("Controller："+product_id);
        return adminProductService.deleteProduct(product_id);
    }

    @PostMapping("insertProduct")
    public  Response<List<AdminProduct>> insertProduct(@RequestBody@Valid AdminProduct adminProduct){
        return adminProductService.insertProduct(adminProduct);
    }

    @GetMapping("getProductInfo")
    public Response<List<AdminProduct>> getProductInfo(@RequestParam Integer product_id){
        return adminProductService.selectProductInfo(product_id);
    }

}
