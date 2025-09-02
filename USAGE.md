# Tensor Pay 使用指南

## 项目结构

```
tensor-pay-spring-boot-starter/
├── src/
│   ├── main/
│   │   ├── java/com/tensor/pay/
│   │   │   ├── config/                    # 配置类
│   │   │   │   ├── TensorPayAutoConfiguration.java
│   │   │   │   ├── WechatPayProperties.java
│   │   │   │   ├── AlipayProperties.java
│   │   │   │   └── BankPayProperties.java
│   │   │   ├── controller/                # 示例控制器
│   │   │   │   └── PaymentController.java
│   │   │   ├── enums/                     # 枚举类
│   │   │   │   ├── PaymentType.java
│   │   │   │   └── PaymentStatus.java
│   │   │   ├── exception/                 # 异常类
│   │   │   │   └── PaymentException.java
│   │   │   ├── factory/                   # 工厂类
│   │   │   │   └── PaymentServiceFactory.java
│   │   │   ├── model/                     # 数据模型
│   │   │   │   ├── PaymentRequest.java
│   │   │   │   └── PaymentResponse.java
│   │   │   ├── service/                   # 服务类
│   │   │   │   ├── PaymentService.java
│   │   │   │   ├── AbstractPaymentService.java
│   │   │   │   ├── UnifiedPaymentService.java
│   │   │   │   ├── WechatPaymentService.java
│   │   │   │   ├── AlipayPaymentService.java
│   │   │   │   └── BankPaymentService.java
│   │   │   ├── utils/                     # 工具类
│   │   │   │   ├── HttpUtils.java
│   │   │   │   └── SignatureUtils.java
│   │   │   └── example/                   # 示例代码
│   │   │       ├── PaymentExampleApplication.java
│   │   │       └── PaymentExampleController.java
│   │   └── resources/
│   │       ├── META-INF/
│   │       │   └── spring.factories       # Spring Boot自动配置
│   │       ├── static/
│   │       │   └── payment-test.html      # 测试页面
│   │       ├── application.yml            # 默认配置
│   │       └── application-example.yml    # 示例配置
│   └── test/
│       └── java/com/tensor/pay/
│           └── PaymentServiceTest.java    # 测试类
├── pom.xml                                # Maven配置
├── README.md                              # 项目说明
└── USAGE.md                               # 使用指南
```

## 快速集成指南

### 1. 在你的项目中添加依赖

```xml
<dependency>
    <groupId>com.tensor</groupId>
    <artifactId>tensor-pay-spring-boot-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 2. 配置支付参数

在你的 `application.yml` 中添加配置：

```yaml
tensor:
  pay:
    wechat:
      enabled: true
      app-id: ${WECHAT_APP_ID:}
      mch-id: ${WECHAT_MCH_ID:}
      private-key-path: ${WECHAT_PRIVATE_KEY_PATH:}
      merchant-serial-number: ${WECHAT_MERCHANT_SERIAL_NUMBER:}
      api-v3-key: ${WECHAT_API_V3_KEY:}
      notify-url: ${WECHAT_NOTIFY_URL:}
```

### 3. 注入服务并使用

```java
@RestController
public class OrderController {
    
    @Autowired
    private UnifiedPaymentService paymentService;
    
    @PostMapping("/order/pay")
    public ResponseEntity<PaymentResponse> payOrder(@RequestBody PayOrderRequest request) {
        // 构建支付请求
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setOutTradeNo(request.getOrderNo());
        paymentRequest.setPaymentType(PaymentType.valueOf(request.getPaymentType()));
        paymentRequest.setAmount(request.getAmount());
        paymentRequest.setSubject(request.getProductName());
        paymentRequest.setNotifyUrl("https://your-domain.com/api/payment/notify");
        
        // 创建支付
        PaymentResponse response = paymentService.createPayment(paymentRequest);
        
        return ResponseEntity.ok(response);
    }
}
```

## 环境变量配置

建议使用环境变量来管理敏感配置信息：

```bash
# 微信支付配置
export WECHAT_APP_ID=wx1234567890abcdef
export WECHAT_MCH_ID=1234567890
export WECHAT_PRIVATE_KEY_PATH=/path/to/wechat/private_key.pem
export WECHAT_MERCHANT_SERIAL_NUMBER=1234567890ABCDEF
export WECHAT_API_V3_KEY=your-api-v3-key
export WECHAT_NOTIFY_URL=https://your-domain.com/api/payment/wechat/notify

