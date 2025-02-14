package ltd.dudu.dsrc.pojo;

// Copyright (c) 2025 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import ltd.dudu.dsrc.DsrcApi;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author yeweicheng
 * @since 2025-02-10 7:00
 * <br>Now is history!
 */
@Data
@AllArgsConstructor
@DsrcApi(
  value = "dsrc.router", 
  prompt = "你可以选择一个话题，或者结束对话。",
  subTopics = {Chess.class, Discuss.class},
  plainTopics = "say.goodbye"
)
public class RouteValue {
  
  @Schema(description = "开启话题，可选值：say.goodbye|<topicKeys>")
  final private String route;
  
  @Schema(description = "数据格式，可选值：\"<topicValues>\"")
  final private Object data;
  
  @Schema(description = "是否结束对话")
  final private Boolean finished;
  
  @Schema(description = "轮次")
  final private Integer round;
  
}
