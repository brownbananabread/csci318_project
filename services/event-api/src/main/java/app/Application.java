package app;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import app.model.EventDto;
import app.service.EventService;

import java.time.OffsetDateTime;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public CommandLineRunner loadDatabase(EventService eventService) {
        return args -> {
            EventDto event1 = new EventDto();
            event1.setTitle("Tech Conference 2024");
            event1.setDescription("Annual technology conference featuring the latest in software development");
            event1.setLocation("Convention Center, Downtown");
            event1.setStartTime(OffsetDateTime.now().plusDays(30));
            event1.setEndTime(OffsetDateTime.now().plusDays(30).plusHours(8));
            event1.setMaxParticipants(100);

            EventDto event2 = new EventDto();
            event2.setTitle("Team Building Workshop");
            event2.setDescription("Interactive workshop focused on team collaboration and communication");
            event2.setLocation("Corporate Training Room");
            event2.setStartTime(OffsetDateTime.now().plusDays(15));
            event2.setEndTime(OffsetDateTime.now().plusDays(15).plusHours(4));
            event2.setMaxParticipants(25);

            EventDto event3 = new EventDto();
            event3.setTitle("Project Demo Day");
            event3.setDescription("Showcase of innovative projects and prototypes");
            event3.setLocation("Innovation Lab");
            event3.setStartTime(OffsetDateTime.now().plusDays(7));
            event3.setEndTime(OffsetDateTime.now().plusDays(7).plusHours(6));
            event3.setMaxParticipants(50);

            EventDto event4 = new EventDto();
            event4.setTitle("Networking Mixer");
            event4.setDescription("Casual networking event for professionals in tech");
            event4.setLocation("Rooftop Lounge");
            event4.setStartTime(OffsetDateTime.now().plusDays(21));
            event4.setEndTime(OffsetDateTime.now().plusDays(21).plusHours(3));
            event4.setMaxParticipants(75);

            // Using sample user IDs that match the user-api initialization
            String johnDoeId = "1"; // John Doe
            String janeSmithId = "2"; // Jane Smith
            String bobJohnsonId = "3"; // Bob Johnson

            eventService.createEvent(johnDoeId, event1);
            eventService.createEvent(janeSmithId, event2);
            eventService.createEvent(bobJohnsonId, event3);
            eventService.createEvent(johnDoeId, event4);

            System.out.println("Loaded sample events:");
            eventService.getAllEvents().forEach(event ->
                System.out.println("Event: " + event.getTitle() + " - " + event.getLocation())
            );
        };
    }
}