// Copyright (c) 2025 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

part of '../../de_src.dart';

Map<String, dynamic> tryJsonDecode(String? msg) {
  msg = msg?.trim() ?? '';
  var start = msg.indexOf('{');
  var end = msg.lastIndexOf('}');
  if (start != -1 && end != -1) {
    msg = msg.substring(start, end + 1);
    try {
      return JSON5.parse(msg);
    } catch (e) {
      return {"content": msg};
    }
  }
  return {"content": msg};
}
