package com.productEvee.productEvee;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void init() throws IOException {
        InputStream serviceAccount;

        // ✅ Primeiro tenta carregar das variáveis de ambiente (produção)
        String firebaseJson = System.getenv("FIREBASE_CONFIG_JSON");
        if (firebaseJson != null && !firebaseJson.isEmpty()) {
            System.out.println("Inicializando Firebase a partir da variável de ambiente.");
            serviceAccount = new ByteArrayInputStream(firebaseJson.getBytes(StandardCharsets.UTF_8));
        } else {
            // 🔁 Fallback para o arquivo local no classpath (desenvolvimento)
            System.out.println("Inicializando Firebase com arquivo no classpath.");
            serviceAccount = getClass().getClassLoader()
                    .getResourceAsStream("producteve-65ffb-firebase-adminsdk-fbsvc-2469a53581.json");

            if (serviceAccount == null) {
                throw new IllegalStateException("Arquivo Firebase não encontrado no classpath!");
            }
        }

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options);
            System.out.println("Firebase inicializado com sucesso!");
        }
    }
}
