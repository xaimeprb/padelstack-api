package com.padelstack.api.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.cloud.StorageClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.Map;

/**
 * Inicializa Firebase Admin SDK para los servicios backend de PadelStack.
 */
@Configuration
public class FirebaseConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(FirebaseConfig.class);
    private static final TypeReference<Map<String, Object>> SERVICE_ACCOUNT_TYPE = new TypeReference<>() {
    };

    private final ObjectMapper objectMapper;

    public FirebaseConfig(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Configura la aplicacion Firebase Admin reutilizando una instancia existente si ya fue creada.
     *
     * @param properties propiedades de credenciales privadas y proyecto Firebase.
     * @return instancia Firebase Admin lista para Auth, Firestore y Storage.
     */
    @Bean
    public FirebaseApp firebaseApp(FirebaseProperties properties) throws IOException {
        if (!FirebaseApp.getApps().isEmpty()) {
            LOGGER.info("Firebase Admin ya estaba inicializado; se reutiliza la instancia existente.");
            return FirebaseApp.getInstance();
        }

        CredentialsSource source = resolveCredentials(properties);
        LOGGER.info("Inicializando Firebase Admin con {}.", source.description());

        FirebaseOptions.Builder builder = FirebaseOptions.builder()
                .setCredentials(source.credentials());

        if (StringUtils.hasText(properties.getProjectId())) {
            builder.setProjectId(properties.getProjectId());
        }
        if (StringUtils.hasText(properties.getStorageBucket())) {
            builder.setStorageBucket(properties.getStorageBucket());
        }

        return FirebaseApp.initializeApp(builder.build());
    }

    /**
     * Obtiene Firestore desde la instancia Firebase Admin ya inicializada.
     *
     * @param firebaseApp instancia Firebase Admin.
     * @return cliente Firestore.
     */
    @Bean
    public Firestore firestore(FirebaseApp firebaseApp) {
        return FirestoreClient.getFirestore(firebaseApp);
    }

    /**
     * Obtiene Storage desde la instancia Firebase Admin ya inicializada.
     *
     * @param firebaseApp instancia Firebase Admin.
     * @return cliente Storage.
     */
    @Bean
    public StorageClient storageClient(FirebaseApp firebaseApp) {
        return StorageClient.getInstance(firebaseApp);
    }

    private CredentialsSource resolveCredentials(FirebaseProperties properties) throws IOException {
        if (StringUtils.hasText(properties.getServiceAccountJson())) {
            return fromServiceAccountJson(properties.getServiceAccountJson(), "FIREBASE_SERVICE_ACCOUNT_JSON");
        }

        if (StringUtils.hasText(properties.getServiceAccountBase64())) {
            return fromServiceAccountBase64(properties.getServiceAccountBase64(), "FIREBASE_SERVICE_ACCOUNT_BASE64");
        }

        if (StringUtils.hasText(properties.getConfigPath())) {
            return fromServiceAccountPath(properties.getConfigPath(), "FIREBASE_CONFIG_PATH");
        }

        for (Path candidate : localConfigCandidates()) {
            if (Files.isRegularFile(candidate)) {
                return fromServiceAccountPath(candidate.toString(), "archivo local config/firebase-service-account.json");
            }
        }

        return new CredentialsSource(GoogleCredentials.getApplicationDefault(), "Application Default Credentials");
    }

    private CredentialsSource fromServiceAccountBase64(String base64, String description) throws IOException {
        try {
            String json = new String(Base64.getMimeDecoder().decode(base64), StandardCharsets.UTF_8);
            return fromServiceAccountJson(json, description);
        } catch (IllegalArgumentException exception) {
            throw new IOException("FIREBASE_SERVICE_ACCOUNT_BASE64 no contiene Base64 valido.", exception);
        }
    }

    private CredentialsSource fromServiceAccountJson(String serviceAccountJson, String description) throws IOException {
        byte[] credentialsBytes = normalizePrivateKey(serviceAccountJson).getBytes(StandardCharsets.UTF_8);
        try (InputStream inputStream = new ByteArrayInputStream(credentialsBytes)) {
            return new CredentialsSource(GoogleCredentials.fromStream(inputStream), description);
        }
    }

    private CredentialsSource fromServiceAccountPath(String configuredPath, String description) throws IOException {
        Path path = Paths.get(configuredPath).toAbsolutePath().normalize();
        try (InputStream inputStream = new FileInputStream(path.toFile())) {
            return new CredentialsSource(GoogleCredentials.fromStream(inputStream), description + " (" + path + ")");
        }
    }

    private String normalizePrivateKey(String serviceAccountJson) throws IOException {
        Map<String, Object> serviceAccount = objectMapper.readValue(serviceAccountJson, SERVICE_ACCOUNT_TYPE);
        Object privateKey = serviceAccount.get("private_key");

        if (privateKey instanceof String key && key.contains("\\n")) {
            serviceAccount.put("private_key", key.replace("\\n", "\n"));
            return objectMapper.writeValueAsString(serviceAccount);
        }

        return serviceAccountJson;
    }

    private List<Path> localConfigCandidates() {
        return List.of(
                Paths.get("config", "firebase-service-account.json").toAbsolutePath().normalize(),
                Paths.get("Codigo", "padelstack-api", "config", "firebase-service-account.json").toAbsolutePath().normalize()
        );
    }

    private record CredentialsSource(GoogleCredentials credentials, String description) {
    }
}
