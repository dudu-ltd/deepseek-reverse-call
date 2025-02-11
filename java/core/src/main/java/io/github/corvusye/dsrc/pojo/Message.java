package io.github.corvusye.dsrc.pojo;

// Copyright (c) 2025 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import io.github.corvusye.dsrc.Roles;
import lombok.Data;

/**
 * 消息
 * 
 * @author yeweicheng
 * @since 2025-02-10 1:22
 * <br>Now is history!
 */
@Data
public class Message {
  
  final private String content;
  
  final private String role;
  
  public Message(String content, Roles role) {
    this.content = content;
    this.role = role.getName();
  }
  
  public Message reverseRole() {
    return new Message(content, Roles.valueOf(role).reverse());
  }
  
  public boolean isNotSystem() {
    return !Roles.system.getName().equals(role);
  }
  
}
