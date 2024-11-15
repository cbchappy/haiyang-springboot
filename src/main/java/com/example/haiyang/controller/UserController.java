package com.example.haiyang.controller;

import com.example.haiyang.dto.LoginDTO;
import com.example.haiyang.dto.SignInDTO;
import com.example.haiyang.entity.User;
import com.example.haiyang.service.IUserService;
import com.example.haiyang.util.MyThreadLocal;
import com.example.haiyang.util.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Chen
 * @since 2024-10-13
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    IUserService service;

    @PostMapping("/login")
    public R login(@RequestBody LoginDTO loginDTO){
        log.info("手机号{}进行登录", loginDTO.getNumber());
        return service.login(loginDTO);
    }


    @PostMapping("/signIn")
    public R signIn(@RequestBody SignInDTO signInDTO){
        log.info("用户{}注册", signInDTO.getNumber());
        return service.signIn(signInDTO);
    }

    @PostMapping("/update")
    public R update(@RequestBody User user){
        log.info("更新用户");
        return service.updateUser(user);
    }

    @PostMapping("/logout")
    public R logout(){
        log.info("用户{}退出登录", MyThreadLocal.getUserId());
        return service.logout();
    }

}
