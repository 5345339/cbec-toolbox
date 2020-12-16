package com.cbec.goods.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class UploadProductOption {
    /**
     * 目标平台帐号
     */
    @NotBlank(message = "目标平台帐号不能为空")
    private String dstPlatformAccount;
    /**
     * 产品ID列表
     */
    @NotNull(message = "产品id列表不能为空")
    private List<String> productIdList;
}
