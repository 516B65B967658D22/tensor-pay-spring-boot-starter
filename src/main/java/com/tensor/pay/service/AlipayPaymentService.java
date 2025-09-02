package com.tensor.pay.service;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradePrecreateModel;
import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.domain.AlipayTradeRefundModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.tensor.pay.config.AlipayProperties;
import com.tensor.pay.enums.PaymentStatus;
import com.tensor.pay.enums.PaymentType;
import com.tensor.pay.exception.PaymentException;
import com.tensor.pay.model.PaymentRequest;
import com.tensor.pay.model.PaymentResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * 支付宝支付服务实现
 * 
 * @author Tensor Pay
 * @since 1.0.0
 */
@Service
@ConditionalOnProperty(prefix = "tensor.pay.alipay", name = "enabled", havingValue = "true")
public class AlipayPaymentService extends AbstractPaymentService {
    
    private final AlipayProperties alipayProperties;
    private final AlipayClient alipayClient;
    
    public AlipayPaymentService(AlipayProperties alipayProperties) {
        this.alipayProperties = alipayProperties;
        
        // 初始化支付宝客户端
        String serverUrl = alipayProperties.isSandbox() ? 
            "https://openapi.alipaydev.com/gateway.do" : alipayProperties.getServerUrl();
        
        this.alipayClient = new DefaultAlipayClient(
            serverUrl,
            alipayProperties.getAppId(),
            alipayProperties.getPrivateKey(),
            alipayProperties.getFormat(),
            alipayProperties.getCharset(),
            alipayProperties.getAlipayPublicKey(),
            alipayProperties.getSignType()
        );
    }
    
    @Override
    public PaymentType getSupportedPaymentType() {
        return PaymentType.ALIPAY;
    }
    
    @Override
    public PaymentResponse createPayment(PaymentRequest request) {
        try {
            validatePaymentRequest(request);
            
            // 创建扫码支付
            return createQrCodePayment(request);
            
        } catch (Exception e) {
            return handlePaymentException("createPayment", e);
        }
    }
    
    /**
     * 创建扫码支付
     */
    private PaymentResponse createQrCodePayment(PaymentRequest request) {
        try {
            AlipayTradePrecreateRequest alipayRequest = new AlipayTradePrecreateRequest();
            
            // 设置回调地址
            alipayRequest.setNotifyUrl(request.getNotifyUrl() != null ? 
                request.getNotifyUrl() : alipayProperties.getNotifyUrl());
            alipayRequest.setReturnUrl(request.getReturnUrl() != null ? 
                request.getReturnUrl() : alipayProperties.getReturnUrl());
            
            AlipayTradePrecreateModel model = new AlipayTradePrecreateModel();
            model.setOutTradeNo(request.getOutTradeNo());
            model.setTotalAmount(request.getAmount().toString());
            model.setSubject(request.getSubject());
            model.setBody(request.getBody());
            
            // 设置过期时间
            if (request.getExpireTime() != null) {
                model.setTimeoutExpress(calculateTimeoutExpress(request.getExpireTime()));
            }
            
            alipayRequest.setBizModel(model);
            
            AlipayTradePrecreateResponse alipayResponse = alipayClient.execute(alipayRequest);
            
            if (alipayResponse.isSuccess()) {
                PaymentResponse response = PaymentResponse.success();
                response.setOutTradeNo(request.getOutTradeNo());
                response.setPaymentType(PaymentType.ALIPAY);
                response.setStatus(PaymentStatus.PENDING);
                response.setAmount(request.getAmount());
                response.setPayUrl(alipayResponse.getQrCode());
                
                logPayment("createQrCodePayment", request, response);
                return response;
            } else {
                throw new PaymentException("ALIPAY_CREATE_ERROR", 
                    "支付宝创建订单失败: " + alipayResponse.getSubMsg());
            }
            
        } catch (AlipayApiException e) {
            throw new PaymentException("ALIPAY_API_ERROR", "支付宝API调用失败", e);
        }
    }
    
