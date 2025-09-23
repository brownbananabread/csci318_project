package app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import app.exception.ServiceException;
import app.service.PersonaliseService;
import app.utils.Fetch;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class PersonaliseController {

    private final PersonaliseService personaliseService;

    public PersonaliseController(PersonaliseService personaliseService) {
        this.personaliseService = personaliseService;
    }

    @GetMapping("/summary")
    public ResponseEntity<?> summary(@RequestHeader("Authorization") String bearerToken) {
        try {
            String userId = Fetch.extractBearerToken(bearerToken);
            Map<String, Object> response = personaliseService.processSummary(userId);
            return ResponseEntity.ok(response);
        } catch (ServiceException e) {
            return ResponseEntity.status(e.getStatus()).build();
        }
    }

    @PostMapping("/chat")
    public ResponseEntity<?> chat(@RequestHeader("Authorization") String bearerToken,
                                @RequestBody Map<String, Object> chatData) {
        try {
            String userId = Fetch.extractBearerToken(bearerToken);
            Map<String, Object> response = personaliseService.processChat(userId, chatData);
            return ResponseEntity.ok(response);
        } catch (ServiceException e) {
            return ResponseEntity.status(e.getStatus()).build();
        }
    }
}