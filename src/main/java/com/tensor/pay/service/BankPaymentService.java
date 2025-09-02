package com.tensor.pay.service;

import com.tensor.pay.config.BankPayProperties;
import com.tensor.pay.enums.PaymentStatus;
import com.tensor.pay.enums.PaymentType;
import com.tensor.pay.exception.PaymentException;
import com.tensor.pay.model.PaymentRequest;
import com.tensor.pay.model.PaymentResponse;
import com.tensor.pay.utils.HttpUtils;
import com.tensor.pay.utils.SignatureUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * 银行支付服务实现
 * 
 * @author Tensor Pay
 * @since 1.0.0
 */
@Service
@ConditionalOnProperty(prefix = "tensor.pay.bank", name = "enabled", havingValue = "true")
public class BankPaymentService extends AbstractPaymentService {
    
    private final BankPayProperties bankPayProperties;
    private final HttpUtils httpUtils;
    private final SignatureUtils signatureUtils;
    
    public BankPaymentService(BankPayProperties bankPayProperties, 
                             HttpUtils httpUtils, 
                             SignatureUtils signatureUtils) {
        this.bankPayProperties = bankPayProperties;
        this.httpUtils = httpUtils;
        this.signatureUtils = signatureUtils;
    }
    
    @Override
    public PaymentType getSupportedPaymentType() {
        return PaymentType.BANK;
    }
    
    @Override
    public PaymentResponse createPayment(PaymentRequest request) {
        try {
            validatePaymentRequest(request);
            
            // 构建银行支付请求参数
            Map<String, String> params = buildPaymentParams(request);
            
            // 生成签名
            String signature = signatureUtils.generateSignature(params, bankPayProperties.getMerchantKey());
            params.put("sign", signature);
            
            // 发送请求到银行网关
            String response = httpUtils.post(bankPayProperties.getGatewayUrl(), params);
            
            // 解析响应
            return parsePaymentResponse(request, response);
            
        } catch (Exception e) {
            return handlePaymentException("createPayment", e);
        }
    }
    
