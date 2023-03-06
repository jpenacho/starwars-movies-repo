package com.jpenacho.starwarsmoviesrepo.controller.character;

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

@RequestMapping(value = "/characters")
@Tag(name = "Characters", description = "CRUD Endpoints of Characters")
public interface CharacterController {

    @Operation(description = "Creates a new character entity", summary = "Create character")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    Mono<CharacterResponse> createCharacter(@Valid @RequestBody CharacterCreateRequest request);

    @Operation(description = "Lists the character entities that exist in the database",
            summary = "List character")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    Flux<CharacterResponse> listCharacters();

    @Operation(description = "Retrieves the character with the provided ID", summary = "Get character")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    Mono<CharacterResponse> getCharacter(@PathVariable UUID id);

    @Operation(description = "Updates an existing character entity", summary = "Update character")
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{id}")
    Mono<CharacterResponse> updateCharacter(@PathVariable UUID id, @Valid @RequestBody CharacterUpdateRequest request);

    @Operation(description = "Deletes an existing character entity", summary = "Delete character")
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{id}")
    Mono<UUID> deleteCharacter(@PathVariable UUID id);

}