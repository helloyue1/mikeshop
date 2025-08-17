package com.example.demo.Dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data // Lombok注解，自动生成getter、setter、toString等方法
public class UserRequest {

    // 校验规则：不允许为空（包括空字符串或纯空格），为空时提示"用户名不能为空"
    @NotBlank(message = "用户名不能为空")
    private String username;

    // 校验规则：不允许为空，为空时提示"密码不能为空"
    @NotBlank(message = "密码不能为空")
    private String password;

    private Integer id;
    @Email(message = "邮箱格式不正确")
    private  String email;


    private String nickname;
    @Min(value = 0, message = "状态值不能小于0")
    @Max(value = 1, message = "状态值不能大于1")
    private Integer status;

    private int code;

}
