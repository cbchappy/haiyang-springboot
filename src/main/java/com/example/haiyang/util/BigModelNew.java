package com.example.haiyang.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import okhttp3.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

//鉴权 连接 (发送 监听)
public class BigModelNew extends WebSocketListener {
    // 地址与鉴权信息  https://spark-api.xf-yun.com/v1.1/chat   1.5地址  domain参数为general
    // 地址与鉴权信息  https://spark-api.xf-yun.com/v2.1/chat   2.0地址  domain参数为generalv2
    public static final String hostUrl = "https://spark-api.xf-yun.com/v3.5/chat";//模型不同, 地址也不同(domain也是)
    public static final String appid = "9dd2b260";
    public static final String apiSecret = "MDZhYjZkMTEyMzU5OWExMjFkYThkNThj";
    public static final String apiKey = "96040cf3e92346e9dcf20531dd2425a6";

    public static Map<String, List<RoleContent>> historyListMap = new ConcurrentHashMap<>(); // 对话历史存储集合 单独移除出来

    private List<RoleContent> historyList;
    public static String totalAnswer = ""; // 答案汇总

    private String question;


    public static final Gson gson = new Gson();



    // 个性化参数
    private String userId;
    private Boolean wsCloseFlag;

    private BlockingQueue<String> queue;


    // 构造函数
    public BigModelNew(String userId, Boolean wsCloseFlag, BlockingQueue<String> queue,  String question ) {
        this.userId = userId;
        this.wsCloseFlag = wsCloseFlag;
        this.queue = queue;
        this.question = question;
        if(historyListMap.containsKey(String.valueOf(MyThreadLocal.getUserId()))){
            this.historyList = historyListMap.get(userId);
        }else {
            this.historyList = new ArrayList<>();
            historyListMap.put(userId, this.historyList);
        }
    }



    public static boolean canAddHistory(List<RoleContent> historyList) {// 由于历史记录最大上限1.2W左右，需要判断是能能加入历史

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

    // 线程来参数
    class MyThread extends Thread {
        private WebSocket webSocket;

        public MyThread(WebSocket webSocket) {
            this.webSocket = webSocket;
        }

        public void run() {
            try {
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
                // 等待服务端返回完毕后关闭
                while (true) {
                    // System.err.println(wsCloseFlag + "---");
                    Thread.sleep(200);
                    if (wsCloseFlag) {
                        break;
                    }
                }
                webSocket.close(1000, "");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        super.onOpen(webSocket, response);
        MyThread myThread = new MyThread(webSocket);
        myThread.start();
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        // System.out.println(userId + "用来区分那个用户的结果" + text);
        JsonParse myJsonParse = gson.fromJson(text, JsonParse.class);
        if (myJsonParse.header.code != 0) {
            System.out.println("发生错误，错误码为：" + myJsonParse.header.code);
            System.out.println("本次请求的sid为：" + myJsonParse.header.sid);
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
            totalAnswer = totalAnswer + temp.content;
        }
        if (myJsonParse.header.status == 2) {
            // 可以关闭连接，释放资源
            if (canAddHistory(historyList)) {
                RoleContent roleContent = new RoleContent();
                roleContent.setRole("assistant");
                roleContent.setContent(totalAnswer);
                historyList.add(roleContent);
            } else {
                historyList.remove(0);
                RoleContent roleContent = new RoleContent();
                roleContent.setRole("assistant");
                roleContent.setContent(totalAnswer);
                historyList.add(roleContent);
            }
            wsCloseFlag = true;
            try {
                queue.put("");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        super.onFailure(webSocket, t, response);
        try {
            if (null != response) {
                int code = response.code();
                System.out.println("onFailure code:" + code);
                System.out.println("onFailure body:" + response.body().string());
                if (101 != code) {
                    System.out.println("connection failed");
                    System.exit(0);
                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    // 鉴权方法
    public static String getAuthUrl(String hostUrl, String apiKey, String apiSecret) throws Exception {
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

    //返回的json结果拆解
    class JsonParse {
        Header header;
        Payload payload;
    }

    class Header {
        int code;
        int status;
        String sid;
    }

    class Payload {
        Choices choices;
    }

    class Choices {
        List<Text> text;
    }

    class Text {
        String role;
        String content;
    }

    class RoleContent {
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