package io.github.corvusye.dsrc;

// Copyright (c) 2025 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import com.alibaba.fastjson2.JSON;
import io.github.corvusye.dsrc.pojo.DeepSeekOptions;
import io.github.pigmesh.ai.deepseek.core.chat.AssistantMessage;
import io.github.pigmesh.ai.deepseek.core.chat.ChatCompletionChoice;
import io.github.pigmesh.ai.deepseek.core.chat.ChatCompletionResponse;
import io.github.pigmesh.ai.deepseek.core.chat.Message;
import io.github.pigmesh.ai.deepseek.core.chat.UserMessage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yeweicheng
 * @since 2025-02-09 23:32
 * <br>Now is history!
 */
public interface DeepSeekReverseCall {

  default <T> T  api(
    String apiName,
    List<Message> messages,
    DeepSeekOptions options,
    Modes mode,
    Class<T> returnType
  ) throws IOException {
    return api(apiName, messages, options, mode, returnType);
  }

  default <T> T  api(
    String apiName,
    List<Message> messages,
    Modes mode,
    Class<T> returnType
  ) throws IOException {
    return api(apiName, messages, null, mode, returnType);
  }

  default <T> T api(
    String apiName,
    List<Message> messages,
    Class<T> returnType
  ) throws IOException {
    return api(apiName, messages, Modes.chat, returnType);
  }

  default Object api(
    String apiName,
    List<Message> messages
  ) throws IOException {
    return api(apiName, messages, Object.class);
  }

  default Object api(
    String apiName,
    String message
  ) throws IOException {
    List<Message> messages = new ArrayList<>();
    messages.add(UserMessage.from(message));
    return api(apiName, messages, Object.class);
  }
  
  default List<Message> reverseRole(List<Message> messages) {
    List<Message> msgs = new ArrayList<>();
    for (Message message : messages) {
      msgs.add(reverseRole(message));
    }
    return msgs;
  }
  
  default Message reverseRole(Message message) {
    if (message instanceof UserMessage) {
      return AssistantMessage.from(content(message));
    } else if (message instanceof AssistantMessage) {
      return UserMessage.from(content(message));
    }
    return message;
  }
  
  default String content(Message message) {
    if (message instanceof UserMessage) {
      Object content = ((UserMessage) message).content();
      return JSON.toJSONString(content);
    } else if (message instanceof AssistantMessage) {
      return ((AssistantMessage) message).content();
    }
    return message.toString();
  }


  default List<Message> allMessage(ChatCompletionResponse response) {
    List<ChatCompletionChoice> choices = response.choices();
    return choices.stream()
      .map(ChatCompletionChoice::message)
      .collect(Collectors.toList());
  }

  default String one(ChatCompletionResponse response) {
    return content(allMessage(response).get(0));
  }
  
}

