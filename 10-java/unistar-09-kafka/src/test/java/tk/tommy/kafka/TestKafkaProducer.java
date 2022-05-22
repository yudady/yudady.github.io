package tk.tommy.kafka;

import java.time.Duration;
import java.util.Properties;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;
import tk.tommy.topic.TestTopics;

public class TestKafkaProducer {

    @Test
    public void test01() throws InterruptedException {
        Properties props = new Properties();
        // bootstrap.servers 配置项处需要填写我们要发送到的Kafka集群地址。
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        // ack 配置项用来控制producer要求leader确认多少消息后返回调用成功。
        // 当值为0时producer不需要等待任何确认消息。当值为1时只需要等待leader确认。
        // 当值为-1或all时需要全部ISR集合返回确认才可以返回成功。
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        // 当 retries > 0 时，如果发送失败，会自动尝试重新发送数据。发送次数为retries设置的值。
        props.put("retries", 3);
        // buffer.memory、batch.size、linger.ms三个参数用来控制缓冲区大小和延迟发送时间，具体含义可以参考官方文档的配置。
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        // key.serializer 和 value.serializer 指定使用什么序列化方式将用户提供的key和value进行序列化。
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");


        // Producer由一个持有未发送消息记录的资源池和一个用来向Kafka集群发送消息记录的后台IO线程组成。
        // 使用后未关闭producer将导致这些资源泄露。

        Producer<String, String> producer = new KafkaProducer<>(props);
        for (int i = 0; i < 1000; i++) {
            // send方法是异步的。当它被调用时，它会将消息记录添加到待发送缓冲区并立即返回。
            // 使用这种方式可以使生产者聚集一批消息记录后一起发送，从而提高效率。
            producer.send(new ProducerRecord<String, String>(TestTopics.TEST02, "key-" + i, "value-" + i));
            System.out.println("[LOG]" + TestTopics.TEST02 + " " + "key-" + i);
            Thread.sleep(Duration.ofSeconds(1).toMillis());
        }


        producer.close();

    }

}

