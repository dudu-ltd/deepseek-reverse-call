package io.github.corvusye.dsrc;

// Copyright (c) 2025 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import static io.github.corvusye.dsrc.DsrcConst.BUILD_IN_API;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import io.github.corvusye.dsrc.pojo.DeepSeekResult;
import io.github.corvusye.dsrc.pojo.Message;
import io.github.corvusye.dsrc.pojo.RouteValue;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javafx.util.Pair;
import lombok.extern.slf4j.Slf4j;

/**
 * DeepSeek 反向调用 实现类
 *
 * @author yeweicheng
 * @since 2025-02-10 6:29
 * <br>Now is history!
 */
@Slf4j
public class DeepSeekReverseCallImpl implements DeepSeekReverseCall {

  final static private String CONFIG_KEY_TASK = "task";
  final static private String CONFIG_KEY_NAME = "name";
  final static private String CONFIG_KEY_MESSAGE = "message";
  final static private String CONFIG_KEY_SCHEMA = "schema";

  final static YAMLMapper mapper = new YAMLMapper();
  final String configFilePath;
  final JsonNode yaml;
  final DeepSeek deepSeek;
  final Iterable<DsrcAnswer> answers;
  final boolean allowAnswer;
  final Iterable<DsrcInterceptor> interceptors;

  final Map<String, Pair<Method, DsrcAnswer>> answerMap = new HashMap<>();

  public DeepSeekReverseCallImpl(String configFilePath, DeepSeek deepSeek) {
    this.configFilePath = configFilePath;
    this.deepSeek = deepSeek;
    yaml = getYaml(configFilePath);
    answers = null;
    allowAnswer = false;
    interceptors = null;
  }

  public DeepSeekReverseCallImpl(String configFilePath, DeepSeek deepSeek,
    Iterable<DsrcAnswer> answers) {
    this.configFilePath = configFilePath;
    this.deepSeek = deepSeek;
    this.answers = answers;
    yaml = getYaml(configFilePath);
    allowAnswer = answers != null && answers.iterator().hasNext();
    interceptors = null;
    registerAnswers();
  }

  public DeepSeekReverseCallImpl(String configFilePath, DeepSeek deepSeek,
    Iterable<DsrcAnswer> answers, Iterable<DsrcInterceptor> intercepters) {
    this.configFilePath = configFilePath;
    this.deepSeek = deepSeek;
    this.answers = answers;
    this.interceptors = intercepters;
    yaml = getYaml(configFilePath);
    allowAnswer = answers != null && answers.iterator().hasNext();
    registerAnswers();
  }

  @Override
  public <T> T api(
    String apiName,
    List<Message> messages,
    Map<String, Object> options,
    List<Object> args,
    Modes mode,
    Class<T> clazz
  ) throws IOException {

    JsonNode data = getApiConfig(apiName);
    // 当未声明 api 时，直接返回
    if (data == null) {
      return null;
    }

    // 添加 yaml 中的消息
    List<Message> msgsTotal = addYamlMessages(data);

    // 添加用户输入的消息
    msgsTotal.addAll(messages);
    if (msgsTotal.isEmpty()) {
      return null;
    }

    outputConversation("send", msgsTotal);
    DeepSeekResult result = deepSeek.createChat(msgsTotal, mode, options);
    outputConversation("receive", result.allMessage());

    boolean returnDefault = doIntercepts(result);
    if (returnDefault) {
      return resultToReturn(result, clazz);
    }

    T rs = null;
    try {
      String text = result.getOne();
      RouteValue routerValue = JSONObject.parseObject(text, RouteValue.class);
      List<Message> fromLocal = route(routerValue, messages, options, args, mode);
      if (routerValue.getFinished() && fromLocal != null && !fromLocal.isEmpty()) {
        ArrayList<Message> allMsgs = new ArrayList<>(messages);
        allMsgs.addAll(fromLocal);
        RouteValue route = api(BUILD_IN_API, allMsgs, options, args, mode, RouteValue.class);
        if (route != null && route.getData() != null) {
          rs = JSON.parseObject(route.getData().toString(), clazz);
        }
      }
    } catch (Exception e) {
      return resultToReturn(result, clazz);
    }
    return rs == null ? resultToReturn(result, clazz) : rs;
  }

  private List<Message> route(
    RouteValue route,
    List<Message> messages,
    Map<String, Object> options,
    List<Object> args,
    Modes mode
  ) throws IOException {
    // 通过 route 获取 answer
    Pair<Method, DsrcAnswer> pair = answerMap.get(route.getRoute());
    if (pair == null) {
      return null;
    }
    DsrcAnswer answer = pair.getValue();

    try {
      Object obj = pair.getKey().invoke(answer, route.getData(), messages);
      if (obj instanceof List) {
        return (List<Message>) obj;
      } else if (obj instanceof Message) {
        return Arrays.asList((Message) obj);
      }
    } catch (Exception e) {
      log.error("invoke error", e);
    }
    return null;
  }

  @SuppressWarnings("unchecked")
  private <T> T resultToReturn(List<Message> messages, Class<T> clazz) {
    if (clazz == Object.class) {
      return (T) messages;
    } else if (clazz == List.class) {
      return (T) messages;
    } else {
      return tryParse(messages.get(0).getContent(), clazz);
//      return JSON.parseObject(messages.get(0).getContent(), clazz);
    }
  }

  private <T> T tryParse(String content, Class<T> clazz) {
    content = content.trim();
    if (content.startsWith("```json") && content.endsWith("```")) {
      content = content.substring(7, content.length() - 3).trim();
    }
    return JSON.parseObject(content, clazz);
  }

