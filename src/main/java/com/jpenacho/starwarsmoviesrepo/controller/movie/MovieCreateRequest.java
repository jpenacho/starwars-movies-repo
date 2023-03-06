package com.jpenacho.starwarsmoviesrepo.controller.movie;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

import java.time.LocalDate;

@Value
public class MovieCreateRequest {

    @NotEmpty
    String title;

    @NotNull
    Integer episodeId;

    @NotEmpty
    String director;

    @NotEmpty
    String producer;

    @NotNull
    LocalDate releaseDate;

    String openingCrawl;
}