    @Override
    public PaymentResponse queryPayment(String outTradeNo) {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("merchantId", bankPayProperties.getMerchantId());
            params.put("outTradeNo", outTradeNo);
            params.put("timestamp", String.valueOf(System.currentTimeMillis()));
            
            String signature = signatureUtils.generateSignature(params, bankPayProperties.getMerchantKey());
            params.put("sign", signature);
            
            String response = httpUtils.post(bankPayProperties.getGatewayUrl() + "/query", params);
            
            return parseQueryResponse(outTradeNo, response);
            
        } catch (Exception e) {
            return handlePaymentException("queryPayment", e);
        }
    }
    
    @Override
    public PaymentResponse cancelPayment(String outTradeNo) {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("merchantId", bankPayProperties.getMerchantId());
            params.put("outTradeNo", outTradeNo);
            params.put("timestamp", String.valueOf(System.currentTimeMillis()));
            
            String signature = signatureUtils.generateSignature(params, bankPayProperties.getMerchantKey());
            params.put("sign", signature);
            
            String response = httpUtils.post(bankPayProperties.getGatewayUrl() + "/cancel", params);
            
            PaymentResponse paymentResponse = PaymentResponse.success();
            paymentResponse.setOutTradeNo(outTradeNo);
            paymentResponse.setPaymentType(PaymentType.BANK);
            paymentResponse.setStatus(PaymentStatus.CANCELLED);
            
            logPayment("cancelPayment", outTradeNo, paymentResponse);
            return paymentResponse;
            
        } catch (Exception e) {
            return handlePaymentException("cancelPayment", e);
        }
    }
    
    @Override
    public PaymentResponse refund(String outTradeNo, BigDecimal refundAmount, String refundReason) {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("merchantId", bankPayProperties.getMerchantId());
            params.put("outTradeNo", outTradeNo);
            params.put("refundAmount", refundAmount.toString());
            params.put("refundReason", refundReason);
            params.put("outRefundNo", generateOrderNo());
            params.put("timestamp", String.valueOf(System.currentTimeMillis()));
            
            String signature = signatureUtils.generateSignature(params, bankPayProperties.getMerchantKey());
            params.put("sign", signature);
            
            String response = httpUtils.post(bankPayProperties.getGatewayUrl() + "/refund", params);
            
            PaymentResponse paymentResponse = PaymentResponse.success();
            paymentResponse.setOutTradeNo(outTradeNo);
            paymentResponse.setPaymentType(PaymentType.BANK);
            paymentResponse.setStatus(PaymentStatus.REFUNDED);
            paymentResponse.setPaidAmount(refundAmount);
            
            logPayment("refund", outTradeNo, paymentResponse);
            return paymentResponse;
            
        } catch (Exception e) {
            return handlePaymentException("refund", e);
        }
    }
    
    @Override
    public PaymentResponse queryRefund(String outTradeNo, String outRefundNo) {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("merchantId", bankPayProperties.getMerchantId());
            params.put("outTradeNo", outTradeNo);
            params.put("outRefundNo", outRefundNo);
            params.put("timestamp", String.valueOf(System.currentTimeMillis()));
            
            String signature = signatureUtils.generateSignature(params, bankPayProperties.getMerchantKey());
            params.put("sign", signature);
            
            String response = httpUtils.post(bankPayProperties.getGatewayUrl() + "/refund/query", params);
            
            PaymentResponse paymentResponse = PaymentResponse.success();
            paymentResponse.setOutTradeNo(outTradeNo);
            paymentResponse.setPaymentType(PaymentType.BANK);
            paymentResponse.setStatus(PaymentStatus.REFUNDED);
            
            logPayment("queryRefund", outTradeNo, paymentResponse);
            return paymentResponse;
            
        } catch (Exception e) {
            return handlePaymentException("queryRefund", e);
        }
    }
    
    @Override
    public PaymentResponse handleCallback(String callbackData) {
        try {
            // 解析银行回调数据
            Map<String, String> params = parseCallbackParams(callbackData);
            
            // 验证签名
            if (!verifyCallback(callbackData, params.get("sign"))) {
                throw new PaymentException("INVALID_SIGNATURE", "回调签名验证失败");
            }
            
            PaymentResponse response = PaymentResponse.success();
            response.setOutTradeNo(params.get("outTradeNo"));
            response.setTradeNo(params.get("tradeNo"));
            response.setPaymentType(PaymentType.BANK);
            response.setStatus(convertBankStatus(params.get("status")));
            
            if (params.get("amount") != null) {
                response.setPaidAmount(new BigDecimal(params.get("amount")));
            }
            
            logPayment("handleCallback", callbackData, response);
            return response;
            
        } catch (Exception e) {
            return handlePaymentException("handleCallback", e);
        }
    }
    
    @Override
    public boolean verifyCallback(String callbackData, String signature) {
        try {
            Map<String, String> params = parseCallbackParams(callbackData);
            params.remove("sign"); // 移除签名参数
            
            String expectedSignature = signatureUtils.generateSignature(params, bankPayProperties.getMerchantKey());
            return signature != null && signature.equals(expectedSignature);
            
        } catch (Exception e) {
            logger.error("银行支付回调验签失败", e);
            return false;
        }
    }
    
    /**
     * 构建支付请求参数
     */
    private Map<String, String> buildPaymentParams(PaymentRequest request) {
        Map<String, String> params = new HashMap<>();
        params.put("merchantId", bankPayProperties.getMerchantId());
        params.put("outTradeNo", request.getOutTradeNo());
        params.put("amount", request.getAmount().toString());
        params.put("subject", request.getSubject());
        params.put("body", request.getBody());
        params.put("notifyUrl", request.getNotifyUrl() != null ? 
            request.getNotifyUrl() : bankPayProperties.getNotifyUrl());
        params.put("returnUrl", request.getReturnUrl() != null ? 
            request.getReturnUrl() : bankPayProperties.getReturnUrl());
        params.put("timestamp", String.valueOf(System.currentTimeMillis()));
        
        if (request.getExpireTime() != null) {
            params.put("expireTime", request.getExpireTime()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
        
        return params;
    }
    
    /**
     * 解析支付响应
     */
    private PaymentResponse parsePaymentResponse(PaymentRequest request, String responseData) {
        // 简化实现，实际项目中需要根据银行接口文档解析响应
        PaymentResponse response = PaymentResponse.success();
        response.setOutTradeNo(request.getOutTradeNo());
        response.setPaymentType(PaymentType.BANK);
        response.setStatus(PaymentStatus.PENDING);
        response.setAmount(request.getAmount());
        
        // 假设银行返回支付URL
        response.setPayUrl(bankPayProperties.getGatewayUrl() + "/pay?orderNo=" + request.getOutTradeNo());
        
        return response;
    }
    
    /**
     * 解析查询响应
     */
    private PaymentResponse parseQueryResponse(String outTradeNo, String responseData) {
        // 简化实现
        PaymentResponse response = PaymentResponse.success();
        response.setOutTradeNo(outTradeNo);
        response.setPaymentType(PaymentType.BANK);
        response.setStatus(PaymentStatus.SUCCESS);
        
        return response;
    }
    
    /**
     * 转换银行支付状态
     */
    private PaymentStatus convertBankStatus(String bankStatus) {
        if (bankStatus == null) {
            return PaymentStatus.PENDING;
        }
        
        switch (bankStatus.toUpperCase()) {
            case "SUCCESS":
            case "PAID":
                return PaymentStatus.SUCCESS;
            case "FAILED":
            case "ERROR":
                return PaymentStatus.FAILED;
            case "CANCELLED":
            case "CLOSED":
                return PaymentStatus.CANCELLED;
            case "REFUNDED":
                return PaymentStatus.REFUNDED;
            default:
                return PaymentStatus.PENDING;
        }
    }
    
    /**
     * 解析回调参数
     */
    private Map<String, String> parseCallbackParams(String callbackData) {
        // 简化实现，实际项目中需要根据回调数据格式解析
        Map<String, String> params = new HashMap<>();
        
        // 假设回调数据是form格式或JSON格式
        if (callbackData.contains("=") && callbackData.contains("&")) {
            // Form格式
            String[] pairs = callbackData.split("&");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=", 2);
                if (keyValue.length == 2) {
                    params.put(keyValue[0], keyValue[1]);
                }
            }
        }
        
        return params;
    }
}