package com.gallery_app.core_service.mq.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class InboundDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @JsonProperty("image_id")
    String imageId;
    @JsonProperty("status")
    boolean status;
}
