package com.project.community;

import com.project.community.dao.AlphaDao;
import com.project.community.service.AlphaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.SimpleDateFormat;
import java.util.Date;

@SpringBootTest
//@ContextConfiguration(classes = CommunityApplication.class)
class CommunityApplicationTests {

    @Autowired
    @Qualifier("alphaHibernate")
    private AlphaDao alphaDao;

    @Autowired
    private AlphaService alphaService;

    @Autowired
    private SimpleDateFormat simpleDateFormat;

    @Test
    void test() {
//        System.out.println(applicationContext);
        System.out.println(alphaDao.select());
    }

    @Test
    public void testBeanManagement() {
        System.out.println(alphaService);
    }

    @Test
    public void testBeanConfig() {
        System.out.println(simpleDateFormat.format(new Date()));
    }

}
