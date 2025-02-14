package io.github.corvusye.dsrc.pojo;

// Copyright (c) 2025 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import io.github.corvusye.dsrc.DsrcApi;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author yeweicheng
 * @since 2025-02-11 8:17
 * <br>Now is history!
 */
@Data
@Schema(description = "闲聊")
@DsrcApi(value = "say.discuss", 
  prompt = "你是个象棋高手，请试图讲话题引导至下棋"
)
public class Discuss {
  @Schema(description = "讨论内容")
  private String discuss;
}
