package ltd.dudu.dsrc.internal;

// Copyright (c) 2025 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import com.alibaba.fastjson2.JSON;
import ltd.dudu.dsrc.DeepSeekReverseCall;
import ltd.dudu.dsrc.DeepSeekReverseCallImpl;
import ltd.dudu.dsrc.DsrcAnswer;
import ltd.dudu.dsrc.DsrcApi;
import ltd.dudu.dsrc.Modes;
import ltd.dudu.dsrc.TalkingConvertor;
import ltd.dudu.dsrc.pojo.DeepSeekOptions;
import io.github.pigmesh.ai.deepseek.core.chat.ChatCompletionResponse;
import io.github.pigmesh.ai.deepseek.core.chat.Message;
import io.github.pigmesh.ai.deepseek.core.chat.SystemMessage;
import io.github.pigmesh.ai.deepseek.core.chat.UserMessage;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import javafx.util.Pair;
import org.jetbrains.annotations.NotNull;

/**
 * @author yeweicheng
 * @since 2025-02-14 4:14
 * <br>Now is history!
 */
public class TalkingConvertorReflectImpl implements TalkingConvertor {

  @Override
  @SuppressWarnings("unchecked")
  public List<Message> toMessages(Object obj) {
    if (obj == null) {
      return Collections.emptyList();
    } else if (obj instanceof List) {
      List<?> listCasted = (List<?>) obj;
      if (listCasted.stream().allMatch(o -> o instanceof Message)) {
        return (List<Message>) obj;
      } else {
        return listCasted.stream()
          .filter(Objects::nonNull)
          .map(this::toMessage)
          .collect(Collectors.toList());
      }
    } else if (obj instanceof Message) {
      return Collections.singletonList((Message) obj);
    } else {
      return Collections.singletonList(toMessage(obj));
    }
  }

  private Message toMessage(Object obj) {
    return obj == null ? null
      : obj instanceof Message ? (Message) obj
      : obj instanceof String ? UserMessage.from((String) obj)
      : UserMessage.from(JSON.toJSONString(obj));
  }


  @Override
  public Object[] toSubTopicArgs(
    Class<?> topic,
    Method method,
    Object resultObj,
    Message message,
    List<Message> messages,
    ChatCompletionResponse result,
    DeepSeekOptions options,
    Modes mode,
    DeepSeekReverseCall dsrc
  ) {
    int parameterCount = method.getParameterCount();
    Object[] methodArgs = new Object[parameterCount];
    if (parameterCount == 0) {
      return methodArgs;
    }
    // 根据类型填充参数
    Class<?>[] parameterTypes = method.getParameterTypes();
    // 记录剩余多少个参数需要填充
    List<Integer> retained = fillGlobalVar(method, message, messages, result, options, mode,
      dsrc, parameterCount, methodArgs, parameterTypes);
    // 填充剩余的参数
    
    if (retained.size() == 0) {
      return methodArgs;
    }
    
    Object paramByType = null;
    
    if (retained.size() == 1) {
      Integer retainIdx = retained.get(0);
      paramByType = onlyOneParam(resultObj, message, dsrc, paramByType, parameterTypes[retainIdx]);
      if (paramByType != null) {
        methodArgs[retained.get(0)] = paramByType;
        return methodArgs;
      }
    }

    return methodArgs;
  }

  @Override
  public List<Message> subTopic(Class<?> topic, Object resultObj, Message message,
    List<Message> messages, ChatCompletionResponse result, DeepSeekOptions options, Modes mode,
    DeepSeekReverseCallImpl dsrc) throws InvocationTargetException, IllegalAccessException {

    String topicName = dsrc.getSchemaGenerator().topicName(topic);
    Pair<Method, DsrcAnswer> topicHandler = dsrc.getAnswerMap().get(topicName);
    if (topicHandler == null) {
      return null;
    }

    Method method = topicHandler.getKey();
    DsrcAnswer answer = topicHandler.getValue();

    Object[] args = toSubTopicArgs(topic, method, resultObj, message, messages, result, options, mode, dsrc);
    Object subResult = method.invoke(answer, args);
    return toMessages(subResult);
  }

  @Override
  public Message schemaMessage(Method method, DeepSeekReverseCall dsrc) {
    List<Integer> customParam = new ArrayList<>();
    int parameterCount = method.getParameterCount();
    Class<?>[] parameterTypes = method.getParameterTypes();
    String schemaFormat = null;
    for (int i = 0; i < parameterCount; i++) {
      Class<?> parameterType = parameterTypes[i];
      boolean builtInParamType = isBuiltInParamType(method, i, parameterType);
      if (!builtInParamType) {
        customParam.add(i);
      }
    }
    if (customParam.size() == 0) {
      return null;
    }
    if (customParam.size() == 1) {
      Class<?> paramType = parameterTypes[customParam.get(0)];
      if (isSimpleType(paramType)) {
        schemaFormat = paramType.getSimpleName();
      } else {
        boolean annotationPresent = paramType.isAnnotationPresent(DsrcApi.class);
        if (annotationPresent) {
          schemaFormat = dsrc.getSchemaGenerator().schemaMessage(paramType);
        }
      }
    } else {
      // 处理多参数问题
      Map<String, Object> indexParam = new HashMap<>();
      for (int i : customParam) {
        Class<?> paramType = parameterTypes[i];
        String schemaMessage = dsrc.getSchemaGenerator().schemaMessage(paramType);
        indexParam.put(String.valueOf(i), schemaMessage);
      }
      schemaFormat = JSON.toJSONString(indexParam);
    }
    
    if (schemaFormat != null) {
      return SystemMessage.from("output json: " + schemaFormat);
    }
    return null;
  }

