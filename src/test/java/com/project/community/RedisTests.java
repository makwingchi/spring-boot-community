package com.project.community;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.*;

import java.util.concurrent.TimeUnit;

/**
 * @author makwingchi
 * @Description
 * @create 2020-04-18 17:07
 */
@SpringBootTest
class RedisTests {

    @Qualifier("redisTemplate")
    @Autowired
    private RedisTemplate template;

    @Test
    void testStrings() {
        String redisKey = "test:count";

        template.opsForValue().set(redisKey, 1);
        System.out.println(template.opsForValue().get(redisKey));
        System.out.println(template.opsForValue().decrement(redisKey));
    }

    @Test
    void testHashes() {
        String redisKey = "test:user";

        template.opsForHash().put(redisKey, "id", 1);
        template.opsForHash().put(redisKey, "username", "tom");

        System.out.println(template.opsForHash().get(redisKey, "id"));
        System.out.println(template.opsForHash().get(redisKey, "username"));
    }

    @Test
    void testLists() {
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
    void testSets() {
        String redisKey = "test:teachers";

        template.opsForSet().add(redisKey, "tom", "and", "jerry");
        System.out.println(template.opsForSet().size(redisKey));
        System.out.println(template.opsForSet().pop(redisKey));
        System.out.println(template.opsForSet().members(redisKey));
    }

    @Test
    void testSortedSets() {
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
    void testKeys() {
        template.delete("test:user");
        System.out.println(template.hasKey("test:user"));

        template.expire("test:students", 10, TimeUnit.SECONDS);
    }

    // access the same key
    @Test
    void testBoundOperations() {
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
    void testTransaction() {
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

    // unique values of 200K numbers
    @Test
    void testHyperLogLog() {
        String redisKey = "test:hll:01";
        for (int i = 1; i <= 100000; i++) {
            template.opsForHyperLogLog().add(redisKey, i);
        }

        for (int i = 1; i <= 100000; i++) {
            int r = (int) Math.random() * 100000 + 1;
            template.opsForHyperLogLog().add(redisKey, r);
        }

        Long size = template.opsForHyperLogLog().size(redisKey);
        System.out.println(size);
    }

    @Test
    void testHyperLogLogUnion() {
        String redisKey1 = "test:hll:02";
        for (int i = 1; i <= 10000; i++) {
            template.opsForHyperLogLog().add(redisKey1, i);
        }

        String redisKey2 = "test:hll:02";
        for (int i = 5001; i <= 15000; i++) {
            template.opsForHyperLogLog().add(redisKey2, i);
        }

        String redisKey3 = "test:hll:02";
        for (int i = 10001; i <= 20000; i++) {
            template.opsForHyperLogLog().add(redisKey3, i);
        }

        String unionKey = "test:hll:union";
        template.opsForHyperLogLog().union(unionKey, redisKey1, redisKey2, redisKey3);

        Long size = template.opsForHyperLogLog().size(unionKey);
        System.out.println(size);
    }

    @Test
    void testBitMap() {
        String redisKey = "test:bm:01";

        template.opsForValue().setBit(redisKey, 1, true);
        template.opsForValue().setBit(redisKey, 4, true);
        template.opsForValue().setBit(redisKey, 7, true);

        System.out.println(template.opsForValue().getBit(redisKey, 0));
        System.out.println(template.opsForValue().getBit(redisKey, 1));
        System.out.println(template.opsForValue().getBit(redisKey, 2));

        Object obj = template.execute((RedisCallback) conn -> conn.bitCount(redisKey.getBytes()));
        System.out.println(obj);
    }

    @Test
    void testOr() {
        String redisKey2 = "test:bm:02";
        template.opsForValue().setBit(redisKey2, 0, true);
        template.opsForValue().setBit(redisKey2, 1, true);
        template.opsForValue().setBit(redisKey2, 2, true);

        String redisKey3 = "test:bm:03";
        template.opsForValue().setBit(redisKey3, 2, true);
        template.opsForValue().setBit(redisKey3, 3, true);
        template.opsForValue().setBit(redisKey3, 4, true);

        String redisKey4 = "test:bm:04";
        template.opsForValue().setBit(redisKey4, 4, true);
        template.opsForValue().setBit(redisKey4, 5, true);
        template.opsForValue().setBit(redisKey4, 6, true);

        String redisKey = "test:bm:or";
        Object obj = template.execute((RedisCallback) conn -> {
            conn.bitOp(RedisStringCommands.BitOperation.OR,
                    redisKey.getBytes(), redisKey2.getBytes(),
                    redisKey3.getBytes(), redisKey4.getBytes());
            return conn.bitCount(redisKey.getBytes());
        });

        System.out.println(obj);

    }

}
