package com.cbec.order.dao.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.cbec.order.dao.entity.OrderEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper extends BaseMapper<OrderEntity> {
}
