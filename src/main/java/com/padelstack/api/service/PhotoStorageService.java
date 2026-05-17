package com.padelstack.api.service;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.firebase.cloud.StorageClient;
import com.padelstack.api.config.AppProperties;
import com.padelstack.api.exception.BadRequestException;
import com.padelstack.api.exception.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

/**
 * Servicio encargado de la lógica relacionada con photo storage.
 */
@Service
public class PhotoStorageService {

    private final StorageClient storageClient;
    private final AppProperties appProperties;

    /**
     * Crea una instancia de PhotoStorageService con las dependencias necesarias.
     *
     * @param storageClient valor recibido por el método.
     * @param appProperties valor recibido por el método.
     */
    public PhotoStorageService(StorageClient storageClient, AppProperties appProperties) {
        this.storageClient = storageClient;
        this.appProperties = appProperties;
    }

    /**
     * Gestiona la operación saveIncidentPhoto.
     *
     * @param incidentId identificador de la incidencia.
     * @param file valor recibido por el método.
     * @return resultado de la operación.
     */
    public StoredPhoto saveIncidentPhoto(String incidentId, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        try {
            byte[] bytes = file.getBytes();
            if (bytes.length == 0) {
                return null;
            }

            String contentType = resolveContentType(bytes);
            String safeExt = extensionFor(contentType);
            String objectName = appProperties.getIncidentPhotosFolder() + "/" + incidentId + "/" + UUID.randomUUID() + safeExt;
            Bucket bucket = storageClient.bucket();
            bucket.create(objectName, bytes, contentType);

            String photoUrl = UriComponentsBuilder.fromHttpUrl(appProperties.getPublicBaseUrl())
                    .path("/api/v1/public/incidents/{incidentId}/photo")
                    .queryParam("path", objectName)
                    .buildAndExpand(incidentId)
                    .toUriString();
            return new StoredPhoto(objectName, photoUrl, contentType);
        } catch (IOException ex) {
            throw new BadRequestException("No se pudo subir la imagen");
        } catch (RuntimeException ex) {
            if (ex instanceof BadRequestException badRequestException) {
                throw badRequestException;
            }
            throw new BadRequestException("No se pudo subir la imagen");
        }
    }

    /**
     * Carga .
     *
     * @param path ruta del recurso solicitado.
     * @return resultado de la operación.
     */
    public byte[] load(String path) {
        Bucket bucket = storageClient.bucket();
        Blob blob = bucket.get(path);
        if (blob == null || !blob.exists()) {
            throw new NotFoundException("Foto no encontrada");
        }
        return blob.getContent();
    }

    /**
     * Gestiona la operación contentType.
     *
     * @param path ruta del recurso solicitado.
     * @return texto obtenido por el método.
     */
    public String contentType(String path) {
        Bucket bucket = storageClient.bucket();
        Blob blob = bucket.get(path);
        if (blob == null || !blob.exists()) {
            throw new NotFoundException("Foto no encontrada");
        }
        return StringUtils.hasText(blob.getContentType()) ? blob.getContentType() : "application/octet-stream";
    }

    /**
     * Resuelve y valida el tipo MIME de la imagen recibida.
     *
     * @param bytes contenido recibido del archivo.
     * @return tipo MIME normalizado.
     */
    private String resolveContentType(byte[] bytes) {
        String detectedContentType = detectContentType(bytes);
        if (detectedContentType != null) {
            return detectedContentType;
        }
        throw new BadRequestException("La imagen seleccionada no es valida");
    }

    /**
     * Detecta si el archivo recibido parece JPEG o PNG por sus primeros bytes.
     *
     * @param bytes contenido recibido del archivo.
     * @return tipo MIME detectado o null si no se reconoce.
     */
    private String detectContentType(byte[] bytes) {
        if (bytes.length >= 3
                && (bytes[0] & 0xFF) == 0xFF
                && (bytes[1] & 0xFF) == 0xD8
                && (bytes[2] & 0xFF) == 0xFF) {
            return "image/jpeg";
        }
        if (bytes.length >= 8
                && (bytes[0] & 0xFF) == 0x89
                && bytes[1] == 0x50
                && bytes[2] == 0x4E
                && bytes[3] == 0x47
                && bytes[4] == 0x0D
                && bytes[5] == 0x0A
                && bytes[6] == 0x1A
                && bytes[7] == 0x0A) {
            return "image/png";
        }
        return null;
    }

    /**
     * Devuelve una extension segura segun el tipo de imagen.
     *
     * @param contentType tipo MIME validado.
     * @return extension del archivo.
     */
    private String extensionFor(String contentType) {
        return "image/png".equals(contentType) ? ".png" : ".jpg";
    }

    /**
     * DTO que transporta los datos de stored photo.
     *
     * @param storagePath valor recibido por el método.
     * @param photoUrl valor recibido por el método.
     * @param contentType valor recibido por el método.
     */
    public record StoredPhoto(String storagePath, String photoUrl, String contentType) {
    }
}
