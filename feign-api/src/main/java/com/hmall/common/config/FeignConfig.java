package com.hmall.common.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
* @description:
* @ClassName FeignConfig
* @author Zle
* @date 2022-06-20 20:00
* @version 1.0
*/
@Configuration
public class FeignConfig {
    @Bean
    public RequestInterceptor requestInterceptor(){
        return new MyFeignInterceptor();
    }
}
