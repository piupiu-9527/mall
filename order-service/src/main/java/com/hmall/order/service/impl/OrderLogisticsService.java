package com.hmall.order.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmall.order.mapper.OrderLogisticsMapper;
import com.hmall.order.pojo.OrderLogistics;
import com.hmall.order.service.IOrderLogisticsService;
import org.springframework.stereotype.Service;

/**
* @description:
* @author Zle
* @date 2022-06-20  20:32
* @version 1.0
*/
@Service
public class OrderLogisticsService extends ServiceImpl<OrderLogisticsMapper, OrderLogistics>
        implements IOrderLogisticsService {
}
