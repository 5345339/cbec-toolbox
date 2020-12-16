package com.cbec.common.config;

import com.cbec.common.exception.RestAdviceHandler;
import org.springframework.context.annotation.Bean;

public class BeanConfiguration {
    @Bean
    public RestAdviceHandler restExceptionAdvice() {
        return new RestAdviceHandler();
    }

    @Bean
    public FeignErrorDecoder feignExceptionErrorDecoder() {
        return new FeignErrorDecoder();
    }
}
