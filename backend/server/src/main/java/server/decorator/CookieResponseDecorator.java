package server.decorator;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CookieResponseDecorator {
    public ResponseEntity<?> decorateWithCookie(ResponseEntity<?> response, String accessToken) {
        ResponseCookie cookie = ResponseCookie.from("accessToken", accessToken)
            .httpOnly(false).path("/").maxAge(24 * 60 * 60).sameSite("Lax").build();
        return ResponseEntity.status(response.getStatusCode())
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .body(response.getBody());
    }
}