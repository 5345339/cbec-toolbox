package com.cbec.goods.service;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.cbec.goods.dao.entity.ProductEntity;
import com.cbec.goods.dao.mapper.ProductMapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService extends ServiceImpl<ProductMapper, ProductEntity> {
}
