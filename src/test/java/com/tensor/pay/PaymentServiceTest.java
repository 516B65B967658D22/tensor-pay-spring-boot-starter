package com.tensor.pay;

import com.tensor.pay.enums.PaymentType;
import com.tensor.pay.model.PaymentRequest;
import com.tensor.pay.model.PaymentResponse;
import com.tensor.pay.service.UnifiedPaymentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

/**
 * 支付服务测试类
 * 
 * @author Tensor Pay
 * @since 1.0.0
 */
@SpringBootTest
public class PaymentServiceTest {
    
    @Autowired
    private UnifiedPaymentService unifiedPaymentService;
    
    @Test
    public void testCreateWechatPayment() {
        PaymentRequest request = new PaymentRequest();
        request.setOutTradeNo("TEST" + System.currentTimeMillis());
        request.setPaymentType(PaymentType.WECHAT);
        request.setAmount(new BigDecimal("0.01"));
        request.setSubject("测试商品");
        request.setBody("这是一个测试商品");
        request.setNotifyUrl("https://your-domain.com/api/payment/wechat/notify");
        
        PaymentResponse response = unifiedPaymentService.createPayment(request);
        
        System.out.println("微信支付创建结果: " + response);
    }
    
    @Test
    public void testCreateAlipayPayment() {
        PaymentRequest request = new PaymentRequest();
        request.setOutTradeNo("TEST" + System.currentTimeMillis());
        request.setPaymentType(PaymentType.ALIPAY);
        request.setAmount(new BigDecimal("0.01"));
        request.setSubject("测试商品");
        request.setBody("这是一个测试商品");
        request.setNotifyUrl("https://your-domain.com/api/payment/alipay/notify");
        
        PaymentResponse response = unifiedPaymentService.createPayment(request);
        
        System.out.println("支付宝支付创建结果: " + response);
    }
    
    @Test
    public void testCreateBankPayment() {
        PaymentRequest request = new PaymentRequest();
        request.setOutTradeNo("TEST" + System.currentTimeMillis());
        request.setPaymentType(PaymentType.BANK);
        request.setAmount(new BigDecimal("100.00"));
        request.setSubject("测试商品");
        request.setBody("这是一个测试商品");
        request.setNotifyUrl("https://your-domain.com/api/payment/bank/notify");
        
        PaymentResponse response = unifiedPaymentService.createPayment(request);
        
        System.out.println("银行支付创建结果: " + response);
    }
    
    @Test
    public void testQueryPayment() {
        String outTradeNo = "TEST123456789";
        PaymentResponse response = unifiedPaymentService.queryPayment(PaymentType.WECHAT, outTradeNo);
        
        System.out.println("支付查询结果: " + response);
    }
}