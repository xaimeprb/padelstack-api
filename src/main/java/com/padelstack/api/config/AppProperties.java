package com.padelstack.api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public class AppProperties {

    /**
     * URL pública del backend usada para construir photoUrl.
     * En Android Emulator suele ser http://10.0.2.2:8080
     */
    private String publicBaseUrl = "http://10.0.2.2:8080";

    /**
     * Carpeta lógica para subir fotos de incidencias.
     */
    private String incidentPhotosFolder = "incident-photos";

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
}
