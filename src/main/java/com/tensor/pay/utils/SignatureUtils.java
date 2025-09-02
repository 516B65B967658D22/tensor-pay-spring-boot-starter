package com.tensor.pay.utils;

import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Map;
import java.util.TreeMap;

/**
 * 签名工具类
 * 
 * @author Tensor Pay
 * @since 1.0.0
 */
@Component
public class SignatureUtils {
    
    /**
     * 生成MD5签名
     * 
     * @param params 参数Map
     * @param key 签名密钥
     * @return 签名字符串
     */
    public String generateMD5Signature(Map<String, String> params, String key) {
        try {
            String signString = buildSignString(params) + "&key=" + key;
            return md5(signString).toUpperCase();
        } catch (Exception e) {
            throw new RuntimeException("生成MD5签名失败", e);
        }
    }
    
    /**
     * 生成HMAC-SHA256签名
     * 
     * @param params 参数Map
     * @param key 签名密钥
     * @return 签名字符串
     */
    public String generateHmacSha256Signature(Map<String, String> params, String key) {
        try {
            String signString = buildSignString(params);
            return hmacSha256(signString, key);
        } catch (Exception e) {
            throw new RuntimeException("生成HMAC-SHA256签名失败", e);
        }
    }
    
    /**
     * 生成签名（默认使用MD5）
     * 
     * @param params 参数Map
     * @param key 签名密钥
     * @return 签名字符串
     */
    public String generateSignature(Map<String, String> params, String key) {
        return generateMD5Signature(params, key);
    }
    
    /**
     * 验证签名
     * 
     * @param params 参数Map
     * @param key 签名密钥
     * @param signature 待验证的签名
     * @return 验证结果
     */
    public boolean verifySignature(Map<String, String> params, String key, String signature) {
        String expectedSignature = generateSignature(params, key);
        return expectedSignature.equals(signature);
    }
    
    /**
     * 构建签名字符串
     * 
     * @param params 参数Map
     * @return 签名字符串
     */
    private String buildSignString(Map<String, String> params) {
        // 使用TreeMap自动排序
        TreeMap<String, String> sortedParams = new TreeMap<>(params);
        
        StringBuilder signString = new StringBuilder();
        for (Map.Entry<String, String> entry : sortedParams.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            
            // 跳过空值和签名参数
            if (value != null && !value.isEmpty() && !"sign".equals(key)) {
                if (signString.length() > 0) {
                    signString.append("&");
                }
                signString.append(key).append("=").append(value);
            }
        }
        
        return signString.toString();
    }
    
    /**
     * MD5加密
     * 
     * @param input 输入字符串
     * @return MD5哈希值
     */
    private String md5(String input) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
        
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(String.format("%02x", b));
        }
        
        return sb.toString();
    }
    
    /**
     * HMAC-SHA256加密
     * 
     * @param data 数据
     * @param key 密钥
     * @return HMAC-SHA256哈希值
     */
    private String hmacSha256(String data, String key) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        mac.init(secretKeySpec);
        
        byte[] digest = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(String.format("%02x", b));
        }
        
        return sb.toString();
    }
}