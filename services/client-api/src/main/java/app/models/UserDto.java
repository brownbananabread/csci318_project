package app.models;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "User data transfer object")
public class UserDto {
    @Schema(description = "User's unique identifier (auto-generated)", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private String id;

    @Schema(description = "User's full name", example = "John Doe", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "User's email address (must be unique)", example = "john.doe@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @Schema(description = "User's password", example = "mySecurePassword123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

    public UserDto() {}

    public UserDto(String id, String name, String email, String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}