package com.example.demo.service;

import com.example.demo.entity.Admin.AdminBuyCar;
import com.example.demo.unitls.Response;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface AdminBuyCarService {
    Response  insertAdminBuyCar(AdminBuyCar adminBuyCar);

    Response deleteAdminBuyCar(Integer product_id);



    Response<List<AdminBuyCar>>  selectAllAdminBuyCar(Integer user_id);
}


