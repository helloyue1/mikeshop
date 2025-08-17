package com.example.demo.service;

import com.example.demo.entity.User_address;
import com.example.demo.unitls.Response;

public interface UserAddressService {

  Response insertUserAddress(User_address userAddress);

  Response getAddressinfo(Integer user_id);

  Response deleteUserAddress(Integer user_id,Integer addressid);

  Response updateUserAddress(User_address user_address);

}
