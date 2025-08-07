package org.example.absolutecinema.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity @Table(name = "movies")
public class Movie {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private int year;
    @Column(nullable = false)
    private String genre;
    @Column(nullable = false)
    private String description;
    @Column(nullable = false)
    private int duration;
    @Column(nullable = false)
    private String image;

}
