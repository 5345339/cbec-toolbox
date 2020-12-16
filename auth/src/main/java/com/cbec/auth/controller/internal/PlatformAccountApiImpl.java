package com.cbec.auth.controller.internal;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.cbec.auth.dao.entity.PlatformAccountEntity;
import com.cbec.auth.service.PlatformAccountService;
import com.cbec.internal.api.auth.PlatformAccountApi;
import com.cbec.internal.api.auth.PlatformAccountDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/internal/platform_account")
public class PlatformAccountApiImpl implements PlatformAccountApi {
    @Autowired
    private PlatformAccountService platformAccountService;

    @Override
    public List<PlatformAccountDTO> listPlatformAccountByUser(String userName) {
        var list = platformAccountService.selectList(new EntityWrapper<PlatformAccountEntity>()
                .eq("user", userName));
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyList();
        }
        return list.stream()
                .map(s -> PlatformAccountEntity.getConverter().doForward(s))
                .collect(Collectors.toList());
    }

    @Override
    public List<PlatformAccountDTO> listAllUserPlatformAccount() {
        var list = platformAccountService.selectList(null);
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyList();
        }
        return list.stream()
                .map(s -> PlatformAccountEntity.getConverter().doForward(s))
                .collect(Collectors.toList());
    }

    @Override
    public PlatformAccountDTO getUserPlatformAccount(String userName, String platformAccount) {
        return listPlatformAccountByUser(userName).stream()
                .filter(s -> s.getPlatformUser().equals(platformAccount))
                .findFirst()
                .orElse(null);
    }
}
