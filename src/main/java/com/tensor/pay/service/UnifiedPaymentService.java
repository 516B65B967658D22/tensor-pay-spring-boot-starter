package com.tensor.pay.service;

import com.tensor.pay.factory.PaymentServiceFactory;
import com.tensor.pay.model.PaymentRequest;
import com.tensor.pay.model.PaymentResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * 统一支付服务
 * 提供统一的支付接口，自动路由到对应的支付服务
 * 
 * @author Tensor Pay
 * @since 1.0.0
 */
@Service
public class UnifiedPaymentService {
    
    private final PaymentServiceFactory paymentServiceFactory;
    
    @Autowired
    public UnifiedPaymentService(PaymentServiceFactory paymentServiceFactory) {
        this.paymentServiceFactory = paymentServiceFactory;
    }
    
    /**
     * 创建支付订单
     * 
     * @param request 支付请求
     * @return 支付响应
     */
    public PaymentResponse createPayment(PaymentRequest request) {
        PaymentService paymentService = paymentServiceFactory.getPaymentService(request.getPaymentType());
        return paymentService.createPayment(request);
    }
    
    /**
     * 查询支付状态
     * 
     * @param paymentType 支付类型
     * @param outTradeNo 商户订单号
     * @return 支付响应
     */
    public PaymentResponse queryPayment(com.tensor.pay.enums.PaymentType paymentType, String outTradeNo) {
        PaymentService paymentService = paymentServiceFactory.getPaymentService(paymentType);
        return paymentService.queryPayment(outTradeNo);
    }
    
    /**
     * 取消支付
     * 
     * @param paymentType 支付类型
     * @param outTradeNo 商户订单号
     * @return 支付响应
     */
    public PaymentResponse cancelPayment(com.tensor.pay.enums.PaymentType paymentType, String outTradeNo) {
        PaymentService paymentService = paymentServiceFactory.getPaymentService(paymentType);
        return paymentService.cancelPayment(outTradeNo);
    }
    
    /**
     * 申请退款
     * 
     * @param paymentType 支付类型
     * @param outTradeNo 商户订单号
     * @param refundAmount 退款金额
     * @param refundReason 退款原因
     * @return 支付响应
     */
    public PaymentResponse refund(com.tensor.pay.enums.PaymentType paymentType, 
                                 String outTradeNo, 
                                 BigDecimal refundAmount, 
                                 String refundReason) {
        PaymentService paymentService = paymentServiceFactory.getPaymentService(paymentType);
        return paymentService.refund(outTradeNo, refundAmount, refundReason);
    }
    
    /**
     * 查询退款状态
     * 
     * @param paymentType 支付类型
     * @param outTradeNo 商户订单号
     * @param outRefundNo 商户退款号
     * @return 支付响应
     */
    public PaymentResponse queryRefund(com.tensor.pay.enums.PaymentType paymentType, 
                                      String outTradeNo, 
                                      String outRefundNo) {
        PaymentService paymentService = paymentServiceFactory.getPaymentService(paymentType);
        return paymentService.queryRefund(outTradeNo, outRefundNo);
    }
    
    /**
     * 处理支付回调
     * 
     * @param paymentType 支付类型
     * @param callbackData 回调数据
     * @return 支付响应
     */
    public PaymentResponse handleCallback(com.tensor.pay.enums.PaymentType paymentType, String callbackData) {
        PaymentService paymentService = paymentServiceFactory.getPaymentService(paymentType);
        return paymentService.handleCallback(callbackData);
    }
    
    /**
     * 验证回调签名
     * 
     * @param paymentType 支付类型
     * @param callbackData 回调数据
     * @param signature 签名
     * @return 验证结果
     */
    public boolean verifyCallback(com.tensor.pay.enums.PaymentType paymentType, 
                                 String callbackData, 
                                 String signature) {
        PaymentService paymentService = paymentServiceFactory.getPaymentService(paymentType);
        return paymentService.verifyCallback(callbackData, signature);
    }
    
    /**
     * 获取所有支持的支付类型
     * 
     * @return 支付类型集合
     */
    public java.util.Set<com.tensor.pay.enums.PaymentType> getSupportedPaymentTypes() {
        return paymentServiceFactory.getSupportedPaymentTypes();
    }
    
    /**
     * 检查是否支持指定的支付类型
     * 
     * @param paymentType 支付类型
     * @return 是否支持
     */
    public boolean isSupported(com.tensor.pay.enums.PaymentType paymentType) {
        return paymentServiceFactory.isSupported(paymentType);
    }
}