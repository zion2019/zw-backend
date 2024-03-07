package com.zion.resource.file.service.impl;

import cn.hutool.core.io.FileUtil;
import com.zion.common.basic.BaseFile;
import com.zion.resource.file.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.time.LocalDateTime;

@Slf4j
@Service
public class LocalFileService implements FileService {
    @Override
    public BaseFile upload(MultipartFile file) {
        BaseFile result = new BaseFile();
        FileInputStream ins = null;
        try{
            LocalDateTime now = LocalDateTime.now();
            int year = now.getYear();
            int month = now.getMonthValue();
            // 当前用户工作目录，容器中是在jar包同级目录下
            String baseDir = System.getProperty("user.dir")+ File.separator+"image";

            String datePathFormat = "/%s/%s";
            String datePathDir = String.format(datePathFormat,year,month);
            String fullDirPath = baseDir + datePathDir;

            // 项目是容器部署，直接写到根目录下的，当月的文件夹下
            if(!FileUtil.exist(fullDirPath)){
                FileUtil.mkdir(fullDirPath);
            }

            // 写入文件
            String fullPath = fullDirPath + File.separator + file.getOriginalFilename();
            log.info("write file:{}",fullPath);
            FileUtil.writeFromStream(file.getInputStream(),new File(fullPath));

            result.setFileUrl(datePathDir+ File.separator + file.getOriginalFilename());
            result.setFileName(file.getOriginalFilename());
        }catch (Exception e){
            log.error("upload file error",e);
        }finally {

        }
        return result;
    }
}
