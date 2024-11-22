package com.gallery_app.core_service.mq;

import com.gallery_app.core_service.image.entity.Image;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageProducer {

    private final RabbitTemplate rabbitTemplate;

    @Value("${broadcast.common.exchange}")
    private String broadcastCommonExchange;

    public void publishBroadcastMessage(Image image) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("image_id", String.valueOf(image.getUuid()));
        jsonObject.addProperty("location", image.getUrl());

        String message = jsonObject.toString();
        if (null == message) {
            log.error("Message not published as message is NULL");
            return;
        }
        rabbitTemplate.convertAndSend(broadcastCommonExchange, "", message);
        log.info("Broadcast message sent to all queues: {}", message);
    }
}
