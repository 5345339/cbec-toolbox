package com.cbec.goods.dao.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@TableName("t_upload_record")
public class UploadRecordEntity {
    private String uploadId;
    private String platformAccount;
    private String originalProductId;
    private String originalParentSku;
    private String newProductId;
    private String status;
    private String message;
}
