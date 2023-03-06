package com.jpenacho.starwarsmoviesrepo.datasource.repository.character;

import com.jpenacho.starwarsmoviesrepo.datasource.repository.movie.MovieEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString
@Entity(name = "character")
public class CharacterEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
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

    @ManyToMany(mappedBy = "characters")
    @ToString.Exclude
    List<MovieEntity> movies;

    OffsetDateTime createdAt;

    OffsetDateTime updatedAt;

}
