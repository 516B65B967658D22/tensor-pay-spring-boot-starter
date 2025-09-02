package com.tensor.pay.example;

import com.tensor.pay.enums.PaymentType;
import com.tensor.pay.model.PaymentRequest;
import com.tensor.pay.model.PaymentResponse;
import com.tensor.pay.service.UnifiedPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付示例控制器
 * 
 * @author Tensor Pay
 * @since 1.0.0
 */
@RestController
@RequestMapping("/example")
public class PaymentExampleController {
    
    @Autowired
    private UnifiedPaymentService paymentService;
    
    /**
     * 创建微信支付示例
     */
    @PostMapping("/wechat/pay")
    public PaymentResponse createWechatPayment() {
        PaymentRequest request = new PaymentRequest();
        request.setOutTradeNo("WX_" + System.currentTimeMillis());
        request.setPaymentType(PaymentType.WECHAT);
        request.setAmount(new BigDecimal("0.01"));
        request.setSubject("测试商品");
        request.setBody("这是一个微信支付测试订单");
        request.setExpireTime(LocalDateTime.now().plusMinutes(30));
        
        return paymentService.createPayment(request);
    }
    
    /**
     * 创建支付宝支付示例
     */
    @PostMapping("/alipay/pay")
    public PaymentResponse createAlipayPayment() {
        PaymentRequest request = new PaymentRequest();
        request.setOutTradeNo("ALI_" + System.currentTimeMillis());
        request.setPaymentType(PaymentType.ALIPAY);
        request.setAmount(new BigDecimal("0.01"));
        request.setSubject("测试商品");
        request.setBody("这是一个支付宝支付测试订单");
        request.setExpireTime(LocalDateTime.now().plusMinutes(30));
        
        return paymentService.createPayment(request);
    }
    
    /**
     * 创建银行支付示例
     */
    @PostMapping("/bank/pay")
    public PaymentResponse createBankPayment() {
        PaymentRequest request = new PaymentRequest();
        request.setOutTradeNo("BANK_" + System.currentTimeMillis());
        request.setPaymentType(PaymentType.BANK);
        request.setAmount(new BigDecimal("100.00"));
        request.setSubject("测试商品");
        request.setBody("这是一个银行支付测试订单");
        request.setExpireTime(LocalDateTime.now().plusMinutes(30));
        
        return paymentService.createPayment(request);
    }
    
    /**
     * 查询支付状态示例
     */
    @GetMapping("/query/{paymentType}/{outTradeNo}")
    public PaymentResponse queryPayment(@PathVariable String paymentType, 
                                       @PathVariable String outTradeNo) {
        PaymentType type = PaymentType.fromCode(paymentType);
        return paymentService.queryPayment(type, outTradeNo);
    }
    
    /**
     * 退款示例
     */
    @PostMapping("/refund")
    public PaymentResponse refund(@RequestParam String paymentType,
                                 @RequestParam String outTradeNo,
                                 @RequestParam BigDecimal amount,
                                 @RequestParam(required = false) String reason) {
        PaymentType type = PaymentType.fromCode(paymentType);
        return paymentService.refund(type, outTradeNo, amount, reason);
    }
    
    /**
     * 获取支持的支付类型
     */
    @GetMapping("/types")
    public java.util.Set<PaymentType> getSupportedTypes() {
        return paymentService.getSupportedPaymentTypes();
    }
}