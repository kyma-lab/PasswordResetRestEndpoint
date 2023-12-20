package mb.cloudnative.passwordreset;

import mb.cloudnative.passwordreset.domain.UserEntity;
import mb.cloudnative.passwordreset.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

// Application.java
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    CommandLineRunner run(UserRepository userRepository) {
        return args -> {
            UserEntity jonny = new UserEntity();
            jonny.setId(12345L);
            jonny.setUsername("jonny");
            jonny.setEmail("jonny@tester.de");
            userRepository.save(jonny);


            userRepository.findAll().forEach(user -> System.out.println(user.getUsername()));
        };
    }
}


