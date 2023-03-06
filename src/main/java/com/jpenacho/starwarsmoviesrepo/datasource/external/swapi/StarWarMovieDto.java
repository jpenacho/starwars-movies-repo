package com.jpenacho.starwarsmoviesrepo.datasource.external.swapi;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Set;

@Value
public class StarWarMovieDto implements StarWarDto {
    String title;

    @JsonProperty("episode_id")
    Integer episodeId;

    String director;

    String producer;

    @JsonProperty("release_date")
    LocalDate releaseDate;

    @JsonProperty("opening_crawl")
    String openingCrawl;

    Set<String> characters;

    OffsetDateTime created;

    OffsetDateTime edited;

    String url;
}