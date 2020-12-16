package com.cbec.order.feign;

import com.cbec.internal.api.auth.UserApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("auth")
public interface UserApiFeign extends UserApi {
}
