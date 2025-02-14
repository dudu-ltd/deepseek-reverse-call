package ltd.dudu.dsrc.action;

// Copyright (c) 2025 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import ltd.dudu.dsrc.DeepSeekReverseCall;
import ltd.dudu.dsrc.DsrcAnswer;
import ltd.dudu.dsrc.DsrcApi;
import ltd.dudu.dsrc.pojo.RouteValue;
import io.github.pigmesh.ai.deepseek.core.chat.ChatCompletionResponse;
import io.github.pigmesh.ai.deepseek.core.chat.Message;
import io.github.pigmesh.ai.deepseek.core.chat.UserMessage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author yeweicheng
 * @since 2025-02-11 2:59
 * <br>Now is history!
 */
@Slf4j
@Data
@DsrcApi("dsrc")
@Component
@NoArgsConstructor
public class RouterAnswer implements DsrcAnswer {
  
  @DsrcApi("router")
  public List<Message> route(
    RouteValue prev,
    Message message,
    List<Message> messages,
    ChatCompletionResponse response,
    DeepSeekReverseCall dsrc
  ) throws IOException {
    try {
      Class<?> subTopicClass = 
          dsrc.getSchemaGenerator().findSubTopic(prev.getClass(), prev.getRoute());

      Object incoming = convertDataToClass(prev.getData(), subTopicClass);
      
      if (!prev.getFinished() && prev.getRound() < 3) {
        List<Message> fromLocal = dsrc.subTopic(prev.getRoute(), incoming, message, messages, response, null, null);
        if (fromLocal != null && !fromLocal.isEmpty() ) {
          ArrayList<Message> allMsgs = new ArrayList<>(messages);
          allMsgs.addAll(fromLocal);
          RouteValue route = dsrc.api(allMsgs, RouteValue.class);
          if (route != null && !route.getFinished() && route.getData() != null) {
            String msg = route.getData() instanceof String ? route.getData().toString() : JSON.toJSONString(route.getData());
            allMsgs.add(UserMessage.from(msg));
          }
        }
      } else {
        return null;
      }
    } catch (Exception e) {
      log.warn("exception: []", e);
      return null;
    }
    return null;
  }
  
  public Object convertDataToClass(Object data, Class<?> clazz) {
    if (clazz == null) {
      if (data instanceof String) {
        return data;
      } else {
        return JSON.toJSONString(data);
      }
    } else if (clazz.equals(data.getClass())) {
      return data;
    } else if (data instanceof JSONObject) {
      return JSON.parseObject(JSON.toJSONString(data), clazz);
    } else if (data instanceof String) {
      return JSON.parseObject((String) data, clazz);
    } else {
      return data;
    }
  }
  
  

}
