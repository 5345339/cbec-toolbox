package com.cbec.goods.service;

public enum UploadStatusEnum {
    /**
     * 准备中
     */
    PREPARE("prepare"),
    /**
     * 上传完成
     */
    UPLOADED("uploaded"),
    /**
     * 已上架
     */
    IN_SALE("in_sale"),
    /**
     * 已下架
     */
    NOT_IN_SALE("not_in_sale");

    private final String status;

    UploadStatusEnum(String status) {
        this.status = status;
    }

    String getStatus() {
        return this.status;
    }
}
