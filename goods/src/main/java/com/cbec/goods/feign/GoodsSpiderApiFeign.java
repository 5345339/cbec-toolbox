package com.cbec.goods.feign;

import com.cbec.internal.api.ecommerce_facade.GoodsSpiderApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "goodsSpider", url = "${SPIDER_HOST:http://ecommerce-facade:33023}/goods", contextId = "goodsSpiderFeign")
public interface GoodsSpiderApiFeign extends GoodsSpiderApi {
}
