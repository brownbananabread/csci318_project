package csci318.demo.controller;

import csci318.demo.model.ApiResponse;
import csci318.demo.model.User;
import csci318.demo.service.UserApiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class UsersController {

    private final UserApiService userApiService;

    public UsersController(UserApiService userApiService) {
        this.userApiService = userApiService;
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse> signup(@RequestBody User user) {
        ApiResponse response = userApiService.signup(user);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody User user) {
        ApiResponse response = userApiService.login(user);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PutMapping("/update-account")
    public ResponseEntity<ApiResponse> updateAccount(@RequestHeader(value = "Authorization", required = false) String token, @RequestBody User user) {
        if (token == null || token.trim().isEmpty()) {
            ApiResponse response = new ApiResponse("User must first login to update account", 401);
            return ResponseEntity.status(401).body(response);
        }
        ApiResponse response = userApiService.updateAccount(token, user);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @DeleteMapping("/remove-account")
    public ResponseEntity<ApiResponse> removeAccount(@RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || token.trim().isEmpty()) {
            ApiResponse response = new ApiResponse("User must first login to remove account", 401);
            return ResponseEntity.status(401).body(response);
        }
        ApiResponse response = userApiService.removeAccount(token);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/account")
    public ResponseEntity<ApiResponse> getAccount(@RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || token.trim().isEmpty()) {
            ApiResponse response = new ApiResponse("User must first login to view account details", 401);
            return ResponseEntity.status(401).body(response);
        }
        ApiResponse response = userApiService.getAccount(token);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}