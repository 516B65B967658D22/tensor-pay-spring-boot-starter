package com.tensor.pay.enums;

/**
 * 支付类型枚举
 * 
 * @author Tensor Pay
 * @since 1.0.0
 */
public enum PaymentType {
    
    /**
     * 微信支付
     */
    WECHAT("wechat", "微信支付"),
    
    /**
     * 支付宝支付
     */
    ALIPAY("alipay", "支付宝支付"),
    
    /**
     * 银行卡支付
     */
    BANK("bank", "银行卡支付"),
    
    /**
     * 云闪付
     */
    UNIONPAY("unionpay", "云闪付");
    
    private final String code;
    private final String description;
    
    PaymentType(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    public static PaymentType fromCode(String code) {
        for (PaymentType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown payment type code: " + code);
    }
}