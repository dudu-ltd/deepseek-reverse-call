package io.github.corvusye.dsrc.action;

// Copyright (c) 2025 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import com.alibaba.fastjson2.JSONObject;
import io.github.corvusye.dsrc.DeepSeekReverseCall;
import io.github.corvusye.dsrc.DsrcAnswer;
import io.github.corvusye.dsrc.DsrcApi;
import io.github.corvusye.dsrc.pojo.Chess;
import io.github.pigmesh.ai.deepseek.core.chat.Message;
import io.github.pigmesh.ai.deepseek.core.chat.UserMessage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * @author yeweicheng
 * @since 2025-02-11 2:10
 * <br>Now is history!
 */
@Component
@DsrcApi("say")
public class SayDiscussAnswer implements DsrcAnswer {
  
  @Lazy
  @Autowired
  private DeepSeekReverseCall deepSeekReverseCall;
  
  @DsrcApi("discuss")
  public List<Message> discuss(Object args, List<Message> messages)
    throws IOException {
    String discuss = args.toString();
    List<Message> newMsgs = new ArrayList<>(deepSeekReverseCall.reverseRole(messages));
    Message prevMsg = UserMessage.from(discuss);
    newMsgs.add(prevMsg);
    String currentMsg = deepSeekReverseCall.api("say.discuss", newMsgs, String.class);
    List<Message> response = new ArrayList<>();
    response.add(deepSeekReverseCall.reverseRole(prevMsg));
    response.add(UserMessage.from(currentMsg));
    return response;
  }
  
  @DsrcApi("chess")
  public List<Message> chess(Object args, List<Message> messages)
    throws IOException {
    String discuss = JSONObject.parseObject(args.toString(), Chess.class).getChess();
    List<Message> newMsgs = new ArrayList<>(deepSeekReverseCall.reverseRole(messages));
    Message prevMsg = UserMessage.from(discuss);
    newMsgs.add(prevMsg);
    Chess currentMsg = deepSeekReverseCall.api("say.chess", newMsgs, Chess.class);
    List<Message> response = new ArrayList<>();
    response.add(deepSeekReverseCall.reverseRole(prevMsg));
    response.add(UserMessage.from(currentMsg.getChess()));
    return response;
  }
  
}
