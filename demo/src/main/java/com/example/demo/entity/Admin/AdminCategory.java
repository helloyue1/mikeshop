package com.example.demo.entity.Admin;


import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminCategory {
    @Id
    private Integer  category_id;
      private String  category_name;
      private String  categorySon_name;
      private Integer  product_count;
      private String  status;
      private String  create_time;
}
