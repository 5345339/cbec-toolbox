package com.cbec.internal.api.ecommerce_facade;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SkuDTO {
    private String sku;
    @JsonProperty("product_id")
    private String productId;
    private BigDecimal price;
    @JsonProperty("shipping_fee")
    private BigDecimal shippingFee;
    private Integer storage;
    @JsonProperty("style_color")
    private String styleColor;
    @JsonProperty("style_size")
    private String styleSize;
    @JsonProperty("image_url")
    private String imageUrl;
    private Float weight;
}
