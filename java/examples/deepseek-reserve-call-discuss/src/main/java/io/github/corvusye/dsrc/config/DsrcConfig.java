package io.github.corvusye.dsrc.config;

// Copyright (c) 2025 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import io.github.corvusye.dsrc.DeepSeek;
import io.github.corvusye.dsrc.DeepSeek4jImpl;
import io.github.corvusye.dsrc.DeepSeekReverseCall;
import io.github.corvusye.dsrc.DeepSeekReverseCallImpl;
import io.github.corvusye.dsrc.DsrcAnswer;
import io.github.pigmesh.ai.deepseek.core.DeepSeekClient;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author yeweicheng
 * @since 2025-02-10 1:03
 * <br>Now is history!
 */
@Configuration
public class DsrcConfig {
  
  @Value("${dsrc.config}")
  private String configFilePath;
  
  @Value("${dsrc.appKey}")
  private String appKey;
  
  // 请注意，如果不允许 AI 反向调用，可以不注入 answers   
  @Autowired(required = false)
  private List<DsrcAnswer> answers;
  @Autowired
  private DeepSeekClient deepSeekClient;
  
  @Bean
  public DeepSeek deepSeek() {
    return new DeepSeek4jImpl(deepSeekClient);
  }
  
  @Bean
  public DeepSeekReverseCall deepSeekReverseCall(DeepSeek deepSeek) {
    // 如果不允许 AI 反向调用，请不要传入 answers
    return new DeepSeekReverseCallImpl(deepSeek, answers);
  }

}
