package io.github.corvusye.dsrc.pojo;

// Copyright (c) 2025 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 提示 token 详情
 * 
 * @author yeweicheng
 * @since 2025-02-11 0:34
 * <br>Now is history!
 */
@Data
public class DeepSeekPromptTokensDetails {
  /** 缓存的 token 数 */
  @JsonProperty("cached_tokens")
  private int cachedTokens;
}
