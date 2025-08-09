package org.example.absolutecinema.dto;

public record MovieForSessionDto(Long id,
                                 String title,
                                 int year,
                                 String genre,
                                 String image,
                                 String country) {
}
