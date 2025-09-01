package server.logging;

import java.time.LocalDateTime;

public interface ActivityObserver {
    void onActivityLogged(int userId, String description, LocalDateTime timestamp);
}