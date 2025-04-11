// Copyright (c) 2025 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

part of '../../de_src.dart';

extension MessageExtensions on ChatCompletionMessage {
  ChatCompletionMessage reverseRole() {
    if (this is ChatCompletionUserMessage) {
      return ChatCompletionMessage.assistant(
        content: (this as ChatCompletionUserMessage).content.value.toString(),
      );
    } else if (this is ChatCompletionAssistantMessage) {
      return ChatCompletionUserMessage(
        content: ChatCompletionUserMessageContent.string(
            (this as ChatCompletionAssistantMessage).content ?? ''),
      );
    }
    return this;
  }

  ChatCompletionUserMessage asUser() {
    if (this is ChatCompletionUserMessage) {
      return this as ChatCompletionUserMessage;
    }
    return ChatCompletionUserMessage(
      content: ChatCompletionUserMessageContent.string(
        (this as ChatCompletionAssistantMessage).content ?? '',
      ),
    );
  }
}

extension MessageListExtensions on List<ChatCompletionMessage> {
  List<ChatCompletionMessage> reverseRole() {
    return map((e) => e.reverseRole()).toList();
  }

  List<ChatCompletionUserMessage> asUser() {
    return map((e) => e.asUser()).toList();
  }
}
