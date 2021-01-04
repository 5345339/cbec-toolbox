drop table IF EXISTS `t_clone_product`;
create table `t_clone_product` (
  `user_name` varchar(64) NOT NULL COMMENT '用户名',
  `src_platform_account` varchar(128) NOT NULL COMMENT '源平台帐号',
  `dst_platform_account` varchar(128) NOT NULL COMMENT '目的平台帐号',
  `add_time` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '添加时间',
  `update_time` timestamp NOT NULL DEFAULT current_timestamp() ON update current_timestamp() COMMENT '更新时间',
  UNIQUE KEY uk_src_dst_platform_account (`src_platform_account`, `dst_platform_account`)
) ENGINE = InnoDB COMMENT = '商品克隆表';