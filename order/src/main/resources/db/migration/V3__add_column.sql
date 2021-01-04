alter table `t_order` add column `detail_url` varchar(1024) not null default '';
alter table `t_order` add column `remark` varchar(8192) default '';
alter table `t_order` add column `sku` varchar(128) not null;