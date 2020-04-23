package com.project.community.quartz;

import com.project.community.entity.DiscussPost;
import com.project.community.service.DiscussPostService;
import com.project.community.service.ElasticsearchService;
import com.project.community.service.LikeService;
import com.project.community.util.CommunityConstant;
import com.project.community.util.RedisKeyUtil;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author makwingchi
 * @Description
 * @create 2020-04-22 00:46
 */
public class PostScoreRefreshJob implements Job, CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(PostScoreRefreshJob.class);
    private static final Date epoch;

    // Initialize epoch
    static {
        try {
            epoch = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2014-08-01 00:00:00");
        } catch (ParseException e) {
            throw new RuntimeException("initialize error", e);
        }
    }

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private ElasticsearchService elasticsearchService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        String redisKey = RedisKeyUtil.getPostScoreKey();
        BoundSetOperations<String, Object> operations = redisTemplate.boundSetOps(redisKey);

        if (operations.size() == 0) {
            logger.info("[MISSION CANCELLED]");
            return;
        }
        logger.info("[MISSION......] REFRESHING POST SCORES: " + operations.size());

        while (operations.size() > 0) {
            this.refresh((int) operations.pop());
        }

        logger.info("[MISSION ACCOMPLISHED......]");
    }

    private void refresh(int postId) {
        DiscussPost post = discussPostService.findDiscussPostById(postId);
        if (post == null) {
            logger.error("Post does not exist");
            return;
        }

        boolean wonderful = post.getStatus() == 1;
        int commentCount = post.getCommentCount();
        long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, postId);

        // calculate weight
        double w = (wonderful ? 75 : 0) + commentCount * 10 + likeCount * 2;
        // calculate score
        double score = Math.log10(Math.max(w, 1))
                + (post.getCreateTime().getTime() - epoch.getTime()) / (1000 * 3600 * 24);

        // update score
        discussPostService.updateScore(postId, score);
        // sync elasticsearch data
        post.setScore(score);
        elasticsearchService.saveDiscussPost(post);
    }
}
