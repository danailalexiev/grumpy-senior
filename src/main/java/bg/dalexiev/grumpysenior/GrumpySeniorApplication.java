package bg.dalexiev.grumpysenior;

import jakarta.annotation.Resource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class GrumpySeniorApplication {

    static void main(String[] args) {
        SpringApplication.run(GrumpySeniorApplication.class, args);
    }

}
