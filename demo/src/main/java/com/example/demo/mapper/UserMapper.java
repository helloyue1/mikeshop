package com.example.demo.mapper;

import com.example.demo.Dto.UserRequest;
import com.example.demo.entity.User;
import com.example.demo.entity.User_address;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface UserMapper {
//    登录
    @Select("SELECT * FROM users WHERE username = #{username}")
    User selectByUsername(String username);

//注册
    @Update("INSERT INTO users (username, password) values (#{username},#{password}) ")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void userRejister(User user);

//    更新信息
//    @Update("UPDATE users set username = #{username} password =#{password}, email=#{email},nickname=#{nickname},id=#{id},status=#{status } where login.users.id=#{id} ")
@Update("UPDATE users " +
        "SET username = #{username}, " +
        "    password = #{password}, " +
        "    email = #{email}, " +
        "    nickname = #{nickname}, " +
        "    status = #{status}, " +
        "    phone = #{phone}, " +
        "    birthday = #{birthday}, " +  // 修正拼写错误
        "    gender = #{gender}, " +
        "    signature = #{signature},rolename=#{rolename} " +  // 修正多余空格
        "WHERE id = #{id}")
int userUpdate(User user);

    @Select("select  * from users where id=#{id}")
    User getUserById(Integer id);

    @Select("select id from users where username =#{uername}")
    User getUserIdByUsername(String username);

    @Select("select email from users where email=#{email}")
    User getUserByEmail(String email);

    @Select("select phone from users where phone=#{phone}")
    User    getUserByPhone(String phone);

    @Select("select rolename from users where username=#{username}")
    String    getUserByUserRolename(String username);

    @Select("select  * from  users")
    List<User> getUserInfo();




}


