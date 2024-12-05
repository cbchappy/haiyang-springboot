package com.example.haiyang.util;

import com.example.haiyang.config.MyServerEndpointConfigurator;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;



import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author Cbc
 * @DateTime 2024/10/24 15:25
 * @Description
 */
@ServerEndpoint(value = "/chat", configurator = MyServerEndpointConfigurator.class)
@Component
@Slf4j
public class MyWebSocket {

    /**
     * 存放所有在线的客户端
     */
    private static final ConcurrentHashMap<Integer, MyWebSocket> webSocketMap = new ConcurrentHashMap<>();
    /**
     * 连接uid和连接会话
     */
    private Integer userId;
    private Session session;

    //新增一个方法用于主动向客户端发送消息
    public static void sendMessage(Integer userId,  BlockingQueue<String> queue,JsonObject jsonObject){

        MyWebSocket webSocket = webSocketMap.get(userId);
        if (webSocket != null) {
                while (true){
                    try {
                        String v = queue.take();
                        if(v.equals("")){
                            BigModel.closeBigModel(userId);
                            break;
                        }
                        jsonObject.addProperty("text", v);
                        String r = jsonObject.toString();
                        log.info("返回消息：{}", r);
                        webSocket.session.getBasicRemote().sendText(r);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
        }
    }



    @OnOpen
    public void onOpen(Session session) {
        log.info("进入opOpen的线程名字为:{}", Thread.currentThread().getName());
        this.userId = MyThreadLocal.getUserId();
        this.session = session;
        webSocketMap.put(userId, this);
        System.out.println("【websocket消息】有新的连接,连接id=" + userId + ":" + this);
    }

    //前端关闭时一个websocket时
    @OnClose
    public void onClose() {
        webSocketMap.remove(userId);
        BigModel.closeBigModel(userId);//关闭以移除历史对话
        System.out.println("【websocket消息】连接断开:" + userId);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        System.out.println("【websocket消息】WebSocket发生错误，错误信息为：" + error.getMessage());
        error.printStackTrace();
    }

    //前端向后端发送消息
    @OnMessage
    public void onMessage(String message) {

        log.info("前端发来的websocket信息:{}", message);

        JsonParser jsonParser = new JsonParser();
        JsonElement jsonElement = jsonParser.parse(message);
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        String msg = jsonObject.get("text").getAsString();
        log.info("msg:{}", msg);


        BlockingQueue<String> queue = BigModel.sendToBigModel(msg, userId);
        sendMessage(userId, queue, jsonObject);
    }
}