package csci318.demo.controller;

import csci318.demo.model.ApiResponse;
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
    public ResponseEntity<ApiResponse> getUser(@RequestParam(required = false) String email, 
                                   @RequestHeader(value = "Authorization", required = false) String token) {
        if (email != null) {
            boolean exists = userRepository.existsByEmail(email);
            ApiResponse response = new ApiResponse(Map.of("exists", exists), 200);
            return ResponseEntity.ok(response);
        }
        
        if (token != null) {
            try {
                Long userId = Long.valueOf(token);
                Optional<User> user = userRepository.findById(userId);
                if (user.isPresent()) {
                    ApiResponse response = new ApiResponse(user.get(), 200);
                    return ResponseEntity.ok(response);
                }
                ApiResponse response = new ApiResponse("User not found", 404);
                return ResponseEntity.status(404).body(response);
            } catch (NumberFormatException e) {
                ApiResponse response = new ApiResponse("Invalid token format", 400);
                return ResponseEntity.badRequest().body(response);
            }
        }
        
        ApiResponse response = new ApiResponse("Authorization header is required to access user data", 401);
        return ResponseEntity.status(401).body(response);
    }

    @PostMapping("/user")
    public ResponseEntity<ApiResponse> createUser(@RequestBody User user) {
        User savedUser = userRepository.save(user);
        ApiResponse response = new ApiResponse(savedUser.getId().toString(), 201);
        return ResponseEntity.status(201).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");
        
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent() && user.get().getPassword().equals(password)) {
            ApiResponse response = new ApiResponse(user.get().getId().toString(), 200);
            return ResponseEntity.ok(response);
        }
        
        ApiResponse response = new ApiResponse("Invalid credentials", 401);
        return ResponseEntity.status(401).body(response);
    }

    @PutMapping("/user")
    public ResponseEntity<ApiResponse> updateUser(@RequestHeader("Authorization") String token, 
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
                ApiResponse response = new ApiResponse("User updated successfully", 200);
                return ResponseEntity.ok(response);
            }
            
            ApiResponse response = new ApiResponse("User not found", 404);
            return ResponseEntity.status(404).body(response);
        } catch (NumberFormatException e) {
            ApiResponse response = new ApiResponse("Invalid token format", 400);
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/user")
    public ResponseEntity<ApiResponse> deleteUser(@RequestHeader("Authorization") String token) {
        try {
            Long userId = Long.valueOf(token);
            if (userRepository.existsById(userId)) {
                userRepository.deleteById(userId);
                ApiResponse response = new ApiResponse("User deleted successfully", 200);
                return ResponseEntity.ok(response);
            }
            
            ApiResponse response = new ApiResponse("User not found", 404);
            return ResponseEntity.status(404).body(response);
        } catch (NumberFormatException e) {
            ApiResponse response = new ApiResponse("Invalid token format", 400);
            return ResponseEntity.badRequest().body(response);
        }
    }
}