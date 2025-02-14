package ltd.dudu.dsrc.pojo;

// Copyright (c) 2025 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import java.util.HashMap;
import java.util.Map;
import lombok.Data;

/**
 * DeepSeek 请求的参数
 * 
 * @author yeweicheng
 * @since 2025-02-10 23:12
 * <br>Now is history!
 */
@Data
public class DeepSeekOptions {
  
  /** 频率惩罚 */
  final private int frequencyPenalty;
  
  /** 最大生成 token 数 */
  final private Integer maxTokens;
  
  /** 存在惩罚 */
  final private int presencePenalty;
  
  /** 是否流式生成 */
  final private boolean stream;
  
  /** 温度 */
  final private double temperature;
  
  /** top-p */
  final private double topP;
  
  /** 是否返回 logprobs */
  final private boolean logProbs;
  
  /** 结束标记 */
  final private String stop;
  
  /** top-logprobs */
  final private Integer topLogProbs;

  public DeepSeekOptions(
    int frequencyPenalty, Integer maxTokens, int presencePenalty, boolean stream,
    double temperature, double topP, boolean logProbs, String stop, Integer topLogProbs) {
    this.frequencyPenalty = frequencyPenalty;
    this.maxTokens = maxTokens;
    this.presencePenalty = presencePenalty;
    this.stream = stream;
    this.temperature = temperature == 0 ? 1 : temperature;
    this.topP = topP == 0 ? 1 : topP;
    this.logProbs = logProbs;
    this.stop = stop;
    this.topLogProbs = topLogProbs;
  }

  public Map<String, ?> toMap() {
    Map<String, Object> param = new HashMap<>();
    param.put("frequency_penalty", frequencyPenalty);
    param.put("max_tokens", maxTokens);
    param.put("presence_penalty", presencePenalty);
    param.put("stream", stream);
    param.put("temperature", temperature);
    param.put("top_p", topP);
    param.put("logprobs", logProbs);
    param.put("stop", stop);
    param.put("top_logprobs", topLogProbs);
    return param;
  }
}
