package io.github.corvusye.dsrc;

// Copyright (c) 2025 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import static io.github.corvusye.dsrc.DsrcConst.MODEL_KEY;
import static io.github.corvusye.dsrc.DsrcConst.MSG_KEG;

import com.alibaba.fastjson2.JSON;
import io.github.corvusye.dsrc.pojo.DeepSeekOptions;
import io.github.corvusye.dsrc.pojo.DeepSeekResult;
import io.github.corvusye.dsrc.pojo.Message;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DeepSeek 接口
 * 
 * @author yeweicheng
 * @since 2025-02-10 5:46
 * <br>Now is history!
 */
public interface DeepSeek {

  String BASE_URL = "https://api.deepseek.com";
  String COMPLETIONS = "/chat/completions";

  DeepSeekOptions DEFAULT_OPTIONS =
    new DeepSeekOptions(0, null, 0, false, 1, 1, false, null, null);

  DeepSeekResult createChat(List<Message> messages, Modes mode, Map<String, Object> options)
    throws IOException;

  default DeepSeekResult createChat(List<Message> messages, Modes mode) throws IOException {
    return createChat(messages, mode, null);
  }

  default DeepSeekResult createChat(List<Message> messages) throws IOException {
    return createChat(messages, Modes.chat);
  }

  default String jsonParam(List<Message> messages, Modes mode, Map<String, Object> options) {
    Map<String, Object> param = new HashMap<>(DEFAULT_OPTIONS.toMap());
    if (options != null) {
      param.putAll(options);
    }
    param.put(MSG_KEG, messages);
    param.put(MODEL_KEY, mode.getName());
    return JSON.toJSONString(param);
  }


}