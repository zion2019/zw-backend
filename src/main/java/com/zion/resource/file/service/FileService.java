package com.zion.resource.file.service;

import com.zion.common.basic.BaseFile;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    BaseFile upload(MultipartFile file);
}
