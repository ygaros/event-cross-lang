package edu.jakub.jureczko.storage.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Configuration
@EnableKafka
public class KafkaConfig {

    @Bean
    KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<UUID, BookingMessage>> bookContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<UUID, BookingMessage> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerBookFactory());
        factory.setConcurrency(1);
        factory.getContainerProperties().setPollTimeout(3000);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        return factory;
    }

    @Bean
    KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<UUID, BookingPaymentMessage>> bookPaymentContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<UUID, BookingPaymentMessage> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerPaymentFactory());
        factory.setConcurrency(1);
        factory.getContainerProperties().setPollTimeout(3000);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        return factory;
    }

    @Bean
    public KafkaTemplate<UUID, PaymentFinishedMessage> kafkaTemplate() {
        return new KafkaTemplate<>(producerPaymentFinishedFactory());
    }

    public ProducerFactory<UUID, PaymentFinishedMessage> producerPaymentFinishedFactory(){
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                "kafka:9092");
        configProps.put(
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class);
        configProps.put(
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }
    public ConsumerFactory<UUID, BookingMessage> consumerBookFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerBookConfigs());
    }

    public ConsumerFactory<UUID, BookingPaymentMessage> consumerPaymentFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerBookPaymentConfigs());
    }

    public Map<String, Object> consumerBookConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka:9092");
        props.put(
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class);
        props.put(
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                JsonDeserializer.class);
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, BookingMessage.class);
        props.put(JsonDeserializer.TYPE_MAPPINGS,
                "products:edu.jakub.jureczko.storage.model.Product[], customer:edu.jakub.jureczko.storage.model.Customer, address:edu.jakub.jureczko.storage.model.Address"
        );
        return props;
    }

    public Map<String, Object> consumerBookPaymentConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka:9092");
        props.put(
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class);
        props.put(
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                JsonDeserializer.class);
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, BookingPaymentMessage.class);
//        props.put(JsonDeserializer.TYPE_MAPPINGS,
//                "products:edu.jakub.jureczko.storage.model.Product[], customer:edu.jakub.jureczko.storage.model.Customer, address:edu.jakub.jureczko.storage.model.Address"
//        );
        return props;
    }
}