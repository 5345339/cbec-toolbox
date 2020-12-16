package com.cbec.auth.service;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.cbec.auth.dao.entity.PlatformAccountEntity;
import com.cbec.auth.dao.mapper.PlatformAccountMapper;
import org.springframework.stereotype.Service;

@Service
public class PlatformAccountService extends ServiceImpl<PlatformAccountMapper, PlatformAccountEntity> {
}
