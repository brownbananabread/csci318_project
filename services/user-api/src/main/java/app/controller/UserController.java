package app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import app.model.UserEntity;
import app.repository.UserRepository;

import java.util.Map;
import java.util.Optional;

@RestController
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUserEntity(@RequestParam(required = false) String email, 
                                   @RequestHeader(value = "Authorization", required = false) String token) {
        if (email != null) {
            boolean exists = userRepository.existsByEmail(email);
            return ResponseEntity.ok(Map.of("exists", exists));
        }
        
        if (token != null) {
            try {
                Long userId = Long.valueOf(token);
                Optional<UserEntity> user = userRepository.findById(userId);
                if (user.isPresent()) {
                    return ResponseEntity.ok(user.get());
                }
                return ResponseEntity.status(404).body(Map.of("error", "UserEntity not found"));
            } catch (NumberFormatException e) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid token format"));
            }
        }
        
        return ResponseEntity.status(401).body(Map.of("error", "Authorization header is required to access user data"));
    }

    @PostMapping("/user")
    public ResponseEntity<String> createUserEntity(@RequestBody UserEntity user) {
        if (user.getName() == null || user.getName().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Incomplete sign up form");
        }
        UserEntity savedUserEntity = userRepository.save(user);
        return ResponseEntity.status(201).body(savedUserEntity.getId().toString());
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");
        
        Optional<UserEntity> user = userRepository.findByEmail(email);
        if (user.isPresent() && user.get().getPassword().equals(password)) {
            return ResponseEntity.ok(user.get().getId().toString());
        }
        
        return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
    }

    @PutMapping("/user")
    public ResponseEntity<?> updateUserEntity(@RequestHeader("Authorization") String token, 
                                         @RequestBody UserEntity updatedUserEntity) {
        try {
            Long userId = Long.valueOf(token);
            Optional<UserEntity> existingUserEntity = userRepository.findById(userId);
            
            if (existingUserEntity.isPresent()) {
                UserEntity user = existingUserEntity.get();
                user.setName(updatedUserEntity.getName());
                user.setEmail(updatedUserEntity.getEmail());
                if (updatedUserEntity.getPassword() != null) {
                    user.setPassword(updatedUserEntity.getPassword());
                }
                userRepository.save(user);
                return ResponseEntity.ok(Map.of("message", "UserEntity updated successfully"));
            }
            
            return ResponseEntity.status(404).body(Map.of("error", "UserEntity not found"));
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid token format"));
        }
    }

    @DeleteMapping("/user")
    public ResponseEntity<?> deleteUserEntity(@RequestHeader("Authorization") String token) {
        try {
            Long userId = Long.valueOf(token);
            if (userRepository.existsById(userId)) {
                userRepository.deleteById(userId);
                return ResponseEntity.ok(Map.of("message", "UserEntity deleted successfully"));
            }
            
            return ResponseEntity.status(404).body(Map.of("error", "UserEntity not found"));
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid token format"));
        }
    }
}