package com.example.haiyang.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.MD5;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.haiyang.constants.CommonConstants;
import com.example.haiyang.dto.LoginDTO;
import com.example.haiyang.dto.SignInDTO;
import com.example.haiyang.entity.User;
import com.example.haiyang.dao.UserMapper;
import com.example.haiyang.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.haiyang.util.MyThreadLocal;
import com.example.haiyang.util.MyUtil;
import com.example.haiyang.util.R;
import com.example.haiyang.constants.RedisConstants;
import com.example.haiyang.vo.LoginVO;
import com.example.haiyang.vo.UpdateUserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import static com.example.haiyang.constants.CommonConstants.DEFAULT_SIGNATURE;
import static com.example.haiyang.constants.CommonConstants.SALT;

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
            return R.failMsg("用户不存在");
        }
        String id = String.valueOf(user.getId());

        if(template.opsForValue().get(RedisConstants.LOGIN + id) != null){
            return R.failMsg("用户已在线");
        }

        //进行md5加密
        String md5PW = MyUtil.digest(loginDTO.getPassword());
        if(!md5PW.equals(user.getPassword())){
            return R.failMsg("输入密码错误");
        }

        //将信息存入redis 设置过期时间
        String token = MyUtil.digest(user.getNumber() + LocalDateTime.now() + SALT);
        template.opsForValue().set(RedisConstants.LOGIN + id, token, 30, TimeUnit.MINUTES);

        LoginVO loginVO = new LoginVO();
        loginVO.setWxToken(token);
        loginVO.setUserId(id);
        loginVO.setNumber(user.getNumber());
        loginVO.setAvatar(user.getAvatar());
        loginVO.setNickname(user.getNickname());
        loginVO.setSignature(user.getSignature());

        return R.success("登录成功", loginVO);
    }



    @Override
    public R signIn(SignInDTO signInDTO) {
        if(StrUtil.isBlankIfStr(signInDTO.getNumber())){
            return R.failMsg("输入账户名格式不合理!");
        }
        if(StrUtil.isBlankIfStr(signInDTO.getPassword())){
            return R.failMsg("输入密码格式不合理!");
        }
        if(signInDTO.getNumber().length() < 5){
            return R.failMsg("账号长度不能小于5");
        }
        if(signInDTO.getPassword().length() < 8){
            return R.failMsg("密码长度不能小于8");
        }

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getNumber, signInDTO.getNumber());
        User user = getOne(wrapper);
        if(user != null){
            return R.failMsg("该账号已注册");
        }
        User newUser = new User();
        newUser.setNumber(signInDTO.getNumber());
        newUser.setCreateTime(LocalDateTime.now());
        newUser.setPassword(new MD5().digestHex16(signInDTO.getPassword()));
        newUser.setNickname("user_" + RandomUtil.randomString(5));
        newUser.setAvatar(CommonConstants.DEFAULT_AVATAR);
        newUser.setSignature(DEFAULT_SIGNATURE);
        save(newUser);

        return R.success();
    }

    @Override
    public R updateUser(User user) {
        //todo 优化
        user.setId(MyThreadLocal.getUserId());

        User r = this.getById(user.getId());
        UpdateUserVo userVo = new UpdateUserVo();
        userVo.setAvatar(r.getAvatar());
        userVo.setSignature(r.getSignature());
        userVo.setNickname(r.getNickname());

        return R.success("更新成功", userVo);
    }

    @Override
    public R logout() {
        //从redis中移除key
        template.delete(RedisConstants.LOGIN + MyThreadLocal.getUserId());

        return R.success();
    }
}
