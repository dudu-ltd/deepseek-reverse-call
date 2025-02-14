package ltd.dudu.dsrc;

// Copyright (c) 2025 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import ltd.dudu.dsrc.pojo.DeepSeekOptions;
import io.github.pigmesh.ai.deepseek.core.chat.ChatCompletionResponse;
import io.github.pigmesh.ai.deepseek.core.chat.Message;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * 对话中，涉及切换不同话题时，需要进行参数转换
 * <br>这个接口主要用于将不同的对话转换为不同的参数
 *
 * @author yeweicheng
 * @since 2025-02-14 4:06
 * <br>Now is history!
 */
public interface TalkingConvertor {

  List<Message> toMessages(Object obj);

  Object[] toSubTopicArgs(
    Class<?> topic,
    Method method,
    Object resultObj,
    Message message,
    List<Message> messages,
    ChatCompletionResponse result,
    DeepSeekOptions options,
    Modes mode,
    DeepSeekReverseCall dsrc
  );

  List<Message> subTopic(
    Class<?> topic, 
    Object resultObj, 
    Message message, 
    List<Message> messages,
    ChatCompletionResponse result, 
    DeepSeekOptions options, 
    Modes mode,
    DeepSeekReverseCallImpl deepSeekReverseCall
  ) throws InvocationTargetException, IllegalAccessException;

  Message schemaMessage(Method method, DeepSeekReverseCall dsrc);
}
