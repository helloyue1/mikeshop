package com.example.demo.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User_Accout {
            @Id
            @TableId(type = IdType.AUTO)
          private Integer          accout_id ;

           private  Integer         user_id   ;
//总资产
           private  Integer         total_asset;

           private   String         last_updated;

}
