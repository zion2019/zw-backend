package com.zion.common.utils;

import com.zion.common.basic.BaseEntity;
import com.zion.common.basic.CommonConstant;

import java.time.LocalDateTime;

public class BaseEntityUtil {

    private static final SnowflakeIdGenerator idGenerator = new SnowflakeIdGenerator(1);
    public static void setId(BaseEntity baseEntity){
        baseEntity.setId(idGenerator.nextId());
    }

    public static void filedBasicInfo(BaseEntity baseEntity) {
        LocalDateTime now = LocalDateTime.now();
        String currentUsername = SpringSecurityUtil.getCurrentUsername();
        if(baseEntity.getId() == null){
            baseEntity.setId(idGenerator.nextId());
            baseEntity.setCreatedTime(now);
            baseEntity.setCreatedUser(currentUsername);
        }else{
            baseEntity.setUpdatedUser(currentUsername);
            baseEntity.setUpdatedTime(now);
        }

        if(baseEntity.getVersion() == null){
            baseEntity.setVersion(0);
        }else{
            baseEntity.setVersion(baseEntity.getVersion()+1);
        }

        baseEntity.setDeleted(CommonConstant.DELETED_NO);
    }
}
