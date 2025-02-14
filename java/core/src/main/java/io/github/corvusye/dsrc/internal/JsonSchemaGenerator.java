package io.github.corvusye.dsrc.internal;

// Copyright (c) 2025 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import io.github.corvusye.dsrc.DeepSeekReverseCallImpl;
import io.github.corvusye.dsrc.DsrcApi;
import io.github.corvusye.dsrc.SchemaGenerator;
import io.github.pigmesh.ai.deepseek.core.chat.Message;
import io.github.pigmesh.ai.deepseek.core.chat.SystemMessage;
import io.swagger.v3.oas.annotations.media.Schema;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.stringtemplate.v4.ST;

/**
 * 根据调用侧的注解、方法、字段等信息生成 JSON Schema
 * 并自动填充到请求的 Message 中
 * 
 * @author yeweicheng
 * @since 2025-02-14 2:37
 * <br>Now is history!
 */
public class JsonSchemaGenerator implements SchemaGenerator {

  /**
   * 根据实体类的 DsrcApi 注解获取 topic 名称
   * 
   * @param topic topic 类
   * @return topic 名称
   */
  @Override
  public String topicName(Class<?> topic) {
    if (topic == null) return null;
    DsrcApi dsrcApi = topic.getAnnotation(DsrcApi.class);
    return dsrcApi.value();
  }

  /**
   * 从实体类的 DsrcApi 注解中的 subTopics，
   * 寻找与 topicName 匹配的子 topic 类
   * 
   * @param topic topic 类
   * @param topicName 子 topic 名称
   * @return 子 topic 类
   */
  @Override
  public Class<?> findSubTopic(Class<?> topic, String topicName) {
    DsrcApi dsrcApi = topic.getAnnotation(DsrcApi.class);
    Class<?>[] classes = dsrcApi.subTopics();
    return Arrays.stream(classes)
      .filter(c -> topicName(c).equals(topicName))
      .findFirst()
      .orElse(null);
  }

  /**
   * 生成字段的 JSON Schema 描述
   * 
   * @param topic topic 类
   * @param field 字段
   * @return 字段的 JSON Schema 描述
   */
  @Override
  public String fieldMessage(Class<?> topic, Field field) {
    Schema schema = schemaAnnotation(field);
    String schemaStr = schema.description();
    if (topic.isAnnotationPresent(DsrcApi.class)) {
      DsrcApi annotation = topic.getAnnotation(DsrcApi.class);
      Class<?>[] classes = annotation.subTopics();
      String[] plainTopics = annotation.plainTopics();
      if (classes.length > 0) {
        Map<String, String> subTopicSchema = Arrays.stream(classes)
          .collect(Collectors.toMap(this::topicName, this::schemaMessage));
        if (!subTopicSchema.isEmpty()) {
          ST st = new ST(schemaStr);
          st.add("topicKeys", String.join("|", subTopicSchema.keySet()));
          st.add("topicValues", String.join("|", subTopicSchema.values()));
          schemaStr = st.render();
        }
      }
    } else {
      schemaStr = field.getName();
    }
    return String.format("\"%s\": <%s>", field.getName(), schemaStr);
  }

  /**
   * 获取字段的 @Schema 注解
   * 
   * @param field 字段
   * @return @Schema 注解
   */
  @Override
  public Schema schemaAnnotation(Field field) {
    return field.getAnnotation(Schema.class);
  }

  /**
   * 生成 topic 类的 JSON Schema 描述
   * 
   * @param topic topic 类
   * @return topic 类的 JSON Schema 描述
   */
  @Override
  public String schemaMessage(Class<?> topic) {
    String fields = Arrays.stream(topic.getDeclaredFields())
      .map(field -> fieldMessage(topic, field))
      .collect(Collectors.joining(","));

    return String.format("{ %s }", fields);
  }

  /**
   * 生成 topic 类的消息
   *
   * @param topic               topic 类
   * @param dsrc        反向调用实现  
   * @return topic 类的消息
   */
  @Override
  public List<Message> topicMessage(
    Class<?> topic, 
    DeepSeekReverseCallImpl dsrc
  ) {
    List<Message> systemMessages = new ArrayList<>();

    if (topic.getAnnotation(DsrcApi.class) != null) {
      DsrcApi dsrcApi = topic.getAnnotation(DsrcApi.class);
      Arrays.stream(dsrcApi.prompt())
        .map(SystemMessage::from)
        .forEach(systemMessages::add);
    }

    return systemMessages;
  }

  /**
   * 获取 topic 类的子 topic 类
   * 
   * @param topic topic 类
   * @return 子 topic 类
   */
  @Override
  public List<Class> subTopics(Class<?> topic) {
    DsrcApi dsrcApi = topic.getAnnotation(DsrcApi.class);
    if (dsrcApi == null) return Collections.emptyList();
    return Arrays.asList(dsrcApi.subTopics());
  }


}
