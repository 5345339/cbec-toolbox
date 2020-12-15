package com.github.lzk90s.cbec.internal.api.spider;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class ProductDTO {
    private String id;
    @JsonProperty("cat_id")
    private String catId;
    @JsonProperty("main_image")
    private String mainImage;
    private String name;
    private String description;
    @JsonProperty("parent_sku")
    private String parentSku;
    @JsonProperty("sku_list")
    private List<SkuDTO> skuList;
    @JsonProperty("image_list")
    private List<ResourceDTO> imageList;

}
