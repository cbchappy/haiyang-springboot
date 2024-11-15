package com.example.haiyang.util;

import com.example.haiyang.dto.WebSocketDto;
import jakarta.websocket.DecodeException;
import jakarta.websocket.Decoder;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.ByteBuffer;

/**
 * @Author Cbc
 * @DateTime 2024/10/26 12:09
 * @Description
 */
@Slf4j
public class MyDecoder implements Decoder.Binary<WebSocketDto> {
    @Override
    public WebSocketDto decode(ByteBuffer bytes) throws DecodeException {

        log.info("进入了websocket解码器");
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes.array());
        try {
            ObjectInputStream ois = new ObjectInputStream(bis);
            return (WebSocketDto) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean willDecode(ByteBuffer bytes) {
        return true;
    }
}
