import java.io.FileInputStream;
import java.io.IOException;

import javax.annotation.PostConstruct;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

public class FirebaseConfig {
    @PostConstruct
    public static void initialize() {
        try {
            FileInputStream refreshToken = new FileInputStream("./ServiceAccountKey.json");

            FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(refreshToken))
                .setDatabaseUrl("https://java-web-server-314cc-default-rtdb.firebaseio.com/")
                .build();

            FirebaseApp.initializeApp(options);
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
    }

}

