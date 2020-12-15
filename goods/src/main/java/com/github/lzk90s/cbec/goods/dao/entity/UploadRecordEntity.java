package com.github.lzk90s.cbec.goods.dao.entity;

import com.baomidou.mybatisplus.annotations.TableName;
import com.github.lzk90s.cbec.common.bean.BeanConverter;
import com.github.lzk90s.cbec.internal.api.spider.UploadStatusDTO;
import lombok.Data;

@Data
@TableName("t_upload_record")
public class UploadRecordEntity {
    private String id;
    private String platformAccount;
    private String productId;
    private String status;
    private String message;

    public static BeanConverter<UploadRecordEntity, UploadStatusDTO> getConverter(){
        return new BeanConverter<>(UploadRecordEntity.class, UploadStatusDTO.class);
    }
}
