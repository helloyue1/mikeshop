package com.example.demo.service;

import com.example.demo.entity.Admin.AdminCategory;
import com.example.demo.unitls.Response;

import java.util.List;

public interface AdminCategoryService {

    Response selectAllCategory();
    Response insertCategory(AdminCategory adminCategory);
    Response updateCategory(AdminCategory adminCategory);
    Response deleteCategory(AdminCategory adminCategory);

}
