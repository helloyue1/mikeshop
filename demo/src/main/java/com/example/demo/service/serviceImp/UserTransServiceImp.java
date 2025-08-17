package com.example.demo.service.serviceImp;

import com.example.demo.entity.User_trans;
import com.example.demo.mapper.UserTransMapper;
import com.example.demo.service.UserTransService;
import com.example.demo.unitls.Response;
import org.apache.ibatis.jdbc.Null;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class UserTransServiceImp implements UserTransService {
    @Autowired
    private UserTransMapper userTransMapper;


    //    查询信息
    @Override
    public Response selectTransInfo(Integer user_id) {

        if (user_id == null) {
            return Response.fail(400, "用户id不存在");
        }
        List<User_trans> info = userTransMapper.selectTransInfo(user_id);

        if (info != null && info.size() > 0) {
            return Response.success(info);
        }
        return null;
    }

    //插入消息
    @Override
    public Response insertTransInfo(User_trans user_trans) {
        System.out.println("传进来的信息:" + user_trans);
        if (user_trans.getUser_id() == null) {
            return Response.fail(400, "用户id为空");
        }

        Integer TRANSid = userTransMapper.selectUserTransID(user_trans.getUser_id());
        if (user_trans.getTrans_id() == null) {
            user_trans.setTrans_id(1);
        } else {
            user_trans.setTrans_id(TRANSid + 1);
        }

        DateTimeFormatter Time = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());

        String nowTime = Time.format(Instant.now());
        user_trans.setTrans_date(nowTime);

        Integer row = userTransMapper.insertTransInfo(user_trans);
        System.out.println(row);
        if (row > 0) {
            return Response.success(row);
        } else {
            return Response.fail(400, "插入失败");
        }

    }


    //查询交易信息
    @Override
    public Response selectUserTransByType(String trans_type) {
        System.out.println("传进来的:" + trans_type);
        if (trans_type == null) {
            return Response.fail(400, "交易类型为空");
        }
        try {
            String encodedStr = trans_type;
            String decodedStr = URLDecoder.decode(encodedStr, StandardCharsets.UTF_8.name());
            System.out.println("解码后：" + decodedStr);
            List<User_trans> INFO = userTransMapper.selectUserTransByType(decodedStr);
            return Response.success(INFO);


        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }   // 解码（指定 UTF-8 字符集）


        @Override
        public Response selectUserTransByDate (String trans_date){
            if (trans_date == null) {
                return Response.fail(400, "时间不存在");
            } else {
                List<User_trans> INFO = userTransMapper.selectUserTransByDate(trans_date);
                return Response.success(INFO);
            }
        }

        @Override
        public Response selectUserTransByDateAndType (String trans_date, String trans_type){
            if (trans_date == null && trans_type == null) {
                return Response.fail(400, "时间和交易类型不存在");
            }


            try {
                String encodedStr = trans_type;
                String decodedStr = URLDecoder.decode(encodedStr, StandardCharsets.UTF_8.name());
                System.out.println("解码后：" + decodedStr);
                List<User_trans> INFO = userTransMapper.selectUserTransByDateAndType(trans_date,decodedStr);
                return Response.success(INFO);


            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }   // 解码（指定 UTF-8 字符集）



}



