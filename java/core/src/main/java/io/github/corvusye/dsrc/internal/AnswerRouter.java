package io.github.corvusye.dsrc.internal;

// Copyright (c) 2025 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import io.github.corvusye.dsrc.DeepSeekReverseCall;
import io.github.corvusye.dsrc.DsrcAnswer;
import io.github.corvusye.dsrc.DsrcApi;
import io.github.corvusye.dsrc.pojo.Message;
import java.util.List;

/**
 * @author yeweicheng
 * @since 2025-02-11 2:59
 * <br>Now is history!
 */
@DsrcApi("dsrc")
public class AnswerRouter implements DsrcAnswer {
  
  final DeepSeekReverseCall dsrc;
  
  public AnswerRouter(DeepSeekReverseCall dsrc) {
    this.dsrc = dsrc;
  }
  
  public List<Message> route(Object args) {
    return null;
  }

}
