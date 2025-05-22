package ru.mart.pioneer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import lombok.Data;

import java.math.BigDecimal;

@Schema(description = "Money transfer request")
@Data
public class TransferRequest {
    @Schema(description = "Recipient user ID", example = "2", required = true)
    private Long toUserId;

    @Schema(description = "Amount to transfer", example = "100.00", required = true)
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;
}
