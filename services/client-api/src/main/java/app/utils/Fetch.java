package app.utils;

import org.springframework.web.reactive.function.client.WebClientResponseException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Fetch {

    public static String extractBearerToken(String token) {
        return token.startsWith("Bearer ") ? token.substring(7) : token;
    }


    public static String extractErrorMessage(WebClientResponseException e) {
        String errorMessage = e.getResponseBodyAsString();
        try {
            ObjectMapper mapper = new ObjectMapper();
            var json = mapper.readTree(errorMessage);
            return json.get("error").asText();
        } catch (Exception ex) {
            return errorMessage;
        }
    }
}