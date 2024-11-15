package com.example.haiyang.util;

import cn.hutool.core.lang.UUID;
import cn.hutool.crypto.digest.MD5;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.http.HttpProtocol;
import com.qcloud.cos.model.*;
import com.qcloud.cos.region.Region;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


import static com.example.haiyang.util.BigModelNew.*;

/**
 * @Author Cbc
 * @DateTime 2024/10/16 22:22
 * @Description
 */
public class MyUtil {

    private static final MD5 md5 = new MD5();
    public static String digest(String str){
        return md5.digestHex16(str);
    }

    public static String uploadFile(MultipartFile file, String key){

        String secretId = "AKIDzpUyEJFyYxDH6ix1rUJvJSM1VowTpwrX";
        String secretKey = "bx77uXwAB1d4D6oLhorlV3PdeJnQr8GC";
        COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);

        Region region = new Region("ap-nanjing");
        ClientConfig clientConfig = new ClientConfig(region);

        clientConfig.setHttpProtocol(HttpProtocol.https);

        COSClient cosClient = new COSClient(cred, clientConfig);



        String bucketName = "haiyang-1318727264";
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());

        PutObjectRequest putObjectRequest = null;
        try {
            putObjectRequest = new PutObjectRequest(bucketName, key, file.getInputStream(), metadata);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        cosClient.putObject(putObjectRequest);

        URL url = cosClient.getObjectUrl(bucketName, key);

        cosClient.shutdown();
        //SecretId:AKIDzpUyEJFyYxDH6ix1rUJvJSM1VowTpwrX
        //SecretKey:bx77uXwAB1d4D6oLhorlV3PdeJnQr8GC
        return url.toString();
    }

    public static BlockingQueue<String> sendToBigModel(String question, String userId){

        BlockingQueue<String> queue = new LinkedBlockingQueue<>();
        new MyStartBMThread(queue, question, userId).start();
        return queue;
    }

    private static class MyStartBMThread extends Thread{
        private final BlockingQueue<String> queue;
        private final String question;
        private final String userId;
        public MyStartBMThread(BlockingQueue<String> queue, String question, String userId){
            this.queue = queue;
            this.question = question;
            this.userId = userId;
        }
        @Override
        public void run() {

            // 构建鉴权url
            String authUrl = null;
            try {
                authUrl = getAuthUrl(hostUrl, apiKey, apiSecret);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            OkHttpClient client = new OkHttpClient.Builder().build();//会开一个新线程，记得关闭
            String url = authUrl.replace("http://", "ws://").replace("https://", "wss://");
            Request request = new Request.Builder().url(url).build();
            totalAnswer = "";
            WebSocket webSocket = client.newWebSocket(request, new BigModelNew(userId,
                    false, queue, question));

            client.dispatcher().executorService().shutdown();

        }
    }
}
