package com.tensor.pay.service;

import com.tensor.pay.enums.PaymentType;
import com.tensor.pay.exception.PaymentException;
import com.tensor.pay.model.PaymentRequest;
import com.tensor.pay.model.PaymentResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 抽象支付服务基类
 * 
 * @author Tensor Pay
 * @since 1.0.0
 */
public abstract class AbstractPaymentService implements PaymentService {
    
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    
    /**
     * 获取支持的支付类型
     * 
     * @return 支付类型
     */
    public abstract PaymentType getSupportedPaymentType();
    
    /**
     * 验证支付请求参数
     * 
     * @param request 支付请求
     */
    protected void validatePaymentRequest(PaymentRequest request) {
        if (request == null) {
            throw new PaymentException("INVALID_REQUEST", "支付请求不能为空");
        }
        
        if (request.getPaymentType() != getSupportedPaymentType()) {
            throw new PaymentException("UNSUPPORTED_PAYMENT_TYPE", 
                "不支持的支付类型: " + request.getPaymentType());
        }
        
        if (request.getAmount() == null || request.getAmount().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new PaymentException("INVALID_AMOUNT", "支付金额必须大于0");
        }
        
        if (request.getOutTradeNo() == null || request.getOutTradeNo().trim().isEmpty()) {
            throw new PaymentException("INVALID_OUT_TRADE_NO", "商户订单号不能为空");
        }
        
        if (request.getSubject() == null || request.getSubject().trim().isEmpty()) {
            throw new PaymentException("INVALID_SUBJECT", "商品描述不能为空");
        }
    }
    
    /**
     * 生成订单号
     * 
     * @return 订单号
     */
    protected String generateOrderNo() {
        return System.currentTimeMillis() + String.valueOf((int)(Math.random() * 1000));
    }
    
    /**
     * 记录支付日志
     * 
     * @param operation 操作类型
     * @param request 请求参数
     * @param response 响应结果
     */
    protected void logPayment(String operation, Object request, Object response) {
        logger.info("Payment operation: {}, Request: {}, Response: {}", 
            operation, request, response);
    }
    
    /**
     * 处理支付异常
     * 
     * @param operation 操作类型
     * @param e 异常
     * @return 错误响应
     */
    protected PaymentResponse handlePaymentException(String operation, Exception e) {
        logger.error("Payment operation failed: {}", operation, e);
        
        if (e instanceof PaymentException) {
            PaymentException pe = (PaymentException) e;
            return PaymentResponse.failure(pe.getErrorCode(), pe.getMessage());
        }
        
        return PaymentResponse.failure("PAYMENT_ERROR", "支付处理失败: " + e.getMessage());
    }
}