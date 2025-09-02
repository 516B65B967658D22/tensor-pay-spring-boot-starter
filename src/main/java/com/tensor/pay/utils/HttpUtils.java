package com.tensor.pay.utils;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * HTTP工具类
 * 
 * @author Tensor Pay
 * @since 1.0.0
 */
@Component
public class HttpUtils {
    
    private final CloseableHttpClient httpClient;
    
    public HttpUtils() {
        this.httpClient = HttpClients.createDefault();
    }
    
    /**
     * 发送GET请求
     * 
     * @param url 请求URL
     * @return 响应内容
     * @throws IOException IO异常
     */
    public String get(String url) throws IOException {
        HttpGet httpGet = new HttpGet(url);
        
        try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
            return EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        }
    }
    
    /**
     * 发送POST请求
     * 
     * @param url 请求URL
     * @param params 请求参数
     * @return 响应内容
     * @throws IOException IO异常
     */
    public String post(String url, Map<String, String> params) throws IOException {
        HttpPost httpPost = new HttpPost(url);
        
        // 构建表单参数
        List<NameValuePair> formParams = new ArrayList<>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            formParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        
        httpPost.setEntity(new UrlEncodedFormEntity(formParams, StandardCharsets.UTF_8));
        
        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
            return EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        }
    }
    
    /**
     * 发送POST请求（JSON格式）
     * 
     * @param url 请求URL
     * @param jsonData JSON数据
     * @return 响应内容
     * @throws IOException IO异常
     */
    public String postJson(String url, String jsonData) throws IOException {
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setEntity(new org.apache.hc.core5.http.io.entity.StringEntity(jsonData, StandardCharsets.UTF_8));
        
        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
            return EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        }
    }
}