package ltd.dudu.dsrc.utils;

// Copyright (c) 2025 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import com.alibaba.fastjson2.JSONObject;
import io.github.pigmesh.ai.deepseek.core.chat.Message;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yeweicheng
 * @since 2025-02-13 5:03
 * <br>Now is history!
 */
@Slf4j
public class DebugUtil {
  
  public static void outputConversation(String prompt, List<Message> messages) {
    if (log.isDebugEnabled()) {
      String conversation = messages.stream()
        // .filter(m -> !(m instanceof SystemMessage))
        .map(m -> m.role() + ": " + content(m))
        .collect(Collectors.joining("\n"));
      log.debug("\n---- {} ----\n{}", prompt, conversation);
    }
  }
  
  private static String content(Message message) {
    JSONObject json = JSONObject.from(message);
    if (json == null) return null;
    Object content = json.get("content");
    if (content == null) return null;
    return content.toString();
  }

}
