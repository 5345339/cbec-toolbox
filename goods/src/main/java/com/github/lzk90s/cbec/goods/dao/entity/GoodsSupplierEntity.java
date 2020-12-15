package com.github.lzk90s.cbec.goods.dao.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.github.lzk90s.cbec.common.bean.BeanConverter;
import com.github.lzk90s.cbec.internal.api.spider.GoodsInfoDTO;
import lombok.Data;

@Data
@TableName("t_goods_supplier")
public class GoodsSupplierEntity {
    @TableId(value = "id", type = IdType.INPUT)
    private String id;
    private String goodsId;
    private String subject;
    private String category;
    private String platform;
    private String detailUrl;
    private String imageUrl;
    private Float price;
    private String sellerName;
    private String sellerShopUrl;
    private Boolean purchased;

    public static BeanConverter<GoodsSupplierEntity, GoodsInfoDTO> getConverter() {
        return new BeanConverter<>(GoodsSupplierEntity.class, GoodsInfoDTO.class);
    }
}
