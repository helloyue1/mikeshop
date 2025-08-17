package com.example.demo.service;

import com.example.demo.entity.User_paymentMethod;
import com.example.demo.unitls.Response;

public interface UserPayMethodService {


    Response selectPayMethodByUserId(User_paymentMethod userPaymentMethod);

    Response insertPayMethod(User_paymentMethod userPaymentMethod);

    Response updatePayMethod(User_paymentMethod userPaymentMethod);

    Response deletePayMethod(User_paymentMethod userPaymentMethod);

}
