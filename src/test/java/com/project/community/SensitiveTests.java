package com.project.community;

import com.project.community.util.SensitiveFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author makwingchi
 * @Description
 * @create 2020-04-17 12:15
 */
@SpringBootTest
public class SensitiveTests {

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Test
    public void testSensitiveFilter() {
        String text = "这里可以赌博, 可以嫖娼, 可以吸毒, 可以开票!";
        text = sensitiveFilter.filter(text);
        System.out.println(text);
    }

}
