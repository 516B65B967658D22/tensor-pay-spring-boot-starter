package com.tensor.pay.factory;

import com.tensor.pay.enums.PaymentType;
import com.tensor.pay.exception.PaymentException;
import com.tensor.pay.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 支付服务工厂
 * 
 * @author Tensor Pay
 * @since 1.0.0
 */
@Component
public class PaymentServiceFactory {
    
    private final Map<PaymentType, PaymentService> paymentServices = new ConcurrentHashMap<>();
    
    @Autowired
    public PaymentServiceFactory(List<PaymentService> services) {
        // 注册所有支付服务
        for (PaymentService service : services) {
            if (service instanceof com.tensor.pay.service.AbstractPaymentService) {
                com.tensor.pay.service.AbstractPaymentService abstractService = 
                    (com.tensor.pay.service.AbstractPaymentService) service;
                paymentServices.put(abstractService.getSupportedPaymentType(), service);
            }
        }
    }
    
    /**
     * 获取支付服务
     * 
     * @param paymentType 支付类型
     * @return 支付服务
     */
    public PaymentService getPaymentService(PaymentType paymentType) {
        PaymentService service = paymentServices.get(paymentType);
        if (service == null) {
            throw new PaymentException("UNSUPPORTED_PAYMENT_TYPE", 
                "不支持的支付类型: " + paymentType);
        }
        return service;
    }
    
    /**
     * 获取所有支持的支付类型
     * 
     * @return 支付类型列表
     */
    public java.util.Set<PaymentType> getSupportedPaymentTypes() {
        return paymentServices.keySet();
    }
    
    /**
     * 检查是否支持指定的支付类型
     * 
     * @param paymentType 支付类型
     * @return 是否支持
     */
    public boolean isSupported(PaymentType paymentType) {
        return paymentServices.containsKey(paymentType);
    }
}