package tk.tommy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tk.tommy.service.Producer;

/**
 * http://localhost:9000/kafka/publish?message=hello
 */
@RestController
@RequestMapping(value = "/kafka")
public class KafkaController {

    @Autowired
    private Producer producer;

    @GetMapping(value = "/publish")
    public void sendMessageToKafkaTopic(@RequestParam("message") final String message) {
        System.out.println("[LOG] KafkaController.sendMessageToKafkaTopic:" + message);
        this.producer.sendMessage(message);
    }
}