// Copyright (c) 2025 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

part of '../de_src.dart';

typedef SubTopicAction = Future<List<ChatCompletionMessage>?> Function(
    ActionArgs);
typedef GlobalParamGetter = Map<String, dynamic> Function();
var interpolation = Interpolation();

class DeepSeekReverseCall {
  String appKey;
  String baseUrl;
  String modelId;
  bool supportJson;
  bool stream;
  SubTopicAction? subTopicAction;
  Logger? log;
  bool isStop = false;

  late final DeepSeekClient client;
  GlobalParamGetter? globalParamGetter;

  DeepSeekReverseCall({
    required this.appKey,
    this.baseUrl = 'https://api.deepseek.com',
    this.modelId = 'deepseek-reasoner',
    this.supportJson = false,
    this.stream = false,
    this.subTopicAction,
    this.globalParamGetter,
    this.log,
  }) {
    client = DeepSeekClient(baseUrl: baseUrl, apiKey: appKey);
  }

  Future<String?> api({
    String? msg,
    List<String>? msgs,
    required dynamic api,
    List<ChatCompletionMessage>? messages,
    StreamController<String>? cotStream,
    StreamController<String>? contentStream,
    dynamic data,
  }) async {
    if (api is String) {
      api = DsrcApi.context[api];
      if (api == null) return null;
    }
    assert(api is DsrcApi);
    var dsrcApi = api as DsrcApi;
    List<ChatCompletionMessage> defaultPromptMessage = apiSettings(dsrcApi);
    ChatCompletionMessage? formatMessage = formatSetting(dsrcApi);

    var tools = toTools(dsrcApi);

    var userMessages = <ChatCompletionMessage>[
      if (messages != null) ...messages,
      if (msg != null)
        ChatCompletionMessage.user(
          content: ChatCompletionUserMessageContent.string(msg),
        ),
      ...(msgs?.map((e) {
            return ChatCompletionMessage.user(
              content: ChatCompletionUserMessageContent.string(e),
            );
          }).toList() ??
          []),
    ];
    log?.d(userMessages.map((e) => '${e.role}: ${e.content}').join('\n'));

    var payloads = <ChatCompletionMessage>[
      ...defaultPromptMessage,
      if (formatMessage != null) formatMessage,
      ...userMessages
    ];

    var logitBias = <String, int>{};
    final res = client.createChatCompletionStream(
      request: CreateChatCompletionRequest(
        tools: tools.isEmpty ? null : tools,
        logitBias: logitBias,
        maxTokens: 8192,
        model: ChatCompletionModel.modelId(modelId),
        messages: payloads,
        temperature: 0,
        stream: stream,
        responseFormat: formatMessage == null || !supportJson
            ? ResponseFormat.text()
            : ResponseFormat.jsonObject(),
      ),
    );

    Completer c = Completer();
    String content = '';

    done() {
      log?.d('done');
      c.complete();
      cotStream?.close();
      contentStream?.close();
    }

    var toolCallsJson = <Map<String, dynamic>>[];

    res.listen(
      (event) {
        var delta = event.choices.first.delta;
        if (delta.refusal != null) {
          // stdout.write(delta.refusal!);
          cotStream?.sink.add(delta.refusal!);
        } else if (delta.content != null) {
          // stdout.write(delta.content!);
          contentStream?.sink.add(delta.content!);
          content += delta.content!;
        }
        if (delta.toolCalls != null) {
          for (ChatCompletionStreamMessageToolCallChunk tcchunk
              in delta.toolCalls ?? []) {
            if (toolCallsJson.length <= tcchunk.index) {
              toolCallsJson.add({
                "id": "",
                "type": "function",
                "function": {"name": "", "arguments": ""}
              });
            }
            var tc = toolCallsJson[tcchunk.index];
            if (tcchunk.id != null) {
              tc['id'] += tcchunk.id!;
            }
            if (tcchunk.function?.name != null) {
              tc['function']['name'] += tcchunk.function!.name!;
            }
            if (tcchunk.function?.arguments != null) {
              tc['function']['arguments'] += tcchunk.function!.arguments!;
            }
          }
        }
      },
      onDone: done,
      onError: (e) => {
        contentStream?.sink.add(e.toString()),
      },
    );

    await c.future;

    var toolCalls = toolCallsJson
        .map((e) => ChatCompletionMessageToolCall.fromJson(e))
        .toList();

    if (toolCalls.isEmpty) {
      return content;
    }

    List<ChatCompletionMessage>? nextStep =
        await (answerSettings[dsrcApi.value] ?? subTopicAction)?.call(
      ActionArgs(
        dsrc: this,
        prev: tryJsonDecode(content),
        message: ChatCompletionMessage.assistant(
          content: content,
        ),
        messages: userMessages,
        data: data,
        toolCalls: toolCalls,
        api: dsrcApi,
        // response: res,
      ),
    );

    if (nextStep != null && nextStep.isNotEmpty) {
      userMessages.addAll(nextStep);
      return await this.api(api: api, messages: userMessages);
    }

    return content;
  }

  ChatCompletionMessage? formatSetting(DsrcApi api) {
    var globalParams = globalParamGetter?.call();
    var subTopics = api.subTopics.call();
    var plainTopics = api.plainTopics;
    if (api.properties?.call().isNotEmpty == false &&
        subTopics.isEmpty &&
        plainTopics.isEmpty) {
      return null;
    }
    // var hasRouter = subTopics.isNotEmpty || plainTopics.isNotEmpty;
    var json = <String, dynamic>{};
    // if (hasRouter) {
    //   json.addAll({
    //     api.routerProp: '<${api.routerPrompt}${[
    //       ...subTopics.map((e) => e.value),
    //       ...plainTopics,
    //     ].join("|")}|end，分别代表：${api.subTopics.call().map((e) => e.name).join('|')}|结束>',
    //     api.dataProp: '<${api.dataPrompt}>',
    //   });
    // }
    if (api.properties?.call().isNotEmpty == true) {
      var props = api.properties?.call() ?? {};
      if (globalParams == null) {
        json.addAll((api.properties?.call() ?? {}));
      } else {
        json.addAll(
          {}..addEntries(
              props.entries.map((e) {
                return MapEntry<String, dynamic>(
                    e.key, interpolation.eval(e.value, globalParams));
              }),
            ),
        );
      }
    }
    var schemaContent = 'example output json: \n${JSON5.stringify(json)}';
    log?.d(schemaContent);
    return ChatCompletionMessage.system(
      content: schemaContent,
    );
  }

  List<ChatCompletionMessage> apiSettings(DsrcApi api) {
    return [
      ...api.prompt().map((e) {
        return ChatCompletionMessage.system(content: e);
      }),
    ];
  }

  List<ChatCompletionTool> toTools(DsrcApi api) {
    return api.subTopics().map((e) => toTool(e)).toList();
  }

  ChatCompletionTool toTool(DsrcApi api) {
    var props = api.properties?.call() ?? <String, String>{};
    return ChatCompletionTool.fromJson(
      {
        "type": "function",
        "function": {
          "name": api.value,
          "strict": true,
          "description": api.prompt().join('\n'),
          "parameters": {
            "type": "object",
            "properties": {}..addEntries(
                props.entries.map(
                  (e) {
                    return MapEntry<String, dynamic>(
                      e.key,
                      {
                        "type": "string",
                        "description": e.value,
                      },
                    );
                  },
                ),
              ),
            "required": api.properties?.call().keys.toList() ?? [],
            "additionalProperties": false
          },
        }
      },
    );
  }
}
