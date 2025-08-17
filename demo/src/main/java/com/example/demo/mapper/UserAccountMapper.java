package com.example.demo.mapper;


import com.example.demo.entity.User_Accout;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserAccountMapper {

    @Insert("insert into user_accout  (accout_id, user_id, total_asset, last_updated)" +
            " VALUES  (" +
            "#{accout_id} , #{user_id} ,#{total_asset} ,#{last_updated} )")

    Integer insertAccountInfoByid(User_Accout user_accout);

    @Select("select MAX(accout_id) from user_accout where  user_id=#{user_id}")
    Integer  selectAccountIdByid(Integer user_id);

    @Update("update user_accout set  total_asset=#{total_asset},last_updated=#{last_updated} where user_id=#{user_id}")
    Integer updateAccount(User_Accout user_accout);


    @Select("select * from user_accout  where  user_id=#{user_id}")
    User_Accout selectAccountInfoByid(Integer user_id);

    @Select("select  total_asset from user_accout where  user_id=#{user_id}")
    User_Accout selectTotal_asset(Integer user_id);



}
