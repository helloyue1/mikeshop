package com.example.demo.service;

import com.example.demo.entity.Admin.AdminOrder;
import com.example.demo.unitls.Response;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface AdminOrderService {

    Response<List<AdminOrder>> selectAdminOderAll();



    Response<List<AdminOrder>> selectAdminOder(Integer user_id  );



    Response  insertAdminOrder(AdminOrder adminOrder);


    Response updateAdminOrder(AdminOrder  adminOrder);

}
