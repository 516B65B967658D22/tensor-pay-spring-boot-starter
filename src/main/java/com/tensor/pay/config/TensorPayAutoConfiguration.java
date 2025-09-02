package com.tensor.pay.config;

import com.tensor.pay.factory.PaymentServiceFactory;
import com.tensor.pay.service.UnifiedPaymentService;
import com.tensor.pay.utils.HttpUtils;
import com.tensor.pay.utils.SignatureUtils;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

/**
 * Tensor Pay 自动配置类
 * 
 * @author Tensor Pay
 * @since 1.0.0
 */
@AutoConfiguration
@EnableConfigurationProperties({
    WechatPayProperties.class,
    AlipayProperties.class,
    BankPayProperties.class
})
@ComponentScan(basePackages = "com.tensor.pay")
public class TensorPayAutoConfiguration {
    
    @Bean
    @ConditionalOnMissingBean
    public HttpUtils httpUtils() {
        return new HttpUtils();
    }
    
    @Bean
    @ConditionalOnMissingBean
    public SignatureUtils signatureUtils() {
        return new SignatureUtils();
    }
    
    @Bean
    @ConditionalOnMissingBean
    public PaymentServiceFactory paymentServiceFactory(java.util.List<com.tensor.pay.service.PaymentService> services) {
        return new PaymentServiceFactory(services);
    }
    
    @Bean
    @ConditionalOnMissingBean
    public UnifiedPaymentService unifiedPaymentService(PaymentServiceFactory paymentServiceFactory) {
        return new UnifiedPaymentService(paymentServiceFactory);
    }
}