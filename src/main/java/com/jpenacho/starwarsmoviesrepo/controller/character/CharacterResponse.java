package com.jpenacho.starwarsmoviesrepo.controller.character;

import lombok.Value;

import java.time.OffsetDateTime;
import java.util.UUID;

@Value
public class CharacterResponse {

    UUID id;

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
