package app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
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
        try {
            if (email != null) {
                boolean exists = userRepository.existsByEmail(email);
                return ResponseEntity.ok(Map.of("exists", exists));
            }
            if (token != null) {
                Long userId = Long.valueOf(token);
                return userRepository.findById(userId)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
            }
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/user")
    public ResponseEntity<?> createUserEntity(@RequestBody UserEntity user) {
        try {
            if (user.getName() == null || user.getName().trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            UserEntity savedUserEntity = userRepository.save(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedUserEntity.getId().toString());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        try {
            String email = credentials.get("email");
            String password = credentials.get("password");

            Optional<UserEntity> user = userRepository.findByEmail(email);
            if (user.isPresent() && user.get().getPassword().equals(password)) {
                return ResponseEntity.ok(user.get().getId().toString());
            }

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PatchMapping("/user")
    public ResponseEntity<?> updateUserEntity(@RequestHeader("Authorization") String token,
                                         @RequestBody UserEntity updatedUserEntity) {
        try {
            Long userId = Long.valueOf(token);
            Optional<UserEntity> existingUserEntity = userRepository.findById(userId);

            if (existingUserEntity.isPresent()) {
                UserEntity user = existingUserEntity.get();

                if (updatedUserEntity.getName() != null && !updatedUserEntity.getName().trim().isEmpty()) {
                    user.setName(updatedUserEntity.getName());
                }
                if (updatedUserEntity.getEmail() != null && !updatedUserEntity.getEmail().trim().isEmpty()) {
                    user.setEmail(updatedUserEntity.getEmail());
                }
                if (updatedUserEntity.getPassword() != null && !updatedUserEntity.getPassword().trim().isEmpty()) {
                    user.setPassword(updatedUserEntity.getPassword());
                }

                userRepository.save(user);
                return ResponseEntity.ok().build();
            }

            return ResponseEntity.notFound().build();
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/user")
    public ResponseEntity<?> deleteUserEntity(@RequestHeader("Authorization") String token) {
        try {
            Long userId = Long.valueOf(token);
            if (userRepository.existsById(userId)) {
                userRepository.deleteById(userId);
                return ResponseEntity.ok().build();
            }

            return ResponseEntity.notFound().build();
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}