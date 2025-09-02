package com.tensor.pay.service;

import com.tensor.pay.config.WechatPayProperties;
import com.tensor.pay.enums.PaymentStatus;
import com.tensor.pay.enums.PaymentType;
import com.tensor.pay.exception.PaymentException;
import com.tensor.pay.model.PaymentRequest;
import com.tensor.pay.model.PaymentResponse;
import com.wechat.pay.java.core.Config;
import com.wechat.pay.java.core.RSAAutoCertificateConfig;
import com.wechat.pay.java.service.payments.jsapi.JsapiServiceExtension;
import com.wechat.pay.java.service.payments.jsapi.model.*;
import com.wechat.pay.java.service.payments.nativepay.NativePayService;
import com.wechat.pay.java.service.payments.nativepay.model.PrepayRequest;
import com.wechat.pay.java.service.payments.nativepay.model.PrepayResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 微信支付服务实现
 * 
 * @author Tensor Pay
 * @since 1.0.0
 */
@Service
@ConditionalOnProperty(prefix = "tensor.pay.wechat", name = "enabled", havingValue = "true")
public class WechatPaymentService extends AbstractPaymentService {
    
    private final WechatPayProperties wechatPayProperties;
    private final Config config;
    private final NativePayService nativePayService;
    private final JsapiServiceExtension jsapiService;
    
    public WechatPaymentService(WechatPayProperties wechatPayProperties) {
        this.wechatPayProperties = wechatPayProperties;
        try {
            // 初始化微信支付配置
            this.config = new RSAAutoCertificateConfig.Builder()
                    .merchantId(wechatPayProperties.getMchId())
                    .privateKeyFromPath(wechatPayProperties.getPrivateKeyPath())
                    .merchantSerialNumber(wechatPayProperties.getMerchantSerialNumber())
                    .apiV3Key(wechatPayProperties.getApiV3Key())
                    .build();
            
            this.nativePayService = new NativePayService.Builder().config(config).build();
            this.jsapiService = new JsapiServiceExtension.Builder().config(config).build();
            
        } catch (Exception e) {
            throw new PaymentException("WECHAT_CONFIG_ERROR", "微信支付配置初始化失败", e);
        }
    }
    
    @Override
    public PaymentType getSupportedPaymentType() {
        return PaymentType.WECHAT;
    }
    
    @Override
    public PaymentResponse createPayment(PaymentRequest request) {
        try {
            validatePaymentRequest(request);
            
            // 根据不同场景选择不同的支付方式
            if (request.getUserId() != null) {
                // JSAPI支付（公众号、小程序）
                return createJsapiPayment(request);
            } else {
                // Native支付（扫码支付）
                return createNativePayment(request);
            }
            
        } catch (Exception e) {
            return handlePaymentException("createPayment", e);
        }
    }
    
