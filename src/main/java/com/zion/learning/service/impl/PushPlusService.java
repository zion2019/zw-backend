package com.zion.learning.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.zion.common.basic.ServiceException;
import com.zion.learning.service.PushService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 *  WeChat push Service implements
 *  Power By: <a href="http://www.pushplus.plus">...</a>
 */
@Slf4j
@Service
public class PushPlusService implements PushService {
    private String cache_access_key;
    private static long expiresIn;
    private static final String ACCESS_KEY_URL = "https://www.pushplus.plus/api/common/openApi/getAccessKey";
    private static final String ACCESS_TOKEN_URL = "https://www.pushplus.plus/api/open/user/token";
    private static final String SEND_URL = "http://www.pushplus.plus/send";

    @Value("${zw.WeChat.PushPlus.token}")
    private String token;
    @Value("${zw.WeChat.PushPlus.secretKey}")
    private String secretKey;

    @Override
    public boolean push(String content, String receiptId) {
        String accessToken = this.getAccessToken();
        Assert.isTrue(StrUtil.isNotBlank(accessToken),()->new ServiceException("To push wechat notice error,the token is null"));
        Map<String,String> map = new HashMap<>(6);

        map.put("token", accessToken);
        map.put("title", "Z-Learning Notice");
        map.put("content", content);
        map.put("channel", "wechat");
        map.put("template", "html");
        if(StrUtil.isNotBlank(receiptId)){
            map.put("to", receiptId);
        }
        String reqJson = JSONUtil.toJsonStr(map);

        log.info("Sending msg :{}",reqJson);
        HttpResponse rsp = HttpRequest.post(SEND_URL)
                .body(reqJson)
                .execute();
        if(rsp == null || !rsp.isOk()){
            log.error("Failed to get access key. Error:{}",rsp!=null?rsp.body():"NO RSP");
            return false;
        }
        log.info("Sending msg rsp:{}",rsp);
        return true;
    }

    private String getAccessToken() {
        String accessKey = getAccessKey();
        if(StrUtil.isBlank(accessKey)){
            log.error("Fail to get access token,Error: access key is blank");
            return null;
        }
        try{
            HttpResponse rsp = HttpRequest.get(ACCESS_TOKEN_URL).header("access-key", accessKey).execute();
            if(rsp == null || !rsp.isOk()){
                log.error("Failed to get access token. Error:{}",rsp!=null?rsp.body():"NO RSP");
                return null;
            }
            log.info("To get access token rsp:{}",rsp);
            JSONObject responseJson = JSONUtil.parseObj(rsp.body());
            return responseJson.getStr("data");
        }catch (Exception e){
            log.error("Fail to get access token,Err: ",e);
        }

        return null;
    }


    private String getAccessKey() {
        if (cache_access_key == null || System.currentTimeMillis() / 1000 > expiresIn) {
            refreshAccessKey();
        }
        return cache_access_key;
    }

    private void refreshAccessKey() {
        try{
            Map<String,String> map = new HashMap<>(2);
            map.put("token", token);
            map.put("secretKey", secretKey);
            String reqJson = JSONUtil.toJsonStr(map);

            log.info("Refresh accessKey params:{}",reqJson);
            HttpResponse response = HttpRequest.post(ACCESS_KEY_URL)
                    .body(reqJson)
                    .execute();
            if(response == null || !response.isOk()){
                log.error("Failed to get access key. Error:{}",response!=null?response.body():"NO RSP");
                return ;
            }
            log.info("Refresh accessKey rsp:{}",response);

            JSONObject responseJson = JSONUtil.parseObj(response.body());
            cache_access_key = responseJson.getJSONObject("data").getStr("accessKey");
            expiresIn = System.currentTimeMillis() / 1000 + responseJson.getJSONObject("data").getLong("expiresIn");
        }catch (Exception e){
            log.error("Failed to get access key. Error: ",e);
        }

    }
}
