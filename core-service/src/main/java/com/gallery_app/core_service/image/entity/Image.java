package com.gallery_app.core_service.image.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@Entity
@Table(name = "images")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class Image {

    @Id
    @Column(nullable = false, updatable = false, columnDefinition = "char(36)")
    @GeneratedValue
    @UuidGenerator
    private UUID uuid;

    @Column(nullable = false, unique = true)
    private String url;

    @Column(columnDefinition = "tinyint", length = 1)
    private Boolean thumbnailStatus;

    @Column(columnDefinition = "tinyint", length = 1)
    private Boolean metadataStatus;

    @Column(columnDefinition = "tinyint", length = 1)
    private Boolean objectDetectionStatus;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private OffsetDateTime dateCreated;

    @LastModifiedDate
    @Column(nullable = false)
    private OffsetDateTime lastUpdated;

}
