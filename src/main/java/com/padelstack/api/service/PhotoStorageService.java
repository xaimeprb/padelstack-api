package com.padelstack.api.service;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.firebase.cloud.StorageClient;
import com.padelstack.api.config.AppProperties;
import com.padelstack.api.exception.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class PhotoStorageService {

    private final StorageClient storageClient;
    private final AppProperties appProperties;

    public PhotoStorageService(StorageClient storageClient, AppProperties appProperties) {
        this.storageClient = storageClient;
        this.appProperties = appProperties;
    }

    public StoredPhoto saveIncidentPhoto(String incidentId, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        try {
            String safeExt = extractExtension(file.getOriginalFilename());
            String objectName = appProperties.getIncidentPhotosFolder() + "/" + incidentId + "/" + UUID.randomUUID() + safeExt;
            Bucket bucket = storageClient.bucket();
            String contentType = StringUtils.hasText(file.getContentType()) ? file.getContentType() : "application/octet-stream";
            bucket.create(objectName, file.getBytes(), contentType);

            String photoUrl = UriComponentsBuilder.fromHttpUrl(appProperties.getPublicBaseUrl())
                    .path("/api/v1/public/incidents/{incidentId}/photo")
                    .queryParam("path", objectName)
                    .buildAndExpand(incidentId)
                    .toUriString();
            return new StoredPhoto(objectName, photoUrl, contentType);
        } catch (IOException ex) {
            throw new IllegalStateException("No se pudo guardar la foto", ex);
        }
    }

    public byte[] load(String path) {
        Bucket bucket = storageClient.bucket();
        Blob blob = bucket.get(path);
        if (blob == null || !blob.exists()) {
            throw new NotFoundException("Foto no encontrada");
        }
        return blob.getContent();
    }

    public String contentType(String path) {
        Bucket bucket = storageClient.bucket();
        Blob blob = bucket.get(path);
        if (blob == null || !blob.exists()) {
            throw new NotFoundException("Foto no encontrada");
        }
        return StringUtils.hasText(blob.getContentType()) ? blob.getContentType() : "application/octet-stream";
    }

    private String extractExtension(String originalName) {
        if (!StringUtils.hasText(originalName) || !originalName.contains(".")) {
            return ".bin";
        }
        return originalName.substring(originalName.lastIndexOf('.'));
    }

    public record StoredPhoto(String storagePath, String photoUrl, String contentType) {
    }
}
