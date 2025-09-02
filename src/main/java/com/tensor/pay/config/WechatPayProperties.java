package com.tensor.pay.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 微信支付配置属性
 * 
 * @author Tensor Pay
 * @since 1.0.0
 */
@ConfigurationProperties(prefix = "tensor.pay.wechat")
public class WechatPayProperties {
    
    /**
     * 是否启用微信支付
     */
    private boolean enabled = false;
    
    /**
     * 应用ID
     */
    private String appId;
    
    /**
     * 商户号
     */
    private String mchId;
    
    /**
     * 商户API私钥路径
     */
    private String privateKeyPath;
    
    /**
     * 商户证书序列号
     */
    private String merchantSerialNumber;
    
    /**
     * 微信支付平台证书路径
     */
    private String wechatPayCertificatePath;
    
    /**
     * APIv3密钥
     */
    private String apiV3Key;
    
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
    
    public String getMchId() {
        return mchId;
    }
    
    public void setMchId(String mchId) {
        this.mchId = mchId;
    }
    
    public String getPrivateKeyPath() {
        return privateKeyPath;
    }
    
    public void setPrivateKeyPath(String privateKeyPath) {
        this.privateKeyPath = privateKeyPath;
    }
    
    public String getMerchantSerialNumber() {
        return merchantSerialNumber;
    }
    
    public void setMerchantSerialNumber(String merchantSerialNumber) {
        this.merchantSerialNumber = merchantSerialNumber;
    }
    
    public String getWechatPayCertificatePath() {
        return wechatPayCertificatePath;
    }
    
    public void setWechatPayCertificatePath(String wechatPayCertificatePath) {
        this.wechatPayCertificatePath = wechatPayCertificatePath;
    }
    
    public String getApiV3Key() {
        return apiV3Key;
    }
    
    public void setApiV3Key(String apiV3Key) {
        this.apiV3Key = apiV3Key;
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