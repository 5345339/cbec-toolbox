package com.cbec.auth.controller.internal;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.cbec.auth.dao.entity.UserEntity;
import com.cbec.auth.service.UserService;
import com.cbec.internal.api.auth.UserApi;
import com.cbec.internal.api.auth.UserInfoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/internal/user")
public class UserApiImpl implements UserApi {
    @Autowired
    private UserService userService;

    @Override
    public UserInfoDTO getUserInfo(String userName) {
        return Optional.ofNullable(userService.selectOne(new EntityWrapper<UserEntity>().eq("name", userName)))
                .map(s -> UserEntity.getConverter().doForward(s)).orElseThrow(IllegalArgumentException::new);
    }
}
