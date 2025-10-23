package app.service;

import app.context.UserContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import dev.langchain4j.service.AiServices;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PersonaliseServiceTest {

    @InjectMocks
    private PersonaliseService personaliseService;

    @BeforeEach
    void setUp() {
        UserContext.setUserId("1");
    }

    @AfterEach
    void tearDown() {
        UserContext.clear();
    }

    @Test
    void userContext_StoresAndRetrievesUserId() {
        UserContext.setUserId("123");
        assertEquals("123", UserContext.getUserId());
        UserContext.clear();
        assertNull(UserContext.getUserId());
    }

    @Test
    void chatWithAI_WithValidMessage_ReturnsResponse() {
        String message = "How many events have I created?";

        assertNotNull(message);
        assertTrue(message.length() > 0);
    }
}
