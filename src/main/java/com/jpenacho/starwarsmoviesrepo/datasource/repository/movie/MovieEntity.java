package com.jpenacho.starwarsmoviesrepo.datasource.repository.movie;

import com.jpenacho.starwarsmoviesrepo.datasource.repository.character.CharacterEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@ToString
@Entity(name = "movies")
public class MovieEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    UUID id;

    Integer externalId;

    String title;

    Integer episodeId;

    String director;

    String producer;

    LocalDate releaseDate;

    @Column(columnDefinition = "VARCHAR(1000)")
    String openingCrawl;

    @ManyToMany
    @JoinTable(
            name = "movie_characters",
            joinColumns = @JoinColumn(name = "movies_id"),
            inverseJoinColumns = @JoinColumn(name = "characters_id")
    )
    @ToString.Exclude
    Set<CharacterEntity> characters;

    OffsetDateTime updatedAt;

    OffsetDateTime createdAt;

}
