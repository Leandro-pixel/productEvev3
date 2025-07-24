package com.productEvee.productEvee;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Configuration
public class FirebaseConfig {
   private static final String DATABASE_URL = "https://producteve-65ffb-default-rtdb.firebaseio.com";
    @PostConstruct
    public void init() throws IOException {
        if (FirebaseApp.getApps().isEmpty()) {
            InputStream serviceAccount;

            String json = System.getenv("FIREBASE_CONFIG_JSON");

            if (json != null && !json.isEmpty()) {
                System.out.println("Inicializando Firebase com FIREBASE_CONFIG_JSON.");
                serviceAccount = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
            } else {
                System.out.println("Inicializando Firebase com arquivo no classpath.");
                serviceAccount = getClass().getClassLoader()
                        .getResourceAsStream("producteve-65ffb-firebase-adminsdk-fbsvc-2469a53581.json");

                if (serviceAccount == null) {
                    throw new IllegalStateException("Arquivo Firebase não encontrado!");
                }
            }

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl(DATABASE_URL)
                    .build();

            FirebaseApp.initializeApp(options);
        }
    }

  
    @Bean
    public DatabaseReference databaseReference() {
        return FirebaseDatabase.getInstance().getReference();
    }
}
