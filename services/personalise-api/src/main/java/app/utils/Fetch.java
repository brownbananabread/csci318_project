package app.utils;

public class Fetch {

    public static String extractBearerToken(String token) {
        return token.startsWith("Bearer ") ? token.substring(7) : token;
    }
}