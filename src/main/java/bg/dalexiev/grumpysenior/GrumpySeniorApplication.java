package bg.dalexiev.grumpysenior;

import jakarta.annotation.Resource;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class GrumpySeniorApplication implements CommandLineRunner  {

    @Resource
    PasswordEncoder passwordEncoder;

    public static void main(String[] args) { SpringApplication.run(GrumpySeniorApplication.class, args); }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Encrypted admin password: " + passwordEncoder.encode("secret123"));
    }
}
