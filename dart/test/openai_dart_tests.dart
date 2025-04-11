// Copyright (c) 2025 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import 'package:openai_dart/openai_dart.dart';

final client = OpenAIClient(
  baseUrl: 'https://api.deepseek.com',
  apiKey: 'sk-**',
  headers: {'api-key': 'sk-**'},
);

main() async {
  final res = await client.createChatCompletion(
    request: CreateChatCompletionRequest(
      model: ChatCompletionModel.modelId('deepseek-reasoner'),
      messages: [
        ChatCompletionMessage.system(
          content:
              r"""帮我构建一个知识图谱，用的数据库是 NebulaGraph，需要返回建表的 nGQL 脚本，表名用大驼峰，属性名用小驼峰。为了规避使用到关键字导致语法错误，schema的名称包括属性名需要使用``进行包裹。需要注意的是注释符为 #。另外每个 Tag 至少包含 10 个节点。数据尽可能偏向交通这个领域，尽可能使用中文。并且尽可能让不同的节点之间具有边关系。ddl跟dml脚本分开放。dml更新数据时需要注意数据类型，特别是时间类型需要时间函数包裹，另外，插入边数据，需要特别注意语法。结构如下：""",
        ),
        ChatCompletionMessage.system(
          content: 'output json: {"ddl": <建表语句>, "dml": <数据语句>}',
        ),
        ChatCompletionMessage.user(
          content: ChatCompletionUserMessageContent.string(r"""
```mermaid
flowchart TD 
  规程-->安全防护措施
  规程-->工具名称
  规程-->工作标准
  规程-->设备类型
  设备类型-->上级类型
  规程-->设备
  设备-->设备类型
  设备-->专业系统
  专业系统-->上级专业
  设备-->资产位置
  资产位置-->上级位置
  设备-->厂家
  设备-->部件
  部件-->厂家
  设备-->故障现象
  故障现象-->故障原因
  故障原因-->处理措施
  处理措施-->消耗物料
  消耗物料-->货位名称
  故障现象-->故障类型
  故障现象-->故障等级
```"""),
        ),
      ],
      temperature: 0,
      responseFormat: ResponseFormat.text(),
    ),
  );
  print(res.choices.first.message.content);
}
