package com.example.haiyang.controller;

import com.example.haiyang.dto.LoginDTO;
import com.example.haiyang.dto.SignInDTO;
import com.example.haiyang.entity.User;
import com.example.haiyang.service.IUserService;
import com.example.haiyang.util.Desc;
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

    @Desc("登录")
    @PostMapping("/login")
    public R login(@RequestBody LoginDTO loginDTO){
        log.info("账号{}进行登录", loginDTO.getNumber());
        return service.login(loginDTO);
    }

    @Desc("注册")
    @PostMapping("/signIn")
    public R signIn(@RequestBody SignInDTO signInDTO){
        String number = signInDTO.getNumber();
        log.info("用户{}注册", number == null ? "" : number);
        return service.signIn(signInDTO);
    }

    @Desc("更新user信息")
    @PostMapping("/update")
    public R update(@RequestBody User user){
        log.info("userId:{}-更新用户信息", user.getId());
        return service.updateUser(user);
    }

    @Desc("退出登录")
    @PostMapping("/logout")
    public R logout(){
        log.info("用户{}退出登录", MyThreadLocal.getUserId());
        return service.logout();
    }

}
