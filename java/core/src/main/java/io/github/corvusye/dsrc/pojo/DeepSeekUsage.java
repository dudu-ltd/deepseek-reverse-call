package io.github.corvusye.dsrc.pojo;

// Copyright (c) 2025 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author yeweicheng
 * @since 2025-02-11 0:30
 * <br>Now is history!
 */
@Data
public class DeepSeekUsage {
  /** 上行 token 数 */
  @JsonProperty("prompt_tokens")
  private int promptTokens;
  
  /** 下行 token 数 */
  @JsonProperty("completion_tokens")
  private int completionTokens;
  
  /** 总 token 数 */
  @JsonProperty("total_tokens")
  private int totalTokens;
  
  /** 提示 token 详情 */
  @JsonProperty("prompt_tokens_details")
  private DeepSeekPromptTokensDetails promptTokensDetails;
  
  /** 提示缓存命中 token 数 */
  @JsonProperty("prompt_cache_hit_tokens")
  private int promptCacheHitTokens;
  
  /** 提示缓存未命中 token 数 */
  @JsonProperty("prompt_cache_miss_tokens")
  private int promptCacheMissTokens;

}
