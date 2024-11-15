package com.example.haiyang.service;

import com.example.haiyang.dto.LoginDTO;
import com.example.haiyang.dto.SignInDTO;
import com.example.haiyang.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.haiyang.util.R;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Chen
 * @since 2024-10-13
 */
public interface IUserService extends IService<User> {

    R login(LoginDTO loginDTO);



    R signIn(SignInDTO signInDTO);

    R updateUser(User user);

    R logout();
}
