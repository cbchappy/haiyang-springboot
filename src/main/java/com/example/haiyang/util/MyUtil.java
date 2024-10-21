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
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URL;

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
}
