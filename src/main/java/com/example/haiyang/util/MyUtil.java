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
    //md5加密
    public static String digest(String str){
        return md5.digestHex16(str);
    }

    //上传文件到腾讯云对象存储
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




    //获取exception的具体内容
    public static String getThrowableContent(Throwable e){
        StringBuilder builder = new StringBuilder();
        builder.append(e);
        builder.append("\n");

        StackTraceElement[] stackTrace = e.getStackTrace();
        for (StackTraceElement ele : stackTrace) {
            builder.append("\tat ").append(ele);
            builder.append("\n");
        }

        Throwable cause = e.getCause();
        return getThrowableCause(builder, cause, stackTrace);
    }

    private static String getThrowableCause(StringBuilder builder, Throwable cause, StackTraceElement[] encloseTrace){
        if(cause == null){
            return builder.toString();
        }
        builder.append("Cause By: ");
        builder.append(cause).append("\n");
        StackTraceElement[] stackTrace = cause.getStackTrace();
        int n = stackTrace.length - 1;
        int m = encloseTrace.length - 1;
        while (n >= 0 && m >= 0 && stackTrace[n].equals(encloseTrace[m])){
            n--;
            m--;
        }
        for (int i = 0; i <= n; i++) {
            builder.append("\tat ").append(stackTrace[i]);
            builder.append("\n");
        }
        int r = stackTrace.length - 1 - n;
        if(r > 0){
           builder.append("\t... ").append(r).append(" more").append("\n");
        }

        return getThrowableCause(builder, cause.getCause(), stackTrace);
    }

}
