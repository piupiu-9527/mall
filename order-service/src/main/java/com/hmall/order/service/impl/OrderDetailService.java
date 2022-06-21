package com.hmall.order.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmall.order.mapper.OrderDetailMapper;
import com.hmall.order.pojo.OrderDetail;
import com.hmall.order.service.IOrderDetailService;
import org.springframework.stereotype.Service;

/**
* @description:
* @author Zle
* @date 2022-06-20  20:32
* @version 1.0
*/
@Service
public class OrderDetailService extends ServiceImpl<OrderDetailMapper, OrderDetail> implements IOrderDetailService {
}
