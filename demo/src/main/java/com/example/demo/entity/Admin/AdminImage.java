package com.example.demo.entity.Admin;


import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "image")
public class AdminImage {
 //图片id 不用传
 private Integer   image_id  ;

 private String   image_url;


 private String  image_category;


 private  String   create_time;

}
