package com.padelstack.api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * Propiedades generales de la API PadelStack.
 */
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private String publicBaseUrl = "http://10.0.2.2:8080";

    private String incidentPhotosFolder = "incident-photos";

    private List<String> corsAllowedOrigins = List.of(
            "http://localhost:5174",
            "http://127.0.0.1:5174",
            "http://localhost:5173",
            "http://127.0.0.1:5173"
    );

    public String getPublicBaseUrl() {
        return publicBaseUrl;
    }

    public void setPublicBaseUrl(String publicBaseUrl) {
        this.publicBaseUrl = publicBaseUrl;
    }

    public String getIncidentPhotosFolder() {
        return incidentPhotosFolder;
    }

    public void setIncidentPhotosFolder(String incidentPhotosFolder) {
        this.incidentPhotosFolder = incidentPhotosFolder;
    }

    public List<String> getCorsAllowedOrigins() {
        return corsAllowedOrigins;
    }

    public void setCorsAllowedOrigins(List<String> corsAllowedOrigins) {
        this.corsAllowedOrigins = corsAllowedOrigins;
    }
}
