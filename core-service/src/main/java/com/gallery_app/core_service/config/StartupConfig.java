package com.gallery_app.core_service.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;

import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class StartupConfig {

    private final ConfigurableEnvironment environment;


    @EventListener(ApplicationReadyEvent.class)
    public void printAppConfig() {
        for (PropertySource<?> propertySource : environment.getPropertySources()) {
            // Filter for application.properties by name
            if (propertySource.getName().contains("applicationConfig: [file:")
                    && propertySource.getName().endsWith(".properties]")) {
                @SuppressWarnings("unchecked")
                Map<String, Object> source = (Map<String, Object>) propertySource.getSource();
                source.forEach((key, value) -> log.info("[PROPERTY] {}: {}", key, value));
            }
        }
    }
}
