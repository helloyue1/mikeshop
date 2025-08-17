package com.example.demo.entity.Admin;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminBuyCar {
 private int buyCar_id;
   private  Integer  user_id;
    private Integer   product_id;
     private String product_value;
    private  String  product_name;
    private String  image;
    private  Integer price;
}
