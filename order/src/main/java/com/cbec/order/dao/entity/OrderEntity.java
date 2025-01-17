package com.cbec.order.dao.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.cbec.common.bean.BeanConverter;
import com.cbec.internal.api.ecommerce_facade.OrderDTO;
import lombok.Data;

import java.util.Date;

@Data
@TableName("t_order")
public class OrderEntity {
    @TableId(value = "id", type = IdType.INPUT)
    private String id;
    private String platform;
    private String user;
    private String platformAccount;
    private String type;
    private Date confirmTime;
    private String sn;
    private String sku;
    private Integer num;
    private Float price;
    private String imageUrl;
    private String detailUrl;
    private String remark;
    private Date lastNotifyTime;

    public static BeanConverter<OrderEntity, OrderDTO> getConverter() {
        return new BeanConverter<>(OrderEntity.class, OrderDTO.class);
    }
}
