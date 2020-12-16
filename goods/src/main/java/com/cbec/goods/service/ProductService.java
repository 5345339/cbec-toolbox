package com.cbec.goods.service;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.cbec.goods.dao.entity.ProductEntity;
import com.cbec.goods.dao.mapper.ProductMapper;
import org.springframework.stereotype.Service;

@Service
public class ProductService extends ServiceImpl<ProductMapper, ProductEntity> {
}
