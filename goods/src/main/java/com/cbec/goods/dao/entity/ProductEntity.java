package com.cbec.goods.dao.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.cbec.common.bean.BeanConverter;
import com.cbec.internal.api.ecommerce_facade.ProductDTO;
import lombok.Data;

@Data
@TableName("t_product")
public class ProductEntity {
    /**
     * 产品id
     */
    @TableId(type = IdType.INPUT)
    private String id;
    /**
     * 平台帐号
     */
    private String platformAccount;
    /**
     * 分类id
     */
    private String catId;
    /**
     * 主图
     */
    private String mainImage;
    /**
     * 产品名称
     */
    private String name;
    /**
     * 产品描述
     */
    private String description;
    /**
     * 父sku
     */
    private String parentSku;

    public static BeanConverter<ProductEntity, ProductDTO> getConverter() {
        return new BeanConverter<>(ProductEntity.class, ProductDTO.class);
    }
}
