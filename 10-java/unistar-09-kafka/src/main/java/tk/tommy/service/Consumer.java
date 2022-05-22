package tk.tommy.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import tk.tommy.topic.TestTopics;

@Service
public class Consumer {
    private final Logger logger = LoggerFactory.getLogger(Consumer.class);

    @KafkaListener(topics = TestTopics.USERS, groupId = "group_id")
    public void consume(final String message) {
        System.out.println("[LOG]" + String.format("$$ -> Consumed message --> %s", message));
    }
}