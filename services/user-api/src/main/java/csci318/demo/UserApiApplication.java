package csci318.demo;

import csci318.demo.model.User;
import csci318.demo.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class UserApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserApiApplication.class, args);
    }

    @Bean
    public CommandLineRunner loadDatabase(UserRepository userRepository) {
        return args -> {
            User user1 = new User("John Doe", "john.doe@example.com");
            User user2 = new User("Jane Smith", "jane.smith@example.com");
            User user3 = new User("Bob Johnson", "bob.johnson@example.com");
            
            userRepository.save(user1);
            userRepository.save(user2);
            userRepository.save(user3);
            
            System.out.println("Loaded sample users:");
            userRepository.findAll().forEach(System.out::println);
        };
    }
}