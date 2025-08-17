package com.example.demo.service.serviceImp;


import com.example.demo.Dto.UserDTO;
import com.example.demo.UserConverter.UserConverter;
import com.example.demo.entity.User;
import com.example.demo.mapper.UserMapper;
import com.example.demo.service.UserService;
import com.example.demo.unitls.JwtUtils;

import com.example.demo.unitls.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class UserServiceImp implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private UserDTO userDTO;

//    登录
    @Override
    public Response<UserDTO> login(String username, String password) {
        User user = userMapper.selectByUsername(username);

        if (user == null) {

            return Response.fail(400, "用户不存在");
        }


            // 生成JWT令牌

// 2. 密码校验（核心修正）
            // 注意：matches方法参数是（原始密码，加密密码）
            boolean passwordMatches = bCryptPasswordEncoder.matches(password, user.getPassword());
            System.out.println("用户输入的原始密码：" + password);
            System.out.println("数据库存储的加密密码：" + user.getPassword());
            if (!passwordMatches) {
                return Response.fail(401, "密码错误");
            }




//                User id = userMapper.getUserIdByUsername(username);
                Map<String, Object> map = new HashMap<>();
                map.put("id", user.getId());

                String jwt = JwtUtils.generateJwt(map);
                      user.setToken(jwt);

                System.out.println(jwt);

                 UserDTO userdto = UserConverter.convertUserToUserDTO(user);
        String rolename  = userMapper.getUserByUserRolename(user.getUsername());
        if (rolename.equals("普通用户")) {
            return Response.Loginsuccess(userdto,"/mikeshop");
        }
        if (rolename.equals("管理员")){
            return Response.Loginsuccess(userdto,"/admin");
        }
                return null;

    }






    //注册账号
    @Override
    public Response userRejister(String username, String password) {


        if (username == null || username.trim().isEmpty()) {
            return Response.fail(400, "用户名不能为空");
        }
        if (password == null || password.trim().isEmpty()) {
            return Response.fail(400, "密码不能为空");
        }

        // 2. 校验用户名是否已存在（核心：插入前查询）
        User existingUser = userMapper.selectByUsername(username); // 需要新增这个查询方法
        if (existingUser != null) {
            return Response.fail(400, "账号已被注册");
        }

        // 3. 密码加密（必须做，避免明文存储）
        // 需注入BCryptPasswordEncode
        String encodedPassword = bCryptPasswordEncoder.encode(password);


        // 4. 执行插入操作

        // 注意：此方法应返回void或int，而非User

        // 5. 构建成功响应
        User user = new User();


        user.setPassword(encodedPassword);
        user.setUsername(username);
        userMapper.userRejister(user);
        UserDTO userDTO = UserConverter.convertUserToUserDTO(user);

        return Response.success(userDTO);

//        System.out.println("生成的BCrypt密码：" + encodedPassword);
//        System.out.println("数据库中存储的密码：" + existinguser.getPassword());
//        System.out.println("两者是否一致：" + encodedPassword.equals(existinguser.getPassword()));
//        if (username==null || password==null ) {
//            return Response.fail(400,"账号密码不能为空");
//        } else  {
//            if(user.getPassword().equals(password)||user.getUsername().equals(username)) {
//                return Response.fail(400,"账号已经被注册");
//            }else{
//
//                if( password==null ) {
//                    return Response.fail(400,"密码不能为空");
//                }else {
//                Map<String, Object> map = new HashMap<>();
//                 UserDTO  userDTO= UserConverter.convertUserToUserDTO(user);
//                map.put("username", user.getUsername());
//                map.put("password", user.getPassword());
//                return Response.success(userDTO ,"注册成功");
//                }
//            }
//
//
//
//
    }

    //更新信息
    @Override
    public Response userUpdate(User user) {
        userDTO.getToken();



        Integer id = user.getId();
//        基础参数校验
        if (user == null || id == null) {
            return Response.fail(400, "用户id不能为空");
        }
//        查看用户是否存在
        User existinguser = userMapper.getUserById(user.getId());
        if(existinguser ==null) {
            Response.fail(400,"用户名不存在");
        }
        System.out.println("id：" + existinguser.getId() + "nihao" + user.getId());
        System.out.println("数据1：" + existinguser);
        System.out.println("名字：" + existinguser.getUsername());



//校验用户名
        if (user.getUsername() != null && !user.getUsername().trim().isEmpty()) {
            User existingusername = userMapper.selectByUsername(user.getUsername());
            System.out.println("existingusername名字：" + existingusername.getUsername());
            System.out.println("数据2：" + existingusername);

            if(existingusername!=null){
            if(user.getUsername()!=null&&user.getUsername().trim().isEmpty()) {

                if(user.getUsername().trim().equals(existingusername.getUsername())) {
//                   输入的名字和数据库相同
                }else{
//                    输入的名字和数据库不相同
                    if (!existingusername.getId().equals(user.getId())) {
                        return Response.fail(400, "用户已经存在");}

                }
            }
            }
        }else {
            user.setUsername(existinguser.getUsername());

        }

//密码处理
            if (user.getPassword() != null && !user.getPassword().trim().isEmpty()) {
                if (!bCryptPasswordEncoder.matches(user.getPassword(), existinguser.getPassword())) {
                    user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
                    System.out.println("user密码：" + user.getPassword());
                    System.out.println("生成的BCrypt密码：" + bCryptPasswordEncoder.encode(user.getPassword()));
                    System.out.println("数据库中存储的密码：" + existinguser.getPassword());
                    System.out.println("两者是否一致：" + bCryptPasswordEncoder.encode(user.getPassword()).equals(existinguser.getPassword()));
                } else {
                    user.setPassword(existinguser.getPassword());
                }
            } else {
                user.setPassword(existinguser.getPassword());

            }
//邮箱地址处理
        // 先判断existinguser是否存在，避免空指针（尤其是注册场景）
        User existingUser = userMapper.getUserById(user.getId());
        if (existingUser == null) {
            throw new RuntimeException("用户不存在");
        }

        // 2. 检查邮箱是否变更，且新邮箱已被其他用户占用
        if (user.getEmail()!=null&&!Objects.equals(user.getEmail(), existingUser.getEmail())) {
            User userByEmail = userMapper.getUserByEmail(user.getEmail());
            if (userByEmail != null) {
              return   Response.fail(400,"邮箱已被占用：" + user.getEmail());
            }
        }



//        状态码更新


                if (user.getStatus() != null) {
                    if (user.getStatus() != 1 && user.getStatus() != 0) {

                        return Response.fail(400, "状态码只能是0或1");
                    }
                }
//更新昵称

        if(user.getNickname()!=null&&!user.getNickname().trim().isEmpty()){
            user.setNickname(user.getNickname());
        }else {
            user.setNickname(existinguser.getUsername());
        }

//        更新生日
        if(user.getBirthday()!=null&&!user.getBirthday().trim().isEmpty()){
            user.setBirthday(user.getBirthday());
        }else{
            user.setBirthday(existinguser.getBirthday());
        }
//        更新性别
        if(user.getGender()!=null&&!user.getGender().trim().isEmpty()){
            user.setGender(user.getGender());

        }else {
            user.setGender(existinguser.getGender());
        }
//        更新电话
        String newPhone = user.getPhone();
        String oldPhone = existingUser.getPhone();
        if (!Objects.equals(newPhone, oldPhone) && newPhone != null) {
            // 查询新手机号是否被其他用户占用
            User userByPhone = userMapper.getUserByPhone(newPhone);
            if (userByPhone != null) {
                throw new RuntimeException("手机号已被占用：" + newPhone);
            }
        }
//        更新签名
        if (user.getSignature()!=null){
            user.setSignature(user.getSignature());
        }else{
            user.setSignature(existinguser.getSignature());
        }

//        更新信息

        try {
            if(user.getStatus()==null) {
                user.setStatus(1);
            }

            // 只执行一次更新操作并获取返回的User对象
            int row = userMapper.userUpdate(user);
            System.out.println("更新后的用户信息：" + row);
            System.out.println("更新后的用户信息：" + user);


            // 判断返回的用户对象是否有效
            if (row >0) {
                User user1 = userMapper.getUserById(user.getId());
                UserDTO userDTO = UserConverter.convertUserToUserDTO(user1);
                return Response.success(userDTO);
            } else {
                return Response.fail(500, "更新失败，未找到对应数据或更新未生效");
            }
        } catch (Exception e) {
            // 记录异常详情便于排查
            e.printStackTrace();
            return Response.fail(500, "更新失败：" + e.getMessage());
        }}

    @Override
    public Response userAllInfo() {
       List<User>   user= userMapper.getUserInfo();
        return Response.success(user);
    }


}
//            try {
//
//                userMapper.userUpdate(user);
//                System.out.println(userMapper.userUpdate(user));
//                UserDTO userDTO = UserConverter.convertUserToUserDTO(user);
//                return Response.success(userDTO, "更新成功");
//
//
//            } catch (Exception e) {
//
//                return Response.fail(500, "更新失败");
//
//            }
//
//
//        }
//
//    }








