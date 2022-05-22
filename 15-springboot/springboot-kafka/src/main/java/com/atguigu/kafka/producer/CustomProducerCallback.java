package com.atguigu.kafka.producer;

import java.util.Properties;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;

public class CustomProducerCallback {

    public static void main(String[] args) throws InterruptedException {

        // 0 配置
        Properties properties = new Properties();

        // 连接集群 bootstrap.servers
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");

        // 指定对应的key和value的序列化类型 key.serializer
        // properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.StringSerializer");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        // 1 创建kafka生产者对象
        // "" hello
        KafkaProducer<String, String> kafkaProducer = new KafkaProducer<>(properties);

        // 2 发送数据
        for (int i = 0; i < 30; i++) {
            kafkaProducer.send(new ProducerRecord<>("first", "atguigu" + i), new Callback() {
                @Override
                public void onCompletion(RecordMetadata metadata, Exception exception) {

                    if (exception == null) {
                        System.out.println("主题： " + metadata.topic() + " 分区： " + metadata.partition());
                    }
                }
            });

            Thread.sleep(2);
        }

        // 3 关闭资源
        kafkaProducer.close();
    }
}
