package ltd.dudu.dsrc;

// Copyright (c) 2025 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import io.github.pigmesh.ai.deepseek.core.chat.ChatCompletionResponse;

/**
 * @author yeweicheng
 * @since 2025-02-11 3:24
 * <br>Now is history!
 */
public interface DsrcInterceptor {
  
  boolean intercept(ChatCompletionResponse result);

}
