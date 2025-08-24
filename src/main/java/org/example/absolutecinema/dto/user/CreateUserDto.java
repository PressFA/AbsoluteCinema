package org.example.absolutecinema.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO для запроса на регистрацию нового пользователя.<br>
 * Используется при регистрации через AuthService.
 */
@Data
public class CreateUserDto {
    @NotBlank(message = "Логин не может быть пустыми")
    @Email(message = "Некорректный адрес электронной почты")
    private String username;
    @NotBlank(message = "Имя не может быть пустыми")
    @Size(min = 2, max = 30, message = "Допустимый размер имени от 2 до 30 символов")
    private String name;
    @NotBlank(message = "Пароль не может быть пустыми")
    @Size(min = 8, max = 50, message = "Пароль должен быть от 8 до 50 символов")
    private String password;
}