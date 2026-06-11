package com.padelstack.api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Propiedades de configuracion de Firebase Admin para el backend.
 */
@ConfigurationProperties(prefix = "firebase")
public class FirebaseProperties {

    private String serviceAccountJson;

    private String serviceAccountBase64;

    private String configPath;

    private String projectId;

    private String storageBucket;

    public String getServiceAccountJson() {
        return serviceAccountJson;
    }

    public void setServiceAccountJson(String serviceAccountJson) {
        this.serviceAccountJson = serviceAccountJson;
    }

    public String getServiceAccountBase64() {
        return serviceAccountBase64;
    }

    public void setServiceAccountBase64(String serviceAccountBase64) {
        this.serviceAccountBase64 = serviceAccountBase64;
    }

    public String getConfigPath() {
        return configPath;
    }

    public void setConfigPath(String configPath) {
        this.configPath = configPath;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getStorageBucket() {
        return storageBucket;
    }

    public void setStorageBucket(String storageBucket) {
        this.storageBucket = storageBucket;
    }
}
