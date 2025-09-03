// Copyright (c) 2025 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

part of '../de_src.dart';

// 因为 flutter 在运行时，无法使用 mirror 的反射机制，所以这里使用了代码生成的方式来实现

class DsrcApi {
  final String value;
  final String name;
  final List<String> Function() prompt;
  final List<DsrcApi> Function() subTopics;
  final List<String> plainTopics;
  // 用于弥补 flutter 无法使用 mirror 的反射机制
  final Map<String, String> Function()? properties;
  final bool single;
  final String routerProp;
  final String? routerPrompt;
  final String dataProp;
  final String? dataPrompt;
  static final Map<String, DsrcApi> context = {};

  DsrcApi({
    required this.value,
    required this.name,
    required this.prompt,
    required this.subTopics,
    required this.plainTopics,
    this.properties,
    this.single = false,
    this.routerProp = 'router',
    this.routerPrompt = 'Options：',
    this.dataProp = 'data',
    this.dataPrompt = 'Task description',
  }) {
    context[value] = this;
  }

  static DsrcApi getApi(String value) {
    return context[value]!;
  }
}
