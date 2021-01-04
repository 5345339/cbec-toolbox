package com.cbec.goods.dao.entity;

import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

@Data
@TableName("t_clone_product")
public class CloneProductEntity {
    private String userName;
    private String srcPlatformAccount;
    private String dstPlatformAccount;
}
