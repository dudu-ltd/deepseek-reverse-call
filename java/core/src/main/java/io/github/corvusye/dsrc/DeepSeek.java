package io.github.corvusye.dsrc;

// Copyright (c) 2025 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import io.github.corvusye.dsrc.pojo.DeepSeekOptions;
import io.github.pigmesh.ai.deepseek.core.chat.ChatCompletionResponse;
import io.github.pigmesh.ai.deepseek.core.chat.Message;
import java.io.IOException;
import java.util.List;

/**
 * DeepSeek 接口
 * 
 * @author yeweicheng
 * @since 2025-02-10 5:46
 * <br>Now is history!
 */
public interface DeepSeek {

  ChatCompletionResponse createChat(
    List<Message> messages,
    String topicPrompt,
    Modes mode,
    DeepSeekOptions options
  ) throws IOException;

  ChatCompletionResponse createChat(
    List<Message> messages,
    Message schemaMessage,
    Class<?> topic,
    Modes mode,
    DeepSeekOptions options
  ) throws IOException;

  default ChatCompletionResponse createChat(
    List<Message> messages,
    Modes mode,
    DeepSeekOptions options
  ) throws IOException {
    return createChat(messages, (String) null, mode, options);
  }

  default ChatCompletionResponse createChat(
    List<Message> messages,
    Modes mode
  ) throws IOException {
    return createChat(messages, mode, null);
  }

  default ChatCompletionResponse createChat(List<Message> messages) throws IOException {
    return createChat(messages, Modes.chat);
  }

}