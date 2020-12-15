package com.github.lzk90s.cbec.goods.dao.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.github.lzk90s.cbec.common.bean.BeanConverter;
import com.github.lzk90s.cbec.internal.api.spider.SkuDTO;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("t_product_sku")
public class ProductSkuEntity {
    /**
     * sku
     */
    @TableId(type = IdType.INPUT)
    private String sku;
    /**
     * sku对应的产品id
     */
    private String productId;
    /**
     * 售价
     */
    private BigDecimal price;
    /**
     * 运费
     */
    private BigDecimal shippingFee;
    /**
     * 库存
     */
    private Integer storage;
    /**
     * 颜色
     */
    private String styleColor;
    /**
     * 尺寸
     */
    private String styleSize;
    /**
     * sku图片
     */
    private String imageUrl;
    /**
     * 重量
     */
    private Float weight;

    public static BeanConverter<ProductSkuEntity, SkuDTO> getConverter(){
        return new BeanConverter<>(ProductSkuEntity.class, SkuDTO.class);
    }
}