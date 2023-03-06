package com.jpenacho.starwarsmoviesrepo.controller.movie;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequestMapping(value = "/movies")
@Tag(name = "Movies", description = "CRUD Endpoints of Movies")
public interface MovieController {

    @Operation(description = "Creates a new movie entity", summary = "Create movie")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    Mono<MovieResponse> createMovie(@Valid @RequestBody MovieCreateRequest movieCreaterequest);

    @Operation(description = "Lists the movies entities that exist in the database",
            summary = "List movies")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    Flux<MovieResponse> listMovies();

    @Operation(description = "Retrieves the movie with the provided ID", summary = "Get movie")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    Mono<MovieResponse> getMovie(@PathVariable UUID id);

    @Operation(description = "Updates an existing movie entity", summary = "Update movie")
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{id}")
    Mono<MovieResponse> updateMovie(@PathVariable UUID id, @Valid @RequestBody MovieUpdateRequest movieUpdateRequest);

    @Operation(description = "Deletes an existing movie entity", summary = "Delete movie")
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{id}")
    Mono<UUID> deleteMovie(@PathVariable UUID id);

}
