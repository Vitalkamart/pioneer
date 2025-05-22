package ru.mart.pioneer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import ru.mart.pioneer.util.PastDate;
import ru.mart.pioneer.util.StringConstants;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;

@Schema(description = "User data")
@Data
@Builder
public class UserDto implements Serializable {
    @Schema(description = "User ID", example = "1")
    private Long id;

    @Schema(description = "User name", example = "John Doe")
    @NotBlank(message = "Name is required")
    @Size(max = 500, message = "Name must be less than 500 characters")
    private String name;

    @Schema(description = "Date of birth", example = "01.05.1993")
    @PastDate
    @NotBlank(message = "Date of birth is required")
    @Pattern(regexp = StringConstants.DATE_OF_BIRTH_FORMAT_REGEXP, message = "Date format must be DD.MM.YYYY")
    private String dateOfBirth;

    @Schema(description = "List of emails")
    @Size(min = 1, message = "At least one email is required")
    private Set<@Email(message = "Email should be valid")
    @Size(max = 200, message = "Email must be less than 200 characters") String> emails;

    @Schema(description = "List of phones")
    @Size(min = 1, message = "At least one phone is required")
    private Set<@Pattern(regexp = StringConstants.PHONE_REGEXP, message = "Phone must start with 7 and contain 11 digits")
    @Size(max = 13, message = "Phone must be less than 13 characters") String> phones;

    @Schema(description = "Account balance", example = "1000.00")
    private BigDecimal balance;
}
