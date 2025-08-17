package com.example.demo.service.serviceImp;


import com.baomidou.mybatisplus.toolkit.StringUtils;
import com.example.demo.entity.Admin.AdminImage;
import com.example.demo.mapper.admin.AdminImageMapper;
import com.example.demo.service.AdminImageService;
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
public class AdminImageServiceImp  implements AdminImageService {

    private static final Logger log = LoggerFactory.getLogger(AdminImageServiceImp.class);
    @Autowired
    private AdminImageMapper adminImageMapper;

    @Override
    public Response insertImage(AdminImage adminImage) {
        System.out.println("传进来的照片信息是："+adminImage);
      if (adminImage==null){
          return Response.fail(400,"所传照片信息为空");
      }

        if (StringUtils.isEmpty(adminImage.getImage_url())) {
            return Response.fail(400, "图片URL不能为空");
        }

        DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());
        String NowTime=TIME_FORMAT.format(Instant.now());
        adminImage.setCreate_time(NowTime);

         int row=   adminImageMapper.insertImage(adminImage);
        try {
            if(row>0){
                return Response.Mysuccess(200,"图片信息添加成功");
            }else {
                return Response.fail(400,"图片添加失败");
            }
        }catch (Exception e){
            log.error(e.getMessage(),e);
            e.printStackTrace();
             System.out.println(e);
             return Response.fail(500,"服务器异常"+e.getMessage());
        }

    }

    @Override
    public Response deleteByPrimaryKey(Integer image_id) {
        if (image_id==null) {
            return Response.fail(400,"id不能为空");
        }
          int  row =  adminImageMapper.deleteByPrimaryKey(image_id);
        try {
            if(row>0){
                return Response.Mysuccess(200,"照片删除成功");
            }else{
                return Response.fail(400,"照片删除失败");
            }
        }catch (Exception e){
            log.error(e.getMessage(),e);
            e.printStackTrace();
            System.out.println(e);
            return Response.fail(500,"服务器出错"+e.getMessage());
        }

    }

    @Override
    public Response updateByPrimaryKey(AdminImage adminImage) {
        if (adminImage==null) {
            return Response.fail(400,"照片信息不能为空");
        }
        if (StringUtils.isEmpty(adminImage.getImage_url())) {
            return Response.fail(400, "图片URL不能为空");
        }

         int  a=  adminImageMapper.updateByPrimaryKey(adminImage);
        try {
            if(a>0){
                return Response.Mysuccess(200,"照片更新成功");
            }else{
                return Response.fail(400,"照片更新失败");
            }
        }catch (Exception e){
            log.error("错误信息："+e.getMessage(),e);
            e.printStackTrace();
            System.out.println(e);
            return Response.fail(500,"服务器出错:"+e.getMessage());
        }

    }

    @Override
    public Response<List<AdminImage>> selectAllimage() {
         List   images =  adminImageMapper.selectAllimage();
        try {
            if(images!=null){
                return Response.success(images);
            }else {
                return Response.fail(400, "查询失败");
            }
        }catch (Exception e){
            log.error(e.getMessage(),e);
            e.printStackTrace();
            System.out.println(e);
            return Response.fail(500,"服务器出错："+e.getMessage());
        }

    }
}
