package ltd.dudu.dsrc;

// Copyright (c) 2025 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import com.alibaba.fastjson2.JSON;
import ltd.dudu.dsrc.pojo.DeepSeekOptions;
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

  // region 这个系列主要用于指定返回值类型的多轮对话，List<Message> & topic class
  default <T> T api(
    List<Message> messages,
    Class<T> returnType
  ) throws IOException {
    return api(messages, returnType, Modes.chat);
  }

  default <T> T api(
    List<Message> messages,
    Class<T> returnType,
    Modes mode
  ) throws IOException {
    return api(messages, returnType, null, mode);
  }

  default <T> T api(
    List<Message> messages,
    Class<T> returnType,
    DeepSeekOptions options
  ) throws IOException {
    return api(messages, returnType, options, Modes.chat);
  }

  <T> T api(
    List<Message> messages,
    Class<T> returnType,
    DeepSeekOptions options,
    Modes mode
  ) throws IOException;
  // endregion

  // region 这个系列主要用于返回字符串的多轮对话，List<Message> and prompt
  default String api(
    List<Message> messages
  ) throws IOException {
    return api(messages, (String) null);
  }

  default String api(
    List<Message> messages,
    String prompt
  ) throws IOException {
    return api(messages, prompt, Modes.chat);
  }

  default String api(
    List<Message> messages,
    String prompt,
    Modes mode
  ) throws IOException {
    return api(messages, prompt, null, mode);
  }

  default String api(
    List<Message> messages,
    String prompt,
    DeepSeekOptions options
  ) throws IOException {
    return api(messages, prompt, options, Modes.chat);
  }

  String api(
    List<Message> messages,
    String prompt,
    DeepSeekOptions options,
    Modes mode
  ) throws IOException;
  
  // endregion
  
  // region 这个系列主要用于返回字符串的单轮对话，String & prompt
  default String api(
    String message
  ) throws IOException {
    return api(message, (String) null);
  }
  
  default String api(
    String message,
    String prompt
  ) throws IOException {
    return api(toUserMessage(message), prompt, null, Modes.chat);
  }
  
  default String api(
    String message,
    String prompt,
    Modes mode
  ) throws IOException {
    return api(toUserMessage(message), prompt, mode);
  }

  default String api(
    String message,
    String prompt,
    DeepSeekOptions options
  ) throws IOException {
    return api(toUserMessage(message), prompt, options, Modes.chat);
  }

  default String api(
    String message,
    String prompt,
    DeepSeekOptions options,
    Modes mode
  ) throws IOException {
    return api(toUserMessage(message), prompt, options, mode);
  }

  // endregion
  
  // region 这个系列主要用于指定返回值类型的单轮对话，String & topic class
  default <T> T api(
    String message,
    Class<T> returnType
  ) throws IOException {
    return api(toUserMessage(message), returnType, null, Modes.chat);
  }
  
  default <T> T api(
    String message,
    Class<T> returnType,
    Modes mode
  ) throws IOException {
    return api(toUserMessage(message), returnType, null, mode);
  }
  
  default <T> T api(
    String message,
    Class<T> returnType,
    DeepSeekOptions options
  ) throws IOException {
    return api(toUserMessage(message), returnType, options, Modes.chat);
  }
  
  default <T> T api(
    String message,
    Class<T> returnType,
    DeepSeekOptions options,
    Modes mode
  ) throws IOException {
    return api(toUserMessage(message), returnType, options, mode);
  }
  
  // endregion
  
  default List<Message> toUserMessage(String message) {
    List<Message> messages = new ArrayList<>();
    messages.add(UserMessage.from(message));
    return messages;
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

  default List<Message> allMessage(ChatCompletionResponse response) {
    List<ChatCompletionChoice> choices = response.choices();
    return choices.stream()
      .map(ChatCompletionChoice::message)
      .collect(Collectors.toList());
  }

  default Message oneMessage(ChatCompletionResponse response) {
    return allMessage(response).get(0);
  }

  default String content(Message message) {
    if (message instanceof UserMessage) {
      Object content = ((UserMessage) message).content();
      return content instanceof String ? (String) content : JSON.toJSONString(content);
    } else if (message instanceof AssistantMessage) {
      return ((AssistantMessage) message).content();
    }
    return message.toString();
  }

  default String content(ChatCompletionResponse response) {
    return content(allMessage(response).get(0));
  }

  List<Message> subTopic(
    Class<?> topicName,
    Object resultObj,
    Message message,
    List<Message> messages,
    ChatCompletionResponse result,
    DeepSeekOptions options,
    Modes mode
  );

  /**
   * 
   * @param topicName 子路由路径，来自 {@link DsrcApi#value()}
   * @param resultObj 本次对话的实际结果
   * @param message 本次对话的消息
   * @param messages 历次对话的多轮消息
   * @param result 一次请求的完整结果
   * @param options DeepSeek 配置的选项
   * @param mode DeepSeek 的模型
   * @return 下一步的消息
   */
  List<Message> subTopic(
    String topicName,
    Object resultObj,
    Message message,
    List<Message> messages,
    ChatCompletionResponse result,
    DeepSeekOptions options,
    Modes mode
  );
  
  SchemaGenerator getSchemaGenerator();
}

