package ru.mart.pioneer.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.mart.pioneer.dto.UserDto;
import ru.mart.pioneer.dto.UserFilter;
import ru.mart.pioneer.dto.UserUpdateDto;
import ru.mart.pioneer.service.UserService;
import ru.mart.pioneer.util.StringConstants;
import ru.mart.pioneer.util.StringDateConverter;


@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "User management operations")
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserService userService;

    @Operation(
            summary = "Get user by ID",
            description = "Returns user details by user ID",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUser(
            @Parameter(description = "ID of user to be retrieved", required = true)
            @PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @Operation(
            summary = "Search users",
            description = "Search users with filtering and pagination",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Users found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/search")
    public ResponseEntity<Page<UserDto>> searchUsers(
            @Parameter(description = "Filter by name (starts with)")
            @RequestParam(required = false) String name,

            @Parameter(description = "Filter by date of birth (greater than)")
            @RequestParam(required = false) @Pattern(regexp = StringConstants.DATE_OF_BIRTH_FORMAT_REGEXP) String dateOfBirth,

            @Parameter(description = "Filter by exact email match)")
            @RequestParam(required = false) String email,

            @Parameter(description = "Filter by exact phone match)")
            @RequestParam(required = false) String phone,

            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") int size) {

        UserFilter filter = new UserFilter(name, StringDateConverter.convertToLocalDate(dateOfBirth), email, phone);
        Pageable pageable = PageRequest.of(page, size);

        return ResponseEntity.ok(userService.searchUsers(filter, pageable));
    }

    @Operation(
            summary = "Update user",
            description = "Update user's emails and phones (only own data)",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User updated"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - can't update other users"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PutMapping("/{id}")
    @PreAuthorize("#id == principal.userId")
    public ResponseEntity<UserDto> updateUser(
            @Parameter(description = "ID of user to be updated", required = true)
            @PathVariable Long id,

            @RequestBody @Valid
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User update data",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UserUpdateDto.class)))
            UserUpdateDto updateDto) {

        return ResponseEntity.ok(userService.updateUser(id, updateDto));
    }
}
