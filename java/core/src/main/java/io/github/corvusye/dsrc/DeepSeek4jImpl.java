package io.github.corvusye.dsrc;

// Copyright (c) 2025 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import io.github.corvusye.dsrc.pojo.DeepSeekResult;
import io.github.corvusye.dsrc.pojo.Message;
import io.github.pigmesh.ai.deepseek.core.DeepSeekClient;
import io.github.pigmesh.ai.deepseek.core.SyncOrAsyncOrStreaming;
import io.github.pigmesh.ai.deepseek.core.chat.ChatCompletionRequest;
import io.github.pigmesh.ai.deepseek.core.chat.ChatCompletionResponse;
import java.util.List;
import java.util.Map;

/**
 * @author yeweicheng
 * @since 2025-02-12 2:51
 * <br>Now is history!
 */
public class DeepSeek4jImpl implements DeepSeek {

  private DeepSeekClient deepSeekClient;

  public DeepSeekResult createChat(List<Message> messages, Modes mode, Map<String, Object> options) {
    ChatCompletionRequest request = ChatCompletionRequest.builder()
      // 模型选择，支持 DEEPSEEK_CHAT、DEEPSEEK_REASONER 等
      .model(mode.getName())
      // 添加用户消息
//      .addUserMessage(prompt)
      // 添加助手消息，用于多轮对话
      .addAssistantMessage("上轮结果")
      // 添加系统消息，用于设置角色和行为
      .addSystemMessage("你是一个专业的助手")
      // 设置最大生成 token 数，默认 2048
        .build();

    SyncOrAsyncOrStreaming<ChatCompletionResponse> response = deepSeekClient.chatCompletion(
      request);
    ChatCompletionResponse execute = response.execute();
    return null;
  }

}
