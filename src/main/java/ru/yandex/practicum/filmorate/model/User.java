package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {
    Long id;

    @NotBlank(message = "Электронная почта не может быть пустой")
    @Email(message = "Электронная почта должна быть в корректном формате")
    String email;

    @NotBlank(message = "Логин не может быть пустым")
    @Pattern(regexp = "^\\S+$", message = "Логин не может содержать пробелы")
    String login;

    String name;

    @Past(message = "Дата рождения не может быть в будущем")
    @NotNull(message = "Дата рождения не может быть пустой")
    LocalDate birthday;

    Set<Long> friends = new HashSet<>();
}
