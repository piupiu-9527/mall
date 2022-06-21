package com.hmall.common.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
* @description: Feign请求头
* @ClassName MyFeignInterceptor
* @author Zle
* @date 2022-06-20 19:24
* @version 1.0
*/
@Slf4j

//@Component
public class MyFeignInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate template) {
//        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
//        log.info("===request: {}", template.url());
        template.header("authorization", "2");
    }
}
