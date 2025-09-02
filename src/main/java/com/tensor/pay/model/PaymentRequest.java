package com.tensor.pay.model;

import com.tensor.pay.enums.PaymentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 统一支付请求模型
 * 
 * @author Tensor Pay
 * @since 1.0.0
 */
public class PaymentRequest {
    
    /**
     * 商户订单号
     */
    @NotBlank(message = "商户订单号不能为空")
    private String outTradeNo;
    
    /**
     * 支付类型
     */
    @NotNull(message = "支付类型不能为空")
    private PaymentType paymentType;
    
    /**
     * 支付金额（单位：元）
     */
    @NotNull(message = "支付金额不能为空")
    @Positive(message = "支付金额必须大于0")
    private BigDecimal amount;
    
    /**
     * 商品描述
     */
    @NotBlank(message = "商品描述不能为空")
    private String subject;
    
    /**
     * 商品详情
     */
    private String body;
    
    /**
     * 支付超时时间
     */
    private LocalDateTime expireTime;
    
    /**
     * 异步通知地址
     */
    private String notifyUrl;
    
    /**
     * 同步返回地址
     */
    private String returnUrl;
    
    /**
     * 用户标识（微信openid、支付宝用户id等）
     */
    private String userId;
    
    /**
     * 客户端IP
     */
    private String clientIp;
    
    /**
     * 扩展参数
     */
    private Map<String, Object> extraParams;
    
    // Constructors
    public PaymentRequest() {}
    
    public PaymentRequest(String outTradeNo, PaymentType paymentType, BigDecimal amount, String subject) {
        this.outTradeNo = outTradeNo;
        this.paymentType = paymentType;
        this.amount = amount;
        this.subject = subject;
    }
    
    // Getters and Setters
    public String getOutTradeNo() {
        return outTradeNo;
    }
    
    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }
    
    public PaymentType getPaymentType() {
        return paymentType;
    }
    
    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public String getSubject() {
        return subject;
    }
    
    public void setSubject(String subject) {
        this.subject = subject;
    }
    
    public String getBody() {
        return body;
    }
    
    public void setBody(String body) {
        this.body = body;
    }
    
    public LocalDateTime getExpireTime() {
        return expireTime;
    }
    
    public void setExpireTime(LocalDateTime expireTime) {
        this.expireTime = expireTime;
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
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getClientIp() {
        return clientIp;
    }
    
    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }
    
    public Map<String, Object> getExtraParams() {
        return extraParams;
    }
    
    public void setExtraParams(Map<String, Object> extraParams) {
        this.extraParams = extraParams;
    }
    
    @Override
    public String toString() {
        return "PaymentRequest{" +
                "outTradeNo='" + outTradeNo + '\'' +
                ", paymentType=" + paymentType +
                ", amount=" + amount +
                ", subject='" + subject + '\'' +
                ", body='" + body + '\'' +
                ", expireTime=" + expireTime +
                ", notifyUrl='" + notifyUrl + '\'' +
                ", returnUrl='" + returnUrl + '\'' +
                ", userId='" + userId + '\'' +
                ", clientIp='" + clientIp + '\'' +
                ", extraParams=" + extraParams +
                '}';
    }
}