  @SuppressWarnings("unchecked")
  private <T> T resultToReturn(DeepSeekResult result, Class<T> clazz) {
    if (clazz == Object.class) {
      return (T) result;
    } else if (clazz == List.class) {
      return (T) result.allMessage();
    } else {
      return tryParse(result.getOne(), clazz);
    }
  }

  private boolean doIntercepts(DeepSeekResult result) {
    boolean returnDefault = false;
    if (interceptors != null) {
      for (DsrcInterceptor interceptor : interceptors) {
        boolean hasNext = interceptor.intercept(result);
        if (!hasNext) {
          returnDefault = true;
        }
      }
    }
    return returnDefault;
  }

  private List<Message> addYamlMessages(JsonNode data) {
    JsonNode message = data.get(CONFIG_KEY_MESSAGE);
    List<Message> msgs = new ArrayList<>();
    if (message != null) {
      // 兼容数组和字符串
      if (message.isArray()) {
        for (JsonNode msg : message) {
          msgs.add(new Message(msg.asText(), Roles.system));
        }
      } else if (message.isTextual()) {
        msgs.add(new Message(message.asText(), Roles.system));
      }
    }
    JsonNode schema = data.get(CONFIG_KEY_SCHEMA);
    if (schema == null) {
      return msgs;
    }
    String schemaFormat = schema.toString();
    msgs.add(
      new Message(String.format(
        "输出：%s，连```json```都不要出现，不然我会很生气", schemaFormat), Roles.system
      )
    );
    return msgs;
  }

  private JsonNode getApiConfig(String apiName) {
    String[] paths = apiName.split("\\.");
    JsonNode data = yaml.get(CONFIG_KEY_TASK);
    for (String path : paths) {
      if (data == null) {
        return null;
      }
      data = data.get(path);
    }
    return data;
  }

  private JsonNode getYaml(String configFilePath) {
    URL resource = DeepSeekReverseCallImpl.class.getResource(configFilePath);
    try {
      String relativePath = resource.getPath();
      FileInputStream fis = new FileInputStream(relativePath);
      InputStreamReader inReader = new InputStreamReader(fis);
      BufferedReader reader = new BufferedReader(inReader);
      return mapper.readTree(reader);
    } catch (IOException e) {
      throw new RuntimeException("请检查配置文件参数是否正确，如 /dsrc.yaml：" + configFilePath, e);
    }
  }

  private void registerAnswers() {
    if (!allowAnswer) {
      return;
    }
    for (DsrcAnswer answer : answers) {
      DsrcApi api = answer.getClass().getAnnotation(DsrcApi.class);
      if (api == null) {
        continue;
      }
      String apiName = api.value();
      Method[] methods = answer.getClass().getMethods();
      for (Method method : methods) {
        DsrcApi methodApi = method.getAnnotation(DsrcApi.class);
        if (methodApi == null) {
          continue;
        }
        String methodApiName = methodApi.value();
        if (methodApiName.isEmpty()) {
          continue;
        }
        answerMap.put(apiName + "." + methodApiName, new Pair<>(method, answer));
      }
    }

    addRouteConfigToYaml();
  }

  private void addRouteConfigToYaml() {
    JsonNode task = yaml.get(CONFIG_KEY_TASK);
    if (task == null) {
      return;
    }
    Map<String, Map<String, String>> api = apiList(task);
    String[] yamlLines = new String[]{
      "router:",
      "  message:",
      String.format(
        "    - 你可以问我以下几种问题：\"%s\"，继续追问。如果不需要追问，必须返回空对象 {}",
        JSON.toJSONString(api)),
      "  schema:",
      /** 这里的文本不要改。{@link RouteValue#route}*/
      "    route: <API路径，如 say.hello>",
      "    finished: <你有没有问题问我，类型：Boolean>",
      "    data: <按data提到的格式组织json数据>"
    };
    try {
      String join = String.join("\n", yamlLines);
      JsonNode jsonNode = mapper.readTree(join);
      ((ObjectNode) task).put("dsrc", jsonNode);
    } catch (IOException e) {
      throw new RuntimeException("你的应用不支持使用回调");
    }
  }

  private Map<String, Map<String, String>> apiList(JsonNode task) {
    // 如：ai.api -> {"name":"闲聊","data":{"discuss":<随便说>}}
    Map<String, Map<String, String>> apiList = new HashMap<>();
    Iterator<String> scopes = task.fieldNames();
    while (scopes.hasNext()) {
      String scope = scopes.next();
      JsonNode api = task.get(scope);
      Iterator<String> apiNames = api.fieldNames();
      while (apiNames.hasNext()) {
        String apiName = apiNames.next();
        JsonNode apiData = api.get(apiName);
        Map<String, String> apiMap = new HashMap<>();
        apiMap.put(CONFIG_KEY_NAME, apiData.get(CONFIG_KEY_NAME).asText());
        apiMap.put("data", apiData.get(CONFIG_KEY_SCHEMA).toString());
        apiList.put(scope + "." + apiName, apiMap);
      }
    }
    return apiList;
  }

  void outputConversation(String prompt, List<Message> messages) {
    if (log.isDebugEnabled()) {
      String conversation = messages.stream().filter(Message::isNotSystem)
        .map(m -> m.getRole() + ": " + m.getContent())
        .collect(Collectors.joining("\n"));
      log.debug("\n---- {} ----\n{}", prompt, conversation);
    }
  }
}
