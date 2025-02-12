package io.github.corvusye.dsrc;

// Copyright (c) 2025 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import static io.github.corvusye.dsrc.DsrcConst.ROUTER_API;

import com.alibaba.fastjson2.JSON;
import io.github.pigmesh.ai.deepseek.core.chat.Message;
import io.github.pigmesh.ai.deepseek.core.chat.UserMessage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
@TestMethodOrder(OrderAnnotation.class)
class DeepseekReverseCallDiscussApplicationTests {

  @Autowired
  private DeepSeekReverseCall dsrc;

  @Test
  @Order(1)
  void chess() throws IOException {
    talkToDeepSeek("我们来下棋吧，你先下");
  }

  @Test
  @Order(2)
  void discuss() throws IOException {
    talkToDeepSeek("你那个时候最新的电影是哪一部啊");
  }

  void talkToDeepSeek(String message) throws IOException {
    List<Message> messages = new ArrayList<>();
    messages.add(UserMessage.from(message));
    Object discussResult = dsrc.api(ROUTER_API, messages, String.class);
    log.info(JSON.toJSONString(discussResult));
  }
}
