package com.zion.resource.file.controller;

import com.zion.common.basic.BaseFile;
import com.zion.common.basic.R;
import com.zion.resource.file.service.FileService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/res/file")
public class FileController {

    @Resource
    private FileService fileService;

    /**
     * 为了节省服务器资源，做一个简易的文件上传，通过nginx访问图片
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public R<BaseFile> upload(@RequestPart("file") MultipartFile file){
        return R.ok(fileService.upload(file));
    }
}
