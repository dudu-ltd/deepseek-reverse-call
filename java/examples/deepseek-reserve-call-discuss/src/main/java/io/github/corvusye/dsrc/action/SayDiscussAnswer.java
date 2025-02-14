package io.github.corvusye.dsrc.action;

// Copyright (c) 2025 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import io.github.corvusye.dsrc.DeepSeekReverseCall;
import io.github.corvusye.dsrc.DsrcAnswer;
import io.github.corvusye.dsrc.DsrcApi;
import io.github.corvusye.dsrc.pojo.Chess;
import io.github.corvusye.dsrc.pojo.Discuss;
import io.github.pigmesh.ai.deepseek.core.chat.Message;
import io.github.pigmesh.ai.deepseek.core.chat.UserMessage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * @author yeweicheng
 * @since 2025-02-11 2:10
 * <br>Now is history!
 */
@Slf4j
@Component
@DsrcApi("say")
public class SayDiscussAnswer implements DsrcAnswer {
  
  @Lazy
  @Autowired
  private DeepSeekReverseCall deepSeekReverseCall;
  
  @DsrcApi("goodbye")
  public List<Message> goodbye(String goodbye, List<Message> messages) {
    log.info("Goodbye!");
    System.out.println(goodbye);
    return null;
  }
  
  @DsrcApi("discuss")
  public List<Message> discuss(Discuss discuss, List<Message> messages)
    throws IOException {
    List<Message> newMsgs = new ArrayList<>(deepSeekReverseCall.reverseRole(messages));
    Message prevMsg = UserMessage.from(discuss.getDiscuss());
    newMsgs.add(prevMsg);
    Discuss currentMsg = deepSeekReverseCall.api(newMsgs, Discuss.class);
    List<Message> response = new ArrayList<>();
    response.add(deepSeekReverseCall.reverseRole(prevMsg));
    response.add(UserMessage.from(currentMsg.getDiscuss()));
    return response;
  }
  
  @DsrcApi("chess")
  public List<Message> chess(Chess chess, List<Message> messages)
    throws IOException {
    String actualChess = chess.getChess();
    List<Message> newMsgs = new ArrayList<>(deepSeekReverseCall.reverseRole(messages));
    Message prevMsg = UserMessage.from(actualChess);
    newMsgs.add(prevMsg);
    Chess currentMsg = deepSeekReverseCall.api(newMsgs, Chess.class);
    List<Message> response = new ArrayList<>();
    response.add(deepSeekReverseCall.reverseRole(prevMsg));
    response.add(UserMessage.from(currentMsg.getChess()));
    return response;
  }
  
}
