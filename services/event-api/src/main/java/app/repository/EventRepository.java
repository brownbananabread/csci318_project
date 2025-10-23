package app.repository;

import app.model.EventDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<EventDto, String> {
    List<EventDto> findByCreatedBy(String createdBy);
}
