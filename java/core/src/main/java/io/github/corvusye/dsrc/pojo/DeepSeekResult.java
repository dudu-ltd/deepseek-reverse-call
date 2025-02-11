package io.github.corvusye.dsrc.pojo;

// Copyright (c) 2025 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 * DeepSeek 请求的结果
 * 
 * @author yeweicheng
 * @since 2025-02-11 0:26
 * <br>Now is history!
 */
@Data
public class DeepSeekResult {
  
  /** 请求的 ID */
  private String id;
  
  /** 请求的对象，调用的接口，如：chat.completion */
  private String object;
  
  /** 创建时间 */
  private long created;
  
  /** 模型 */
  private String model;
  
  /** 应答消息 */
  private DeepSeekChoices[] choices;
  
  /** token 使用情况 */
  private DeepSeekUsage usage;
  
  /** 系统指纹 */
  @JsonProperty("system_fingerprint")
  private String systemFingerprint;
  
  public List<Message> allMessage() {
    if (choices != null && choices.length > 0) {
      List<Message> messages = new ArrayList<>();
      for (DeepSeekChoices choice : choices) {
        messages.add(choice.getMessage());
      }
      return messages;
    }
    return null;
  }
  
  public String getOne() {
    if (choices != null && choices.length > 0) {
      return choices[0].getMessage().getContent();
    }
    return null;
  }
  
}




