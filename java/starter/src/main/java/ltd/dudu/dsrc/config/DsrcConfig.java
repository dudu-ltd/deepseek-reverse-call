package ltd.dudu.dsrc.config;

// Copyright (c) 2025 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import io.github.pigmesh.ai.deepseek.core.DeepSeekClient;
import java.util.List;
import ltd.dudu.dsrc.DeepSeek;
import ltd.dudu.dsrc.DeepSeek4jImpl;
import ltd.dudu.dsrc.DeepSeekReverseCall;
import ltd.dudu.dsrc.DeepSeekReverseCallImpl;
import ltd.dudu.dsrc.DsrcAnswer;
import ltd.dudu.dsrc.DsrcInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author yeweicheng
 * @since 2025-02-10 1:03
 * <br>Now is history!
 */
@Configuration
public class DsrcConfig {
  
  // 请注意，如果不允许 AI 反向调用，可以不注入 answers   
  @Autowired(required = false)
  private List<DsrcAnswer> answers;
  
  @Autowired(required = false)
  private List<DsrcInterceptor> interceptors;
  
  @Autowired
  private DeepSeekClient deepSeekClient;
  
  @Bean
  public DeepSeek deepSeek() {
    return new DeepSeek4jImpl(deepSeekClient);
  }
  
  @Bean
  public DeepSeekReverseCall deepSeekReverseCall(DeepSeek deepSeek) {
    // 如果不允许 AI 反向调用，请不要传入 answers
    return new DeepSeekReverseCallImpl(deepSeek, answers, interceptors);
  }

}
