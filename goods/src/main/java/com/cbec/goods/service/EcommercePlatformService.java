package com.cbec.goods.service;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.cbec.goods.dao.entity.EcommercePlatformEntity;
import com.cbec.goods.dao.mapper.EcommercePlatformMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EcommercePlatformService extends ServiceImpl<EcommercePlatformMapper, EcommercePlatformEntity> {
}
