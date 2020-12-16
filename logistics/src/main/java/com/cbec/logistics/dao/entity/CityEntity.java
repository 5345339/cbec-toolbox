package com.cbec.logistics.dao.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

@Data
@TableName("t_city")
public class CityEntity {
    @TableId
    private String id;
    private String name;
}
