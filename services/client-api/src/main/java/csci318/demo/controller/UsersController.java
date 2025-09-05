package csci318.demo.controller;

import csci318.demo.service.UserApiService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class UsersController {

    private final UserApiService userApiService;

    public UsersController(UserApiService userApiService) {
        this.userApiService = userApiService;
    }

    @GetMapping("/users")
    public List<Object> getUsers() {
        return userApiService.getUsers();
    }
}