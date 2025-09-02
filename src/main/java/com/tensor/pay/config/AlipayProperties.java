package com.tensor.pay.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 支付宝支付配置属性
 * 
 * @author Tensor Pay
 * @since 1.0.0
 */
@ConfigurationProperties(prefix = "tensor.pay.alipay")
public class AlipayProperties {
    
    /**
     * 是否启用支付宝支付
     */
    private boolean enabled = false;
    
    /**
     * 应用ID
     */
    private String appId;
    
    /**
     * 商户私钥
     */
    private String privateKey;
    
    /**
     * 支付宝公钥
     */
    private String alipayPublicKey;
    
    /**
     * 签名类型
     */
    private String signType = "RSA2";
    
    /**
     * 字符编码
     */
    private String charset = "UTF-8";
    
    /**
     * 数据格式
     */
    private String format = "json";
    
    /**
     * 支付宝网关地址
     */
    private String serverUrl = "https://openapi.alipay.com/gateway.do";
    
    /**
     * 支付回调地址
     */
    private String notifyUrl;
    
    /**
     * 支付成功跳转地址
     */
    private String returnUrl;
    
    /**
     * 是否为沙箱环境
     */
    private boolean sandbox = false;
    
    // Getters and Setters
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public String getAppId() {
        return appId;
    }
    
    public void setAppId(String appId) {
        this.appId = appId;
    }
    
    public String getPrivateKey() {
        return privateKey;
    }
    
    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }
    
    public String getAlipayPublicKey() {
        return alipayPublicKey;
    }
    
    public void setAlipayPublicKey(String alipayPublicKey) {
        this.alipayPublicKey = alipayPublicKey;
    }
    
    public String getSignType() {
        return signType;
    }
    
    public void setSignType(String signType) {
        this.signType = signType;
    }
    
    public String getCharset() {
        return charset;
    }
    
    public void setCharset(String charset) {
        this.charset = charset;
    }
    
    public String getFormat() {
        return format;
    }
    
    public void setFormat(String format) {
        this.format = format;
    }
    
    public String getServerUrl() {
        return serverUrl;
    }
    
    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }
    
    public String getNotifyUrl() {
        return notifyUrl;
    }
    
    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }
    
    public String getReturnUrl() {
        return returnUrl;
    }
    
    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }
    
    public boolean isSandbox() {
        return sandbox;
    }
    
    public void setSandbox(boolean sandbox) {
        this.sandbox = sandbox;
    }
}