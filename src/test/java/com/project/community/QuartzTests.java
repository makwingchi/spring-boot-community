package com.project.community;

import org.junit.jupiter.api.Test;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author makwingchi
 * @Description
 * @create 2020-04-21 22:11
 */
@SpringBootTest
class QuartzTests {

    @Autowired
    private Scheduler scheduler;

    @Test
    void testDeleteJob() throws SchedulerException {
        boolean result = scheduler.deleteJob(new JobKey("alphaJob", "alphaJobGroup"));
        System.out.println(result);

    }

}
