package io.github.corvusye.dsrc;

// Copyright (c) 2025 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

/**
 * @author yeweicheng
 * @since 2025-02-10 6:54
 * <br>Now is history!
 */
public enum Roles {

  user("user"),
  system("system"),
  assistant("assistant");

  final private String name;

  Roles(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
  
  public Roles reverse() {
    switch (this) {
      case user:
        return assistant;
      case assistant:
        return user;
      default:
        return this;
    }
  }
}
