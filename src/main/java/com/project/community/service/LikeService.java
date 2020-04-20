package com.project.community.service;

import com.project.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import javax.management.OperationsException;

/**
 * @author makwingchi
 * @Description
 * @create 2020-04-18 20:05
 */
@Service
public class LikeService {

    @Autowired
    private RedisTemplate<String, Object> template;


    public void like(int userId, int entityType, int entityId, int entityUserId) {
        template.execute(new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
                String userLikeKey = RedisKeyUtil.getUserLikeKey(entityUserId);

                Boolean isMember = redisOperations.opsForSet().isMember(entityLikeKey, userId);
                redisOperations.multi();
                if (isMember) {
                    redisOperations.opsForSet().remove(entityLikeKey, userId);
                    redisOperations.opsForValue().decrement(userLikeKey);
                } else {
                    redisOperations.opsForSet().add(entityLikeKey, userId);
                    redisOperations.opsForValue().increment(userLikeKey);
                }

                return redisOperations.exec();
            }
        });
    }

    public long findEntityLikeCount(int entityType, int entityId) {
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        return template.opsForSet().size(entityLikeKey);
    }

    public int findEntityLikeStatus(int userId, int entityType, int entityId) {
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        return template.opsForSet().isMember(entityLikeKey, userId) ? 1 : 0;
    }

    // query for likes of a given user
    public int findUserLikeCount(int userId) {
        String userLikeKey = RedisKeyUtil.getUserLikeKey(userId);
        Integer count = (Integer) template.opsForValue().get(userLikeKey);
        return count == null ? 0 : count.intValue();
    }

}
