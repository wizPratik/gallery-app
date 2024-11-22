package com.gallery_app.core_service.mq;

import com.gallery_app.core_service.image.service.ImageService;
import com.gallery_app.core_service.mq.dto.FromMetadataExtractionServiceDTO;
import com.gallery_app.core_service.mq.dto.FromObjectDetectionServiceDTO;
import com.gallery_app.core_service.mq.dto.FromThumbnailGenerationServiceDTO;
import com.gallery_app.core_service.util.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageConsumer {

    private final ImageService imageService;

    @RabbitListener(queues = "${metadata.core.queue}")
    public void processMessageFromMetadataExtractionService(final String message) {
        log.info("Message received from Metadata Extraction Service: {}", message);
        FromMetadataExtractionServiceDTO dto = JsonUtils.fromJson(message, FromMetadataExtractionServiceDTO.class);
        if (null == dto) {
            log.error("Could not convert MetadataExtractionService response to DTO");
            return;
        }
        imageService.updateMetadataStatus(dto.getImageId(), dto.isStatus());
    }

    @RabbitListener(queues = "${detection.core.queue}")
    public void processMessageFromObjectDetectionService(final String message) {
        log.info("Message received from Object Detection Service: {}", message);
        FromObjectDetectionServiceDTO dto = JsonUtils.fromJson(message, FromObjectDetectionServiceDTO.class);
        if (null == dto) {
            log.error("Could not convert ObjectDetectionService response to DTO");
            return;
        }
        imageService.updateObjectDetectionStatus(dto.getImageId(), dto.isStatus());
    }

    @RabbitListener(queues = "${thumbnail.core.queue}")
    public void processMessageFromThumbnailGenerationService(final String message) {
        log.info("Message received from Thumbnail Generation Service: {}", message);
        FromThumbnailGenerationServiceDTO dto = JsonUtils.fromJson(message, FromThumbnailGenerationServiceDTO.class);
        if (null == dto) {
            log.error("Could not convert ThumbnailGenerationService response to DTO");
            return;
        }
        imageService.updateThumbnailStatus(dto.getImageId(), dto.isStatus());
    }
}
