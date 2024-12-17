package com.example.haiyang.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

//netstat -tuln | grep 3306
/**
 * @Author Cbc
 * @DateTime 2024/11/24 19:11
 * @Description 重新改写之前的BigModel
 */
@Slf4j
public class BigModel {

    // 地址与鉴权信息  https://spark-api.xf-yun.com/v1.1/chat   1.5地址  domain参数为general
    // 地址与鉴权信息  https://spark-api.xf-yun.com/v2.1/chat   2.0地址  domain参数为generalv2
    private static final String hostUrl = "https://spark-api.xf-yun.com/v3.5/chat";//模型不同, 地址也不同(domain也是)
    private static final String appid = "9dd2b260";
    private static final String apiSecret = "MDZhYjZkMTEyMzU5OWExMjFkYThkNThj";
    private static final String apiKey = "96040cf3e92346e9dcf20531dd2425a6";
    private static final Gson gson = new Gson();
    //todo close后也要删除historyList
    //todo 关闭websocket
    //存储对话历史
    private static final ConcurrentHashMap<Integer, List<RoleContent>> historyMap = new ConcurrentHashMap<>();

    //接受问题, 返回阻塞队列, 可从队列持续获取消息, 用""标识回答已经结束
    public static BlockingQueue<String> sendToBigModel(String question, Integer userId){
        BlockingQueue<String> queue = new LinkedBlockingQueue<>();

        //生成historyList
        if(!historyMap.containsKey(userId)){
            historyMap.put(userId, new ArrayList<>());
        }

        String authUrl;
        try {
            authUrl = getAuthUrl(hostUrl, apiKey, apiSecret);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        OkHttpClient client = new OkHttpClient.Builder().build();//会开一个新线程，记得关闭
        String url = authUrl.replace("http://", "ws://").replace("https://", "wss://");
        Request request = new Request.Builder().url(url).build();
        WebSocket webSocket = client.newWebSocket(request, new AIWebSocketListener(question, historyMap.get(userId), queue));
        client.dispatcher().executorService().shutdown();

        return queue;
    }

    //移除对话历史
    public static void closeBigModel(Integer userId){
        historyMap.remove(userId);
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


    //判断对话还能不能加入历史记录
    private static boolean canAddHistory(List<RoleContent> historyList) {// 由于历史记录最大上限1.2W左右，需要判断是能能加入历史

        int history_length = 0;
        for (RoleContent temp : historyList) {
            history_length = history_length + temp.content.length();
        }
        if (history_length > 12000) {
            historyList.remove(0);
            historyList.remove(1);
            historyList.remove(2);
            historyList.remove(3);
            historyList.remove(4);
            return false;
        } else {
            return true;
        }
    }



    //自定义WebSocketListener, 一个问题对应一个实例
    private static class AIWebSocketListener extends WebSocketListener{

        private final String question;
        private final List<RoleContent> historyList;
        private final StringBuilder totalAnswer = new StringBuilder();
        private final BlockingQueue<String> queue;

        public AIWebSocketListener(String question, List<RoleContent> historyList, BlockingQueue<String> queue) {
            this.question = question;
            this.historyList = historyList;
            this.queue = queue;
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

            // 历史问题获取
            if (historyList.size() > 0) {
                for (RoleContent tempRoleContent : historyList) {
                    text.add(JSON.toJSON(tempRoleContent));
                }
            }

            // 最新问题
            RoleContent roleContent = new RoleContent();
            roleContent.role = "user";
            roleContent.content = question;
            text.add(JSON.toJSON(roleContent));
            historyList.add(roleContent);


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
                log.error("发生错误，错误码为：" + myJsonParse.header.code);
                log.error("本次请求的sid为：" + myJsonParse.header.sid);
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
                // 可以关闭连接，释放资源
                if (!canAddHistory(historyList)) {
                    historyList.remove(0);
                }
                RoleContent roleContent = new  RoleContent();
                roleContent.setRole("assistant");
                roleContent.setContent(totalAnswer.toString());
                historyList.add(roleContent);
                webSocket.close(1000, "");
                try {
                    queue.put("");
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }

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

    private static class RoleContent {
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