# 支付宝配置
export ALIPAY_APP_ID=2021000000000000
export ALIPAY_PRIVATE_KEY=your-alipay-private-key
export ALIPAY_PUBLIC_KEY=your-alipay-public-key
export ALIPAY_NOTIFY_URL=https://your-domain.com/api/payment/alipay/notify
```

## 回调处理最佳实践

### 1. 验证回调签名

```java
@PostMapping("/payment/notify")
public ResponseEntity<String> handlePaymentNotify(
        @RequestParam PaymentType paymentType,
        @RequestBody String callbackData,
        @RequestHeader(value = "signature", required = false) String signature) {
    
    // 验证签名
    if (!paymentService.verifyCallback(paymentType, callbackData, signature)) {
        return ResponseEntity.ok("SIGNATURE_INVALID");
    }
    
    // 处理回调
    PaymentResponse response = paymentService.handleCallback(paymentType, callbackData);
    
    if (response.isSuccess()) {
        // 更新业务订单状态
        orderService.updateOrderStatus(response.getOutTradeNo(), OrderStatus.PAID);
        return ResponseEntity.ok("SUCCESS");
    }
    
    return ResponseEntity.ok("FAIL");
}
```

### 2. 幂等性处理

```java
@Service
public class PaymentCallbackService {
    
    @Transactional
    public void handlePaymentCallback(PaymentResponse response) {
        // 检查订单是否已处理
        Order order = orderService.findByOrderNo(response.getOutTradeNo());
        if (order.getStatus() == OrderStatus.PAID) {
            logger.info("订单已支付，忽略重复回调: {}", response.getOutTradeNo());
            return;
        }
        
        // 更新订单状态
        order.setStatus(OrderStatus.PAID);
        order.setPayTime(response.getPayTime());
        order.setTradeNo(response.getTradeNo());
        orderService.save(order);
        
        // 发送支付成功通知
        notificationService.sendPaymentSuccessNotification(order);
    }
}
```

## 错误处理

### 常见错误码

| 错误码 | 说明 | 解决方案 |
|-------|------|----------|
| INVALID_REQUEST | 请求参数无效 | 检查请求参数是否完整 |
| UNSUPPORTED_PAYMENT_TYPE | 不支持的支付类型 | 检查支付类型是否正确配置 |
| PAYMENT_CONFIG_ERROR | 支付配置错误 | 检查支付配置参数 |
| SIGNATURE_INVALID | 签名验证失败 | 检查密钥配置是否正确 |
| PAYMENT_TIMEOUT | 支付超时 | 重新发起支付请求 |

### 异常处理示例

```java
@ControllerAdvice
public class PaymentExceptionHandler {
    
    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<ErrorResponse> handlePaymentException(PaymentException e) {
        ErrorResponse error = new ErrorResponse();
        error.setCode(e.getErrorCode());
        error.setMessage(e.getMessage());
        error.setTimestamp(LocalDateTime.now());
        
        return ResponseEntity.badRequest().body(error);
    }
}
```

## 测试指南

### 1. 启动示例应用

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=example
```

### 2. 访问测试页面

打开浏览器访问：`http://localhost:8080/payment-test.html`

### 3. 运行单元测试

```bash
mvn test
```

## 生产环境部署

### 1. 证书文件管理

将支付证书文件放在安全的位置：

```
/etc/tensor-pay/
├── wechat/
│   ├── apiclient_key.pem
│   └── apiclient_cert.pem
├── alipay/
│   ├── app_private_key.pem
│   └── alipay_public_key.pem
└── bank/
    └── merchant.p12
```

### 2. 配置加密

使用Spring Boot的配置加密功能保护敏感信息：

```yaml
tensor:
  pay:
    wechat:
      api-v3-key: ENC(encrypted-api-key)
      private-key-path: ENC(encrypted-path)
```

### 3. 监控和日志

```yaml
logging:
  level:
    com.tensor.pay: INFO
  pattern:
    file: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
  file:
    name: logs/tensor-pay.log
```

## 扩展开发

### 添加新的支付方式

1. 创建配置类：

```java
@ConfigurationProperties(prefix = "tensor.pay.newpay")
public class NewPayProperties {
    private boolean enabled = false;
    // 其他配置属性...
}
```

2. 实现支付服务：

```java
@Service
@ConditionalOnProperty(prefix = "tensor.pay.newpay", name = "enabled", havingValue = "true")
public class NewPaymentService extends AbstractPaymentService {
    
    @Override
    public PaymentType getSupportedPaymentType() {
        return PaymentType.NEWPAY;
    }
    
    @Override
    public PaymentResponse createPayment(PaymentRequest request) {
        // 实现创建支付逻辑
    }
    
    // 实现其他方法...
}
```

3. 更新自动配置类：

```java
@EnableConfigurationProperties({
    WechatPayProperties.class,
    AlipayProperties.class,
    BankPayProperties.class,
    NewPayProperties.class  // 添加新的配置
})
public class TensorPayAutoConfiguration {
    // ...
}
```

## 常见问题

### Q: 如何处理支付回调的幂等性？
A: 在回调处理中检查订单状态，避免重复处理已完成的订单。

### Q: 如何处理网络超时？
A: 配置合适的超时时间，并实现重试机制。

### Q: 如何保证支付安全？
A: 使用HTTPS、验证回调签名、定期更新证书。

### Q: 如何处理并发支付？
A: 使用数据库锁或分布式锁确保订单状态的一致性。

## 技术支持

如有问题，请提交Issue或联系技术支持。