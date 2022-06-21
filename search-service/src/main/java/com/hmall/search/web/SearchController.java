package com.hmall.search.web;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hmall.common.dto.PageDTO;
import com.hmall.search.pojo.ItemDoc;
import com.hmall.search.pojo.RequestParams;
import org.checkerframework.checker.units.qual.A;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/search")
public class SearchController {

    @Autowired
    private RestHighLevelClient client;

    private static final ObjectMapper MAPPER =new ObjectMapper();
    /**
     * 自动补全
     * @param prefix
     * @return
     */
    @GetMapping("/suggestion")
    public List<String> getSuggestion(@RequestParam("key") String prefix){
        try {
            //准备request
            SearchRequest request = new SearchRequest("item");
            //准备DSL
            request.source().suggest(new SuggestBuilder().addSuggestion(
                    "suggestions",
                    SuggestBuilders.completionSuggestion("suggestion")
                            .prefix(prefix)
                            .skipDuplicates(true)
                            .size(10)
            ));
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);

            //解析结果
            Suggest suggest = response.getSuggest();
            CompletionSuggestion suggestions = suggest.getSuggestion("suggestions");
            ArrayList<String> list = new ArrayList<String>(suggestions.getOptions().size());

            suggestions.getOptions().forEach(option -> {
                String text = option.getText().string();
                list.add(text);
            });
            return list;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 多条件聚合
     * @param params
     * @return
     */
    @PostMapping("/filters")
    public Map<String,List<String>> getFiltes(@RequestBody RequestParams params){
        try {
            //准备request
            SearchRequest request = new SearchRequest("item");
            bulidBasicQuery(params, request);
            //准备DSL
            request.source().size(0);
            buildAggregation(request);
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);


            //解析结果

            Map<String, List<String>> result=new HashMap<>(2);

            /*Aggregations aggregations = response.getAggregations();
            //根据名称获取品牌结果
            Aggregation brandAgg = aggregations.get("brandAgg");
            ArrayList<String> branList = new ArrayList<String>();*/

            ArrayList<String> brandList = getAggName(response,"brandAgg");
            result.put("brand",brandList);
            //根据名称获取分类结果
            ArrayList<String> categoryList = getAggName(response,"categoryAgg");
            result.put("category",categoryList);

            return result;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 基本搜索
     * @param params
     * @return
     */
    @PostMapping("/list")
    public PageDTO<ItemDoc> queryList(@RequestBody RequestParams params){
        try {
            // 1.准备Request
            SearchRequest request = new SearchRequest("item");

            bulidBasicQuery(params, request);
            // 2.2.分页
            int page = params.getPage();
            int size = params.getSize();
            request.source().from((page - 1) * size).size(size);

            //排序
            request.source().sort("price", SortOrder.ASC);

            // 3.发送请求
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            // 4.解析响应

            return handleResponse(response);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private PageDTO<ItemDoc> handleResponse(SearchResponse response) {
        SearchHits searchHits = response.getHits();
        // 4.1.总条数
        long total = searchHits.getTotalHits().value;
        // 4.2.获取文档数组
        SearchHit[] hits = searchHits.getHits();
        // 4.3.遍历
        List<ItemDoc> items = new ArrayList<>(hits.length);
        for (SearchHit hit : hits) {
            // 4.4.获取source
            String json = hit.getSourceAsString();

            // 4.5.反序列化，非高亮的
            ItemDoc itemDoc = JSON.parseObject(json, ItemDoc.class);
//            items.add(itemDoc);
            // 4.6.处理高亮结果
            // 1)获取高亮map
            Map<String, HighlightField> map = hit.getHighlightFields();
            if (map != null && !map.isEmpty()) {
                // 2）根据字段名，获取高亮结果
                HighlightField highlightField = map.get("name");
                if (highlightField != null) {
                    // 3）获取高亮结果字符串数组中的第1个元素
                    String hName = highlightField.getFragments()[0].toString();
                    // 4）把高亮结果放到HotelDoc中
                    itemDoc.setName(hName);
                }
            }

            // 4.9.放入集合
            items.add(itemDoc);
        }
        return new PageDTO<>(total, items);
    }


    private void buildAggregation(SearchRequest request) {
        request.source().aggregation(AggregationBuilders
                .terms("brandAgg")
                .field("brand")
                .size(20)
        );
        request.source().aggregation(AggregationBuilders
                .terms("categoryAgg")
                .field("category")
                .size(20)
        );
    }

    private void bulidBasicQuery(RequestParams params, SearchRequest request) {
        // 1.准备Bool查询
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        // 1.1.关键字搜索，match查询，放到must中
        String key = params.getKey();
        if (StringUtils.isNotBlank(key)) {
            // 不为空，根据关键字查询
            boolQuery.must(QueryBuilders.matchQuery("all", key));
        } else {
            // 为空，查询所有
            boolQuery.must(QueryBuilders.matchAllQuery());
        }

        // 1.2.品牌
        String brand = params.getBrand(); //keyword类型，不分词
        if (StringUtils.isNotBlank(brand)) {
            boolQuery.filter(QueryBuilders.termQuery("brand", brand));
        }
        // 1.2.分类
        String category = params.getCategory(); //keyword类型，不分词
        if (StringUtils.isNotBlank(category)) {
            boolQuery.filter(QueryBuilders.termQuery("category", category));
        }
        // 1.5.价格范围
        Long minPrice = params.getMinPrice();
        Long maxPrice = params.getMaxPrice();
        if (minPrice != null && maxPrice != null) {
            //如果最高价格小于等于0，则使用Integer最大值
            maxPrice = maxPrice <= 0 ? Integer.MAX_VALUE : maxPrice;
            boolQuery.filter(
                    QueryBuilders.rangeQuery("price").gte(minPrice).lte(maxPrice));
        }

         //2.算分函数查询
        FunctionScoreQueryBuilder functionScoreQuery = QueryBuilders.functionScoreQuery(
                boolQuery,  // 原始的boolQuery条件
                new FunctionScoreQueryBuilder.FilterFunctionBuilder[]{
                        // function数组
                        new FunctionScoreQueryBuilder.FilterFunctionBuilder(
                                QueryBuilders.termQuery("isAD", true), // 过滤条件
                                ScoreFunctionBuilders.weightFactorFunction(10)  // 算分函数
                        )
                }
        );

        // 3.设置查询条件
        request.source().query(boolQuery);
    }

    private ArrayList<String> getAggName(SearchResponse response , String aggName) {
        Aggregations aggregations = response.getAggregations();
        Terms brandTerms = aggregations.get(aggName);
        ArrayList<String> brandList = new ArrayList<String>();
        brandTerms.getBuckets().forEach(bucket -> {
            String key = bucket.getKeyAsString();
            brandList.add(key);
        });
        return brandList;
    }


}
