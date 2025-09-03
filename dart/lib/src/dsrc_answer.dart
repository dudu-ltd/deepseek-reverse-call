// Copyright (c) 2025 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

part of '../de_src.dart';

class ActionArgs {
  final Map<String, dynamic>? prev;
  final dynamic data;
  final ChatCompletionMessage? message;
  final List<ChatCompletionMessage>? messages;
  final CreateChatCompletionResponse? response;
  final DeepSeekReverseCall? dsrc;
  final List<ChatCompletionMessageToolCall>? toolCalls;
  final DsrcApi api;

  ActionArgs({
    this.prev,
    this.data,
    this.message,
    this.messages,
    this.response,
    this.dsrc,
    this.toolCalls,
    required this.api,
  });

  ActionArgs copyWith({
    Map<String, dynamic>? prev,
    ChatCompletionMessage? message,
    List<ChatCompletionMessage>? messages,
    CreateChatCompletionResponse? response,
    DeepSeekReverseCall? dsrc,
  }) {
    return ActionArgs(
      prev: prev ?? this.prev,
      message: message ?? this.message,
      messages: messages ?? this.messages,
      response: response ?? this.response,
      dsrc: dsrc ?? this.dsrc,
      data: data,
      api: api,
    );
  }
}

typedef DsrcAnswerAction = Future<List<ChatCompletionMessage>?> Function(
  ActionArgs,
);

final Map<String, DsrcAnswerAction> answerSettings =
    <String, DsrcAnswerAction>{};

abstract class DsrcAnswer {
  DsrcAnswer() {
    answerSettings[api] = action;
  }

  String get api;
  DsrcAnswerAction get action;
}
