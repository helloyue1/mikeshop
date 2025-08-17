package com.example.demo.service;

import com.example.demo.entity.User_trans;
import com.example.demo.unitls.Response;

public interface UserTransService {

    Response selectTransInfo(Integer user_id);

    Response insertTransInfo(User_trans user_trans);

    Response  selectUserTransByType(String trans_type);

    Response selectUserTransByDate(String trans_date);

    Response selectUserTransByDateAndType(String trans_date, String trans_type);

}
