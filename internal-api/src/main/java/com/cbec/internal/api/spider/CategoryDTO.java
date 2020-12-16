package com.cbec.internal.api.spider;

import lombok.Data;

@Data
public class CategoryDTO {
    private String name;
    private String url;
    private String description;
    private String vendor;
}
