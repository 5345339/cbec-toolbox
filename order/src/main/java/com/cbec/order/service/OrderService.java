package com.cbec.order.service;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.cbec.order.dao.entity.OrderEntity;
import com.cbec.order.dao.mapper.OrderMapper;
import org.springframework.stereotype.Service;

@Service
public class OrderService extends ServiceImpl<OrderMapper, OrderEntity> {
}
