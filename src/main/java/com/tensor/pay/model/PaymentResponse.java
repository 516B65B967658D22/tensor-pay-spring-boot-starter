package com.tensor.pay.model;

import com.tensor.pay.enums.PaymentStatus;
import com.tensor.pay.enums.PaymentType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 统一支付响应模型
 * 
 * @author Tensor Pay
 * @since 1.0.0
 */
public class PaymentResponse {
    
    /**
     * 是否成功
     */
    private boolean success;
    
    /**
     * 错误码
     */
    private String errorCode;
    
    /**
     * 错误信息
     */
    private String errorMessage;
    
    /**
     * 商户订单号
     */
    private String outTradeNo;
    
    /**
     * 第三方交易号
     */
    private String tradeNo;
    
    /**
     * 支付类型
     */
    private PaymentType paymentType;
    
    /**
     * 支付状态
     */
    private PaymentStatus status;
    
    /**
     * 支付金额
     */
    private BigDecimal amount;
    
    /**
     * 实际支付金额
     */
    private BigDecimal paidAmount;
    
    /**
     * 支付时间
     */
    private LocalDateTime payTime;
    
    /**
     * 支付URL或二维码内容（用于扫码支付）
     */
    private String payUrl;
    
    /**
     * 支付参数（用于APP支付等）
     */
    private String payParams;
    
    /**
     * 扩展数据
     */
    private Map<String, Object> extraData;
    
    // Constructors
    public PaymentResponse() {}
    
    public static PaymentResponse success() {
        PaymentResponse response = new PaymentResponse();
        response.setSuccess(true);
        return response;
    }
    
    public static PaymentResponse failure(String errorCode, String errorMessage) {
        PaymentResponse response = new PaymentResponse();
        response.setSuccess(false);
        response.setErrorCode(errorCode);
        response.setErrorMessage(errorMessage);
        return response;
    }
    
    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    public String getOutTradeNo() {
        return outTradeNo;
    }
    
    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }
    
    public String getTradeNo() {
        return tradeNo;
    }
    
    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
    }
    
    public PaymentType getPaymentType() {
        return paymentType;
    }
    
    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }
    
    public PaymentStatus getStatus() {
        return status;
    }
    
    public void setStatus(PaymentStatus status) {
        this.status = status;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public BigDecimal getPaidAmount() {
        return paidAmount;
    }
    
    public void setPaidAmount(BigDecimal paidAmount) {
        this.paidAmount = paidAmount;
    }
    
    public LocalDateTime getPayTime() {
        return payTime;
    }
    
    public void setPayTime(LocalDateTime payTime) {
        this.payTime = payTime;
    }
    
    public String getPayUrl() {
        return payUrl;
    }
    
    public void setPayUrl(String payUrl) {
        this.payUrl = payUrl;
    }
    
    public String getPayParams() {
        return payParams;
    }
    
    public void setPayParams(String payParams) {
        this.payParams = payParams;
    }
    
    public Map<String, Object> getExtraData() {
        return extraData;
    }
    
    public void setExtraData(Map<String, Object> extraData) {
        this.extraData = extraData;
    }
    
    @Override
    public String toString() {
        return "PaymentResponse{" +
                "success=" + success +
                ", errorCode='" + errorCode + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                ", outTradeNo='" + outTradeNo + '\'' +
                ", tradeNo='" + tradeNo + '\'' +
                ", paymentType=" + paymentType +
                ", status=" + status +
                ", amount=" + amount +
                ", paidAmount=" + paidAmount +
                ", payTime=" + payTime +
                ", payUrl='" + payUrl + '\'' +
                ", payParams='" + payParams + '\'' +
                ", extraData=" + extraData +
                '}';
    }
}