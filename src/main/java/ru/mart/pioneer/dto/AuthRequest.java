package ru.mart.pioneer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Schema(description = "Authentication request")
@Data
public class AuthRequest {
    @Schema(description = "User's email or phone", example = "user@example.com", required = true)
    @NotBlank(message = "Login cannot be blank")
    private String login;

    @Schema(description = "User's password", example = "password123", required = true)
    @NotBlank(message = "Password cannot be blank")
    private String password;
}
