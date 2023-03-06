package com.jpenacho.starwarsmoviesrepo.controller.movie;

import com.jpenacho.starwarsmoviesrepo.service.character.CharacterMapper;
import com.jpenacho.starwarsmoviesrepo.service.movie.MovieModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(uses = CharacterMapper.class, unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface MoviesControllerMapper {
    MoviesControllerMapper INSTANCE = Mappers.getMapper(MoviesControllerMapper.class);

    MovieResponse map(MovieModel movie);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "externalId", ignore = true)
    @Mapping(target = "characters", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    MovieModel map(MovieCreateRequest movieCreateRequest);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "externalId", ignore = true)
    @Mapping(target = "characters", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    MovieModel map(MovieUpdateRequest movieUpdateRequest);
}
