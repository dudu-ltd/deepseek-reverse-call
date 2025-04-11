Currently, the project is in the early stages of development and is not yet fully functional. The main goal is to provide a convenient way to call DeepSeek.

## Features

<!-- 支持通过子话题的方式，构建对话流程 -->
- Supports building conversation flows through sub-topics.

## Getting started

```shell
dart pub add de_src
```

## Usage

```dart
// Copyright (c) 2025 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import 'dart:async';
import 'dart:io';

import 'package:de_src/de_src.dart';

var dsrcScriptFix = DsrcApi(
  value: 'script.fix',
  name: 'ai.fix',
  prompt: () => [
    '帮我修正一下这个 nGQL 语句，让它能够正确执行。',
    '如果有报错，需要提供报错原因。',
  ],
  subTopics: () => [],
  plainTopics: ['say.goodbye'],
  properties: () => {
    'reason': '报错原因(语言：{lacate})，允许语句是正确的',
    'script': '在原有代码基础上修改的代码',
  },
  single: true,
);

main() async {
  DeepSeekReverseCall dsrc = DeepSeekReverseCall(
    // appKey: 'sk-**',
    // baseUrl: 'https://dashscope.aliyuncs.com/compatible-mode/v1',
    // modelId: 'qwq-plus',
    // supportJson: true,
    // stream: true,

    //
    appKey: 'sk-**',
    // stream: true,
  );
  answerSettings.addAll({
    // dsrcScriptFix.value: routerAction,
  });
  StreamController<String> cotStream = StreamController();
  StreamController<String> contentStream = StreamController();
  cotStream.stream.listen((event) {
    stdout.write(event);
  });
  print('=================');
  contentStream.stream.listen((event) {
    stdout.write(event);
  });
  var t = await dsrc.api(
    msgs: [
      """MATCH ( n : p_feedback ) 
 RETURN n 
 LIMIT 10;"""
    ],
    api: dsrcScriptFix,
    cotStream: cotStream,
    contentStream: contentStream,
  );
  print(t);
}

```

<!-- ## 开源协议 -->
## License

<!-- 项目遵循 [Apache License, Version 2.0, January 2004](https://www.apache.org/licenses/LICENSE-2.0) 开源协议。 -->
This project is licensed under the [Apache License, Version 2.0, January 2004](https://www.apache.org/licenses/LICENSE-2.0).
