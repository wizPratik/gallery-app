package com.gallery_app.core_service.image.service;

import com.gallery_app.core_service.image.entity.Image;
import com.gallery_app.core_service.image.model.ImageDTO;
import com.gallery_app.core_service.image.repos.ImageRepository;
import com.gallery_app.core_service.util.exceptions.NotFoundException;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class ImageService {

    private final ImageRepository imageRepository;

    public ImageService(final ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    public List<ImageDTO> findAll() {
        final List<Image> images = imageRepository.findAll(Sort.by("uuid"));
        return images.stream()
                .map(image -> mapToDTO(image, new ImageDTO()))
                .toList();
    }

    public ImageDTO get(final UUID uuid) {
        return imageRepository.findById(uuid)
                .map(image -> mapToDTO(image, new ImageDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public UUID create(final ImageDTO imageDTO) {
        final Image image = new Image();
        mapToEntity(imageDTO, image);
        return imageRepository.save(image).getUuid();
    }

    public void update(final UUID uuid, final ImageDTO imageDTO) {
        final Image image = imageRepository.findById(uuid)
                .orElseThrow(NotFoundException::new);
        mapToEntity(imageDTO, image);
        imageRepository.save(image);
    }

    public void delete(final UUID uuid) {
        imageRepository.deleteById(uuid);
    }

    public Image updateImageUrl(final UUID uuid, final String s3Url) {
        final Image image = imageRepository.findById(uuid).orElseThrow(NotFoundException::new);
        image.setUrl(s3Url);
        return imageRepository.save(image);
    }

    public void updateMetadataStatus(final String uuid, final Boolean metadataStatus) {
        final Image image = imageRepository.findById(UUID.fromString(uuid))
                .orElseThrow(NotFoundException::new);
        image.setMetadataStatus(metadataStatus);
        imageRepository.save(image);
    }

    public void updateObjectDetectionStatus(final String uuid, final Boolean objectDetectionStatus) {
        final Image image = imageRepository.findById(UUID.fromString(uuid))
                .orElseThrow(NotFoundException::new);
        image.setObjectDetectionStatus(objectDetectionStatus);
        imageRepository.save(image);
    }

    public void updateThumbnailStatus(final String uuid, final Boolean thumbnailStatus) {
        final Image image = imageRepository.findById(UUID.fromString(uuid))
                .orElseThrow(NotFoundException::new);
        image.setThumbnailStatus(thumbnailStatus);
        imageRepository.save(image);
    }

    public ImageDTO mapToDTO(final Image image, final ImageDTO imageDTO) {
        imageDTO.setUuid(image.getUuid());
        imageDTO.setUrl(image.getUrl());
        imageDTO.setThumbnailStatus(image.getThumbnailStatus());
        imageDTO.setMetadataStatus(image.getMetadataStatus());
        imageDTO.setObjectDetectionStatus(image.getObjectDetectionStatus());
        return imageDTO;
    }

    private Image mapToEntity(final ImageDTO imageDTO, final Image image) {
        image.setUrl(imageDTO.getUrl() == null ? UUID.randomUUID().toString() : imageDTO.getUrl());
        image.setThumbnailStatus(imageDTO.isThumbnailStatus());
        image.setMetadataStatus(imageDTO.isThumbnailStatus());
        image.setObjectDetectionStatus(imageDTO.isObjectDetectionStatus());
        return image;
    }

    public boolean urlExists(final String url) {
        return imageRepository.existsByUrlIgnoreCase(url);
    }

}
