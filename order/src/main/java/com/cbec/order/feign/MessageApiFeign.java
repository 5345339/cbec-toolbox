package com.cbec.order.feign;

import com.cbec.internal.api.messager.MessageApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("messager")
public interface MessageApiFeign extends MessageApi {
}
