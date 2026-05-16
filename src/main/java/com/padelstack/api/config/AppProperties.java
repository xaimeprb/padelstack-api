package com.padelstack.api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Clase de propiedades que recoge la configuración de app.
 */
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

    /**
     * Devuelve public base url.
     *
     * @return texto obtenido por el método.
     */
    public String getPublicBaseUrl() {
        return publicBaseUrl;
    }

    /**
     * Actualiza public base url.
     *
     * @param publicBaseUrl valor recibido por el método.
     */
    public void setPublicBaseUrl(String publicBaseUrl) {
        this.publicBaseUrl = publicBaseUrl;
    }

    /**
     * Devuelve incident photos folder.
     *
     * @return texto obtenido por el método.
     */
    public String getIncidentPhotosFolder() {
        return incidentPhotosFolder;
    }

    /**
     * Actualiza incident photos folder.
     *
     * @param incidentPhotosFolder valor recibido por el método.
     */
    public void setIncidentPhotosFolder(String incidentPhotosFolder) {
        this.incidentPhotosFolder = incidentPhotosFolder;
    }
}
