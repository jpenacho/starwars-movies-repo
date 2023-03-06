package com.jpenacho.starwarsmoviesrepo.service.character;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Value;

import java.time.OffsetDateTime;
import java.util.UUID;

@Value
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class CharacterModel {
    UUID id;

    Integer externalId;

    String name;

    String height;

    String mass;

    String hairColor;

    String skinColor;

    String eyeColor;

    String birthYear;

    String gender;

    OffsetDateTime createdAt;

    OffsetDateTime updatedAt;
}
