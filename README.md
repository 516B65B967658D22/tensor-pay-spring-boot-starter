# Tensor Pay Spring Boot Starter

一个功能完整的Spring Boot支付集成starter，支持微信支付、支付宝支付、银行支付等多种支付方式。

## 功能特性

- 🚀 **开箱即用**：简单配置即可快速集成多种支付方式
- 🔧 **统一接口**：提供统一的支付API，无需关心底层实现差异
- 💳 **多支付支持**：支持微信支付、支付宝支付、银行支付等
- 🛡️ **安全可靠**：内置签名验证、回调处理等安全机制
- 📊 **完整功能**：支持创建订单、查询状态、取消支付、申请退款等完整流程
- ⚙️ **灵活配置**：支持多环境配置，可单独启用/禁用各支付方式

## 支持的支付方式

| 支付方式 | 状态 | 说明 |
|---------|------|------|
| 微信支付 | ✅ | 支持Native支付（扫码）、JSAPI支付（公众号/小程序） |
| 支付宝支付 | ✅ | 支持扫码支付、手机网站支付 |
| 银行支付 | ✅ | 支持各大银行网银支付 |
| 云闪付 | 🚧 | 计划支持 |

## 快速开始

### 1. 添加依赖

```xml
<dependency>
    <groupId>com.tensor</groupId>
    <artifactId>tensor-pay-spring-boot-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 2. 配置支付参数

在 `application.yml` 中配置支付参数：

```yaml
tensor:
  pay:
    # 微信支付配置
    wechat:
      enabled: true
      app-id: your-wechat-app-id
      mch-id: your-merchant-id
      private-key-path: classpath:wechat/apiclient_key.pem
      merchant-serial-number: your-merchant-serial-number
      api-v3-key: your-api-v3-key
      notify-url: https://your-domain.com/api/payment/wechat/notify
    
    # 支付宝配置
    alipay:
      enabled: true
      app-id: your-alipay-app-id
      private-key: your-alipay-private-key
      alipay-public-key: your-alipay-public-key
      notify-url: https://your-domain.com/api/payment/alipay/notify
```

### 3. 使用支付服务

```java
@RestController
public class PaymentController {
    
    @Autowired
    private UnifiedPaymentService paymentService;
    
    @PostMapping("/pay")
    public PaymentResponse createPayment(@RequestBody PaymentRequest request) {
        return paymentService.createPayment(request);
    }
    
    @GetMapping("/query")
    public PaymentResponse queryPayment(@RequestParam PaymentType type, 
                                       @RequestParam String outTradeNo) {
        return paymentService.queryPayment(type, outTradeNo);
    }
}
```

## API 接口

### 创建支付订单

```java
PaymentRequest request = new PaymentRequest();
request.setOutTradeNo("ORDER_" + System.currentTimeMillis());
request.setPaymentType(PaymentType.WECHAT);
request.setAmount(new BigDecimal("100.00"));
request.setSubject("商品名称");
request.setBody("商品描述");

PaymentResponse response = paymentService.createPayment(request);
```

### 查询支付状态

```java
PaymentResponse response = paymentService.queryPayment(PaymentType.WECHAT, "ORDER_123456");
```

### 申请退款

```java
PaymentResponse response = paymentService.refund(
    PaymentType.WECHAT, 
    "ORDER_123456", 
    new BigDecimal("50.00"), 
    "用户申请退款"
);
```

## 配置说明

### 微信支付配置

| 参数 | 必填 | 说明 |
|------|------|------|
| enabled | 否 | 是否启用微信支付，默认false |
| app-id | 是 | 微信应用ID |
| mch-id | 是 | 微信商户号 |
| private-key-path | 是 | 商户API私钥文件路径 |
| merchant-serial-number | 是 | 商户证书序列号 |
| api-v3-key | 是 | APIv3密钥 |
| notify-url | 是 | 支付回调地址 |

### 支付宝配置

| 参数 | 必填 | 说明 |
|------|------|------|
| enabled | 否 | 是否启用支付宝支付，默认false |
| app-id | 是 | 支付宝应用ID |
| private-key | 是 | 应用私钥 |
| alipay-public-key | 是 | 支付宝公钥 |
| notify-url | 是 | 支付回调地址 |

### 银行支付配置

| 参数 | 必填 | 说明 |
|------|------|------|
| enabled | 否 | 是否启用银行支付，默认false |
| merchant-id | 是 | 银行商户号 |
| merchant-key | 是 | 银行商户密钥 |
| gateway-url | 是 | 银行网关地址 |
| supported-banks | 否 | 支持的银行列表 |

## 回调处理

支付回调会自动处理签名验证和状态更新，你只需要在业务代码中监听支付状态变化：

```java
@Component
public class PaymentEventListener {
    
    @EventListener
    public void handlePaymentSuccess(PaymentSuccessEvent event) {
        // 处理支付成功事件
        String outTradeNo = event.getOutTradeNo();
        // 更新订单状态、发送通知等
    }
}
```

## 异常处理

所有支付相关异常都继承自 `PaymentException`：

```java
try {
    PaymentResponse response = paymentService.createPayment(request);
} catch (PaymentException e) {
    logger.error("支付失败: {}, {}", e.getErrorCode(), e.getMessage());
}
```

## 开发指南

### 扩展新的支付方式

1. 继承 `AbstractPaymentService`
2. 实现 `getSupportedPaymentType()` 方法
3. 实现具体的支付逻辑
4. 添加对应的配置类

```java
@Service
@ConditionalOnProperty(prefix = "tensor.pay.newpay", name = "enabled", havingValue = "true")
public class NewPaymentService extends AbstractPaymentService {
    
    @Override
    public PaymentType getSupportedPaymentType() {
        return PaymentType.NEWPAY;
    }
    
    // 实现具体支付逻辑...
}
```

## 许可证

MIT License

## 贡献

欢迎提交Issue和Pull Request！