package com.example.demo.mapper;

import com.example.demo.entity.User_trans;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;


@Mapper
public interface UserTransMapper {

    @Select("select * from user_transactions where user_id=#{user_id}")
    List<User_trans> selectTransInfo(Integer user_id);

    @Insert("insert into user_transactions (user_id, trans_date, trans_type, description, amount, status) VALUES " +
            "(#{user_id}," +
            "#{trans_date}," +
            "#{trans_type},#{description},#{amount},#{status})")
    Integer insertTransInfo(User_trans user_trans);

    @Select("select MAX(trans_id) from user_transactions where user_id=#{user_id}")
    Integer selectUserTransID(Integer user_id);

    @Select("select * from user_transactions where trans_type=#{trans_type}")
    List<User_trans> selectUserTransByType(String trans_type);

    @Select("select * from user_transactions where  trans_date=#{trans_date}")
    List<User_trans> selectUserTransByDate(String trans_date);


    @Select("select * from user_transactions where  trans_date=#{trans_date} and trans_type=#{trans_type}")
    List<User_trans>  selectUserTransByDateAndType(String trans_date, String trans_type);


}
