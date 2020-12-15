package com.github.lzk90s.cbec.goods.service;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.github.lzk90s.cbec.goods.dao.entity.ProductEntity;
import com.github.lzk90s.cbec.goods.dao.mapper.ProductMapper;
import org.springframework.stereotype.Service;

@Service
public class ProductService extends ServiceImpl<ProductMapper, ProductEntity> {
}
