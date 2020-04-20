package com.project.community;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * @author makwingchi
 * @Description
 * @create 2020-04-19 15:34
 */
@SpringBootTest
public class KafkaTests {

    @Autowired
    private KafkaProducer kafkaProducer;



    @Test
    public void testKafka() throws InterruptedException {
        kafkaProducer.sendMessage("test", "hello");
        kafkaProducer.sendMessage("test", "how are you today");

        Thread.sleep(1000 * 10);
    }

}

@Component
class KafkaProducer {
    @Autowired
    private KafkaTemplate kafkaTemplate;

    public void sendMessage(String topic, String content) {
        kafkaTemplate.send(topic, content);
    }
}

@Component
class KafkaConsumer {

    @KafkaListener(topics = {"test"})
    public void handleMessage(ConsumerRecord record) {
        System.out.println(record.value());
    }

}