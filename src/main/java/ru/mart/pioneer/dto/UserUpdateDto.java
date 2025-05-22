package ru.mart.pioneer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Set;

@Schema(description = "User update data")
@Data
@Accessors(chain = true)
public class UserUpdateDto {
    @Schema(description = "List of user's emails (must contain at least one)")
    @Size(min = 1, message = "At least one email is required")
    private Set<@Email String> emails;

    @Schema(description = "List of user's phones (must contain at least one)")
    @Size(min = 1, message = "At least one phone is required")
    private Set<@Pattern(regexp = "^\\+?[0-9]{10,15}$") String> phones;
}
