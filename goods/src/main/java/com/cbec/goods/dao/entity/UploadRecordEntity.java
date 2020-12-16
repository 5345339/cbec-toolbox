package com.cbec.goods.dao.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

@Data
@TableName("t_upload_record")
public class UploadRecordEntity {
    @TableId(type = IdType.INPUT)
    private String id;
    private String uploadId;
    private String platformAccount;
    private String productId;
    private String status;
    private String message;

    public void buildId(String platformAccount, String uploadId, String productId) {
        id = platformAccount + "_" + uploadId + "_" + productId;
    }
}
