package com.project.community;

import com.project.community.service.AlphaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author makwingchi
 * @Description
 * @create 2020-04-17 16:57
 */
@SpringBootTest
public class TxTests {

    @Autowired
    private AlphaService alphaService;

    @Test
    public void testSave1() {
        Object obj = alphaService.save1();
        System.out.println(obj);
    }

    @Test
    public void testSave2() {
        Object obj = alphaService.save2();
        System.out.println(obj);
    }

}
