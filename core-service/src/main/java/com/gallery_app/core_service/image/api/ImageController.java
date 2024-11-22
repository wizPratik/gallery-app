package com.gallery_app.core_service.image.api;

import com.gallery_app.core_service.image.entity.Image;
import com.gallery_app.core_service.image.model.ImageDTO;
import com.gallery_app.core_service.image.service.ImageService;
import com.gallery_app.core_service.image.service.UploadService;
import com.gallery_app.core_service.metadata.MetadataClient;
import com.gallery_app.core_service.mq.MessageProducer;
import com.gallery_app.core_service.util.exceptions.BadRequestException;
import com.google.gson.JsonObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping(value = "/api/images", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;
    private final UploadService uploadService;
    private final MessageProducer messageProducer;
    private final MetadataClient metadataClient;

    @GetMapping
    public ResponseEntity<List<ImageDTO>> getAllImages() {
        return ResponseEntity.ok(imageService.findAll());
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<ImageDTO> getImage(@PathVariable(name = "uuid") final UUID uuid) {
        return ResponseEntity.ok(imageService.get(uuid));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<UUID> createImage(@RequestBody @Valid final ImageDTO imageDTO) {
        final UUID createdUuid = imageService.create(imageDTO);
        return new ResponseEntity<>(createdUuid, HttpStatus.CREATED);
    }

    @PutMapping("/{uuid}")
    public ResponseEntity<UUID> updateImage(@PathVariable(name = "uuid") final UUID uuid,
                                            @RequestBody @Valid final ImageDTO imageDTO) {
        imageService.update(uuid, imageDTO);
        return ResponseEntity.ok(uuid);
    }

    @DeleteMapping("/{uuid}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteImage(@PathVariable(name = "uuid") final UUID uuid) {
        imageService.delete(uuid);
        return ResponseEntity.noContent().build();
    }


    @PostMapping(value = "/upload", consumes = {"multipart/form-data"})
    @ApiResponse(responseCode = "201")
    public ResponseEntity<String> uploadImage(@RequestPart("file") final MultipartFile file) {
        final String[] allowedContentTypes = {"image/jpeg", "image/png", "image/webp"};
        if (!Arrays.asList(allowedContentTypes).contains(file.getContentType())) {
            throw new BadRequestException("Only jpeg, png or webp images are allowed");
        }
        final ImageDTO imageDTO = new ImageDTO();
        final UUID createdUuid = imageService.create(imageDTO);
        final String s3Url = uploadService.uploadMultipartFile(String.valueOf(createdUuid), file);
        final Image image = imageService.updateImageUrl(createdUuid, s3Url);
        messageProducer.publishBroadcastMessage(image);
        JsonObject response = new JsonObject();
        response.addProperty("uuid", String.valueOf(createdUuid));
        return new ResponseEntity<>(response.toString(), HttpStatus.CREATED);
    }

    @GetMapping("/{uuid}/metadata")
    public ResponseEntity<String> getImageMetadata(@PathVariable(name = "uuid") final UUID uuid) {
        try {
           return ResponseEntity.ok(metadataClient.fetchMetadata(uuid.toString()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

}
