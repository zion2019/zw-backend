package com.zion.learning.service;

/**
 * Push interface
 */
public interface PushService {


    /**
     * push message
     * @param content content
     * @param receiptId wxId/...
     */
    boolean push(String content, String receiptId);
}
