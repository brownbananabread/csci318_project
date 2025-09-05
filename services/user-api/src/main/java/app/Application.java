package app;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import app.model.UserEntity;
import app.repository.UserRepository;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public CommandLineRunner loadDatabase(UserRepository userRepository) {
        return args -> {
            UserEntity user1 = new UserEntity("John Doe", "john.doe@example.com", "password123");
            UserEntity user2 = new UserEntity("Jane Smith", "jane.smith@example.com", "password456");
            UserEntity user3 = new UserEntity("Bob Johnson", "bob.johnson@example.com", "password789");
            
            userRepository.save(user1);
            userRepository.save(user2);
            userRepository.save(user3);
            
            System.out.println("Loaded sample users:");
            userRepository.findAll().forEach(System.out::println);
        };
    }
}