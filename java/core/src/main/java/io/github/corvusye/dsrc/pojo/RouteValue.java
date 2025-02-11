package io.github.corvusye.dsrc.pojo;

// Copyright (c) 2025 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author yeweicheng
 * @since 2025-02-10 7:00
 * <br>Now is history!
 */
@Data
@AllArgsConstructor
public class RouteValue {
  
  final private String route;
  
  final private Object data;
  
  final private Boolean finished;
  
}
