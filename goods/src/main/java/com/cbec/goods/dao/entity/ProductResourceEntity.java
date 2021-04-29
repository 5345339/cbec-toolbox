package com.cbec.goods.dao.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.cbec.common.bean.BeanConverter;
import com.cbec.internal.api.ecommerce_facade.ResourceDTO;
import lombok.Data;

@Data
@TableName("t_product_resource")
public class ProductResourceEntity {
    /**
     * 资源id
     */
    @TableId(type = IdType.INPUT)
    private String resourceId;
    /**
     * 产品ID
     */
    private String productId;
    /**
     * 是否是主图
     */
    private Boolean mainImage;
    /**
     * 是否是sku图片
     */
    private Boolean skuImage;
    /**
     * URL
     */
    private String url;

    public static BeanConverter<ProductResourceEntity, ResourceDTO> getConverter() {
        return new BeanConverter<>(ProductResourceEntity.class, ResourceDTO.class);
    }
}
