package com.tensor.pay.service;

import com.tensor.pay.model.PaymentRequest;
import com.tensor.pay.model.PaymentResponse;

/**
 * 支付服务接口
 * 
 * @author Tensor Pay
 * @since 1.0.0
 */
public interface PaymentService {
    
    /**
     * 创建支付订单
     * 
     * @param request 支付请求
     * @return 支付响应
     */
    PaymentResponse createPayment(PaymentRequest request);
    
    /**
     * 查询支付状态
     * 
     * @param outTradeNo 商户订单号
     * @return 支付响应
     */
    PaymentResponse queryPayment(String outTradeNo);
    
    /**
     * 取消支付
     * 
     * @param outTradeNo 商户订单号
     * @return 支付响应
     */
    PaymentResponse cancelPayment(String outTradeNo);
    
    /**
     * 申请退款
     * 
     * @param outTradeNo 商户订单号
     * @param refundAmount 退款金额
     * @param refundReason 退款原因
     * @return 支付响应
     */
    PaymentResponse refund(String outTradeNo, java.math.BigDecimal refundAmount, String refundReason);
    
    /**
     * 查询退款状态
     * 
     * @param outTradeNo 商户订单号
     * @param outRefundNo 商户退款号
     * @return 支付响应
     */
    PaymentResponse queryRefund(String outTradeNo, String outRefundNo);
    
    /**
     * 处理支付回调
     * 
     * @param callbackData 回调数据
     * @return 处理结果
     */
    PaymentResponse handleCallback(String callbackData);
    
    /**
     * 验证回调签名
     * 
     * @param callbackData 回调数据
     * @param signature 签名
     * @return 验证结果
     */
    boolean verifyCallback(String callbackData, String signature);
}