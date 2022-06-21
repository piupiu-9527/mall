package com.hmall.item.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmall.item.mapper.ItemMapper;
import com.hmall.item.pojo.Item;
import com.hmall.item.service.IItemService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Wrapper;

@Service
public class ItemService extends ServiceImpl<ItemMapper, Item> implements IItemService {

/*    @Autowired
    private ItemMapper itemMapper;*/
    @Autowired
    private RabbitTemplate rabbitTemplate;



    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateStatus(long id, Integer status) {
//       Item item = new Item();
//        item.setId(id);
//        item.setStatus(status);
//       //itemMapper.updateById(item);
//        this.getBaseMapper().updateById(item);
        this.update(
                Wrappers.<Item>lambdaUpdate()
                        .set(Item::getStatus, status)
                        .eq(Item::getId, id));
        //根据上下架判断RoutingKey
        String routingKey= status ==1 ? "item.up" : "item.down";
        Item item = getById(id);
        rabbitTemplate.convertAndSend("item.topic", routingKey, item);
    }

    /**
    * @description: 扣减库存
    * @param: [itemId, num]
    * @return: void
    * @author Zle
    * @date: 2022-06-21 10:29
    */
    @Transactional
    @Override
    public void deductStock(Long itemId, Integer num) {
        try {
            // update tb_item set stock = stock - #{num} where id = #{itemId}
            //传递负数：则扣减库存
            if (num < 0) {
                update().setSql("stock = stock - " + Math.abs(num)).eq("id", itemId).update();
            } else { //正数：则添加库存
                update().setSql("stock = stock + " + Math.abs(num)).eq("id", itemId).update();
            }

        } catch (Exception e) {
            throw new RuntimeException("库存不足！");
        }
    }
}
