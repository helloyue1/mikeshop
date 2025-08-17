package com.example.demo.service.serviceImp;


import com.example.demo.entity.User_paymentMethod;
import com.example.demo.mapper.UserPayMethodMapper;
import com.example.demo.service.UserPayMethodService;
import com.example.demo.unitls.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

@Service
public class UserPayMethodServiceImp implements UserPayMethodService {
    @Autowired
    private UserPayMethodMapper userPayMethodMapper;

//查找支付方式信息


    @Override
    public Response selectPayMethodByUserId(User_paymentMethod userPaymentMethod) {

        if(userPaymentMethod.getUser_id()==null){
            return Response.fail(400,"用户地址不存在");
        }


      List<User_paymentMethod> ROW  = userPayMethodMapper.selectPayMethodByUserId(userPaymentMethod);
      System.out.println(ROW);
      if(ROW!=null&&ROW.size()>0){
          return Response.success(ROW);
      }
        return null;
    }
//插入支付方式
    @Override
    public Response insertPayMethod(User_paymentMethod userPaymentMethod) {
        System.out.println("传进来的信息"+userPaymentMethod);
       if ( userPaymentMethod.getUser_id()==null){
           return Response.fail(400,"用户id不存在");
       }

//        Integer MaxPayID  = userPayMethodMapper.selectPayMethodId(userPaymentMethod.getUser_id());
//            System.out.println("MaxPayID:"+MaxPayID);
//          Integer random = new Random().nextInt();
//
//
//
//       if (MaxPayID==null){
//           userPaymentMethod.setPay_id(MaxPayID+random);
//       }else {
//            userPaymentMethod.setPay_id(MaxPayID+random);
//           System.out.println("支付id是这个："+MaxPayID+1);
//        }

       if (userPaymentMethod.getType()==null){
         return    Response.fail(400,"支付类型不能为空");
       }

       if (userPaymentMethod.getAccount_info()==null){
           return Response.fail(400,"支付描述消失 ");
       }


        if (userPaymentMethod.getIs_default()!=null&&userPaymentMethod.getIs_default()==1){
           userPayMethodMapper.resetPayMethodIsDfault(userPaymentMethod.getUser_id());
            userPaymentMethod.setIs_default(1);
        }else{
            userPaymentMethod.setUser_id(userPaymentMethod.getUser_id());
        }

//       使用系统时间
     DateTimeFormatter mysqlTime  = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(ZoneId.systemDefault());
//转换成mysql格式
          String nowTIME = mysqlTime.format(Instant.now());
            userPaymentMethod.setCreate_time(nowTIME);
            userPaymentMethod.setUpdate_time(nowTIME);

            int row  =userPayMethodMapper.insertPayMethod(userPaymentMethod);
        System.out.println("row是："+row);
            if (row>0){
                return Response.success(userPayMethodMapper.insertPayMethod(userPaymentMethod));
            }else{
                return Response.fail(400,"更新失败");
            }
    }
//更新支付信息
    @Override
    public Response updatePayMethod(User_paymentMethod userPaymentMethod) {
        if (userPaymentMethod==null){
            return Response.fail(400,"信息为空");
        }else{
            return Response.success(userPayMethodMapper.updatePayMethod(userPaymentMethod));
        }

    }
//删除
    @Override
    public Response deletePayMethod(User_paymentMethod userPaymentMethod) {
        System.out.println("删除支付方式："+userPaymentMethod);
        if (userPaymentMethod.getUser_id()==null && userPaymentMethod.getPay_id()==null){
            return Response.zifinfyiErro(400,"地址不存在");
        }else{
            int row = userPayMethodMapper.deletePayMeythod(userPaymentMethod);
            if (row>0){
                return Response.success(row);
            }

        }
        return null;
    }


}