  private boolean isSimpleType(Class<?> paramType) {
    return paramType.isPrimitive()
      || String.class.isAssignableFrom(paramType)
      || Number.class.isAssignableFrom(paramType)
      || Boolean.class.isAssignableFrom(paramType);
  }

  private boolean isBuiltInParamType(Method method, int i, Class<?> paramType) {
    return DeepSeekOptions.class.isAssignableFrom(paramType)
      || DeepSeekReverseCall.class.isAssignableFrom(paramType)
      || Modes.class.isAssignableFrom(paramType)
      || Message.class.isAssignableFrom(paramType)
      || ChatCompletionResponse.class.isAssignableFrom(paramType)
      || (List.class.isAssignableFrom(paramType) && isListT(method, i, Message.class));
  }
  
  /**
   * 只有一个参数的情况
   * 
   * @param resultObj 路由的实质结果
   * @param message 当次请求的 Message 对象
   * @param dsrc DeepSeekReverseCall 实例
   * @param paramByType 参数值
   * @param paramType 参数类型
   * @return 返回填充后的参数值
   */
  private static Object onlyOneParam(
    Object resultObj, 
    Message message, 
    DeepSeekReverseCall dsrc,
    Object paramByType, 
    Class<?> paramType
  ) {
    if (resultObj != null && resultObj.getClass().isAssignableFrom(paramType)) {
      paramByType = resultObj;
    } else if (String.class.isAssignableFrom(paramType)) {
      paramByType = dsrc.content(message);
    }
    return paramByType;
  }

  /** 
   * 优先填充 DeepSeekOptions、Modes、ChatCompletionResponse 这类全局性的参数
   * 
   * @param method 话题路由触发的回调方法
   * @param message 当次请求的 Message 对象
   * @param messages 多轮对话的结果
   * @param result 话题路由触发的回调方法的返回值
   * @param options DeepSeekOptions 配置参数
   * @param mode 使用的模型
   * @param dsrc DeepSeekReverseCall 实例
   * @param parameterCount 方法参数个数
   * @param methodArgs 方法参数数组
   * @param parameterTypes 方法参数类型数组
   * @return 返回未填充的参数索引
   */
  @NotNull
  private List<Integer> fillGlobalVar(Method method, Message message, List<Message> messages,
    ChatCompletionResponse result, DeepSeekOptions options, Modes mode, DeepSeekReverseCall dsrc,
    int parameterCount, Object[] methodArgs, Class<?>[] parameterTypes) {
    List<Integer> retained = new ArrayList<>();
    for (int i = 0; i < parameterCount; i++) {
      Class<?> paramType = parameterTypes[i];
      methodArgs[i] = DeepSeekOptions.class.isAssignableFrom(paramType) ? options
        : DeepSeekReverseCall.class.isAssignableFrom(paramType) ? dsrc
        : Modes.class.isAssignableFrom(paramType) ? mode
        : Message.class.isAssignableFrom(paramType) ? message
        : ChatCompletionResponse.class.isAssignableFrom(paramType) ? result
        : (List.class.isAssignableFrom(paramType) && isListT(method, i, Message.class)) ? messages
        : null;
      if (methodArgs[i] == null) {
        retained.add(i);
      }
    }
    return retained;
  }

  private boolean isListT(Method method, int i, Class<?> genericType) {
    Type[] genericParameterTypes = method.getGenericParameterTypes();
    if (genericParameterTypes.length <= i) {
      return false;
    }
    Type genericParameterType = genericParameterTypes[i];
    if (genericParameterType instanceof ParameterizedType) {
      ParameterizedType genParamType = (ParameterizedType) genericParameterType;
      Type rawType = genParamType.getRawType();
      if (!(rawType instanceof Class<?>) || !List.class.isAssignableFrom((Class<?>) rawType)) {
        return false;
      }
      Type[] actualTypeArguments = genParamType.getActualTypeArguments();
      
      boolean hasGen = actualTypeArguments.length == 1;
      if (!hasGen) {
        return false;
      }
      if (actualTypeArguments[0] instanceof Class) {
        return genericType.isAssignableFrom((Class<?>) actualTypeArguments[0]);
      }
    }
    return false;
  }
}
