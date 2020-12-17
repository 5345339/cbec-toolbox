package com.cbec.goods.service;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.cbec.goods.dao.entity.ProductResourceEntity;
import com.cbec.goods.dao.mapper.ProductResourceMapper;
import org.springframework.stereotype.Service;

@Service
public class ProductResourceService extends ServiceImpl<ProductResourceMapper, ProductResourceEntity> {
}
