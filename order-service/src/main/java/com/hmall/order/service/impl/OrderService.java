package com.hmall.order.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmall.common.client.ItemClient;
import com.hmall.common.client.UserClient;
import com.hmall.common.dto.Address;
import com.hmall.common.dto.Item;
import com.hmall.order.mapper.OrderMapper;
import com.hmall.order.pojo.Order;
import com.hmall.order.pojo.OrderDetail;
import com.hmall.order.pojo.OrderLogistics;
import com.hmall.order.pojo.RequestParams;
import com.hmall.order.service.IOrderService;
import com.hmall.order.utils.UserHolder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService extends ServiceImpl<OrderMapper, Order> implements IOrderService {

    @Autowired
    private ItemClient itemClient;

    @Autowired
    private UserClient userClient;

    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private OrderLogisticsService orderLogisticsService;

    @Transactional
    @Override
    public Long createrOrer(RequestParams requestParams) {
        Order order = new Order();
        //- 1）根据雪花算法生成订单id
            // pojo包中已设置
        //- 2）商品微服务提供FeignClient，实现根据id查询商品的接口
        //- 3）根据itemId查询商品信息
        Item item = itemClient.queryItemById(requestParams.getItemId());
        //- 4）基于商品价格、购买数量计算商品总价：totalFee
        Long totalFee= requestParams.getNum() * item.getPrice();
        //- 5）封装Order对象，初识status为未支付
        order.setTotalFee(totalFee);
        order.setStatus(1);
        order.setUserId(UserHolder.getUser());
        order.setPaymentType(requestParams.getPaymentType());
        //- 6）将Order写入数据库tb_order表中
        this.save(order);
        //- 7）将商品信息、orderId信息封装为OrderDetail对象，写入tb_order_detail表
        OrderDetail detail = new OrderDetail();
        BeanUtils.copyProperties(item,detail);
        detail.setNum(requestParams.getNum());
        detail.setItemId(item.getId());
        detail.setOrderId(order.getId());
        orderDetailService.save(detail);
        //- 8）将user-service的根据id查询地址接口封装为FeignClient
        //- 9）根据addressId查询user-service服务，获取地址信息
        Address address = userClient.findAddressById(requestParams.getAddressId());
        //- 10）将地址封装为OrderLogistics对象，写入tb_order_logistics表
        OrderLogistics logistics = new OrderLogistics();
        BeanUtils.copyProperties(address,logistics);
//        logistics.setPhone(address.getMobile());
        logistics.setOrderId(order.getId());
        orderLogisticsService.save(logistics);
        //- 11）在item-service提供减库存接口，并编写FeignClient
        //- 12）调用item-service的减库存接口

/*        try {
            //注意：传递负数，扣减库存
            itemClient.updateStock(requestParams.getItemId(), -requestParams.getNum());
        } catch (Exception e) {
            throw new RuntimeException("库存不足！");
        }*/
        return order.getId();
    }
}
