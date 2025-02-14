package ltd.dudu.dsrc;

// Copyright (c) 2025 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import com.alibaba.fastjson2.JSON;
import ltd.dudu.dsrc.pojo.Chess;
import ltd.dudu.dsrc.pojo.Discuss;
import ltd.dudu.dsrc.pojo.RouteValue;
import java.io.IOException;
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
    String message = "我们来下棋吧，你先下";
    Chess chess = dsrc.api(message, Chess.class);
    log.info(JSON.toJSONString(chess));
  }

  @Test
  @Order(2)
  void discuss() throws IOException {
    String message = "你那个时候最新的电影是哪一部啊";
    Discuss movie = dsrc.api(message, Discuss.class);
    log.info(JSON.toJSONString(movie));
  }

  @Test
  @Order(2)
  void discussMulti() throws IOException {
    String message = "我们来讨论一下做点什么吧";
    RouteValue movie = dsrc.api(message, RouteValue.class);
    log.info(JSON.toJSONString(movie));
  }

  @Test
  @Order(3)
  void route() throws IOException {
    RouteValue seeYou = dsrc.api("你对学习大语言模型有什么建议", RouteValue.class);
    log.info(JSON.toJSONString(seeYou));
  }

  @Test
  @Order(4)
  void stringTopic() throws IOException {
    String greet = dsrc.api("你好", "我们来聊一聊");
    log.info(greet);
  }

  @Test
  @Order(5)
  void goodbye() throws IOException {
    RouteValue seeYou = dsrc.api("再见", RouteValue.class);
    log.info(JSON.toJSONString(seeYou));
  }

}
