package com.cbec.order.feign;

import com.cbec.internal.api.ecommerce_facade.OrderSpiderApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "orderSpider", url = "${SPIDER_HOST:http://ecommerce-facade:33023}/order")
public interface OrderSpiderApiFeign extends OrderSpiderApi {
}
