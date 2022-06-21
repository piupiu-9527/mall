package com.hmall.feign;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hmall.common.client.ItemClient;
import com.hmall.common.dto.Item;
import com.hmall.common.dto.PageDTO;
import com.hmall.search.SearchApplication;
import com.hmall.search.pojo.ItemDoc;
import org.checkerframework.checker.units.qual.A;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.Classes;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=SearchApplication.class)
public class FeignTest {

    @Autowired
    private ItemClient itemClient;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    //jackson提供的Java对象转json字符串
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    public void testQueryItem() throws IOException {
        // http://itemservice/item/list?page=1&size=5
        int page = 1;

        while (true) {
            //远程调用商品微服务进行分页查询：
            PageDTO<Item> pageDTO = itemClient.queryPage(page,500);
            List<Item> list = pageDTO.getList();
            if (list.size() <= 0 || page > 20) {
                break;
            }
            // 1.准备BulkRequest
            BulkRequest request = new BulkRequest();
            // 2.准备DSL
            // 遍历
            for (Item item : list) {
                if(item.getStatus() == 2){
                    // 下架商品直接跳过
                    continue;
                }
                // 把 Item 转为 ItemDoc
                ItemDoc itemDoc = new ItemDoc(item);
                //将itemDoc使用jackson转成json数据
                String json = MAPPER.writeValueAsString(itemDoc);
                // 添加新增请求
                request.add(new IndexRequest("item")
                        .id(itemDoc.getId().toString())
                        .source(json, XContentType.JSON)
                );
            }
            // 3.发请求，批量处理
            restHighLevelClient.bulk(request, RequestOptions.DEFAULT);
            page++;
        }
    }
}
