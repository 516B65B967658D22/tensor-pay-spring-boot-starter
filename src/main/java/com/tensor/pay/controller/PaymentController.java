package com.tensor.pay.controller;

import com.tensor.pay.enums.PaymentType;
import com.tensor.pay.model.PaymentRequest;
import com.tensor.pay.model.PaymentResponse;
import com.tensor.pay.service.UnifiedPaymentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Set;

/**
 * 支付控制器示例
 * 
 * @author Tensor Pay
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/payment")
public class PaymentController {
    
    private final UnifiedPaymentService unifiedPaymentService;
    
    @Autowired
    public PaymentController(UnifiedPaymentService unifiedPaymentService) {
        this.unifiedPaymentService = unifiedPaymentService;
    }
    
    /**
     * 创建支付订单
     */
    @PostMapping("/create")
    public ResponseEntity<PaymentResponse> createPayment(@Valid @RequestBody PaymentRequest request) {
        PaymentResponse response = unifiedPaymentService.createPayment(request);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 查询支付状态
     */
    @GetMapping("/query")
    public ResponseEntity<PaymentResponse> queryPayment(
            @RequestParam PaymentType paymentType,
            @RequestParam String outTradeNo) {
        PaymentResponse response = unifiedPaymentService.queryPayment(paymentType, outTradeNo);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 取消支付
     */
    @PostMapping("/cancel")
    public ResponseEntity<PaymentResponse> cancelPayment(
            @RequestParam PaymentType paymentType,
            @RequestParam String outTradeNo) {
        PaymentResponse response = unifiedPaymentService.cancelPayment(paymentType, outTradeNo);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 申请退款
     */
    @PostMapping("/refund")
    public ResponseEntity<PaymentResponse> refund(
            @RequestParam PaymentType paymentType,
            @RequestParam String outTradeNo,
            @RequestParam BigDecimal refundAmount,
            @RequestParam(required = false) String refundReason) {
        PaymentResponse response = unifiedPaymentService.refund(
            paymentType, outTradeNo, refundAmount, refundReason);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 查询退款状态
     */
    @GetMapping("/refund/query")
    public ResponseEntity<PaymentResponse> queryRefund(
            @RequestParam PaymentType paymentType,
            @RequestParam String outTradeNo,
            @RequestParam String outRefundNo) {
        PaymentResponse response = unifiedPaymentService.queryRefund(
            paymentType, outTradeNo, outRefundNo);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 获取支持的支付类型
     */
    @GetMapping("/types")
    public ResponseEntity<Set<PaymentType>> getSupportedPaymentTypes() {
        Set<PaymentType> types = unifiedPaymentService.getSupportedPaymentTypes();
        return ResponseEntity.ok(types);
    }
    
    /**
     * 微信支付回调
     */
    @PostMapping("/wechat/notify")
    public ResponseEntity<String> wechatNotify(@RequestBody String callbackData) {
        try {
            PaymentResponse response = unifiedPaymentService.handleCallback(PaymentType.WECHAT, callbackData);
            return ResponseEntity.ok(response.isSuccess() ? "SUCCESS" : "FAIL");
        } catch (Exception e) {
            return ResponseEntity.ok("FAIL");
        }
    }
    
    /**
     * 支付宝支付回调
     */
    @PostMapping("/alipay/notify")
    public ResponseEntity<String> alipayNotify(@RequestParam java.util.Map<String, String> params) {
        try {
            // 将参数转换为字符串格式
            StringBuilder callbackData = new StringBuilder();
            for (java.util.Map.Entry<String, String> entry : params.entrySet()) {
                if (callbackData.length() > 0) {
                    callbackData.append("&");
                }
                callbackData.append(entry.getKey()).append("=").append(entry.getValue());
            }
            
            PaymentResponse response = unifiedPaymentService.handleCallback(
                PaymentType.ALIPAY, callbackData.toString());
            return ResponseEntity.ok(response.isSuccess() ? "success" : "fail");
        } catch (Exception e) {
            return ResponseEntity.ok("fail");
        }
    }
    
    /**
     * 银行支付回调
     */
    @PostMapping("/bank/notify")
    public ResponseEntity<String> bankNotify(@RequestBody String callbackData) {
        try {
            PaymentResponse response = unifiedPaymentService.handleCallback(PaymentType.BANK, callbackData);
            return ResponseEntity.ok(response.isSuccess() ? "SUCCESS" : "FAIL");
        } catch (Exception e) {
            return ResponseEntity.ok("FAIL");
        }
    }
}