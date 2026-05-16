package com.padelstack.api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Clase de propiedades que recoge la configuración de firebase.
 */
@ConfigurationProperties(prefix = "firebase")
public class FirebaseProperties {

    /**
     * Ruta al JSON de service account. Si está vacío, se intenta usar
     * GOOGLE_APPLICATION_CREDENTIALS / Application Default Credentials.
     */
    private String credentialsPath;

    private String projectId;

    private String storageBucket;

    /**
     * Devuelve credentials path.
     *
     * @return texto obtenido por el método.
     */
    public String getCredentialsPath() {
        return credentialsPath;
    }

    /**
     * Actualiza credentials path.
     *
     * @param credentialsPath valor recibido por el método.
     */
    public void setCredentialsPath(String credentialsPath) {
        this.credentialsPath = credentialsPath;
    }

    /**
     * Devuelve project id.
     *
     * @return texto obtenido por el método.
     */
    public String getProjectId() {
        return projectId;
    }

    /**
     * Actualiza project id.
     *
     * @param projectId valor recibido por el método.
     */
    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    /**
     * Devuelve storage bucket.
     *
     * @return texto obtenido por el método.
     */
    public String getStorageBucket() {
        return storageBucket;
    }

    /**
     * Actualiza storage bucket.
     *
     * @param storageBucket valor recibido por el método.
     */
    public void setStorageBucket(String storageBucket) {
        this.storageBucket = storageBucket;
    }
}
