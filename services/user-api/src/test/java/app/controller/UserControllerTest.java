package app.controller;

import app.UserController;
import app.UserEntity;
import app.UserEventPublisher;
import app.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private UserEventPublisher userEventPublisher;

    private UserEntity testUser;

    @BeforeEach
    void setUp() {
        testUser = new UserEntity();
        testUser.setId(1L);
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password123");
    }

    @Test
    void getUserEntity_WithValidToken_ReturnsUser() throws Exception {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        mockMvc.perform(get("/user")
                .header("Authorization", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test User"))
                .andExpect(jsonPath("$.email").value("test@example.com"));

        verify(userRepository).findById(1L);
    }

    @Test
    void getUserEntity_WithEmail_ChecksExistence() throws Exception {
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        mockMvc.perform(get("/user")
                .param("email", "test@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exists").value(true));

        verify(userRepository).existsByEmail("test@example.com");
    }

    @Test
    void createUserEntity_WithValidData_CreatesUser() throws Exception {
        when(userRepository.save(any(UserEntity.class))).thenReturn(testUser);

        mockMvc.perform(post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Test User\",\"email\":\"test@example.com\",\"password\":\"password123\"}"))
                .andExpect(status().isCreated())
                .andExpect(content().string("1"));

        verify(userRepository).save(any(UserEntity.class));
        verify(userEventPublisher).publishUserCreated(any());
    }

    @Test
    void createUserEntity_WithEmptyName_ReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"\",\"email\":\"test@example.com\",\"password\":\"password123\"}"))
                .andExpect(status().isBadRequest());

        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void login_WithValidCredentials_ReturnsUserId() throws Exception {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"test@example.com\",\"password\":\"password123\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));

        verify(userRepository).findByEmail("test@example.com");
    }

    @Test
    void login_WithInvalidCredentials_ReturnsUnauthorized() throws Exception {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"test@example.com\",\"password\":\"wrongpassword\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void updateUserEntity_WithValidData_UpdatesUser() throws Exception {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(UserEntity.class))).thenReturn(testUser);

        mockMvc.perform(patch("/user")
                .header("Authorization", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Updated Name\"}"))
                .andExpect(status().isOk());

        verify(userRepository).findById(1L);
        verify(userRepository).save(any(UserEntity.class));
    }

    @Test
    void deleteUserEntity_WithValidToken_DeletesUser() throws Exception {
        when(userRepository.existsById(1L)).thenReturn(true);

        mockMvc.perform(delete("/user")
                .header("Authorization", "1"))
                .andExpect(status().isOk());

        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteUserEntity_WithNonexistentUser_ReturnsNotFound() throws Exception {
        when(userRepository.existsById(1L)).thenReturn(false);

        mockMvc.perform(delete("/user")
                .header("Authorization", "1"))
                .andExpect(status().isNotFound());

        verify(userRepository, never()).deleteById(any());
    }
}
