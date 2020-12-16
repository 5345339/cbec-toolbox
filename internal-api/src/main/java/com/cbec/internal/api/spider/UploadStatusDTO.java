package com.cbec.internal.api.spider;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class UploadStatusDTO {
    private String status;
    private String message;
    @JsonProperty("product_list")
    private List<String> productList;
}
