package server.controller;

import server.model.UserContext;
import server.auth.AuthenticationStrategy;
import server.logging.ActivityLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.Map;

public abstract class BaseController {
    @Autowired protected AuthenticationStrategy authStrategy;
    @Autowired protected ActivityLogger activityLogger;
    
    protected final ResponseEntity<?> executeAuthenticatedRequest(String accessToken, AuthenticatedOperation operation) {
        try {
            if (!authStrategy.authenticate(accessToken)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Unauthorized"));
            }
            
            UserContext context = authStrategy.getUserContext(accessToken);
            validateAuthorization(context);
            
            return operation.execute(context);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Unauthorized")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", e.getMessage()));
            }
            if (e.getMessage().contains("Forbidden") || e.getMessage().contains("Only")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", e.getMessage()));
            }
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    protected abstract void validateAuthorization(UserContext context);
    
    @FunctionalInterface
    protected interface AuthenticatedOperation {
        ResponseEntity<?> execute(UserContext context) throws Exception;
    }
}
