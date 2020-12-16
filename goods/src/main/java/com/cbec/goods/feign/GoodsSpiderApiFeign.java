package com.cbec.goods.feign;

import com.cbec.internal.api.spider.GoodsSpiderApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "goodsSpider", url = "${SPIDER_HOST:http://spider:33023}/goods", contextId = "goodsSpiderFeign")
public interface GoodsSpiderApiFeign extends GoodsSpiderApi {
}
