package com.productEvee.productEvee;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {
@PostConstruct
public void init() throws IOException {
    InputStream serviceAccount = getClass().getClassLoader()
        .getResourceAsStream("producteve-65ffb-firebase-adminsdk-fbsvc-2469a53581.json");

    if (serviceAccount == null) {
        throw new IllegalStateException("Arquivo de credenciais Firebase não encontrado no classpath!");
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
