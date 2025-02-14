package ltd.dudu.dsrc;

// Copyright (c) 2025 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import ltd.dudu.dsrc.internal.JsonSchemaGenerator;
import ltd.dudu.dsrc.pojo.DeepSeekOptions;
import io.github.pigmesh.ai.deepseek.core.DeepSeekClient;
import io.github.pigmesh.ai.deepseek.core.chat.ChatCompletionRequest;
import io.github.pigmesh.ai.deepseek.core.chat.ChatCompletionRequest.Builder;
import io.github.pigmesh.ai.deepseek.core.chat.ChatCompletionResponse;
import io.github.pigmesh.ai.deepseek.core.chat.JsonArraySchema;
import io.github.pigmesh.ai.deepseek.core.chat.JsonBooleanSchema;
import io.github.pigmesh.ai.deepseek.core.chat.JsonEnumSchema;
import io.github.pigmesh.ai.deepseek.core.chat.JsonIntegerSchema;
import io.github.pigmesh.ai.deepseek.core.chat.JsonNumberSchema;
import io.github.pigmesh.ai.deepseek.core.chat.JsonObjectSchema;
import io.github.pigmesh.ai.deepseek.core.chat.JsonSchema;
import io.github.pigmesh.ai.deepseek.core.chat.JsonSchemaElement;
import io.github.pigmesh.ai.deepseek.core.chat.JsonStringSchema;
import io.github.pigmesh.ai.deepseek.core.chat.Message;
import io.github.pigmesh.ai.deepseek.core.chat.ResponseFormat;
import io.github.pigmesh.ai.deepseek.core.chat.ResponseFormatType;
import io.github.pigmesh.ai.deepseek.core.chat.SystemMessage;
import io.swagger.v3.oas.annotations.media.Schema;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ltd.dudu.dsrc.utils.DebugUtil;

/**
 * @author yeweicheng
 * @since 2025-02-12 2:51
 * <br>Now is history!
 */
public class DeepSeek4jImpl implements DeepSeek {

  final private DeepSeekClient deepSeekClient;
  private SchemaGenerator schemaGenerator;

  public DeepSeek4jImpl(DeepSeekClient deepSeekClient) {
    this.deepSeekClient = deepSeekClient;
  }
  
  public DeepSeek4jImpl(DeepSeekClient deepSeekClient, SchemaGenerator schemaGenerator) {
    this.deepSeekClient = deepSeekClient;
    this.schemaGenerator = schemaGenerator == null 
      ? new JsonSchemaGenerator() : schemaGenerator;
  }

  public ChatCompletionResponse createChat(
    List<Message> messages, String topicPrompt, Modes mode, DeepSeekOptions options) {

    Builder requestBuilder = ChatCompletionRequest.builder()
      .model(mode.getName());
    ArrayList<Message> allMessages = new ArrayList<>();
    if (topicPrompt != null) {
      allMessages.add(SystemMessage.from(topicPrompt));
    }
    allMessages.addAll(messages);
    requestBuilder.messages(allMessages);
    requestBuilder.responseFormat(ResponseFormatType.TEXT);

    DebugUtil.outputConversation("send", allMessages);
    ChatCompletionRequest request = requestBuilder.build();

    return deepSeekClient.chatCompletion(request).execute();
  }

  public ChatCompletionResponse createChat(
    List<Message> messages, Message schemaMessage, Class<?> topic, Modes mode, DeepSeekOptions options) {

    Builder requestBuilder = ChatCompletionRequest.builder()
      .model(mode.getName());

    requestBuilder.responseFormat(
      schemaMessage == null ? ResponseFormatType.TEXT : ResponseFormatType.JSON_OBJECT
    );

    requestBuilder.messages(messages);
    
    DebugUtil.outputConversation("send", messages);
    ChatCompletionRequest request = requestBuilder.build();
    
    return deepSeekClient.chatCompletion(request).execute();
  }

  // TODO 目前未支持  
  private ResponseFormat dataFormat(Class<?> topic) {
    if (topic == null || topic == String.class) {
      return ResponseFormat.builder()
        .type(ResponseFormatType.TEXT)
        .build();
    }
    Field[] fields = topic.getFields();
    JsonSchema.Builder schemaBuilder = JsonSchema.builder();
    JsonObjectSchema.Builder objectSchemaBuilder = JsonObjectSchema.builder();
    Map<String, JsonSchemaElement> elements = new HashMap<>();
    for (Field field : fields) {
      // 获取 @Schema 注解
      Schema schema = schemaGenerator.schemaAnnotation(field);
      JsonSchemaElement schemaEle = propertyFormat(field, schema);
      elements.put(field.getName(), schemaEle);
    }
    objectSchemaBuilder.properties(elements);
    schemaBuilder.schema(objectSchemaBuilder.build());
    
    return ResponseFormat.builder()
      .type(ResponseFormatType.JSON_OBJECT)
      .jsonSchema(schemaBuilder.build())
      .build();
  }
  
  
  private JsonSchemaElement propertyFormat(Field field, Schema schema) {
    Class<?> type = field.getType();
    if (type == String.class) {
      return JsonStringSchema.builder()
        .description(schema.description())
        .build();
    }
    
    boolean isInt = type == Integer.class || type == int.class;
    boolean isLong = type == Long.class || type == long.class;
    boolean isShort = type == Short.class || type == short.class;
    boolean isByte = type == Byte.class || type == byte.class;
    
    if (isInt || isLong || isShort || isByte) {
      return JsonIntegerSchema.builder()
        .description(schema.description())
        .build();
    }
    
    if (type == Boolean.class || type == boolean.class) {
      return JsonBooleanSchema.builder()
        .description(schema.description())
        .build();
    }
    
    if (type.isAssignableFrom(List.class)) {
      return JsonArraySchema.builder()
        .description(schema.description())
        .build();
    }
    
    if (type.isAssignableFrom(Number.class)) {
      return JsonNumberSchema.builder()
        .description(schema.description())
        .build();
    }
    
    if (type.isEnum()) {
      return JsonEnumSchema.builder()
        .description(schema.description())
        .enumValues(type)
        .build();
    }
    
    return null;
  }

}
