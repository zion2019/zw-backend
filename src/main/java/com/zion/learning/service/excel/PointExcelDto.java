package com.zion.learning.service.excel;

import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.converters.string.StringStringConverter;
import lombok.Data;

public class PointExcelDto {

    @ExcelProperty(index = 0,converter = StringStringConverter.class)
    private String topic;
    @ExcelProperty(index = 1,converter = StringStringConverter.class)
    private String forceUpdate;
    @ExcelProperty(index = 2,converter = StringStringConverter.class)
    private String title;
    @ExcelProperty(index = 3,converter = StringStringConverter.class)
    private String subPoint;
    @ExcelProperty(index = 4,converter = StringStringConverter.class)
    private String content;
    @ExcelProperty(index = 5,converter = StringStringConverter.class)
    private String error;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getForceUpdate() {
        return forceUpdate;
    }

    public void setForceUpdate(String forceUpdate) {
        this.forceUpdate = forceUpdate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubPoint() {
        return subPoint;
    }

    public void setSubPoint(String subPoint) {
        this.subPoint = subPoint;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getError() {
        return error;
    }

    public void appendError(String error){
        if(StrUtil.isNotBlank(this.error)){
            this.error = this.error+";"+error;
        }else{
            this.error = error;
        }
    }

    public void setError(String error) {
        this.error = error;
    }
}
