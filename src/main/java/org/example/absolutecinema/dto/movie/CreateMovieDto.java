package org.example.absolutecinema.dto.movie;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.example.absolutecinema.utils.annotation.MaxCurrentYear;
import org.hibernate.validator.constraints.URL;

/**
 * DTO для создания нового фильма.<br>
 * Используется в запросах на добавление фильма.<br>
 * Содержит все данные, необходимые для создания записи фильма в базе.
 */
@Data
public class CreateMovieDto {
    @NotBlank(message = "Название фильма обязательно")
    @Size(min = 1, max = 100, message = "Название фильма должно быть от 1 до 100 символов")
    private String title;
    @NotNull(message = "Год фильма обязателен")
    @Min(value = 1927, message = "Минимальный год фильма - 1927 год")
    @MaxCurrentYear
    private Integer year;
    @NotBlank(message = "Жанр фильма обязателен")
    @Size(min = 3, max = 30, message = "Жанр должен быть от 3 до 30 символов")
    private String genre;
    @NotBlank(message = "Описание фильма не может быть пустым")
    @Size(min = 10, max = 2000, message = "Описание должно быть от 10 до 2000 символов")
    private String description;
    @NotNull(message = "Длительность фильма обязательна")
    @Min(value = 40, message = "Кинотеатр показывает только полнометражные фильмы (>= 40)")
    private Integer duration;
    @NotBlank(message = "Постер фильма обязателен")
    @URL(message = "Строка не соответствует формату веб-ссылки")
    @Size(max = 500, message = "Ссылка на постер не может превышать 500 символов")
    private String image;
    @NotBlank(message = "Страна производства фильма обязательно")
    @Size(min = 3, max = 70, message = "Название страны должно быть от 3 до 70 символов")
    private String country;
}
