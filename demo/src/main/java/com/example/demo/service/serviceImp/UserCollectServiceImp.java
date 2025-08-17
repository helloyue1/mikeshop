package com.example.demo.service.serviceImp;


import com.example.demo.entity.User_Collect;
import com.example.demo.mapper.UserCollerctMapper;
import com.example.demo.service.UserCollectService;
import com.example.demo.unitls.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserCollectServiceImp implements UserCollectService {

    private static final Logger log = LoggerFactory.getLogger(UserCollectServiceImp.class);
    @Autowired
    private UserCollerctMapper userCollerctMapper;


    @Override
    public Response<List<User_Collect>> selectCollectProductInfo(Integer user_id) {

        System.out.println("userid:"+user_id);
        if (user_id==null){
            return Response.fail(400,"用户id不能为空，请重新登录");
        }

        try {
            List<User_Collect>    info     =   userCollerctMapper.selectCollectProductInfo(user_id);
            System.out.println("查询到的收藏信息："+info);
                if (info==null){
                    return Response.fail(400,"请重新登录");
                }else{
                    return Response.success(info);
                }
        }catch (Exception e){
            log.error(e.getMessage(),e);
            return Response.fail(500,"服务器出错了");
        }
    }

    @Override
    public Response insertCollect(User_Collect user_collect) {

        System.out.println("收藏信息："+user_collect);
        if (user_collect==null){
            return Response.fail(400,"信息不能为空");
        }
        if (user_collect.getUser_id()== null){
            return Response.fail(400,"User_id信息不能为空");
        }
        try {
          Integer row=   userCollerctMapper.insertCollect(user_collect);
            if (row>0){
                return Response.success("成功修改数"+row);
            }else{
                return Response.fail(400,"插入失败");
            }
        }catch (Exception e){
            log.error(e.getMessage(),e);
            return Response.fail(500,"服务器出错"+e.getMessage());
        }


    }

    @Override
    public Response deleteCollect(Integer product_id, Integer user_id) {
        System.out.println("商品id："+product_id);
        if (product_id==null){
            return Response.fail(400,"商品id不能为空");
        }
        try {
            Integer row=   userCollerctMapper.deleteCollect(product_id,user_id);
            System.out.println("删除行数："+row);
            if (row>0){
                return Response.Mysuccess(200,"成功删除");
            }else {
                return Response.fail(400,"修改失败");
            }
        }catch (Exception e){

            log.error(e.getMessage(),e);
            return Response.fail(500,"服务器出错"+e.getMessage());
        }


    }
}
