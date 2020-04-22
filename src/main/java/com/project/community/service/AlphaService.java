package com.project.community.service;

import com.project.community.dao.AlphaDao;
import com.project.community.dao.DiscussPostMapper;
import com.project.community.dao.UserMapper;
import com.project.community.entity.DiscussPost;
import com.project.community.entity.User;
import com.project.community.util.CommunityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Date;

/**
 * @author makwingchi
 * @Description
 * @create 2020-04-14 18:52
 */
@Service
//@Scope("prototype")
public class AlphaService {

    @Autowired
    private AlphaDao alphaDao;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private TransactionTemplate template;

    private static final Logger logger = LoggerFactory.getLogger(AlphaService.class);

    public AlphaService() {
        // System.out.println("instantiation...");
    }

    public String find() {
        return alphaDao.select();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    // REQUIRED: 支持当前事务(外部事务), 如果不存在则创建新事务
    // REQUIRES_NEW: 创建一个新事务, 并且暂停当前事务
    // NESTED: 如果当前存在事务(外部事务), 则嵌套在该事务中执行(独立的提交和回滚), 否则和REQUIRED一样
    public Object save1() {
        User user = new User();
        user.setUsername("alpha");
        user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
        user.setPassword(CommunityUtil.md5("123456" + user.getSalt()));
        user.setEmail("alpha@gmail.com");
        user.setHeaderUrl("http://image.nowcoder.com/head/99t.png");
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle("haha");
        post.setContent("hehe");
        post.setCreateTime(new Date());
        discussPostMapper.insertDiscussPost(post);

        Integer.valueOf("abc");

        return "ok";
    }

    public Object save2() {
        template.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        template.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        return template.execute(new TransactionCallback<Object>() {
            @Override
            public Object doInTransaction(TransactionStatus transactionStatus) {
                User user = new User();
                user.setUsername("beta");
                user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
                user.setPassword(CommunityUtil.md5("123456" + user.getSalt()));
                user.setEmail("beta@gmail.com");
                user.setHeaderUrl("http://image.nowcoder.com/head/999t.png");
                user.setCreateTime(new Date());
                userMapper.insertUser(user);

                DiscussPost post = new DiscussPost();
                post.setUserId(user.getId());
                post.setTitle("nihao");
                post.setContent("hello");
                post.setCreateTime(new Date());
                discussPostMapper.insertDiscussPost(post);

                Integer.valueOf("abc");

                return "ok";
            }
        });
    }

    @PostConstruct
    public void init() {
        // System.out.println("initialization...");
    }

    @PreDestroy
    public void destroy() {
        // System.out.println("destroy...");
    }

    // 让该方法在多线程环境下, 被异步地调用
    // @Async
    public void execute1() {
        // logger.debug("execute!!!!!");
    }

    // @Scheduled(initialDelay = 10000, fixedRate = 1000)
    public void execute2() {
        // logger.debug("execute22222");
    }

}
