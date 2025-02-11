package io.github.corvusye.dsrc.pojo;

// Copyright (c) 2025 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author yeweicheng
 * @since 2025-02-11 0:29
 * <br>Now is history!
 */
@Data
public class DeepSeekChoices {
  
  private int index;
  private Message message;
  private Boolean logprobs;
  @JsonProperty("finish_reason")
  private String finishReason;

}
