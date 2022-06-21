package com.hmall.common.client;

import com.hmall.common.config.FeignConfig;
import com.hmall.common.dto.Address;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

/**
* @description: TODO
* @ClassName UserClient
* @author Zle
* @date 2022-06-20 19:08
* @version 1.0
*/
@FeignClient(value = "userservice",configuration = FeignConfig.class)
public interface UserClient {
    @GetMapping("/address/{id}")
    public Address findAddressById(@PathVariable("id") Long id);


}
