
<p align="center">
  <h1>Deepseek Reverse Call</h1> 
</p>

## 概述

当前项目旨在为 DeepSeek 的调用提供快捷的方式。

## 主要特性

- [x] 可以直接调用 DeepSeek 的对话请求。生成可以被程序所使用的 json 格式数据，完成非结构化文本向结构化数据的转换。

- [x] 支持通过`实体类`的方式，预设话题。
  ```java
  @Data
  @Schema(description = "闲聊")
  @DsrcApi(
    value = "say.discuss", 
    prompt = "你是个象棋高手，请试图讲话题引导至下棋"
  )
  public class Discuss {
    @Schema(description = "讨论内容")
    private String discuss;
  }
  ```

- [x] 支持通过对实体类追加注解，实现对话题的动态调整。（话题路由）

  ```java
  @Data
  @AllArgsConstructor
  @DsrcApi(
    value = "dsrc.router", 
    prompt = "你可以选择一个话题，或者结束对话。",
    subTopics = {Chess.class, Discuss.class},
    plainTopics = "say.goodbye"
  )
  public class RouteValue {
    
    @Schema(description = "开启话题，可选值：say.goodbye|<topicKeys>")
    final private String route;
    
    @Schema(description = "数据格式，可选值：\"<topicValues>\"")
    final private Object data;
    
    @Schema(description = "是否结束对话")
    final private Boolean finished;
    
    @Schema(description = "轮次")
    final private Integer round;
  }
  ```

  - [x] 结合以上两点功能，在回调中，调用 Deepseek 的对话请求，使得两个 Deepseek 围绕用户所提供的话题进行对话。

## 使用

### 引入

```xml
    <dependency>
      <groupId>ltd.dudu</groupId>
      <artifactId>deepseek-reverse-call-boot-starter</artifactId>
      <version>1.0.0-alpha2</version>
    </dependency>
```
> 查看最新版本：[Maven Central](https://search.maven.org/artifact/ltd.dudu/deepseek-reverse-call-boot-starter)

### 上手开发

```java
  @Autowired
  private DeepSeekReverseCall dsrc;

  @Test
  void discuss() throws IOException {
    String message = "你那个时候最新的电影是哪一部啊";
    Discuss movie = dsrc.api(message, Discuss.class);
  }
```

此处因为预设问题的缘故，DeepSeek 由用户话题切入，完成平滑引导：
```json
  {"discuss": "说到电影，其实象棋也是一部精彩的电影，每一步棋都充满了策略和智慧。你平时喜欢下象棋吗？"}
```

当调用以下代码时，DeepSeek 会根据用户的回答，从 subTopics 中选取预设的回调函数：
```java
  dsrc.api("用户问题", RouteValue.class);
```

回调函数定义方式：
```java
@Slf4j
@Component
@DsrcApi("say")
public class SayDiscussAnswer implements DsrcAnswer {
  
  @DsrcApi("discuss")
  public List<Message> discuss(Discuss discuss, List<Message> messages)
    throws IOException {
      // 业务逻辑，
    return response;
  }
}
```

目前项目还没有最终完成，可以在测试用例（java/examples/deepseek-reserve-call-discuss/src/test）中查看比较详细的使用方式。


## 开源协议

项目遵循 [Apache License, Version 2.0, January 2004](https://www.apache.org/licenses/LICENSE-2.0) 开源协议。
