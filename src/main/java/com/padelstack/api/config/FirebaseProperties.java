package com.padelstack.api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "firebase")
public class FirebaseProperties {

    /**
     * Ruta al JSON de service account. Si está vacío, se intenta usar
     * GOOGLE_APPLICATION_CREDENTIALS / Application Default Credentials.
     */
    private String credentialsPath;

    private String projectId;

    private String storageBucket;

    public String getCredentialsPath() {
        return credentialsPath;
    }

    public void setCredentialsPath(String credentialsPath) {
        this.credentialsPath = credentialsPath;
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
