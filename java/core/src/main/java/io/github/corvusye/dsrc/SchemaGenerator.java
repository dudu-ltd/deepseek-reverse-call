package io.github.corvusye.dsrc;

// Copyright (c) 2025 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import io.github.pigmesh.ai.deepseek.core.chat.Message;
import io.swagger.v3.oas.annotations.media.Schema;
import java.lang.reflect.Field;
import java.util.List;

/**
 * @author yeweicheng
 * @since 2025-02-14 2:35
 * <br>Now is history!
 */
public interface SchemaGenerator {

  String topicName(Class<?> topic);

  Class<?> findSubTopic(Class<?> topic, String name);

  String fieldMessage(Class<?> topic, Field field);

  Schema schemaAnnotation(Field field);

  String schemaMessage(Class<?> topic);

  List<Message> topicMessage(Class<?> topic, DeepSeekReverseCallImpl dsrc);

  List<Class> subTopics(Class<?> topic);
}
