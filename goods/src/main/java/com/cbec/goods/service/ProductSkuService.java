package com.cbec.goods.service;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.cbec.goods.dao.entity.ProductSkuEntity;
import com.cbec.goods.dao.mapper.ProductSkuMapper;
import org.springframework.stereotype.Service;

@Service
public class ProductSkuService extends ServiceImpl<ProductSkuMapper, ProductSkuEntity> {
}
