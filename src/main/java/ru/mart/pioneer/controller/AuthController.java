package ru.mart.pioneer.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mart.pioneer.dto.AuthRequest;
import ru.mart.pioneer.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Authentication operations")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @Operation(
            summary = "Authenticate user",
            description = "Authenticates user by email/phone and password, returns JWT token"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully authenticated"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @PostMapping("/login")
    public ResponseEntity<String> login(
            @RequestBody @Valid
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Authentication request",
                    required = true,
                    content = @Content(schema = @Schema(implementation = AuthRequest.class)))
            AuthRequest request) {
        return ResponseEntity.ok(authService.authenticate(request.getLogin(), request.getPassword()));
    }
}
