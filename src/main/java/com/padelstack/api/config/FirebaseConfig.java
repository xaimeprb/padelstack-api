package com.padelstack.api.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.cloud.StorageClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    @Bean
    public FirebaseApp firebaseApp(FirebaseProperties properties) throws IOException {
        if (!FirebaseApp.getApps().isEmpty()) {
            return FirebaseApp.getInstance();
        }

        GoogleCredentials credentials;
        if (StringUtils.hasText(properties.getCredentialsPath())) {
            try (InputStream inputStream = new FileInputStream(properties.getCredentialsPath())) {
                credentials = GoogleCredentials.fromStream(inputStream);
            }
        } else {
            credentials = GoogleCredentials.getApplicationDefault();
        }

        FirebaseOptions.Builder builder = FirebaseOptions.builder()
                .setCredentials(credentials);

        if (StringUtils.hasText(properties.getProjectId())) {
            builder.setProjectId(properties.getProjectId());
        }
        if (StringUtils.hasText(properties.getStorageBucket())) {
            builder.setStorageBucket(properties.getStorageBucket());
        }

        return FirebaseApp.initializeApp(builder.build());
    }

    @Bean
    public Firestore firestore(FirebaseApp firebaseApp) {
        return FirestoreClient.getFirestore(firebaseApp);
    }

    @Bean
    public StorageClient storageClient(FirebaseApp firebaseApp) {
        return StorageClient.getInstance(firebaseApp);
    }
}
