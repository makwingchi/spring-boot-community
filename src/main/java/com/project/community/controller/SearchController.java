package com.project.community.controller;

import com.project.community.entity.DiscussPost;
import com.project.community.entity.Page;
import com.project.community.service.ElasticsearchService;
import com.project.community.service.LikeService;
import com.project.community.service.UserService;
import com.project.community.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author makwingchi
 * @Description
 * @create 2020-04-20 14:49
 */
@Controller
public class SearchController implements CommunityConstant {

    @Autowired
    private ElasticsearchService elasticsearchService;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    @GetMapping("/search")
    // .../search?keyword=xxx
    public String search(String keyword, Page page, Model model) {
        // search for posts
        org.springframework.data.domain.Page<DiscussPost> searchResult
                = elasticsearchService.searchDiscussPost(keyword, page.getCurrent() - 1, page.getLimit());

        // organize data
        List<Map<String, Object>> discussPosts = new ArrayList<>();
        if (searchResult != null) {
            for (DiscussPost post : searchResult) {
                Map<String, Object> map = new HashMap<>();
                map.put("post", post);
                map.put("user", userService.findUserById(post.getUserId()));
                map.put("likeCount", likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId()));
                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts", discussPosts);
        model.addAttribute("keyword", keyword);

        // pagination
        page.setPath("/search?keyword=" + keyword);
        page.setRows(searchResult == null ? 0 : (int) searchResult.getTotalElements());

        return "/site/search";
    }

}
