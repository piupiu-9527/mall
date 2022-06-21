package com.hmall.search.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.hmall.common.dto.Item;

public interface ISearchService  {
    void deleteItemById(Item item);

    void saveItemById(Item item);
}
