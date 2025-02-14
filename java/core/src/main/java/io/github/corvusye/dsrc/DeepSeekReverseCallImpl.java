package io.github.corvusye.dsrc;

// Copyright (c) 2025 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import static io.github.corvusye.dsrc.utils.DebugUtil.outputConversation;

import com.alibaba.fastjson2.JSON;
import io.github.corvusye.dsrc.internal.JsonSchemaGenerator;
import io.github.corvusye.dsrc.internal.TalkingConvertorReflectImpl;
import io.github.corvusye.dsrc.pojo.DeepSeekOptions;
import io.github.pigmesh.ai.deepseek.core.chat.ChatCompletionResponse;
import io.github.pigmesh.ai.deepseek.core.chat.Message;
import io.github.pigmesh.ai.deepseek.core.chat.SystemMessage;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.util.Pair;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * DeepSeek 反向调用 实现类
 *
 * @author yeweicheng
 * @since 2025-02-10 6:29
 * <br>Now is history!
 */
@Slf4j
@Data
public class DeepSeekReverseCallImpl implements DeepSeekReverseCall {

  final private DeepSeek deepSeek;
  final private Collection<DsrcAnswer> answers;
  final private boolean allowAnswer;
  final private Collection<DsrcInterceptor> interceptors;
  final private SchemaGenerator schemaGenerator;
  final private TalkingConvertor talkingConverter = new TalkingConvertorReflectImpl();

  final private Map<String, Pair<Method, DsrcAnswer>> answerMap = new HashMap<>();
  final private Map<String, Map<String, String>> apiRouters = new HashMap<>();

  final private Map<String, Message> schemaFormatEntity = new HashMap<>();
  final private Map<String, Message> schemaFormatReverse = new HashMap<>();
  
  public DeepSeekReverseCallImpl(DeepSeek deepSeek) {
    schemaGenerator = new JsonSchemaGenerator();
    this.deepSeek = deepSeek;
    answers = null;
    allowAnswer = false;
    interceptors = null;
  }

  public DeepSeekReverseCallImpl(
      DeepSeek deepSeek, Collection<DsrcAnswer> answers
  ) {
    schemaGenerator = new JsonSchemaGenerator();
    this.deepSeek = deepSeek;
    this.answers = answers;
    allowAnswer = answers != null && answers.iterator().hasNext();
    interceptors = null;
    registerAnswers();
  }

  public DeepSeekReverseCallImpl(DeepSeek deepSeek,
      Collection<DsrcAnswer> answers, Collection<DsrcInterceptor> interceptors) {
    schemaGenerator = new JsonSchemaGenerator();
    this.deepSeek = deepSeek;
    this.answers = answers;
    this.interceptors = interceptors;
    allowAnswer = answers != null && answers.iterator().hasNext();
    registerAnswers();
  }

  public DeepSeekReverseCallImpl(DeepSeek deepSeek,
    Collection<DsrcAnswer> answers, Collection<DsrcInterceptor> interceptors, 
    SchemaGenerator schemaGenerator
  ) {
    this.schemaGenerator = schemaGenerator;
    this.deepSeek = deepSeek;
    this.answers = answers;
    this.interceptors = interceptors;
    allowAnswer = answers != null && answers.iterator().hasNext();
    registerAnswers();
  }

  @Override
  public <T> T api(
    List<Message> messages,
    Class<T> topic,
    DeepSeekOptions options,
    Modes mode
  ) throws IOException {
    return api(messages, topic, options, mode, false);
  }

  public <T> T api(
    List<Message> messages,
    Class<T> topic,
    DeepSeekOptions options,
    Modes mode,
    boolean bySubTopic
  ) throws IOException {
    
    List<Message> systemMessage = schemaGenerator.topicMessage(topic,this);
    
    Message schemaMessage = getSchemaMessage(topic, bySubTopic);
    
    List<Message> allMessages = new ArrayList<>(systemMessage);
    if (schemaMessage != null) {
      allMessages.add(schemaMessage);
    }
    allMessages.addAll(messages);
    ChatCompletionResponse result = deepSeek.createChat(allMessages, schemaMessage, topic, mode, options);
    outputConversation("receive",allMessage(result));
    Message message = oneMessage(result);
    doIntercepts(result);
    T resultObj = resultToReturn(result, topic);

    if (schemaGenerator.subTopics(topic).isEmpty()) {
      return resultObj;
    }

    List<Message> nextStep =
      subTopic(topic, resultObj, message, messages, result, options, mode);

    if (nextStep != null && !nextStep.isEmpty()) {
      messages.addAll(nextStep);
      return api(messages, topic, options, mode, true);
    }
    return resultObj;
  }

