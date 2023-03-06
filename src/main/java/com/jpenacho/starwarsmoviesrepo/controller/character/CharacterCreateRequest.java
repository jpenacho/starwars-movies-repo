package com.jpenacho.starwarsmoviesrepo.controller.character;

import jakarta.validation.constraints.NotEmpty;
import lombok.Value;

@Value
public class CharacterCreateRequest {

    @NotEmpty
    String name;

    @NotEmpty
    String height;

    @NotEmpty
    String mass;

    @NotEmpty
    String hairColor;

    @NotEmpty
    String skinColor;

    @NotEmpty
    String eyeColor;

    @NotEmpty
    String birthYear;

    @NotEmpty
    String gender;

}
