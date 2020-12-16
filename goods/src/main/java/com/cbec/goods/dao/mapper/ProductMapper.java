package com.cbec.goods.dao.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.cbec.goods.dao.entity.ProductEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProductMapper extends BaseMapper<ProductEntity> {
}
