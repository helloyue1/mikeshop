package com.example.demo.service.serviceImp;

import com.example.demo.Login1Application;
import com.example.demo.entity.Admin.AdminProduct;
import com.example.demo.mapper.admin.AdminProductMapper;
import com.example.demo.service.AdminProductService;
import com.example.demo.unitls.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class AdminProductServiceImp implements AdminProductService {
    private static final Logger log = LoggerFactory.getLogger(AdminProductServiceImp.class);
    @Autowired
   private AdminProductMapper  adminProductMapper;


    //查询商品
    @Override
    public Response<List<AdminProduct>> searchProduct() {
        try {
            List<AdminProduct> row  =  adminProductMapper.selectProduct();
            return Response.success(row);
        }catch (Exception e){
            log.error(e.getMessage());
            e.printStackTrace();
            System.out.println(e.getMessage());
        }

        return null;
    }
//添加商品
    @Override
    public Response insertProduct(AdminProduct adminProduct) {

        System.out.println("传进来的值："+adminProduct);
        if (adminProduct==null){
            return Response.fail(400,"商品信息不能为空");
        }

        DateTimeFormatter TimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());
        String nowTime=TimeFormatter.format(Instant.now());
        adminProduct.setCreate_time(nowTime);
        adminProduct.setUpdate_time(nowTime);

        int row =  adminProductMapper.insertProduct(adminProduct);
        try {

            if (row>0){
                return Response.success("成功新建商品数量："+row);
            }


        }catch (Exception e){
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return null;
    }

    @Override
    public Response updateProduct(AdminProduct adminProduct) {

        if (adminProduct==null){
            return Response.fail(400,"更新信息不能为空");
        }

        DateTimeFormatter  timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());
        String nowTime=timeFormatter.format(Instant.now());
        adminProduct.setUpdate_time(nowTime);
        adminProduct.setCreate_time(nowTime);

         System.out.println(adminProduct);
        int row =  adminProductMapper.updateProduct(adminProduct);
        try {
            if (row>0){
                return Response.success(row);
        }}catch(Exception e){
          log.error(e.getMessage());
            e.printStackTrace();
                System.out.println(e.getMessage());
            }
        return null;
        }



    @Override
    public Response deleteProduct(Integer product_id) {
        System.out.println("传进来的商品id"+product_id);
        int row = adminProductMapper.deleteProduct(product_id);
        try {
            if (row > 0) {
                return Response.success( "成功删除条数：" + row);
            }else{
                return Response.fail(400,"商品id不存在"+product_id);
            }
        }catch (Exception e){
            log.error("错误：",e.getMessage());
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return null;
        }

    @Override
    public Response<List<AdminProduct>> selectProductInfo(Integer product_id) {
        if (product_id==null){
            return Response.fail(400,"商品id不能为空");
        }

       List<AdminProduct>  row = adminProductMapper.selectProductInfo(product_id);
        try {
             if ( row.size()>0) {
                 return Response.success(row);
             }else{
                 return Response.fail(400,"查询失败");
             }
        }catch (Exception e){
            log.error(e.getMessage());
            e.printStackTrace();
            System.out.println(e.getMessage());
            return Response.fail(500,"服务器异常");
        }



    }


}






