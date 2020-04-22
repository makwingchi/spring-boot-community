package com.project.community.controller;

import com.project.community.entity.Event;
import com.project.community.entity.User;
import com.project.community.event.EventProducer;
import com.project.community.service.LikeService;
import com.project.community.util.CommunityConstant;
import com.project.community.util.CommunityUtil;
import com.project.community.util.HostHolder;
import com.project.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * @author makwingchi
 * @Description
 * @create 2020-04-18 20:15
 */
@Controller
public class LikeController implements CommunityConstant {

    @Autowired
    private LikeService likeService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private EventProducer eventProducer;

    @Autowired
    private HostHolder hostHolder;

    @PostMapping("/like")
    @ResponseBody
    public String like(int entityType, int entityId, int entityUserId, int postId) {
        User user = hostHolder.getUser();
        // like
        likeService.like(user.getId(), entityType, entityId, entityUserId);
        // like count
        long likeCount = likeService.findEntityLikeCount(entityType, entityId);
        // like status
        int likeStatus = likeService.findEntityLikeStatus(user.getId(), entityType, entityId);

        // to return
        Map<String, Object> map = new HashMap<>();
        map.put("likeCount", likeCount);
        map.put("likeStatus", likeStatus);

        // trigger like event
        if (likeStatus == 1) {
            Event event = new Event()
                    .setTopic(TOPIC_LIKE)
                    .setUserId(hostHolder.getUser().getId())
                    .setEntityType(entityType)
                    .setEntityId(entityId)
                    .setEntityUserId(entityUserId)
                    .setData("postId", postId);
            eventProducer.fireEvent(event);
        }

        if (entityType == ENTITY_TYPE_POST) {
            // calculate score
            String redisKey = RedisKeyUtil.getPostScoreKey();
            redisTemplate.opsForSet().add(redisKey, postId);
        }

        return CommunityUtil.getJsonString(0, null, map);
    }

}
