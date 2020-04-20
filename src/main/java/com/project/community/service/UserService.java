package com.project.community.service;

import com.project.community.dao.LoginTicketMapper;
import com.project.community.dao.UserMapper;
import com.project.community.entity.LoginTicket;
import com.project.community.entity.User;
import com.project.community.util.CommunityConstant;
import com.project.community.util.CommunityUtil;
import com.project.community.util.MailClient;
import com.project.community.util.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author makwingchi
 * @Description
 * @create 2020-04-15 13:10
 */
@Service
public class UserService implements CommunityConstant {

    @Autowired
    private UserMapper userMapper;

//    @Autowired
//    private LoginTicketMapper loginTicketMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    public User findUserById(int id) {
        // return userMapper.selectById(id);
        User user = this.getCache(id);
        if (user == null) {
            user = initCache(id);
        }
        return user;
    }

    public Map<String, Object> register(User user) {
        Map<String, Object> map = new HashMap<>();

        // null check
        if (user == null) {
            throw new IllegalArgumentException("user cannot be null!");
        }
        if (StringUtils.isBlank(user.getUsername())) {
            map.put("usernameMsg", "username cannot be null.");
            return map;
        }
        if (StringUtils.isBlank(user.getPassword())) {
            map.put("passwordMsg", "password cannot be null.");
            return map;
        }
        if (StringUtils.isBlank(user.getEmail())) {
            map.put("emailMsg", "email cannot be null.");
            return map;
        }

        // username exists?
        User u = userMapper.selectByName(user.getUsername());
        if (u != null) {
            map.put("usernameMsg", "username already exists.");
            return map;
        }

        //email exists?
        u = userMapper.selectByEmail(user.getEmail());
        if (u != null) {
            map.put("emailMsg", "email has already been used.");
            return map;
        }

        // edit user attributes
        user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
        user.setPassword(CommunityUtil.md5(user.getPassword() + user.getSalt()));
        user.setType(0);
        user.setStatus(0);
        user.setActivationCode(CommunityUtil.generateUUID().substring(0, 10));
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        user.setCreateTime(new Date());

        // add user to database
        userMapper.insertUser(user);

        // send activation email
        Context context = new Context();
        context.setVariable("email", user.getEmail());
        String url = domain + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("url", url);

        String content = templateEngine.process("/mail/activation", context);
        mailClient.sendMail(user.getEmail(), "Please activate your account", content);

        return map;
    }

    public int activation(int userId, String code) {
        User user = userMapper.selectById(userId);
        if (user.getStatus()==1) {
            return ACTIVATION_REPEAT;
        } else if (user.getActivationCode().equals(code)) {
            userMapper.updateStatus(userId, 1);
            this.clearCache(userId);
            return ACTIVATION_SUCCESS;
        } else {
            return ACTIVATION_FAILURE;
        }
    }

    public Map<String, Object> login(String username, String password, int expiredSeconds) {
        Map<String, Object> map = new HashMap<>();
        // null check
        if (StringUtils.isBlank(username)) {
            map.put("usernameMsg", "username cannot be blank");
            return map;
        }
        if (StringUtils.isBlank(password)) {
            map.put("passwordMsg", "password cannot be blank");
            return map;
        }
        // check username
        User user = userMapper.selectByName(username);
        if (user == null) {
            map.put("usernameMsg", "user does not exist");
            return map;
        }
        // check user status
        if (user.getStatus() == 0) {
            map.put("usernameMsg", "user has not been activated");
        }
        // check password
        String encryptedPassword = CommunityUtil.md5(password + user.getSalt());
        if (!user.getPassword().equals(encryptedPassword)) {
            map.put("passwordMsg", "wrong password");
            return map;
        }
        // generate ticket
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000));

        // save ticket
        // loginTicketMapper.insertLoginTicket(loginTicket);
        String redisKey = RedisKeyUtil.getTicketKey(loginTicket.getTicket());
        redisTemplate.opsForValue().set(redisKey, loginTicket);

        map.put("ticket", loginTicket.getTicket());
        return map;
    }

    public void logout(String ticket) {
        // loginTicketMapper.updateStatus(ticket, 1);
        String redisKey = RedisKeyUtil.getTicketKey(ticket);
        LoginTicket loginTicket = (LoginTicket) redisTemplate.opsForValue().get(redisKey);
        loginTicket.setStatus(1);
        redisTemplate.opsForValue().set(redisKey, loginTicket);
    }

    public LoginTicket findLoginTicket(String ticket) {
        //return loginTicketMapper.selectByTicket(ticket);
        String redisKey = RedisKeyUtil.getTicketKey(ticket);
        return (LoginTicket) redisTemplate.opsForValue().get(redisKey);
    }

    public int updateHeader(int userId, String headerUrl) {
        // return userMapper.updateHeader(userId, headerUrl);
        int rows = userMapper.updateHeader(userId, headerUrl);
        this.clearCache(userId);
        return rows;
    }

    public int updatePassword(int userId, String newPassword) {
        return userMapper.updatePassword(userId, newPassword);
    }

    public User findUserByName(String username) {
        return userMapper.selectByName(username);
    }

    // get value from redis cache
    private User getCache(int userId) {
        String redisKey = RedisKeyUtil.getUserKey(userId);
        return (User) redisTemplate.opsForValue().get(redisKey);
    }

    // if not in cache, initialize cache data
    private User initCache(int userId) {
        User user = userMapper.selectById(userId);
        String redisKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.opsForValue().set(redisKey, user, 3600, TimeUnit.SECONDS);
        return user;
    }

    // when data changes, clear cache
    private void clearCache(int userId) {
        String redisKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.delete(redisKey);
    }
}
