package app.repository;

import app.model.EventRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRegistrationRepository extends JpaRepository<EventRegistration, Long> {
    List<EventRegistration> findByUserId(String userId);
    List<EventRegistration> findByEventId(String eventId);
    Optional<EventRegistration> findByUserIdAndEventId(String userId, String eventId);
    void deleteByEventId(String eventId);
    long countByEventId(String eventId);
}
