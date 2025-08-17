package com.example.demo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor//有参构造函数
@NoArgsConstructor//无参构造函数
@Table(name = "user")
public    class User {
    @Id
    private Integer id;

    private String rolename;

    private String username;

    private String password;

    private  String email;

   private  String phone;

  private  String  birthday;

  private String  gender;

   private String signature;


    private String nickname;

    private Integer status;

    private String token;




}
