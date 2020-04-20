package com.project.community;

import com.project.community.dao.DiscussPostMapper;
import com.project.community.dao.elasticsearch.DiscussPostRepository;
import com.project.community.entity.DiscussPost;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author makwingchi
 * @Description
 * @create 2020-04-19 23:29
 */
@SpringBootTest
class EsTests {

    @Autowired
    private DiscussPostMapper mapper;

    @Autowired
    private DiscussPostRepository repo;

    @Autowired
    private ElasticsearchTemplate template;

    @Test
    void testSave() {
        repo.save(mapper.selectDiscussPostById(280));
        repo.save(mapper.selectDiscussPostById(275));
        repo.save(mapper.selectDiscussPostById(245));
    }

    @Test
    void testSaveAll() {
        repo.saveAll(mapper.selectDiscussPosts(101, 0, 100));
        repo.saveAll(mapper.selectDiscussPosts(102, 0, 100));
        repo.saveAll(mapper.selectDiscussPosts(103, 0, 100));
        repo.saveAll(mapper.selectDiscussPosts(111, 0, 100));
        repo.saveAll(mapper.selectDiscussPosts(112, 0, 100));
        repo.saveAll(mapper.selectDiscussPosts(131, 0, 100));
        repo.saveAll(mapper.selectDiscussPosts(132, 0, 100));
        repo.saveAll(mapper.selectDiscussPosts(133, 0, 100));
        repo.saveAll(mapper.selectDiscussPosts(134, 0, 100));
    }

    @Test
    void testUpdate() {
        DiscussPost post = mapper.selectDiscussPostById(231);
        post.setContent("newbee");
        repo.save(post);
    }

    @Test
    void testDelete() {
        repo.deleteById(231);
    }

    @Test
    void testSearchByRepo() {
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery("互联网寒冬", "title", "content"))
                .withSort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .withPageable(PageRequest.of(0, 10))
                .withHighlightFields(
                        new HighlightBuilder.Field("title").preTags("<em>").postTags("<em>"),
                        new HighlightBuilder.Field("content").preTags("<em>").postTags("<em>")
                ).build();

        Page<DiscussPost> page = repo.search(searchQuery);
        System.out.println(page.getTotalElements());
        System.out.println(page.getTotalPages());
        System.out.println(page.getNumber());
        System.out.println(page.getSize());

        for (DiscussPost post : page) {
            System.out.println(post);
        }
    }

    @Test
    void testSearchByTemplate() {
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery("互联网寒冬", "title", "content"))
                .withSort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .withPageable(PageRequest.of(0, 10))
                .withHighlightFields(
                        new HighlightBuilder.Field("title").preTags("<em>").postTags("<em>"),
                        new HighlightBuilder.Field("content").preTags("<em>").postTags("<em>")
                ).build();

        Page<DiscussPost> page = template.queryForPage(searchQuery, DiscussPost.class, new SearchResultMapper() {
            @Override
            public <T> AggregatedPageImpl mapResults(SearchResponse searchResponse, Class<T> aClass, Pageable pageable) {
                SearchHits hits = searchResponse.getHits();
                if (hits.getTotalHits() <= 0) {
                    return null;
                }
                List<DiscussPost> list = new ArrayList<>();
                for (SearchHit hit : hits) {
                    DiscussPost post = new DiscussPost();

                    String id = hit.getSourceAsMap().get("id").toString();
                    post.setId(Integer.parseInt(id));

                    String userId = hit.getSourceAsMap().get("userId").toString();
                    post.setUserId(Integer.parseInt(userId));

                    String title = hit.getSourceAsMap().get("title").toString();
                    post.setTitle(title);

                    String content = hit.getSourceAsMap().get("content").toString();
                    post.setContent(content);

                    String status = hit.getSourceAsMap().get("status").toString();
                    post.setStatus(Integer.parseInt(status));

                    String createTime = hit.getSourceAsMap().get("createTime").toString();
                    post.setCreateTime(new Date(Long.parseLong(createTime)));

                    String commentCount = hit.getSourceAsMap().get("commentCount").toString();
                    post.setCommentCount(Integer.parseInt(commentCount));

                    // highlight results
                    HighlightField titleField = hit.getHighlightFields().get("title");
                    if (titleField != null) {
                        post.setTitle(titleField.getFragments()[0].toString());
                    }
                    HighlightField contentField = hit.getHighlightFields().get("content");
                    if (contentField != null) {
                        post.setContent(contentField.getFragments()[0].toString());
                    }

                    list.add(post);
                }
                return new AggregatedPageImpl(list, pageable,
                        hits.getTotalHits(), searchResponse.getAggregations(), searchResponse.getScrollId(), hits.getMaxScore());
            }

            @Override
            public <T> T mapSearchHit(SearchHit searchHit, Class<T> aClass) {
                return null;
            }
        });

        System.out.println(page.getTotalElements());
        System.out.println(page.getTotalPages());
        System.out.println(page.getNumber());
        System.out.println(page.getSize());

        for (DiscussPost post : page) {
            System.out.println(post);
        }
    }

}
