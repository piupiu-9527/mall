package com.hmall.order;

import com.hmall.common.client.UserClient;
import com.hmall.common.dto.Address;
import com.hmall.order.OrderApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
* @description:
* @ClassName FeignTest
* @author Zle
* @date 2022-06-20 19:15
* @version 1.0
*/
@RunWith(SpringRunner.class)
@SpringBootTest
public class FeignTest {

    @Autowired
    private UserClient userClient;

    /**
    * @description: 测试带请求头调用user服务
    * @param: []
    * @return: void
    * @author Zle
    * @date: 2022-06-20 19:46
    */
    @Test
    public void  testFingAddress(){
        Address addressById = userClient.findAddressById(59L);
        System.out.println("------------"+ addressById);
    }

}
