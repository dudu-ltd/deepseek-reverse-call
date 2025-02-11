package io.github.corvusye.dsrc;

// Copyright (c) 2025 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import io.github.corvusye.dsrc.pojo.Message;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author yeweicheng
 * @since 2025-02-09 23:32
 * <br>Now is history!
 */
public interface DeepSeekReverseCall {

  <T> T  api(
    String apiName,
    List<Message> messages,
    Map<String, Object> options,
    List<Object> args, Modes mode,
    Class<T> returnType
  ) throws IOException;

  default <T> T  api(
    String apiName,
    List<Message> messages,
    Map<String, Object> options,
    Modes mode,
    Class<T> returnType
  ) throws IOException {
    return api(apiName, messages, options, new ArrayList<>(), mode, returnType);
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
  
  default List<Message> reverseRole(List<Message> messages) {
    List<Message> msgs = new ArrayList<>();
    for (Message message : messages) {
      msgs.add(message.reverseRole());
    }
    return msgs;
  }
  
}

