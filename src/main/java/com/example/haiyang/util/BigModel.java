package com.example.haiyang.util;

import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import static com.example.haiyang.constants.RedisConstants.AI_HISTORY;

//netstat -tuln | grep 3306

/**
 * @Author Cbc
 * @DateTime 2024/11/24 19:11
 * @Description 重新改写之前的BigModel  //todo redis存储暂时的历史   todo 存储redis出现错误 无法将数据存入redis
 */
@Slf4j
public class BigModel {


    private static final String hostUrl = "https://spark-api.xf-yun.com/v3.5/chat";//模型不同, 地址也不同(domain也是)
    private static final String appid = "9dd2b260";
    private static final String apiSecret = "MDZhYjZkMTEyMzU5OWExMjFkYThkNThj";
    private static final String apiKey = "96040cf3e92346e9dcf20531dd2425a6";
    private static final Gson gson = new Gson();
    //存储对话历史

    private static final StringRedisTemplate redisTemplate ;

    //初始化redisTemplate
    static {
        redisTemplate = SpringUtil.getBean(StringRedisTemplate.class);
    }
    //接受问题, 返回阻塞队列, 可从队列持续获取消息, 用""标识回答已经结束
    public static BlockingQueue<String> sendToBigModel(String question, Integer userId) {
        BlockingQueue<String> queue = new LinkedBlockingQueue<>();

        String authUrl;
        try {
            authUrl = getAuthUrl(hostUrl, apiKey, apiSecret);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        OkHttpClient client = new OkHttpClient.Builder().build();//会开一个新线程，记得关闭
        String url = authUrl.replace("http://", "ws://").replace("https://", "wss://");
        Request request = new Request.Builder().url(url).build();
        client.newWebSocket(request, new AIWebSocketListener(question, queue, userId));
        client.dispatcher().executorService().shutdown();

        return queue;
    }

    //移除对话历史
    public static void closeBigModel(Integer userId) {
        removeHistory(userId);
    }

    // 鉴权方法, 返回websocket的url
    private static String getAuthUrl(String hostUrl, String apiKey, String apiSecret) throws Exception {
        URL url = new URL(hostUrl);
        // 时间
        SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        String date = format.format(new Date());
        // 拼接
        String preStr = "host: " + url.getHost() + "\n" +
                "date: " + date + "\n" +
                "GET " + url.getPath() + " HTTP/1.1";
        // System.err.println(preStr);
        // SHA256加密
        Mac mac = Mac.getInstance("hmacsha256");
        SecretKeySpec spec = new SecretKeySpec(apiSecret.getBytes(StandardCharsets.UTF_8), "hmacsha256");
        mac.init(spec);

        byte[] hexDigits = mac.doFinal(preStr.getBytes(StandardCharsets.UTF_8));
        // Base64加密
        String sha = Base64.getEncoder().encodeToString(hexDigits);
        // System.err.println(sha);
        // 拼接
        String authorization = java.lang.String.format("api_key=\"%s\", algorithm=\"%s\", headers=\"%s\", signature=\"%s\"", apiKey, "hmac-sha256", "host date request-line", sha);
        // 拼接地址
        HttpUrl httpUrl = Objects.requireNonNull(HttpUrl.parse("https://" + url.getHost() + url.getPath())).newBuilder().//
                addQueryParameter("authorization", Base64.getEncoder().encodeToString(authorization.getBytes(StandardCharsets.UTF_8))).//
                addQueryParameter("date", date).//
                addQueryParameter("host", url.getHost()).//
                build();

        // System.err.println(httpUrl.toString());
        return httpUrl.toString();
    }


    //自定义WebSocketListener, 一个问题对应一个实例
    private static class AIWebSocketListener extends WebSocketListener {

        private final String question;
        private final StringBuilder totalAnswer = new StringBuilder();
        private final BlockingQueue<String> queue;
        private final Integer id;

        public AIWebSocketListener(String question, BlockingQueue<String> queue, Integer id) {
            this.question = question;
            this.queue = queue;
            this.id = id;
        }

        @Override
        public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
            //发送消息
            JSONObject requestJson = new JSONObject();

            JSONObject header = new JSONObject();  // header参数
            header.put("app_id", appid);
            header.put("uid", UUID.randomUUID().toString().substring(0, 10));

            JSONObject parameter = new JSONObject(); // parameter参数
            JSONObject chat = new JSONObject();
            chat.put("domain", "generalv2");
            chat.put("temperature", 0.5);
            chat.put("max_tokens", 4096);
            parameter.put("chat", chat);

            JSONObject payload = new JSONObject(); // payload参数
            JSONObject message = new JSONObject();
            JSONArray text = new JSONArray();

            //添加历史记录到要发送的请求里
            List<String> history = getHistory(this.id);
            for (String s : history) {
                text.add(JSON.parseObject(s, RoleContent.class));//直接添加stringJson进去会导致出现划线, 引起错误
            }
            // 最新问题
            RoleContent roleContent = new RoleContent();
            roleContent.role = "user";
            roleContent.content = question;
            text.add(JSON.toJSON(roleContent));

            //将最新问题也添加进历史记录
            addHistory(roleContent, this.id);

            message.put("text", text);
            payload.put("message", message);

            requestJson.put("header", header);
            requestJson.put("parameter", parameter);
            requestJson.put("payload", payload);
            // System.err.println(requestJson); // 可以打印看每次的传参明细
            webSocket.send(requestJson.toString());
        }

        @Override
        public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {

            JsonParse myJsonParse = gson.fromJson(text, JsonParse.class);

            //校验是否发生错误
            if (myJsonParse.header.code != 0) {
                log.error("发生错误，错误码为:" + myJsonParse.header.code);
                log.error("本次请求的sid为:" + myJsonParse.header.sid);
                //进行错误处理
                try {
                    queue.put("本次回答出现未知错误");
                    queue.put("");
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                webSocket.close(1000, "");
            }
            List<Text> textList = myJsonParse.payload.choices.text;
            for (Text temp : textList) {
                String s = temp.content;
                try {
                    queue.put(s);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                totalAnswer.append(temp.content);
            }
            if (myJsonParse.header.status == 2) {
                //将总回答添加进历史
                RoleContent roleContent = new RoleContent();
                roleContent.setRole("assistant");
                roleContent.setContent(totalAnswer.toString());
                addHistory(roleContent, this.id);
                webSocket.close(1000, "");
                try {
                    queue.put("");
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }

    }


    //添加对话历史  太长则移除
    private static void addHistory(RoleContent roleContent, Integer id) {
        String key = AI_HISTORY + id;
        ListOperations<String, String> opsForList = redisTemplate.opsForList();
        Long size = opsForList.size(key);
        if (size != null && size >= 10000) {
            for (int i = 0; i < 5; i++) {
                opsForList.leftPop(key);
            }
        }

        opsForList.rightPush(key, JSON.toJSONString(roleContent));//右边进左边出   ooooooooo o<- 最右最新
    }

    //获取对话历史
    private static List<String> getHistory(Integer id) {
        String key = AI_HISTORY + id;
        ListOperations<String, String> opsForList = redisTemplate.opsForList();
        Long size = opsForList.size(key);
        if (size == null) {
            return new ArrayList<>();
        }
        return opsForList.range(key, 0, size - 1);
    }

    private static void removeHistory(Integer id) {
        log.info("移除历史");
        redisTemplate.delete(AI_HISTORY + id);
    }


    //todo 解析json数据的含义
    private static class JsonParse {
        Header header;
        Payload payload;
    }

    private static class Header {
        int code;
        int status;
        String sid;
    }

    private static class Payload {
        Choices choices;
    }

    private static class Choices {
        List<Text> text;
    }

    private static class Text {
        String role;
        String content;
    }

     public static class RoleContent {
        String role;
        String content;

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}
