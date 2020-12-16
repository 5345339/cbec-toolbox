package com.cbec.order.feign;

import com.cbec.internal.api.spider.OrderSpiderApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "orderSpider", url = "${SPIDER_HOST:http://spider:33023}/order")
public interface OrderSpiderApiFeign extends OrderSpiderApi {
}
