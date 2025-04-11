// Copyright (c) 2025 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import 'package:de_src/de_src.dart';

var createSchema = DsrcApi(
  value: 'tagOrEdgeTypeCreator',
  name: 'Schema设计器',
  prompt: () => [
    '你是一个模式设计专家。数据库NebulaGraph。',
    '表名称使用大驼峰命名法，字段名称使用小驼峰命名法。换行符请勿使用单斜杠\\，schema名称请用``包裹。',
    'NebulaGraph本身据有隐式主键，不需要额外设计主键。',
  ],
  subTopics: () => [],
  plainTopics: [],
  properties: () => {
    'script': 'nGQL ddl，不包含创建空间。换行符请勿使用单斜杠\\，schema名称请用``包裹。',
    'extra': '额外说明',
  },
);

var initData = DsrcApi(
  value: 'dmlGenerator',
  name: '数据生成器',
  prompt: () => [
    '你是一个nGQL专家，请帮我生成一些图谱数据的dml。数据库NebulaGraph',
    '主键具有唯一性，不可重复。数据尽可能真实，不要有重复数据。样本数据倾向于使用中文。',
  ],
  subTopics: () => [],
  plainTopics: [],
  properties: () => {
    'script': 'nGQL dml，换行符请勿使用单斜杠\\',
    'extra': '额外说明',
  },
);

var indexCreator = DsrcApi(
  value: 'indexCreator',
  name: 'NebulaGraph专家',
  prompt: () => [
    '你是一个nGQL专家，为已有表结构创建原生索引。以 i_ 开头',
  ],
  subTopics: () => [],
  plainTopics: [],
  properties: () => {
    'script': 'nGQL 索引创建语句，换行符请勿使用单斜杠\\',
    'extra': '额外说明',
  },
);

var router = DsrcApi(
  value: 'dsrc.router',
  name: '知识图谱工程师',
  prompt: () => ['你是一个NebulaGraph专家，现在有几个人可以让你调度。请给他们分配任务'],
  subTopics: () => [createSchema, initData, indexCreator],
  plainTopics: [],
  properties: () => {
    // 'router': '开启话题，可选值：tagOrEdgeTypeCreator|dmlGenerator|indexCreator|end',
    // 'data': '分配的任务说明，陈述句，不要出现主语',
    'finished': '是否结束对话',
    'round': '对话轮数',
  },
);

Future<List<ChatCompletionMessage>?> routerAction(ActionArgs args) async {
  if (args.prev?['finsihed'] == true ||
      // args.prev?['round'] >= 3 ||
      args.prev?['router'] == null) {
    return null;
  }
  var text = args.prev?['data'] = args.prev?['data'] ?? args.message?.content;

  print('=================');
  print(text);
  List<ChatCompletionMessage> allMsgs = (args.messages ?? []);
  var task = ChatCompletionMessage.assistant(
    content: text.toString(),
  );
  allMsgs.add(task);
  // allMsgs.add(
  //   ChatCompletionMessage.user(
  //     content: ChatCompletionUserMessageContent.string("听你的"),
  //   ),
  // );

  print(args.prev?['router']);
  String? routerStr =
      await args.dsrc?.api(messages: allMsgs, api: args.prev?['router']);

  print('-----------------');
  print(routerStr);
  if (routerStr == null) {
    return null;
  }
  return [
    task,
    ChatCompletionMessage.user(
      content: ChatCompletionUserMessageContent.string(routerStr),
    )
  ];
}

main() async {
  DeepSeekReverseCall dsrc = DeepSeekReverseCall(
    // appKey: 'sk-**',
    // baseUrl: 'https://dashscope.aliyuncs.com/compatible-mode/v1',
    // modelId: 'qwq-plus',
    appKey: 'sk-**',
    baseUrl: 'https://api.deepseek.com',
    modelId: 'deepseek-chat',
    supportJson: true,
    stream: true,

    //
    // appKey: 'sk-**',
    // stream: true,
  );
  answerSettings.addAll({
    router.value: routerAction,
  });

  var t = await dsrc.api(
    msgs: ["构建农业知识图谱"],
    api: router,
  );

  print(t);
}
