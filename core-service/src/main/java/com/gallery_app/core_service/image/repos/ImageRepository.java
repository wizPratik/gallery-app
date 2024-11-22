package com.gallery_app.core_service.image.repos;

import com.gallery_app.core_service.image.entity.Image;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ImageRepository extends JpaRepository<Image, UUID> {

    boolean existsByUrlIgnoreCase(String url);

}
