package io.github.corvusye.dsrc.pojo;

// Copyright (c) 2025 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import io.github.corvusye.dsrc.DsrcApi;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.Data;

/**
 * @author yeweicheng
 * @since 2025-02-11 8:18
 * <br>Now is history!
 */
@Data
@DsrcApi(value = "say.chess", prompt = "你是个下象棋的高手，请跟我下棋吧")
public class Chess {

  @Schema(
    description = "落子", // prompt
    requiredMode = RequiredMode.REQUIRED
  )
  private String chess;
  
}
