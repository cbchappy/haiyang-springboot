package com.example.haiyang.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @Author Cbc
 * @DateTime 2024/10/16 22:37
 * @Description
 */
@Data
public class LoginVO {
    private String wxToken;

    private String userId;

    private String number;

    private String avatar;

    private String nickname;
}
