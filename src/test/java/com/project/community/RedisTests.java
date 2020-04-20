package com.project.community;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;

import java.util.concurrent.TimeUnit;

/**
 * @author makwingchi
 * @Description
 * @create 2020-04-18 17:07
 */
@SpringBootTest
public class RedisTests {

    @Qualifier("redisTemplate")
    @Autowired
    private RedisTemplate template;

    @Test
    public void testStrings() {
        String redisKey = "test:count";

        template.opsForValue().set(redisKey, 1);
        System.out.println(template.opsForValue().get(redisKey));
        System.out.println(template.opsForValue().decrement(redisKey));
    }

    @Test
    public void testHashes() {
        String redisKey = "test:user";

        template.opsForHash().put(redisKey, "id", 1);
        template.opsForHash().put(redisKey, "username", "tom");

        System.out.println(template.opsForHash().get(redisKey, "id"));
        System.out.println(template.opsForHash().get(redisKey, "username"));
    }

    @Test
    public void testLists() {
        String redisKey = "test:ids";

        template.opsForList().leftPush(redisKey, 101);
        template.opsForList().leftPush(redisKey, 102);
        template.opsForList().leftPush(redisKey, 103);

        System.out.println(template.opsForList().size(redisKey));
        System.out.println(template.opsForList().index(redisKey, 0));
        System.out.println(template.opsForList().range(redisKey, 0, 2));
        System.out.println(template.opsForList().leftPop(redisKey));
    }

    @Test
    public void testSets() {
        String redisKey = "test:teachers";

        template.opsForSet().add(redisKey, "tom", "and", "jerry");
        System.out.println(template.opsForSet().size(redisKey));
        System.out.println(template.opsForSet().pop(redisKey));
        System.out.println(template.opsForSet().members(redisKey));
    }

    @Test
    public void testSortedSets() {
        String redisKey = "test:students";

        template.opsForZSet().add(redisKey, "tom", 1.0);
        template.opsForZSet().add(redisKey, "and", 1.1);
        template.opsForZSet().add(redisKey, "jerry", 1.2);

        System.out.println(template.opsForZSet().zCard(redisKey));
        System.out.println(template.opsForZSet().score(redisKey, "tom"));
        System.out.println(template.opsForZSet().reverseRank(redisKey, "and"));
        System.out.println(template.opsForZSet().reverseRange(redisKey, 0, 2));
    }

    @Test
    public void testKeys() {
        template.delete("test:user");
        System.out.println(template.hasKey("test:user"));

        template.expire("test:students", 10, TimeUnit.SECONDS);
    }

    // access the same key
    @Test
    public void testBoundOperations() {
        String redisKey = "test:count";
        BoundValueOperations operations = template.boundValueOps(redisKey);
        operations.increment();
        operations.increment();
        operations.increment();
        operations.increment();
        operations.increment();

        System.out.println(operations.get());
    }

    // Programmatic Tx
    @Test
    public void testTransaction() {
        Object object = template.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                String redisKey = "test:tx";
                redisOperations.multi();

                redisOperations.opsForSet().add(redisKey, "haha");
                redisOperations.opsForSet().add(redisKey, "ahah");
                redisOperations.opsForSet().add(redisKey, "hhaa");

                System.out.println(redisOperations.opsForSet().members(redisKey)); // []

                return redisOperations.exec();
            }
        });

        System.out.println(object);
    }

}