    /**
     * 创建Native支付（扫码支付）
     */
    private PaymentResponse createNativePayment(PaymentRequest request) {
        try {
            PrepayRequest prepayRequest = new PrepayRequest();
            prepayRequest.setAppid(wechatPayProperties.getAppId());
            prepayRequest.setMchid(wechatPayProperties.getMchId());
            prepayRequest.setDescription(request.getSubject());
            prepayRequest.setOutTradeNo(request.getOutTradeNo());
            prepayRequest.setNotifyUrl(request.getNotifyUrl() != null ? 
                request.getNotifyUrl() : wechatPayProperties.getNotifyUrl());
            
            // 设置金额（微信支付金额单位为分）
            Amount amount = new Amount();
            amount.setTotal(request.getAmount().multiply(new BigDecimal("100")).intValue());
            amount.setCurrency("CNY");
            prepayRequest.setAmount(amount);
            
            // 设置过期时间
            if (request.getExpireTime() != null) {
                prepayRequest.setTimeExpire(request.getExpireTime()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss+08:00")));
            }
            
            PrepayResponse prepayResponse = nativePayService.prepay(prepayRequest);
            
            PaymentResponse response = PaymentResponse.success();
            response.setOutTradeNo(request.getOutTradeNo());
            response.setPaymentType(PaymentType.WECHAT);
            response.setStatus(PaymentStatus.PENDING);
            response.setAmount(request.getAmount());
            response.setPayUrl(prepayResponse.getCodeUrl());
            
            logPayment("createNativePayment", request, response);
            return response;
            
        } catch (Exception e) {
            throw new PaymentException("WECHAT_CREATE_PAYMENT_ERROR", "创建微信支付订单失败", e);
        }
    }
    
    /**
     * 创建JSAPI支付（公众号、小程序）
     */
    private PaymentResponse createJsapiPayment(PaymentRequest request) {
        try {
            com.wechat.pay.java.service.payments.jsapi.model.PrepayRequest prepayRequest = 
                new com.wechat.pay.java.service.payments.jsapi.model.PrepayRequest();
            prepayRequest.setAppid(wechatPayProperties.getAppId());
            prepayRequest.setMchid(wechatPayProperties.getMchId());
            prepayRequest.setDescription(request.getSubject());
            prepayRequest.setOutTradeNo(request.getOutTradeNo());
            prepayRequest.setNotifyUrl(request.getNotifyUrl() != null ? 
                request.getNotifyUrl() : wechatPayProperties.getNotifyUrl());
            
            // 设置用户标识
            Payer payer = new Payer();
            payer.setOpenid(request.getUserId());
            prepayRequest.setPayer(payer);
            
            // 设置金额
            com.wechat.pay.java.service.payments.jsapi.model.Amount amount = 
                new com.wechat.pay.java.service.payments.jsapi.model.Amount();
            amount.setTotal(request.getAmount().multiply(new BigDecimal("100")).intValue());
            amount.setCurrency("CNY");
            prepayRequest.setAmount(amount);
            
            // 设置过期时间
            if (request.getExpireTime() != null) {
                prepayRequest.setTimeExpire(request.getExpireTime()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss+08:00")));
            }
            
            com.wechat.pay.java.service.payments.jsapi.model.PrepayResponse prepayResponse = 
                jsapiService.prepay(prepayRequest);
            
            // 生成前端调用参数
            String payParams = jsapiService.createPayInfo(prepayResponse.getPrepayId());
            
            PaymentResponse response = PaymentResponse.success();
            response.setOutTradeNo(request.getOutTradeNo());
            response.setPaymentType(PaymentType.WECHAT);
            response.setStatus(PaymentStatus.PENDING);
            response.setAmount(request.getAmount());
            response.setPayParams(payParams);
            
            logPayment("createJsapiPayment", request, response);
            return response;
            
        } catch (Exception e) {
            throw new PaymentException("WECHAT_CREATE_JSAPI_ERROR", "创建微信JSAPI支付订单失败", e);
        }
    }
    
    @Override
    public PaymentResponse queryPayment(String outTradeNo) {
        try {
            // 实现查询支付状态的逻辑
            // 这里简化实现，实际项目中需要调用微信支付查询接口
            PaymentResponse response = PaymentResponse.success();
            response.setOutTradeNo(outTradeNo);
            response.setPaymentType(PaymentType.WECHAT);
            response.setStatus(PaymentStatus.PENDING);
            
            logPayment("queryPayment", outTradeNo, response);
            return response;
            
        } catch (Exception e) {
            return handlePaymentException("queryPayment", e);
        }
    }
    
    @Override
    public PaymentResponse cancelPayment(String outTradeNo) {
        try {
            // 实现取消支付的逻辑
            PaymentResponse response = PaymentResponse.success();
            response.setOutTradeNo(outTradeNo);
            response.setPaymentType(PaymentType.WECHAT);
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
            // 实现退款逻辑
            PaymentResponse response = PaymentResponse.success();
            response.setOutTradeNo(outTradeNo);
            response.setPaymentType(PaymentType.WECHAT);
            response.setStatus(PaymentStatus.REFUNDED);
            response.setPaidAmount(refundAmount);
            
            logPayment("refund", outTradeNo, response);
            return response;
            
        } catch (Exception e) {
            return handlePaymentException("refund", e);
        }
    }
    
    @Override
    public PaymentResponse queryRefund(String outTradeNo, String outRefundNo) {
        try {
            // 实现查询退款状态的逻辑
            PaymentResponse response = PaymentResponse.success();
            response.setOutTradeNo(outTradeNo);
            response.setPaymentType(PaymentType.WECHAT);
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
            // 实现回调处理逻辑
            // 解析回调数据，验证签名，更新订单状态
            PaymentResponse response = PaymentResponse.success();
            response.setPaymentType(PaymentType.WECHAT);
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
            // 实现签名验证逻辑
            // 使用微信支付的验签方法验证回调数据的真实性
            return true; // 简化实现
            
        } catch (Exception e) {
            logger.error("微信支付回调验签失败", e);
            return false;
        }
    }
}