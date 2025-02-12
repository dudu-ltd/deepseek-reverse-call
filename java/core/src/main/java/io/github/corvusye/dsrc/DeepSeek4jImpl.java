package io.github.corvusye.dsrc;

// Copyright (c) 2025 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import io.github.corvusye.dsrc.pojo.DeepSeekOptions;
import io.github.pigmesh.ai.deepseek.core.DeepSeekClient;
import io.github.pigmesh.ai.deepseek.core.chat.ChatCompletionRequest;
import io.github.pigmesh.ai.deepseek.core.chat.ChatCompletionResponse;
import io.github.pigmesh.ai.deepseek.core.chat.Message;
import io.github.pigmesh.ai.deepseek.core.chat.ResponseFormatType;
import java.util.List;

/**
 * @author yeweicheng
 * @since 2025-02-12 2:51
 * <br>Now is history!
 */
public class DeepSeek4jImpl implements DeepSeek {

  private DeepSeekClient deepSeekClient;

  public DeepSeek4jImpl(DeepSeekClient deepSeekClient) {
    this.deepSeekClient = deepSeekClient;
  }

  public ChatCompletionResponse createChat(
    List<Message> messages, Modes mode, DeepSeekOptions options) {

    ChatCompletionRequest request = ChatCompletionRequest.builder()
      // 模型选择，支持 DEEPSEEK_CHAT、DEEPSEEK_REASONER 等
      .model(mode.getName())
      .messages(messages)
      .responseFormat(ResponseFormatType.TEXT)
      .build();
    
    return deepSeekClient.chatCompletion(request).execute();
  }

}
