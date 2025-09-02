package com.tensor.pay.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * 银行支付配置属性
 * 
 * @author Tensor Pay
 * @since 1.0.0
 */
@ConfigurationProperties(prefix = "tensor.pay.bank")
public class BankPayProperties {
    
    /**
     * 是否启用银行支付
     */
    private boolean enabled = false;
    
    /**
     * 商户号
     */
    private String merchantId;
    
    /**
     * 商户密钥
     */
    private String merchantKey;
    
    /**
     * 银行网关地址
     */
    private String gatewayUrl;
    
    /**
     * 支持的银行列表
     */
    private List<String> supportedBanks;
    
    /**
     * 支付回调地址
     */
    private String notifyUrl;
    
    /**
     * 支付成功跳转地址
     */
    private String returnUrl;
    
    /**
     * 是否为测试环境
     */
    private boolean testMode = false;
    
    /**
     * 连接超时时间（毫秒）
     */
    private int connectTimeout = 30000;
    
    /**
     * 读取超时时间（毫秒）
     */
    private int readTimeout = 30000;
    
    // Getters and Setters
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public String getMerchantId() {
        return merchantId;
    }
    
    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }
    
    public String getMerchantKey() {
        return merchantKey;
    }
    
    public void setMerchantKey(String merchantKey) {
        this.merchantKey = merchantKey;
    }
    
    public String getGatewayUrl() {
        return gatewayUrl;
    }
    
    public void setGatewayUrl(String gatewayUrl) {
        this.gatewayUrl = gatewayUrl;
    }
    
    public List<String> getSupportedBanks() {
        return supportedBanks;
    }
    
    public void setSupportedBanks(List<String> supportedBanks) {
        this.supportedBanks = supportedBanks;
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
    
    public boolean isTestMode() {
        return testMode;
    }
    
    public void setTestMode(boolean testMode) {
        this.testMode = testMode;
    }
    
    public int getConnectTimeout() {
        return connectTimeout;
    }
    
    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }
    
    public int getReadTimeout() {
        return readTimeout;
    }
    
    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }
}