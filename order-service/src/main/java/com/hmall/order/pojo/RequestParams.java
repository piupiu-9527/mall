package com.hmall.order.pojo;

import lombok.Data;

/**
* @description:
* @ClassName RequestParams
* @author Zle
* @date 2022-06-20 20:53
* @version 1.0
*/
@Data
public class RequestParams {
    // 购买数量
    private Integer num;
    // 商品ID
    private Long itemId;
    // 收货地址id
    private Long addressId;
    // 付款方式
    private Integer paymentType;

}