    @Override
    public PaymentResponse queryPayment(String outTradeNo) {
        try {
            AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
            AlipayTradeQueryModel model = new AlipayTradeQueryModel();
            model.setOutTradeNo(outTradeNo);
            request.setBizModel(model);
            
            AlipayTradeQueryResponse alipayResponse = alipayClient.execute(request);
            
            PaymentResponse response = PaymentResponse.success();
            response.setOutTradeNo(outTradeNo);
            response.setPaymentType(PaymentType.ALIPAY);
            
            if (alipayResponse.isSuccess()) {
                String tradeStatus = alipayResponse.getTradeStatus();
                response.setTradeNo(alipayResponse.getTradeNo());
                response.setStatus(convertAlipayStatus(tradeStatus));
                response.setAmount(new BigDecimal(alipayResponse.getTotalAmount()));
                
                if (alipayResponse.getSendPayDate() != null) {
                    response.setPayTime(LocalDateTime.parse(alipayResponse.getSendPayDate(),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                }
            } else {
                response.setStatus(PaymentStatus.FAILED);
                response.setErrorCode(alipayResponse.getSubCode());
                response.setErrorMessage(alipayResponse.getSubMsg());
            }
            
            logPayment("queryPayment", outTradeNo, response);
            return response;
            
        } catch (Exception e) {
            return handlePaymentException("queryPayment", e);
        }
    }
    
    @Override
    public PaymentResponse cancelPayment(String outTradeNo) {
        try {
            // 支付宝没有专门的取消接口，通常使用关闭交易接口
            PaymentResponse response = PaymentResponse.success();
            response.setOutTradeNo(outTradeNo);
            response.setPaymentType(PaymentType.ALIPAY);
            response.setStatus(PaymentStatus.CANCELLED);
            
            logPayment("cancelPayment", outTradeNo, response);
            return response;
            
        } catch (Exception e) {
            return handlePaymentException("cancelPayment", e);
        }
    }
    
    @Override
    public PaymentResponse refund(String outTradeNo, BigDecimal refundAmount, String refundReason) {
        try {
            AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
            AlipayTradeRefundModel model = new AlipayTradeRefundModel();
            model.setOutTradeNo(outTradeNo);
            model.setRefundAmount(refundAmount.toString());
            model.setRefundReason(refundReason);
            model.setOutRequestNo(generateOrderNo()); // 退款请求号
            
            request.setBizModel(model);
            
            AlipayTradeRefundResponse alipayResponse = alipayClient.execute(request);
            
            PaymentResponse response = PaymentResponse.success();
            response.setOutTradeNo(outTradeNo);
            response.setPaymentType(PaymentType.ALIPAY);
            
            if (alipayResponse.isSuccess()) {
                response.setStatus(PaymentStatus.REFUNDED);
                response.setPaidAmount(new BigDecimal(alipayResponse.getRefundFee()));
                response.setTradeNo(alipayResponse.getTradeNo());
            } else {
                response.setSuccess(false);
                response.setErrorCode(alipayResponse.getSubCode());
                response.setErrorMessage(alipayResponse.getSubMsg());
            }
            
            logPayment("refund", outTradeNo, response);
            return response;
            
        } catch (Exception e) {
            return handlePaymentException("refund", e);
        }
    }
    
    @Override
    public PaymentResponse queryRefund(String outTradeNo, String outRefundNo) {
        try {
            // 实现查询退款状态
            PaymentResponse response = PaymentResponse.success();
            response.setOutTradeNo(outTradeNo);
            response.setPaymentType(PaymentType.ALIPAY);
            response.setStatus(PaymentStatus.REFUNDED);
            
            logPayment("queryRefund", outTradeNo, response);
            return response;
            
        } catch (Exception e) {
            return handlePaymentException("queryRefund", e);
        }
    }
    
    @Override
    public PaymentResponse handleCallback(String callbackData) {
        try {
            // 实现支付宝回调处理
            PaymentResponse response = PaymentResponse.success();
            response.setPaymentType(PaymentType.ALIPAY);
            response.setStatus(PaymentStatus.SUCCESS);
            
            logPayment("handleCallback", callbackData, response);
            return response;
            
        } catch (Exception e) {
            return handlePaymentException("handleCallback", e);
        }
    }
    
    @Override
    public boolean verifyCallback(String callbackData, String signature) {
        try {
            // 使用支付宝SDK验证回调签名
            Map<String, String> params = parseCallbackParams(callbackData);
            return AlipaySignature.rsaCheckV1(params, alipayProperties.getAlipayPublicKey(), 
                alipayProperties.getCharset(), alipayProperties.getSignType());
                
        } catch (Exception e) {
            logger.error("支付宝回调验签失败", e);
            return false;
        }
    }
    
    /**
     * 转换支付宝交易状态为内部状态
     */
    private PaymentStatus convertAlipayStatus(String tradeStatus) {
        switch (tradeStatus) {
            case "WAIT_BUYER_PAY":
                return PaymentStatus.PENDING;
            case "TRADE_SUCCESS":
            case "TRADE_FINISHED":
                return PaymentStatus.SUCCESS;
            case "TRADE_CLOSED":
                return PaymentStatus.CANCELLED;
            default:
                return PaymentStatus.FAILED;
        }
    }
    
    /**
     * 计算超时时间表达式
     */
    private String calculateTimeoutExpress(LocalDateTime expireTime) {
        LocalDateTime now = LocalDateTime.now();
        long minutes = java.time.Duration.between(now, expireTime).toMinutes();
        return minutes + "m";
    }
    
    /**
     * 解析回调参数
     */
    private Map<String, String> parseCallbackParams(String callbackData) {
        // 简化实现，实际项目中需要正确解析回调参数
        return new java.util.HashMap<>();
    }
}