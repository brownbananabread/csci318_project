package app.utils;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.Map;
import java.time.OffsetDateTime;

public class ResponseHelper {

    public static ResponseEntity<?> createResponse(HttpStatus status, String path, String message, Object data) {
        return ResponseEntity.status(status).body(Map.of(
            "path", path,
            "timestamp", OffsetDateTime.now(),
            "status", status.value(),
            "message", message,
            "data", data != null ? data : Map.of()
        ));
    }
}