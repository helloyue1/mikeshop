package com.example.demo.service.serviceImp;


import com.example.demo.entity.Admin.AdminBuyCar;
import com.example.demo.mapper.admin.AdminBuyCarMapper;
import com.example.demo.service.AdminBuyCarService;
import com.example.demo.unitls.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminBuyCarServiceImp implements AdminBuyCarService {

        private static final Logger log = LoggerFactory.getLogger(AdminBuyCarServiceImp.class);
        @Autowired
        private AdminBuyCarMapper adminBuyCarMapper;


        @Override
        public Response insertAdminBuyCar(AdminBuyCar adminBuyCar) {
                if (adminBuyCar==null){
                        return  Response.fail(400,"输入的购物车信息不能为空");
                }

                if (adminBuyCar.getUser_id()==null){
                        return Response.fail(400,"用户id不能为空");
                }

                if (adminBuyCar.getProduct_id()==null){
                        return Response.fail(400,"商品id不能为空");
                }
                int row=   adminBuyCarMapper.insertAdminBuyCar(adminBuyCar);
                try {
                      if (row>0){
                              return Response.Mysuccess(200,"成功插入条数"+row);
                      }else{
                              return Response.fail(400,"插入失败");
                      }
                }catch (Exception e){
                        log.error(e.getMessage(),e);
                        e.printStackTrace();
                        System.out.println(e.getMessage());
                        return Response.fail(500,"服务器出错");
                }


        }

        @Override
        public Response deleteAdminBuyCar(Integer product_id) {
               if (product_id==null){
                       return Response.fail(400,"商品id不可为空");
               }

               try {
                   int  row = adminBuyCarMapper.deleteAdminBuyCar(product_id);
                        if (row>0){
                                return Response.Mysuccess(200,"购物车删除成功");
                        }else{
                                return Response.fail(400,"删除失败");
                        }

               }catch (Exception e){
                       log.error(e.getMessage(),e);
                       e.printStackTrace();
                       System.out.println(e.getMessage());
                       return Response.fail(500,"服务器出错");
               }


        }

        @Override
        public Response<List<AdminBuyCar>> selectAllAdminBuyCar(Integer user_id) {
                if (user_id==null){
                        return  Response.fail(400,"用户id不能为空");
                }
                List<AdminBuyCar> list = adminBuyCarMapper.selectAllAdminBuyCar(user_id);
                System.out.println("商品列表是："+list);
                try {


                        if (list!=null){
                                return Response.success(list);
                        }else{
                                return Response.fail(400,"查询失败");
                        }
                }catch (Exception e){
                        log.error(e.getMessage(),e);
                        e.printStackTrace();
                        System.out.println(e.getMessage());
                        return Response.fail(500,"服务器出错");
                }


        }
}
