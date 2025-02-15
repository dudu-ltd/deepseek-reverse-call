package ltd.dudu.dsrc.controller;

// Copyright (c) 2025 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import java.io.IOException;
import ltd.dudu.dsrc.DeepSeekReverseCall;
import ltd.dudu.dsrc.pojo.Chess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yeweicheng
 * @since 2025-02-15 9:49
 * <br>Now is history!
 */
@RestController
@RequestMapping("chess")
public class ChessController {
  
  @Autowired
  private DeepSeekReverseCall dsrc;

  @RequestMapping("next")
  public Chess chess(String question) throws IOException {
    return dsrc.api(question, Chess.class);
  }
  
}
