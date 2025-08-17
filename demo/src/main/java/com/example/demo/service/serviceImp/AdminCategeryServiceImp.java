package com.example.demo.service.serviceImp;

import com.example.demo.entity.Admin.AdminCategory;
import com.example.demo.mapper.admin.AdminCategoryMapper;
import com.example.demo.service.AdminCategoryService;
import com.example.demo.unitls.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class AdminCategeryServiceImp implements AdminCategoryService {

        @Autowired
      private AdminCategoryMapper adminCategoryMapper;


    @Override
    public Response selectAllCategory() {
        List<AdminCategory> category =  adminCategoryMapper.selectCategeryAll();
        return Response.success(category);
    }

    @Override
    public Response insertCategory(AdminCategory adminCategory) {

        if ( adminCategory==null){
          return   Response.fail(400,"输入分类是空的");
        }

        DateTimeFormatter Time=DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());
        String mysqlTime= Time.format(Instant.now());
        adminCategory.setCreate_time(mysqlTime);


        Integer category   = adminCategoryMapper.insertCategory( adminCategory  )  ;

        return Response.success("成功插入行数："+category);
    }

    @Override
    public Response updateCategory(AdminCategory adminCategory) {
      if ( adminCategory==null){
          return Response.fail(400,"列表不存在");
      }

      try {
            Integer row = adminCategoryMapper.updateCategory(adminCategory);
            if (row>0){
                return Response.success(row);
            }
      }catch (Exception e){
          System.out.println("出错了"+e);
      }

        return null;
    }
//删除
    @Override
    public Response deleteCategory(AdminCategory adminCategory) {
        if ( adminCategory==null){
            return  Response.fail(400,"列表为空");
        }

        try {
         Integer   row   =  adminCategoryMapper.deleteCategory(adminCategory);

         if (row>0){
             return Response.success("成功删除："+row);
         }
        }catch(Exception e) {
            System.out.println("出错了"+e);
        }



        return null;
    }


}
