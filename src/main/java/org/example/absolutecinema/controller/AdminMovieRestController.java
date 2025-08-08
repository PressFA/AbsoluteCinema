package org.example.absolutecinema.controller;

import lombok.RequiredArgsConstructor;
import org.example.absolutecinema.dto.CreateMovieDto;
import org.example.absolutecinema.dto.IdMovieDto;
import org.example.absolutecinema.dto.InfoMovieDto;
import org.example.absolutecinema.service.MovieService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/movies")
public class AdminMovieRestController {
    private final MovieService movieService;

    @GetMapping
    public List<InfoMovieDto> getAllMovie() {
        return movieService.findAllMovies();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Void> addMovie(@RequestBody CreateMovieDto movieDto) {
        IdMovieDto createdMovieDto = movieService.createMovie(movieDto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdMovieDto.id())
                .toUri();
        return ResponseEntity.created(location).build();
    }
}
