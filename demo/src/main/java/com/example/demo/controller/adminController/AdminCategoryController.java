package com.example.demo.controller.adminController;


import com.example.demo.entity.Admin.AdminCategory;
import com.example.demo.service.AdminCategoryService;
import com.example.demo.unitls.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AdminCategoryController {
    @Autowired
    private AdminCategoryService adminCategoryService;


    @GetMapping("selectCategory")
    public Response selectCategory(){
        return adminCategoryService.selectAllCategory();
    }

    @PostMapping("insertCategory")
    public Response insertCategory(@RequestBody  AdminCategory adminCategory){
        return adminCategoryService.insertCategory(adminCategory);
    }

   @PutMapping("updateCategory")
    public Response updateCategory(@RequestBody  AdminCategory adminCategory){
        return adminCategoryService.updateCategory(adminCategory);
   }

    @DeleteMapping("deleteCategory")
    public Response deleteCategory(@RequestBody  AdminCategory adminCategory){
        return adminCategoryService.deleteCategory(adminCategory);
    }

}
