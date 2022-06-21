package com.hmall.item.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hmall.item.pojo.Item;

public interface IItemService extends IService<Item> {

    //修改上架下架
    void updateStatus(long id, Integer status);

    // 扣减库存
    void deductStock(Long itemId, Integer num);

}
