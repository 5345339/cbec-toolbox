package com.cbec.common.config;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.cbec.common.exception.BizException;
import com.cbec.common.rest.Result;
import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;

import java.io.IOException;

/**
 * feign报错信息解析
 *
 * @author liuzhikun
 * @date 2019/12/11
 */
public class FeignErrorDecoder implements ErrorDecoder {
    private final ErrorDecoder errorDecoder = new Default();
    private ObjectMapper mapper = new ObjectMapper();

    public FeignErrorDecoder() {
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    @Override
    public Exception decode(String s, Response response) {
        @SuppressWarnings("all")
        Result result = null;
        try {
            String body = Util.toString(response.body().asReader());
            result = mapper.readValue(body, Result.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (null != result && !result.isOk()) {
            return new BizException(result.getStatus(), result.getMessage());
        }

        return errorDecoder.decode(s, response);
    }
}
