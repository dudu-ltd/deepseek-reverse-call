package io.github.corvusye.dsrc;

// Copyright (c) 2025 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import static io.github.corvusye.dsrc.DsrcConst.MODEL_KEY;
import static io.github.corvusye.dsrc.DsrcConst.MSG_KEG;

import com.alibaba.fastjson2.JSON;
import io.github.corvusye.dsrc.pojo.DeepSeekOptions;
import io.github.pigmesh.ai.deepseek.core.chat.ChatCompletionChoice;
import io.github.pigmesh.ai.deepseek.core.chat.ChatCompletionResponse;
import io.github.pigmesh.ai.deepseek.core.chat.Message;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * DeepSeek 接口
 * 
 * @author yeweicheng
 * @since 2025-02-10 5:46
 * <br>Now is history!
 */
public interface DeepSeek {

  ChatCompletionResponse createChat(List<Message> messages, Modes mode, DeepSeekOptions options)
    throws IOException;

  default ChatCompletionResponse createChat(List<Message> messages, Modes mode) throws IOException {
    return createChat(messages, mode, null);
  }

  default ChatCompletionResponse createChat(List<Message> messages) throws IOException {
    return createChat(messages, Modes.chat);
  }

}