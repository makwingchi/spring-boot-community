package com.project.community;

import com.project.community.util.MailClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

/**
 * @author makwingchi
 * @Description
 * @create 2020-04-15 21:42
 */
@SpringBootTest
public class MailTests {

    @Autowired
    private MailClient client;

    @Autowired
    private TemplateEngine engine;

    @Test
    public void testTextMail() {
        client.sendMail("makwingchi@qq.com", "test", "welcome!");
    }

    @Test
    public void testHtmlMail() {
        Context context = new Context();
        context.setVariable("username", "haha");
        String content = engine.process("/mail/demo", context);
        System.out.println(content);

//        client.sendMail("makwingchi@qq.com", "htmltest", content);
    }

}
