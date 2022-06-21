package com.hmall.item.web;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.hmall.common.dto.PageDTO;
import com.hmall.item.pojo.Item;
import com.hmall.item.service.IItemService;
import lombok.val;
import org.omg.CORBA.NVList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.util.Date;

@RestController
@RequestMapping("item")
public class ItemController {

    @Autowired
    private IItemService itemService;


    @GetMapping("hi")
    public String test(){
        return "hi";
    }


    /**
     * 分页
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/list")
    public PageDTO<Item> queryPage(@RequestParam("page") Integer page, @RequestParam("size") Integer size){
        Page<Item> pageInfo = new Page<Item>(page,size);
        LambdaQueryWrapper<Item> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.orderByAsc(Item::getUpdateTime);
        Page<Item> itemPage = itemService.page(pageInfo, queryWrapper);
        return new PageDTO<>(itemPage.getTotal(),itemPage.getRecords());

    }


    /**
     * 根据id查询商品信息
     * @param id
     * @return
     */
    @GetMapping("{id}")
    public Item queryItemById(@PathVariable("id") Long id) {
        return itemService.getById(id);
    }

    /**
     * 新增商品
     * @param item
     */
    @PostMapping
    public void saveItem(@RequestBody Item item){
        item.setCreateTime(new Date());
        item.setUpdateTime(new Date());
        item.setStatus(2);
        itemService.save(item);
    }

    /**
     * 修改商品上架下架状态
     * @param id
     * @param status
     */
    @PutMapping("/status/{id}/{status}")
    public void updateItemStatus(@PathVariable("id") Long id ,@PathVariable("status") Integer status){
        itemService.updateStatus(id,status);
    }

    /**
     * 修改商品信息
     * @param item
     */
    @PutMapping
    public void updateItem(@RequestBody Item item){
        item.setUpdateTime(new Date());
        //不允许修改商品状态，所以强制设置为null，更新时，就会忽略该字段
        item.setStatus(null);
        itemService.updateById(item);

    }

    /**
     * 根据id删除商品
     * @param id
     */
    @DeleteMapping("{id}")
    public void deleteItemById(@PathVariable("id") Long id){
        itemService.removeById(id);
    }









/*11
    *//**
     * 分页查询
     * @param page
     * @param size
     * @return
     *//*
    @GetMapping("/list")
    public PageDTO<Item> queryPage(@RequestParam("page") Integer page, @RequestParam("size") Integer size){
        Page<Item> pageInfo = new Page<Item>(page,size);
        LambdaQueryWrapper<Item> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.orderByAsc(Item::getUpdateTime);
        Page<Item> itemPage = itemService.page(pageInfo, queryWrapper);
        return new PageDTO<>(itemPage.getTotal(),itemPage.getRecords());
       *//* //分页查询
        Page<Item> result = itemService.page(new Page<>(size, page));
        //封装并返回
        return new PageDTO<>(result.getTotal(), result.getRecords());*//*
    }

    *//**
     * 根据id查询商品信息
     * @param id
     * @return
     *//*
    @GetMapping("/{id}")
    public Item getITemById(@PathVariable("id") Long id){
        return itemService.getById(id);
    }

    *//**
     * 新增商品信息
     * @param item
     *//*
    @PostMapping
    public void saveItem(@RequestBody Item item){
        item.setCreateTime(new Date());
        item.setUpdateTime(new Date());
        item.setStatus(2);
        itemService.save(item);
    }

    *//**
     * 商品下架和上架
     * @param id
     * @param status
     *//*
    @PutMapping("status/{id}/{status}")
    public void putItemStatus(@PathVariable("id") Long id ,@PathVariable("status") Integer status){
        itemService.updateStatus(id,status);
    }


    *//**
     * 修改商品信息
     * @param item
     *//*
    @PutMapping
    public void putItem(@RequestBody Item item) {
        item.setUpdateTime(new Date());
        //不允许修改商品状态，所以强制设置为null，更新时，就会忽略该字段
        item.setStatus(null);
        itemService.updateById(item);
    }

    @DeleteMapping("/{id}")
    public void deleteItemById(@PathVariable Long id){
        itemService.removeById(id);
    }




*//*    @GetMapping("/list")
    public PageDTO<Item> list(@RequestParam("page") Integer page, @RequestParam("size") Integer size){
        Page<Item> pageInfo = new Page<>(page,size);

        LambdaQueryWrapper<Item> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.orderByAsc(Item::getUpdateTime);

        Page<Item> result = itemService.page(pageInfo, queryWrapper);
        return new PageDTO<>(result.getTotal(),result.getRecords());
    }*/
}
