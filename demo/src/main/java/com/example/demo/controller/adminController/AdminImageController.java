package com.example.demo.controller.adminController;


import com.example.demo.entity.Admin.AdminImage;
import com.example.demo.service.AdminImageService;
import com.example.demo.unitls.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AdminImageController {


    @Autowired
    private AdminImageService adminImageService;
    @GetMapping("getImages")
    public Response<List<AdminImage>> getAdminImage() {
        return adminImageService.selectAllimage();
    }

    @PostMapping("insertImages")
    public Response<Integer> insertAdminImage(@RequestBody AdminImage adminImage) {
        return adminImageService.insertImage(adminImage);
    }

    @DeleteMapping("deleteImages")
    public Response<Integer> deleteAdminImage(@RequestBody Integer image_id) {
        return adminImageService.deleteByPrimaryKey( image_id);
    }

    @PutMapping("putImages")
    public Response<Integer> putAdminImage(@RequestBody AdminImage adminImage) {
        return adminImageService.insertImage(adminImage);

    }


}
