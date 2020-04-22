package com.project.community;

import com.project.community.service.AlphaService;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import javax.xml.crypto.Data;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author makwingchi
 * @Description
 * @create 2020-04-21 20:15
 */
@SpringBootTest
class ThreadPoolTests {

    private static final Logger logger = LoggerFactory.getLogger(ThreadPoolTests.class);

    // JDK ThreadPool
    private ExecutorService executorService = Executors.newFixedThreadPool(5);

    // JDK Scheduled ThreadPool
    private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(5);

    // Spring ThreadPool
    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    // Spring Scheduled ThreadPool
    @Autowired
    private ThreadPoolTaskScheduler taskScheduler;

    @Autowired
    private AlphaService alphaService;


    private void sleep(long m) {
        try {
            Thread.sleep(m);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testExecutorService() {
        Runnable task = new Runnable() {
            @Override
            public void run() {
                logger.debug("Hello executor service");
            }
        };
        for (int i = 0; i < 10; i++) {
            executorService.submit(task);
        }
        sleep(10000);
    }

    @Test
    void testScheduledExecutorService() {
        Runnable task = new Runnable() {
            @Override
            public void run() {
                logger.debug("Hello scheduled executor service");
            }
        };

        scheduledExecutorService.scheduleAtFixedRate(task, 10000, 1000, TimeUnit.MILLISECONDS);
        sleep(30000);
    }

    @Test
    void testThreadPoolTaskExecutor() {
        Runnable task = new Runnable() {
            @Override
            public void run() {
                logger.debug("Hello thread pool task executor");
            }
        };
        for (int i = 0; i < 10; i++) {
            taskExecutor.submit(task);
        }
        sleep(10000);
    }

    @Test
    void testThreadPoolTaskScheduler() {
        Runnable task = new Runnable() {
            @Override
            public void run() {
                logger.debug("Hello thread pool task executor");
            }
        };
        Date startTime = new Date(System.currentTimeMillis() + 10000);
        taskScheduler.scheduleAtFixedRate(task, startTime, 1000);

        sleep(20000);
    }

    @Test
    void testThreadPoolTaskExecutorSimplified() {
        for (int i = 0; i < 10; i++) {
            alphaService.execute1();
        }
        sleep(10000);
    }

    @Test
    void testThreadPoolTaskSchedulerSimplified() {
        sleep(20000);
    }

}
