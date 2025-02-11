package io.github.corvusye.dsrc;

// Copyright (c) 2025 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import io.github.corvusye.dsrc.pojo.DeepSeekResult;

/**
 * @author yeweicheng
 * @since 2025-02-11 3:24
 * <br>Now is history!
 */
public interface DsrcInterceptor {
  
  boolean intercept(DeepSeekResult result);

}
