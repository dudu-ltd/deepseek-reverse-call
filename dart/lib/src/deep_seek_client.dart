// Copyright (c) 2025 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

// ignore_for_file: implementation_imports

import 'dart:convert';

import 'package:openai_dart/openai_dart.dart';
import 'package:openai_dart/src/generated/client.dart' as c;
import 'package:http/http.dart' as http;

class DeepSeekClient extends OpenAIClient {
  DeepSeekClient({
    required String apiKey,
    String baseUrl = 'https://api.deepseek.com',
    Map<String, String> headers = const {},
  }) : super(
          apiKey: apiKey,
          baseUrl: baseUrl,
          headers: headers,
        );

  @override
  Future<CreateChatCompletionResponse> createChatCompletion({
    required CreateChatCompletionRequest request,
  }) async {
    final r = await makeRequest(
      baseUrl: 'https://api.openai.com/v1',
      path: '/chat/completions',
      method: c.HttpMethod.post,
      isMultipart: false,
      requestType: 'application/json',
      responseType: 'application/json',
      body: request,
    );
    return CreateChatCompletionResponse.fromJson(_jsonDecode(r));
  }

  dynamic _jsonDecode(http.Response r) {
    return json.decode(utf8.decode(r.bodyBytes));
  }
}