  private <T> Message getSchemaMessage(Class<T> topic, boolean bySubTopic) {
    String topicName = schemaGenerator.topicName(topic);
    if (bySubTopic) {
      String schemaMessage = schemaGenerator.schemaMessage(topic);
      Message message = schemaFormatReverse.get(topicName);
      if (message != null) {
        return message;
      } else {
        SystemMessage formatMessage = SystemMessage.from("output json: " + schemaMessage);
        schemaFormatReverse.put(topicName, formatMessage);
        return formatMessage;
      }
    } else {
      return schemaFormatEntity.get(topicName);
    }
  }

  @Override
  public String api(
    List<Message> messages,
    String prompt,
    DeepSeekOptions options,
    Modes mode
  ) throws IOException {
    List<Message> newMessage;
    if (prompt != null && !prompt.isEmpty()) {
      SystemMessage promptMessage = SystemMessage.from(prompt);
      newMessage = new ArrayList<>();
      newMessage.add(promptMessage);
      newMessage.addAll(messages);
    } else {
      newMessage = messages;
    }
    ChatCompletionResponse result = deepSeek.createChat(newMessage, mode, options);
    outputConversation("receive",allMessage(result));
    doIntercepts(result);
    return content(result);
  }


  @Override
  public List<Message> subTopic(
    Class<?> topic,
    Object resultObj,
    Message message,
    List<Message> messages,
    ChatCompletionResponse result,
    DeepSeekOptions options,
    Modes mode
  ) {
    String topicName = schemaGenerator.topicName(topic);
    return subTopic(topicName, topic, resultObj, message, messages, result, options, mode);
  }

  @Override
  public List<Message> subTopic(
    String topicName,
    Object resultObj,
    Message message,
    List<Message> messages,
    ChatCompletionResponse result,
    DeepSeekOptions options,
    Modes mode
  ) {
    Class<?> topic = resultObj == null ? null : resultObj.getClass();
    return subTopic(topicName, topic, resultObj, message, messages, result, options, mode);
  }

  private List<Message> subTopic(
    String topicName,
    Class<?> topic,
    Object resultObj,
    Message message,
    List<Message> messages,
    ChatCompletionResponse result,
    DeepSeekOptions options,
    Modes mode
  ) {
    try {
      return talkingConverter.subTopic(topic, resultObj, message, messages, result, options, mode, this);
    } catch (IllegalAccessException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }

  private <T> T tryParse(String content, Class<T> clazz) {
    content = content.trim();
    if (content.startsWith("```json") && content.endsWith("```")) {
      content = content.substring(7, content.length() - 3).trim();
    }
    return JSON.parseObject(content, clazz);
  }

  @SuppressWarnings("unchecked")
  private <T> T resultToReturn(ChatCompletionResponse result, Class<T> clazz) {
    if (clazz == String.class) {
      return (T) content(result);
    } else if (clazz == Object.class) {
      return (T) result;
    } else if (clazz == List.class) {
      return (T) allMessage(result);
    } else {
      return tryParse(content(result), clazz);
    }
  }

  private boolean doIntercepts(ChatCompletionResponse result) {
    boolean returnDefault = false;
    if (interceptors != null) {
      for (DsrcInterceptor interceptor : interceptors) {
        boolean hasNext = interceptor.intercept(result);
        if (!hasNext) {
          returnDefault = true;
        }
      }
    }
    return returnDefault;
  }

  private void registerAnswers() {
    if (!allowAnswer) {
      return;
    }
    for (DsrcAnswer answer : answers) {
      DsrcApi api = answer.getClass().getAnnotation(DsrcApi.class);
      if (api == null) {
        continue;
      }
      String apiName = api.value();
      Method[] methods = answer.getClass().getMethods();
      for (Method method : methods) {
        DsrcApi methodApi = method.getAnnotation(DsrcApi.class);
        if (methodApi == null) {
          continue;
        }
        String methodApiName = methodApi.value();
        if (methodApiName.isEmpty()) {
          continue;
        }
        String topicName = apiName + "." + methodApiName;
        answerMap.put(topicName, new Pair<>(method, answer));
        cacheSchemaFormat(topicName, method);
      }
    }
  }

  private void cacheSchemaFormat(String topicName, Method method) {
    Message message = talkingConverter.schemaMessage(method, this);
    if (message != null) {
      schemaFormatEntity.put(topicName, message);
    }
  }
  
}
