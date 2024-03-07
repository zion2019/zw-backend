package com.zion.common.basic;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
public class R<T extends Serializable> implements Serializable{

    private static final long serialVersionUID = 1L;

    /**请求成功标识*/
    private boolean success = false;
    /**响应码*/
    private Integer status;
    /**响应消息*/
    private String message;
    /**响应对象*/
    private T data;
    /**响应列表*/
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    private List<T> dataList;
    /**响应扩展*/
    private Serializable extend;
    /**时间戳*/
    private long timestamp = System.currentTimeMillis();

    private Integer pageNo;

    private Integer pageSize;

    private Long total;

    /**默认响应码：200成功，500失败，401无权限*/
    public static final int OK_200 = 200;
    public static final int ERROR_500 = 500;
    public static final int NO_AUTH_401 = 401;

    /**默认响应消息*/
    public static final String OK_MSG = "成功";
    public static final String ERROR_MSG = "失败";
    public static final String NO_AUTH_MSG = "无权限";

    /**
     * 私有化构造方法
     */
    private R(){

    }

    public static <T extends Serializable> R<T> ok() {
        return R.ok(OK_MSG);
    }

    public static <T extends Serializable> R<T> ok(String message) {
        return R.ok(null, null, null, message);
    }

    public static <T extends Serializable> R<T> ok(T data) {
        return R.ok(data, OK_MSG);
    }

    public static <T extends Serializable> R<T> ok(Page<T> pageData) {
        R<T> r = R.ok();
        r.setDataList(pageData.getDataList());
        r.setPageNo(pageData.getPageNo());
        r.setPageSize(pageData.getPageSize());
        r.setTotal(pageData.getTotal());
        return r;
    }


    public static <T extends Serializable> R<T> ok(T data, String message) {
        return R.ok(data, null, null, message);
    }

    public static <T extends Serializable> R<T> ok(List<T> dataList) {
        return R.ok(dataList, OK_MSG);
    }

    public static <T extends Serializable> R<T> ok(List<T> dataList, String message) {
        return R.ok(null, dataList, null, message);
    }

    public static <T extends Serializable> R<T> ok(T data, Serializable extend) {
        return R.ok(data, null, extend, OK_MSG);
    }

    public static <T extends Serializable> R<T> ok(List<T> dataList, Serializable extend) {
        return R.ok(null, dataList, extend, OK_MSG);
    }

    public static <T extends Serializable> R<T> error() {
        return R.error(ERROR_MSG);
    }

    public static <T extends Serializable> R<T> error(String message) {
        return R.error(ERROR_500, message);
    }

    public static <T extends Serializable> R<T> error(Integer status, String message) {
        R<T> R = new R<>();
        R.status = status;
        R.message = message;
        R.success = false;
        return R;
    }

    /**
     * 无权访问
     */
    public static <T extends Serializable> R<T> noAuth() {
        return R.error(NO_AUTH_401, NO_AUTH_MSG);
    }

    private static <T extends Serializable> R<T> ok(T data, List<T> dataList,
                                                    Serializable extend, String message) {
        R<T> R = new R<>();
        R.success = true;
        R.status = OK_200;
        R.data = data;
        R.dataList = dataList;
        R.extend = extend;
        R.message = message;
        return R;
    }

    public static <T extends Serializable> R ok(int pageNo, int pageSize, long totalCount, List<T> records) {
        R<T> R = new R<>();
        R.success = true;
        R.status = OK_200;
        R.dataList = records;
        R.pageNo = pageNo;
        R.pageSize = pageSize;
        R.total = totalCount;
        return R;
    }

}