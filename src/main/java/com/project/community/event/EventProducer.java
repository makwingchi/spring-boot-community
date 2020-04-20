package com.project.community.event;

import com.alibaba.fastjson.JSONObject;
import com.project.community.entity.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * @author makwingchi
 * @Description
 * @create 2020-04-19 16:35
 */
@Component
public class EventProducer {

    @Autowired
    private KafkaTemplate kafkaTemplate;

    // deal with event
    public void fireEvent(Event event) {
        // send event to given topic
        kafkaTemplate.send(event.getTopic(), JSONObject.toJSONString(event));
    }

}
