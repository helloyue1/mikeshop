package com.example.demo.service.serviceImp;

import com.example.demo.entity.User_address;
import com.example.demo.mapper.UserAddressMapper;
import com.example.demo.service.UserAddressService;
import com.example.demo.unitls.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;


@Service
public class UserAddressServiceImp implements UserAddressService {

    @Autowired
    private UserAddressMapper userAddressMapper;


    @Override
    public Response insertUserAddress(User_address userAddress) {
//        查询用户id


        System.out.println("开始执行");
        System.out.println("刚开始" + userAddress);
        Integer userId = userAddress.getUser_id();
        Integer MaxAddressid = userAddressMapper.selectUserAddressId(userId);
        System.out.println("你的最大地址id" + MaxAddressid);
        System.out.println("你的id" + userId);
        if (userId == null) {
            return Response.fail(400, "用户id为空");
        } else {
            userAddress.setUser_id(userId);
        }
//        创建接收人姓名
        if (userAddress.getReceiver_name() == null) {

            return Response.fail(400, "收件人姓名为空");
        }
//      收件人电话
        if (userAddress.getReceiver_phone() == null ) {

            return Response.fail(400, "请输入正确的手机号");
        }

//省份
        if (userAddress.getProvince() == null) {

            return Response.fail(401, "请输入省份");
        }

//        城市
        if (userAddress.getCity() == null) {

            return Response.fail(400, "请输入城市");
        }
//区/县
        if (userAddress.getDistrict() == null) {

            return Response.fail(400, "请输入区/县");
        }

//       详细地址

        if (userAddress.getDetail_address() == null) {

            return Response.fail(400, "请输入详细地址");
        }

//        邮件编号
        if (userAddress.getPostal_code() == null) {
            return Response.fail(400, "请输入邮件编号");
        }

//是不是默认地址
        if ( userAddress.getIs_default() == 1) {
//            将所有用户改为非默认的
            userAddressMapper.resetAllDefaultByUserId(userId);
            userAddress.setIs_default(1);
//              Integer addressid = userAddress.getAddressid();
//
//            userAddressMapper.setDefaultByAddressId(addressid+1,userId);
        } else {
            userAddress.setIs_default(userAddress.getIs_default());
        }


        DateTimeFormatter mysqlFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(ZoneId.systemDefault());  // 使用系统时区

        // 将当前时间（ISO 8601 格式）转换为 MySQL 格式
        String now = mysqlFormatter.format(Instant.now());

        userAddress.setCreate_time(now);
        userAddress.setUpdate_time(now);


        Integer row = userAddressMapper.insertUserAddress(userAddress);
        System.out.println("插入的地址信息：" + userAddress);

        if (row > 0) {
            Integer newAddressId = userAddress.getAddressid();
            System.out.println(newAddressId);
            return Response.zifinfyiErro(200, "地址添加成功");
        } else {
            return Response.fail(400, "地址添加失败");
        }


    }
//获取地址信息
    @Override
    public Response getAddressinfo(Integer user_id) {
        if (user_id == null) {
            return Response.fail(400, "用户id为空，请重新登录");
        }


        List<User_address> info = userAddressMapper.getAddressinfo(user_id);
        System.out .println("地址信息是："+info);
        if (info != null) {
            return Response.success(info);
        }
        return null;
    }


    //删除地址信息
    @Override
    public Response deleteUserAddress(Integer user_id, Integer addressid) {
        if (user_id == null || addressid == null) {
            return Response.fail(400, "地址不存在");
        } else {
               int  row   = userAddressMapper.deleteUserAddress(user_id, addressid);
                if (row > 0) {
                    return Response.success(row);
                }
        }
        return null;
    }
//更新编辑
    @Override
    public Response updateUserAddress(User_address user_address) {

        if (user_address == null) {
            return Response.fail(400,"编辑信息不能为空");
        }

        if (user_address.getAddressid() == null) {
            return  Response.fail(400,"地址id不能为空");
        }

        if (user_address.getReceiver_name() == null) {

            return Response.fail(400,"收件人姓名不能为空");
        }

        if (user_address.getReceiver_phone() == null) {

            return Response.fail(400,"收件人姓名不能为空");
        }
        if (user_address.getProvince() == null) {

            return Response.fail(400,"收件人电话不能为空");
        }

        if (user_address.getCity() == null) {

            return Response.fail(400,"城市不能为空");
        }

        if (user_address.getDistrict() == null) {

            return Response.fail(400,"市不能为空");
        }

            DateTimeFormatter TimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                    .withZone(ZoneId.systemDefault());
        String now = TimeFormatter.format(Instant.now());
        user_address.setCreate_time(now);
        user_address.setUpdate_time(now);

// 2. 仅处理需要手动干预的字段（如非空校验或格式处理）
// 示例：如果邮政编码为空，可设置默认值（根据业务需求决定）
        if (user_address.getPostal_code() == null || user_address.getPostal_code().toString().trim().isEmpty()) {
            user_address.setPostal_code(000000); // 或保留 null，根据数据库是否允许空值决定
        }


        if (user_address.getAddressid()==null){

            return Response.fail(400,"地址id为空");
        }





               Integer info   =   userAddressMapper.updateUserAddress(user_address);
                    if (info > 0) {
                          return Response.success(info);
                    }else {
                        return  Response.fail(400,"编辑失败");
                    }



    }

}



