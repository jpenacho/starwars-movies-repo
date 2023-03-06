package com.jpenacho.starwarsmoviesrepo.controller.movie;

import com.jpenacho.starwarsmoviesrepo.service.character.CharacterModel;
import lombok.Value;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

@Value
public class MovieResponse {

    UUID id;

    String title;

    Integer episodeId;

    String director;

    String producer;

    LocalDate releaseDate;

    String openingCrawl;

    Set<CharacterModel> characters;

    OffsetDateTime updatedAt;

    OffsetDateTime createdAt;
}
