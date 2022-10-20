package tk.tommy.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import tk.tommy.topic.TestTopics;

@Service
public class Producer {
    private static final Logger logger = LoggerFactory.getLogger(Producer.class);
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendMessage(final String message) {
        System.out.println("[LOG]" + String.format("$$ -> Producing message --> %s", message));
        this.kafkaTemplate.send(TestTopics.USERS, message);
    }
}