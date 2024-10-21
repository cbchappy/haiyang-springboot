package com.example.haiyang.service.impl;

import cn.hutool.Hutool;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.digest.MD5;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.haiyang.dto.LoginDTO;
import com.example.haiyang.dto.SignInDTO;
import com.example.haiyang.entity.User;
import com.example.haiyang.dao.UserMapper;
import com.example.haiyang.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.haiyang.util.MyThreadLocal;
import com.example.haiyang.util.MyUtil;
import com.example.haiyang.util.R;
import com.example.haiyang.util.RedisName;
import com.example.haiyang.vo.LoginVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Chen
 * @since 2024-10-13
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Autowired
    StringRedisTemplate template;
    @Override
    public R login(LoginDTO loginDTO) {
        //查询用户
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getNumber, loginDTO.getNumber());
        User user = getOne(wrapper);
        if(user == null){
            return R.fail("用户不存在", null);
        }

        //进行md5加密

        String md5PW = MyUtil.digest(loginDTO.getPassword());
        if(!md5PW.equals(user.getPassword())){
            return R.fail("输入密码错误", null);
        }

        //将信息存入redis 设置过期时间
        String token = MyUtil.digest(user.getNumber() + LocalDateTime.now());
        template.opsForValue().set(RedisName.LOGIN_EXPIRE + token, String.valueOf(user.getId()), 30, TimeUnit.MINUTES);
        //todo 返回token 头像 昵称 账号
        //todo 数据库查询用户信息
        LoginVO loginVO = new LoginVO();
        loginVO.setWxToken(token);
        loginVO.setNumber(user.getNumber());
        loginVO.setAvatar(user.getAvatar());
        loginVO.setNickname(user.getNickname());
        return R.success("登录成功", loginVO);
    }



    @Override
    public R signIn(SignInDTO signInDTO) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getNumber, signInDTO.getNumber());
        User user = getOne(wrapper);
        if(user != null){
            return R.fail("该账号已注册", null);
        }
        User newUser = new User();
        newUser.setNumber(signInDTO.getNumber());
        newUser.setCreateTime(LocalDateTime.now());
        newUser.setPassword(new MD5().digestHex16(signInDTO.getPassword()));
        newUser.setNickname("user_" + RandomUtil.randomString(5));
        newUser.setAvatar("https://haiyang-1318727264.cos.ap-nanjing.myqcloud.com/avatar/%E9%BB%98%E8%AE%A4%E5%A4%B4%E5%83%8F.jpg?q-sign-algorithm=sha1&q-ak=AKIDjZ6eCw6TFVz2LIK6812uMTCaHUgInz2cfjpk804TRjkiu9GAWUbySvjxFOCLU5dH&q-sign-time=1729234723;1729238323&q-key-time=1729234723;1729238323&q-header-list=host&q-url-param-list=ci-process&q-signature=4ff8a49393ede499b60259508d83c6b1f58a1cd5&x-cos-security-token=geUYldehNZfP3dOk6mk24Wj1NSi5TpJab5dbcc68b53ef0b1e5bed236b45879b9oX-nG1EA05YfkOXzVMCkhLk8-OfFGxfjrSATT2B1eaWSEybedL_9dYZb-uAiZO5UK3y91kCmOGyg5oXzPO3_nb-o_AVlln7uIFWFuTJal8fij7xssUtCLVyY1L7DPm3KBgloQ09L6kari0ijFsLswaRfQ8ZOa5O6c0klnE1jtpEf8F2Mx7oE1FP0Irxaqbdj&ci-process=originImage");
        save(newUser);
        return R.success(signInDTO);
    }

    @Override
    public R updateUser(User user) {

        user.setId(MyThreadLocal.getUserId());
        updateById(user);
        return R.success("更新成功", user);
    }
}
