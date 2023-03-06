package com.jpenacho.starwarsmoviesrepo.datasource.repository.movie;

import com.jpenacho.starwarsmoviesrepo.datasource.repository.character.CharacterEntityMappers;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.time.OffsetDateTime;

@Mapper(
        uses = CharacterEntityMappers.class,
        imports = OffsetDateTime.class,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface MovieEntityMappers {
    MovieEntityMappers INSTANCE = Mappers.getMapper(MovieEntityMappers.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", expression = "java(OffsetDateTime.now())")
    void updateEntity(@MappingTarget MovieEntity movieEntity, MovieEntity updatedMovieEntity);

    MovieEntity map(MovieEntity movieEntity);
}