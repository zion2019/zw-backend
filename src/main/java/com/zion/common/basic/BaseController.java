package com.zion.common.basic;

import cn.hutool.core.text.CharSequenceUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@RestController
public abstract class BaseController {

    private static final String HEADER_KEY_USER_ID = "Header_key_user_id";

    // 提供方法获取指定的请求头消息
    protected Long getCurrentUserId() {
        HttpServletRequest request = getCurrentRequest();
        String str = request.getHeader(HEADER_KEY_USER_ID);
        if(CharSequenceUtil.isNotBlank(str)){
            try{
                return Long.parseLong(str);
            }catch (NumberFormatException e){
                log.error("",e);
            }
        }
        return null;
    }

    // 获取当前请求的 HttpServletRequest
    private HttpServletRequest getCurrentRequest() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            return requestAttributes.getRequest();
        }
        throw new IllegalStateException("No current request found");
    }
}