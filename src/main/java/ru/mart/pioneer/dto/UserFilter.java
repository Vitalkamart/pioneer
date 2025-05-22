package ru.mart.pioneer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Фильтр для поиска пользователей")
public class UserFilter implements Serializable {

    @Schema(description = "Имя пользователя (поиск по началу строки)", example = "Иван")
    private String name;

    @Schema(description = "Дата рождения (искать пользователей, родившихся после указанной даты)",
            example = "01.01.1990")
    private LocalDate dateOfBirth;

    @Schema(description = "Email (точное совпадение)", example = "user@example.com")
    private String email;

    @Schema(description = "Телефон (точное совпадение)", example = "79161234567")
    private String phone;
}
