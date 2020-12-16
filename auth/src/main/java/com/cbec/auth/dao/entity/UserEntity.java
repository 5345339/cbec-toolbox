package com.cbec.auth.dao.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.cbec.common.bean.BeanConverter;
import com.cbec.internal.api.auth.UserInfoDTO;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.Pattern;
import java.util.Date;

@Data
@Accessors(chain = true)
@TableName("t_user")
public class UserEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String password;
    private String phone;
    private Boolean state;
    @Pattern(regexp = "^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$")
    private String email;
    private Date addTime;

    public static BeanConverter<UserEntity, UserInfoDTO> getConverter() {
        return new BeanConverter<>(UserEntity.class, UserInfoDTO.class);
    }
}
