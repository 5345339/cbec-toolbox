package com.cbec.goods.feign;

import com.cbec.internal.api.auth.PlatformAccountApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("auth")
public interface PlatformAccountApiFeign extends PlatformAccountApi {
}
