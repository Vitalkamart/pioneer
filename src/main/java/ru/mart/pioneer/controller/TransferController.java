package ru.mart.pioneer.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mart.pioneer.dto.TransferRequest;
import ru.mart.pioneer.security.UserDetailsImpl;
import ru.mart.pioneer.service.TransferService;

@RestController
@RequestMapping("/api/transfers")
@Tag(name = "Transfers", description = "Money transfer operations")
@RequiredArgsConstructor
public class TransferController {
    private final TransferService transferService;

    @Operation(summary = "Transfer money", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transfer successful"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Account not found"),
            @ApiResponse(responseCode = "422", description = "Insufficient funds")
    })
    @PostMapping
    public ResponseEntity<Void> transfer(
            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetailsImpl userDetails,

            @RequestBody @Valid TransferRequest request) {
        transferService.transfer(userDetails.getUserId(), request.getToUserId(), request.getAmount());
        return ResponseEntity.ok().build();
    }
}
