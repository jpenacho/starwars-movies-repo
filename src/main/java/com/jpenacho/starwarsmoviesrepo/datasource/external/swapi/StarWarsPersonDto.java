package com.jpenacho.starwarsmoviesrepo.datasource.external.swapi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import java.time.OffsetDateTime;

@Value
@JsonIgnoreProperties(ignoreUnknown = true)
public class StarWarsPersonDto implements StarWarDto {
    String name;

    String height;

    String mass;

    @JsonProperty("hair_color")
    String hairColor;

    @JsonProperty("skin_color")
    String skinColor;

    @JsonProperty("eye_color")
    String eyeColor;

    @JsonProperty("birth_year")
    String birthYear;

    String gender;

    OffsetDateTime edited;

    OffsetDateTime created;

    String url;
}
