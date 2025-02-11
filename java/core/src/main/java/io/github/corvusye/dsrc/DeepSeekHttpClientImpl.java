package io.github.corvusye.dsrc;

// Copyright (c) 2025 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import static io.github.corvusye.dsrc.DsrcConst.ACCEPT;
import static io.github.corvusye.dsrc.DsrcConst.APPLICATION_JSON;
import static io.github.corvusye.dsrc.DsrcConst.APPLICATION_JSON_UTF8;
import static io.github.corvusye.dsrc.DsrcConst.AUTH;
import static io.github.corvusye.dsrc.DsrcConst.CONTENT_TYPE;

import com.alibaba.fastjson2.JSONObject;
import io.github.corvusye.dsrc.pojo.DeepSeekResult;
import io.github.corvusye.dsrc.pojo.Message;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 * 基于 Apache HTTP 的请求实现
 * 
 * @author yeweicheng
 * @since 2025-02-10 23:10
 * <br>Now is history!
 */
public class DeepSeekHttpClientImpl implements DeepSeek {

  /**
   * 应用的授权码，请在<a href="https://platform.deepseek.com/api_keys"> DeepSeek 官网中</a>申请
   */
  final private String appKey;

  final private String baseUrl;

  // 创建 HttpClient 实例
  final HttpClient httpClient = HttpClients.createDefault();

  public DeepSeekHttpClientImpl(String appKey) {
    this(appKey, BASE_URL);
  }

  public DeepSeekHttpClientImpl(String appKey, String baseUrl) {
    this.appKey = appKey;
    this.baseUrl = baseUrl == null ? BASE_URL : baseUrl;
  }

  @Override
  public DeepSeekResult createChat(List<Message> messages, Modes mode, Map<String, Object> options)
    throws IOException {
    // 创建 HttpPost 请求
    HttpPost httpPost = new HttpPost(baseUrl + COMPLETIONS);
    
    // 设置请求头
    httpPost.setHeader(CONTENT_TYPE, APPLICATION_JSON);
    httpPost.setHeader(ACCEPT, APPLICATION_JSON_UTF8);
    httpPost.setHeader(AUTH, "Bearer " + appKey);
    
    // 定义请求体
    String payload = jsonParam(messages, mode, options);
    StringEntity entity = new StringEntity(payload, StandardCharsets.UTF_8);
    httpPost.setEntity(entity);
    
    // 执行请求
    HttpResponse response = httpClient.execute(httpPost);
    
    // 读取响应内容
    String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
    
    return JSONObject.parseObject(responseBody, DeepSeekResult.class);
  }
}
