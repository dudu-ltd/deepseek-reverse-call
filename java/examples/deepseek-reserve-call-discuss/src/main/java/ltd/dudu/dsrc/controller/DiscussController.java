package ltd.dudu.dsrc.controller;

// Copyright (c) 2025 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import java.io.IOException;
import ltd.dudu.dsrc.DeepSeekReverseCall;
import ltd.dudu.dsrc.pojo.Discuss;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yeweicheng
 * @since 2025-02-15 9:53
 * <br>Now is history!
 */
@RestController
@RequestMapping("discuss")
public class DiscussController {
  @Autowired
  private DeepSeekReverseCall dsrc;
  
  @RequestMapping("say")
  public Discuss discuss(String say) throws IOException {
    return dsrc.api(say, Discuss.class);
  }

}
