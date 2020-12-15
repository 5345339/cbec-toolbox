package com.github.lzk90s.cbec.auth.dao.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.github.lzk90s.cbec.common.bean.BeanConverter;
import com.github.lzk90s.cbec.internal.api.auth.PlatformAccountDTO;
import lombok.Data;

@Data
@TableName("t_platform_account")
public class PlatformAccountEntity {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private String user;
    private String platform;
    private String platformUser;
    private String platformPassword;
    private String apiToken;

    public static BeanConverter<PlatformAccountEntity, PlatformAccountDTO> getConverter() {
        return new BeanConverter<>(PlatformAccountEntity.class, PlatformAccountDTO.class);
    }
}
