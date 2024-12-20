package com.gallery_app.core_service.mq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    @Value("${core.metadata.queue}")
    private String coreMetadataQueue;

    @Value("${core.detection.queue}")
    private String coreDetectionQueue;

    @Value("${core.thumbnail.queue}")
    private String coreThumbnailQueue;

    @Value("${broadcast.common.exchange}")
    private String broadcastCommonExchange;

    @Bean
    public Queue metadataExtractionServiceQueue() {
        return new Queue(coreMetadataQueue, true);
    }

    @Bean
    public Queue objectDetectionServiceQueue() {
        return new Queue(coreDetectionQueue, true);
    }

    @Bean
    public Queue thumbnailGenerationServiceQueue() {
        return new Queue(coreThumbnailQueue, true);
    }

    @Bean
    public FanoutExchange broadcastExchange() {
        return new FanoutExchange(broadcastCommonExchange);
    }

    @Bean
    public Binding bindingMetadataQueue(Queue metadataExtractionServiceQueue, FanoutExchange broadcastExchange) {
        return BindingBuilder.bind(metadataExtractionServiceQueue).to(broadcastExchange);
    }

    @Bean
    public Binding bindingDetectionQueue(Queue objectDetectionServiceQueue, FanoutExchange broadcastExchange) {
        return BindingBuilder.bind(objectDetectionServiceQueue).to(broadcastExchange);
    }

    @Bean
    public Binding bindingThumbnailQueue(Queue thumbnailGenerationServiceQueue, FanoutExchange broadcastExchange) {
        return BindingBuilder.bind(thumbnailGenerationServiceQueue).to(broadcastExchange);
    }
}
