package com.example.demo.service;

import com.baomidou.mybatisplus.service.IService;
import com.example.demo.entity.Admin.AdminProduct;
import com.example.demo.unitls.Response;

import java.util.List;

public interface AdminProductService {
    Response searchProduct();

    Response  insertProduct(AdminProduct adminProduct);

    Response updateProduct(AdminProduct adminProduct);

    Response deleteProduct(Integer  product_id);

    Response<List<AdminProduct>>  selectProductInfo(Integer product_id);
}
