package org.example.absolutecinema.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO с приватными данными пользователя для аутентификации.<br>
 * Используется при логине (передаётся в запросе).
 */
@Data
public class PrivateUserDto {
    @NotBlank(message = "Логин не может быть пустыми")
    @Email(message = "Некорректный адрес электронной почты")
    private String username;
    @NotBlank(message = "Пароль не может быть пустыми")
    @Size(min = 8, max = 50, message = "Пароль должен быть от 8 до 50 символов")
    private String password;
}
