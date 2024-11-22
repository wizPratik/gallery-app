package com.gallery_app.core_service.metadata;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;

public class MetadataClient {

    @Value("${service.metadata.endpoint}")
    private String endpoint;

    public String fetchMetadata(String imageId) {
        String url = endpoint + imageId;

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(url);

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                int statusCode = response.getCode();
                if (HttpStatus.valueOf(statusCode).is2xxSuccessful()) {
                    return EntityUtils.toString(response.getEntity());
                } else {
                    throw new RuntimeException("Failed to fetch metadata. HTTP Status: " + statusCode);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error occurred while fetching metadata", e);
        }
    }
}
