package com.hmall.search.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hmall.common.dto.Item;
import com.hmall.search.pojo.ItemDoc;
import com.hmall.search.service.ISearchService;
import org.elasticsearch.action.IndicesRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class SearchService implements ISearchService {

    @Autowired
    private RestHighLevelClient client;

    private static final ObjectMapper MAPPER =new ObjectMapper();

    @Override
    public void deleteItemById(Item item) {
        try {
            DeleteRequest request = new DeleteRequest("item",item.getId().toString());
            client.delete(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void saveItemById(Item item) {
        try {
            IndexRequest request= new IndexRequest("item").id(item.getId().toString());
            request.source(MAPPER.writeValueAsString(new ItemDoc(item)), XContentType.JSON);
            client.index(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
