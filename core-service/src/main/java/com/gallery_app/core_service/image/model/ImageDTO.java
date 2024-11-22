package com.gallery_app.core_service.image.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ImageDTO {

    private UUID uuid;

    @NotNull
    @Size(max = 255)
    @ImageUrlUnique
    private String url;

    private boolean thumbnailStatus;

    private boolean metadataStatus;

    private boolean objectDetectionStatus;

}
