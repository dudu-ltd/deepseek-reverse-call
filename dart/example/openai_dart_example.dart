// Copyright (c) 2025 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import 'dart:convert';

import 'package:de_src/de_src.dart';

var graphCreator = DsrcApi(
  value: 'graph.creator',
  name: '文本转图谱',
  prompt: () => [
    [
      '帮我构建一个知识图谱，用的数据库是 NebulaGraph',
      '注释使用 /* */',
      '需要返回建表的 nGQL 脚本，表名用大驼峰，属性名用小驼峰，用英文。',
      '并且尽可能让不同的节点之间具有边关系。',
      'ddl跟dml脚本分开放。',
      '为了规避使用到关键字导致语法错误，schema的名称包括属性名需要使用``进行包裹。',
      'dml更新数据时需要注意数据类型，特别是时间类型需要时间函数包裹，另外，插入边数据，需要特别注意语法。',
    ].join('\n')
  ],
  subTopics: () => [],
  plainTopics: [],
  properties: () => {
    'ddl': '<增删改数据库结构的脚本>',
    'dml': '<增删改数据的脚本>',
  },
);

void main() async {
  GraphCreatorAnswer();
  DeepSeekReverseCall dsrc = DeepSeekReverseCall(
    appKey: 'sk-**',
    baseUrl: 'https://dashscope.aliyuncs.com/compatible-mode/v1',
    modelId: 'deepseek-r1',
  );

  var t = await dsrc.api(
    msgs: [
      'vid类型: string',
      '另外每个 Tag 至少包含 10 个节点，数据用中文，需要有具体含义的名称。数据尽可能偏向交通这个领域，数据层尽可能使用中文，Schema用英文，注意vid唯一性。尽可能公用一些节点，以便形成更完整的数据网络。',
      r"""
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
```""",
    ],
    api: graphCreator,
  );
  print(t);
  print(t?.length);
  var rs = jsonDecode(t ?? '{}');
  print(rs);
}

class GraphCreatorAnswer extends DsrcAnswer {
  @override
  String get api => graphCreator.value;

  @override
  DsrcAnswerAction get action => (ActionArgs args) async {
        print(args.prev);
        return null;
      };
}
