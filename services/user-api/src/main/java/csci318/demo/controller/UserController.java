package csci318.demo.controller;

import csci318.demo.model.User;
import csci318.demo.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUser(@RequestParam(required = false) String email, 
                                   @RequestHeader(value = "Authorization", required = false) String token) {
        if (email != null) {
            boolean exists = userRepository.existsByEmail(email);
            return ResponseEntity.ok(Map.of("exists", exists));
        }
        
        if (token != null) {
            try {
                Long userId = Long.valueOf(token);
                Optional<User> user = userRepository.findById(userId);
                if (user.isPresent()) {
                    return ResponseEntity.ok(user.get());
                }
                return ResponseEntity.status(404).body(Map.of("error", "User not found"));
            } catch (NumberFormatException e) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid token format"));
            }
        }
        
        return ResponseEntity.status(401).body(Map.of("error", "Authorization header is required to access user data"));
    }

    @PostMapping("/user")
    public ResponseEntity<String> createUser(@RequestBody User user) {
        User savedUser = userRepository.save(user);
        return ResponseEntity.status(201).body(savedUser.getId().toString());
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");
        
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent() && user.get().getPassword().equals(password)) {
            return ResponseEntity.ok(user.get().getId().toString());
        }
        
        return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
    }

    @PutMapping("/user")
    public ResponseEntity<?> updateUser(@RequestHeader("Authorization") String token, 
                                         @RequestBody User updatedUser) {
        try {
            Long userId = Long.valueOf(token);
            Optional<User> existingUser = userRepository.findById(userId);
            
            if (existingUser.isPresent()) {
                User user = existingUser.get();
                user.setName(updatedUser.getName());
                user.setEmail(updatedUser.getEmail());
                if (updatedUser.getPassword() != null) {
                    user.setPassword(updatedUser.getPassword());
                }
                userRepository.save(user);
                return ResponseEntity.ok(Map.of("message", "User updated successfully"));
            }
            
            return ResponseEntity.status(404).body(Map.of("error", "User not found"));
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid token format"));
        }
    }

    @DeleteMapping("/user")
    public ResponseEntity<?> deleteUser(@RequestHeader("Authorization") String token) {
        try {
            Long userId = Long.valueOf(token);
            if (userRepository.existsById(userId)) {
                userRepository.deleteById(userId);
                return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
            }
            
            return ResponseEntity.status(404).body(Map.of("error", "User not found"));
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid token format"));
        }
    }
}