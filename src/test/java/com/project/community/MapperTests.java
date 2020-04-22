package com.project.community;

import com.project.community.dao.DiscussPostMapper;
import com.project.community.dao.LoginTicketMapper;
import com.project.community.dao.MessageMapper;
import com.project.community.dao.UserMapper;
import com.project.community.entity.DiscussPost;
import com.project.community.entity.LoginTicket;
import com.project.community.entity.Message;
import com.project.community.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.List;

/**
 * @author makwingchi
 * @Description
 * @create 2020-04-15 01:06
 */
@SpringBootTest
public class MapperTests {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Autowired
    private MessageMapper messageMapper;

    @Test
    public void testSelect() {
        User user = userMapper.selectById(101);
        System.out.println(user);

        user = userMapper.selectByName("liubei");
        System.out.println(user);

        user = userMapper.selectByEmail("nowcoder101@sina.com");
        System.out.println(user);
    }

    @Test
    public void testInsertUser() {
        User user = new User();
        user.setUsername("test");
        user.setPassword("123456");
        user.setSalt("abc");
        user.setEmail("test@qq.com");
        user.setHeaderUrl("http://www.nowcoder.com/101.png");
        user.setCreateTime(new Date());

        int rows = userMapper.insertUser(user);
        System.out.println(rows);
        System.out.println(user.getId());
    }

    @Test
    public void testUpdateUser() {
        userMapper.updateStatus(150, 1);
        userMapper.updateHeader(150, "http://www.nowcoder.com/246.png");
        userMapper.updatePassword(150, "hello");
    }

    @Test
    public void testSelectPosts() {
        List<DiscussPost> posts = discussPostMapper.selectDiscussPosts(0, 0, 10, 0);
        for (DiscussPost post : posts) {
            System.out.println(post);
        }

        int rows = discussPostMapper.selectDiscussPostRows(149);
        System.out.println(rows);
    }

    @Test
    public void testInsertLoginTicket() {
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(101);
        loginTicket.setTicket("abc");
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + 1000 * 60 * 10));

        loginTicketMapper.insertLoginTicket(loginTicket);
    }

    @Test
    public void testSelectLoginTicket() {
        LoginTicket ticket = loginTicketMapper.selectByTicket("abc");
        System.out.println(ticket);

        loginTicketMapper.updateStatus("abc", 1);
        ticket = loginTicketMapper.selectByTicket("abc");
        System.out.println(ticket);
    }

    @Test
    public void testSelectLetters() {
        List<Message> messages = messageMapper.selectConversations(111, 0, 20);
        for(Message message : messages) {
            System.out.println(message);
        }

        int count = messageMapper.selectConversationCount(111);
        System.out.println(count);

        List<Message> letters = messageMapper.selectLetter("111_112", 0, 10);
        for(Message letter : letters) {
            System.out.println(letter);
        }

        int letterCount = messageMapper.selectLetterCount("111_112");
        System.out.println(letterCount);

        int unreadCount = messageMapper.selectLetterUnreadCount(131, "111_131");
        System.out.println(unreadCount);
    }

}
