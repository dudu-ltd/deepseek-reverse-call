// Copyright (c) 2025 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import 'package:de_src/de_src.dart';

var chess = DsrcApi(
  value: 'say.chess',
  name: '下棋',
  prompt: () => ['你是个下象棋的高手，请跟我下棋吧'],
  subTopics: () => [],
  plainTopics: [],
  properties: () => {
    'chess': '落子',
  },
);

var discuss = DsrcApi(
  value: 'say.discuss',
  name: '闲聊',
  prompt: () => ['你是个象棋高手，请试图讲话题引导至下棋'],
  subTopics: () => [],
  plainTopics: [],
  properties: () => {
    'discuss': '讨论内容',
  },
);

var router = DsrcApi(
  value: 'dsrc.router',
  name: '路由',
  prompt: () => ['你可以选择一个话题，或者结束对话。'],
  subTopics: () => [chess, discuss],
  plainTopics: ['say.goodbye'],
  properties: () => {
    // 'router': '开启话题，可选值：{{subTopics.keys..addAll(plainTopics).join("|")}}',
    'router': '开启话题，可选值：say.chess|say.discuss|say.goodbye',
    'data': '数据格式，可选值：{"chess": "<象棋落子>"}|{"discuss": "<讨论内容>"}',
    'finished': '是否结束对话',
    'round': '对话轮数',
  },
);

Future<List<ChatCompletionMessage>?> discussAction(ActionArgs args) async {
  var discussContent = args.prev?['discuss'] ?? args.message?.content;
  List<ChatCompletionMessage> newMsgs = (args.messages ?? []).reverseRole();
  ChatCompletionMessage prevMsg = ChatCompletionMessage.user(
      content: ChatCompletionUserMessageContent.string(discussContent));
  newMsgs.add(prevMsg);
  var res = await args.dsrc?.api(messages: newMsgs, api: discuss);
  var resData = tryJsonDecode(res)['discuss'];
  List<ChatCompletionMessage> response = [];
  response.add(prevMsg.reverseRole());
  response.add(ChatCompletionMessage.user(
      content: ChatCompletionUserMessageContent.string(resData)));
  return response;
}

Future<List<ChatCompletionMessage>?> goodbyeAction(ActionArgs args) async {
  print('再见');
  return null;
}

Future<List<ChatCompletionMessage>?> chessAction(ActionArgs args) async {
  var chessContent = args.prev?['chess'] ?? args.message?.content;
  List<ChatCompletionMessage> newMsgs = (args.messages ?? []).reverseRole();
  ChatCompletionMessage prevMsg = ChatCompletionMessage.user(
      content: ChatCompletionUserMessageContent.string(chessContent));
  newMsgs.add(prevMsg);
  var res = await args.dsrc?.api(messages: newMsgs, api: chess);
  var resData = tryJsonDecode(res)['chess'];
  List<ChatCompletionMessage> response = [];
  response.add(prevMsg.reverseRole());
  response.add(ChatCompletionMessage.user(
      content: ChatCompletionUserMessageContent.string(resData)));
  return response;
}

Future<List<ChatCompletionMessage>?> routerAction(ActionArgs args) async {
  if (args.prev?['finsihed'] == true ||
      // args.prev?['round'] >= 3 ||
      args.prev?['router'] == null ||
      answerSettings[args.prev?['router']] == null) {
    return null;
  }
  var text = args.prev?['data'] = args.prev?['data'] ?? args.message?.content;
  var next = text is Map<String, dynamic> ? text : tryJsonDecode(text);
  var nextArgs = args.copyWith(prev: next);
  var res = await answerSettings[args.prev?['router']]!(nextArgs);
  if (res == null || res.isEmpty) {
    return null;
  }
  List<ChatCompletionMessage> allMsgs = [];
  allMsgs.addAll(res);

  // String? routerStr = await args.dsrc?.api(messages: allMsgs, api: router);
  // Map<String, dynamic>? nextResult = tryJsonDecode(routerStr);

  // if (nextResult['finished'] == true || nextResult['data'] == null) {
  //   return null;
  // }
  // var nextData = nextResult['data'];
  // String contentStr = nextData is String ? nextData : JSON5.stringify(nextData);

  // var userMsg = ChatCompletionMessage.user(
  //     content: ChatCompletionUserMessageContent.string(contentStr));
  // allMsgs.add(userMsg);
  return allMsgs;
}

main() async {
  DeepSeekReverseCall dsrc = DeepSeekReverseCall(
    appKey: 'sk-**',
    baseUrl: 'https://dashscope.aliyuncs.com/compatible-mode/v1',
    modelId: 'deepseek-r1',
  );
  answerSettings.addAll({
    router.value: routerAction,
    chess.value: chessAction,
    discuss.value: discussAction,
    'say.goodbye': goodbyeAction,
  });

  var t = await dsrc.api(
    msgs: ["我好无聊啊"],
    api: router,
  );

  print(t);
}
