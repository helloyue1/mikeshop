package com.example.demo.service.serviceImp;

import com.example.demo.entity.User_Accout;
import com.example.demo.mapper.UserAccountMapper;
import com.example.demo.service.UserAccountService;
import com.example.demo.unitls.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Service
public class UserAccountServiceIpm implements UserAccountService {

    @Autowired
    private   UserAccountMapper userAccountMapper;



    @Override
    public Response insertAccountInfoByid(User_Accout user_accout) {
     System.out.println("传进来的 :"+ user_accout);

        if (user_accout.getUser_id()==null){
             return Response.fail(400,"用户id为空");
                }

        Integer  MaxCountId  =  userAccountMapper.selectAccountIdByid(user_accout.getUser_id());
        if (MaxCountId==null){
           user_accout.setAccout_id(user_accout.getUser_id());
        }



        if (user_accout.getLast_updated()==null){
            return Response.fail(400,"更新时间为空");
        }

        if (user_accout.getTotal_asset()==null){
            return  Response.fail(400,"账户余额为空");
        }
        DateTimeFormatter   mysqlDateTime  =  DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss") .withZone(ZoneId.systemDefault());
        // 将当前时间（ISO 8601 格式）转换为 MySQL 格式
        String now = mysqlDateTime.format(Instant.now());
        System.out.println("更新的时间"+now);
        user_accout.setLast_updated(now);
           Integer row =  userAccountMapper.insertAccountInfoByid(user_accout);
           System.out.println("账户变动信息"+row);
            if (row>0){
                return Response.success(row);
            }else{
                return Response.fail(400,"更新余额信息失败");
            }


    }
//查询账户余额
    @Override
    public Response selectAccountinfoByid(Integer user_id,Integer  total_asset) {
        System.out.println("账户id是："+user_id);

        if (user_id == null) {
            return Response.fail(400, "账户id为空请重新登录");
        }


        if (userAccountMapper.selectTotal_asset(user_id)==null){
            User_Accout user_accout = new User_Accout();
            user_accout.setTotal_asset(0);
            user_accout.setUser_id(user_id);

            DateTimeFormatter TimeFormat =  DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                    .withZone(ZoneId.systemDefault());
            String now = TimeFormat.format(Instant.now());
            user_accout.setLast_updated(now);
            userAccountMapper.insertAccountInfoByid(user_accout);

        }

        User_Accout row = userAccountMapper.selectAccountInfoByid(user_id);


        System.out.println("账户余额是："+row);

        if (row == null) {
            return Response.Mysuccess(200, "账户没有余额");
        } else {
            return Response.success(row);
            }

        }

    @Override
    public Response updateAccountInfoByid(User_Accout user_accout) {

            if (user_accout == null) {
                return Response.fail(400,"数据为空");
            }
            if (user_accout.getTotal_asset()!=null){
                user_accout.setTotal_asset(user_accout.getTotal_asset());
            }else{
                return Response.zifinfyiErro(400,"金额数量为空");
            }
        DateTimeFormatter   mysqlDateTime  =  DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss") .withZone(ZoneId.systemDefault());
        // 将当前时间（ISO 8601 格式）转换为 MySQL 格式
        String now = mysqlDateTime.format(Instant.now());
        System.out.println("更新的时间"+now);
        user_accout.setLast_updated(now);


            if (user_accout.getLast_updated()==null){
                return Response.fail(400,"更新时间为空");
            }else{
                user_accout.setLast_updated(user_accout.getLast_updated());
            }

             int row  = userAccountMapper.updateAccount(user_accout);
                if (row>0){
                    return Response.success(row);
                }else{
                    return Response.fail(400,"更新失败");
                }
    }


}
