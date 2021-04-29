package com.cbec.internal.api.ecommerce_facade;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ResourceDTO {
    @JsonProperty("resource_id")
    private String resourceId;
    @JsonProperty("product_id")
    private String productId;
    @JsonProperty("main_image")
    private Boolean mainImage;
    @JsonProperty("sku_image")
    private Boolean skuImage;
    private String url;
}
