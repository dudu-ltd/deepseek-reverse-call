package ltd.dudu.dsrc;

// Copyright (c) 2025 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import com.alibaba.fastjson2.JSON;
import io.github.pigmesh.ai.deepseek.core.DeepSeekClient;
import io.github.pigmesh.ai.deepseek.core.SyncOrAsyncOrStreaming;
import io.github.pigmesh.ai.deepseek.core.chat.ChatCompletionRequest;
import io.github.pigmesh.ai.deepseek.core.chat.ChatCompletionResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author yeweicheng
 * @since 2025-02-12 3:05
 * <br>Now is history!
 */
@Slf4j
@SpringBootTest
public class DeekSeek4jTests {

  @Autowired
  private DeepSeekClient deepSeekClient;

  @Test
  void test() {
    ChatCompletionRequest request = ChatCompletionRequest.builder()
      // 模型选择，支持 DEEPSEEK_CHAT、DEEPSEEK_REASONER 等
      .model("deepseek-chat")
      // 添加用户消息
      .addUserMessage("苍鹰")
      // 添加助手消息，用于多轮对话
      .addAssistantMessage("上轮结果")
      // 添加系统消息，用于设置角色和行为
      .addSystemMessage("你是一个很好的诗人，任何提问都是一首诗的标题")
      // 添加系统消息，用于设置角色和行为
      .addSystemMessage("返回接口格式为：{\"status\": \"success\", \"data\": {\"completions\": [{\"text\": <回应>}]}}")
      // 设置最大生成 token 数，默认 2048
      .build();
    SyncOrAsyncOrStreaming<ChatCompletionResponse> chatCompletionResponseSyncOrAsyncOrStreaming = deepSeekClient.chatCompletion(
      request);
    ChatCompletionResponse execute = chatCompletionResponseSyncOrAsyncOrStreaming.execute();
    log.info(execute.toString());
    log.info(JSON.toJSONString(execute));
  }

}
