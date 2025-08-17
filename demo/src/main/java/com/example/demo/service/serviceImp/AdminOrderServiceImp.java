package com.example.demo.service.serviceImp;

import com.example.demo.entity.Admin.AdminOrder;
import com.example.demo.mapper.admin.AdminOrderMapper;
import com.example.demo.service.AdminOrderService;
import com.example.demo.unitls.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class AdminOrderServiceImp implements AdminOrderService {

    private static final Logger log = LoggerFactory.getLogger(AdminOrderServiceImp.class);
    @Autowired
    private AdminOrderMapper adminOrderMapper;


    @Override
    public Response<List<AdminOrder>> selectAdminOderAll() {
        try {
               List  info    = adminOrderMapper.selectAdminOderAll();
               if(info!=null&&  info.size()>0){
                   return Response.success(info);
               }else {
                   return Response.fail(400,"查询失败");
               }

        }catch (Exception e){
            log.error(e.getMessage());
            e.printStackTrace();
            System.out.println( e);
            return Response.fail(500,"服务器异常"+e.getMessage());
        }

    }

    @Override
    public Response<List<AdminOrder>> selectAdminOder(Integer user_id) {
        try {
           List  info    = adminOrderMapper.selectAdminOder(user_id);
            if(info!=null&&info.size()>0){
                return Response.success(info);
            }else {
                return Response.fail(400,"查询失败");
            }

        }catch (Exception e){
            log.error(e.getMessage());
            e.printStackTrace();
            System.out.println( e);
            return Response.fail(500,"服务器异常"+e.getMessage());
        }
    }

    @Override
    public Response insertAdminOrder(AdminOrder adminOrder) {
        if(adminOrder==null){
            return Response.fail(400,"信息不能为空");
        }
        if (adminOrder.getOrder_address()==null){
            return Response.fail(400,"地址不能为空");
        }
        if (adminOrder.getProduct_name()==null){
            return Response.fail(400,"商品名字不能为空");
        }
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(ZoneId.systemDefault());
        String time=timeFormatter.format(Instant.now());
        adminOrder.setCreate_time(time);

        try {
            int row = adminOrderMapper.insertAdminOrder(adminOrder);
            Integer  order_id = adminOrderMapper.findLatestOrderIds(adminOrder);
            System.out.println("订单id是："+order_id);
            if(row>0){
                return Response.putOrderSuccess(row,order_id);
            }else {
                return Response.fail(400,"订单插入失败");
            }
        }catch (Exception e){

            log.error(e.getMessage());
            e.printStackTrace();
            System.out.println( e);
            return Response.fail(500,"服务器出错"+e.getMessage());
        }

    }

    @Override
    public Response updateAdminOrder(AdminOrder  adminOrder) {
        System.out.println(adminOrder.getStatus()+" "+adminOrder.getOrder_id());
        if(adminOrder.getStatus()==null){
            return Response.fail(400,"信息不能为空");
        }
        Integer row  = adminOrderMapper.updateAdminOrder(adminOrder );
        try {

            System.out.println("row"+row);
            if(row>0){
                return Response.success(row);

            }else {
                return  Response.fail(400,"修改失败");
            }
        }catch (Exception e){
            log.error(e.getMessage());
            e.printStackTrace();
            System.out.println( e);
            return Response.fail(500,"服务器异常"+e.getMessage());
        }


    }
}
