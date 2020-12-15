drop table IF EXISTS `t_product`;
create TABLE `t_product`  (
  `id` varchar(64) NOT NULL COMMENT 'id',
  `platform_account` varchar(128) NOT NULL COMMENT '平台账户',
  `cat_id` varchar(128) NOT NULL COMMENT '分类id',
  `name` varchar(512) NOT NULL COMMENT '名称',
  `description` varchar(512) NOT NULL COMMENT '描述',
  `main_image` varchar(1024) NOT NULL COMMENT '主图',
  `parent_sku` varchar(128) NOT NULL COMMENT '父sku',
  `add_time` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '添加时间',
  `update_time` timestamp NOT NULL DEFAULT current_timestamp() ON update current_timestamp() COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY uk_parent_sku (`parent_sku`)
) ENGINE = InnoDB COMMENT = '产品表';

drop table IF EXISTS `t_product_resource`;
create TABLE `t_product_resource` (
  `resource_id` varchar(36) NOT NULL COMMENT '资源id',
  `product_id` varchar(64) NOT NULL DEFAULT '' COMMENT '资源对应的产品id',
  `main_image` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否是主图',
  `sku_image` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否是sku图',
  `url` varchar(1024) NOT NULL COMMENT 'url',
  UNIQUE KEY uk_product_id_resource_id (`product_id`, `resource_id`),
  INDEX idx_product_id (`product_id`)
) ENGINE = InnoDB COMMENT = '产品资源表';

drop table IF EXISTS `t_product_sku`;
create TABLE `t_product_sku`  (
  `sku` varchar(64) NOT NULL COMMENT 'sku',
  `product_id` varchar(64) NOT NULL COMMENT 'sku对应的产品id',
  `image_url` varchar(512) NOT NULL COMMENT '图片url',
  `price` DECIMAL(5,2) NOT NULL COMMENT '价格',
  `shipping_fee` DECIMAL(5,2) NOT NULL DEFAULT 0 COMMENT '运费',
  `storage` int(10) DEFAULT 0 COMMENT '库存',
  `weight` int(10) DEFAULT 0 COMMENT '重量',
  `style_color` varchar(512) DEFAULT '' COMMENT '颜色',
  `style_size` varchar(512) DEFAULT '' COMMENT '大小',
  `add_time` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '添加时间',
  `update_time` timestamp NOT NULL DEFAULT current_timestamp() ON update current_timestamp() COMMENT '更新时间',
  UNIQUE KEY uk_product_id_sku (`product_id`, `sku`),
  INDEX idx_product_id (`product_id`)
) ENGINE = InnoDB COMMENT = '商品SKU表';

drop table IF EXISTS `t_product_upload`;
create table `t_product_upload` (
  `id` varchar(64) NOT NULL COMMENT '上传id',
  `platform_account` varchar(128) NOT NULL COMMENT '上传的目的平台帐号',
  `product_id` varchar(128) NOT NULL COMMENT '产品id',
  `status` varchar(128) NOT NULL COMMENT '上传状态',
  `message` varchar(4096) NOT NULL DEFAULT '' COMMENT '上传提示消息',
  `add_time` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '添加时间',
  `update_time` timestamp NOT NULL DEFAULT current_timestamp() ON update current_timestamp() COMMENT '更新时间',
  UNIQUE KEY uk_id_product_id (`id`, `product_id`)
) ENGINE = InnoDB COMMENT = '商品上传信息表';