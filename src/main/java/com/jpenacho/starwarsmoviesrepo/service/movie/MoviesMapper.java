package com.jpenacho.starwarsmoviesrepo.service.movie;

import com.jpenacho.starwarsmoviesrepo.datasource.external.swapi.LinkUtils;
import com.jpenacho.starwarsmoviesrepo.datasource.external.swapi.StarWarMovieDto;
import com.jpenacho.starwarsmoviesrepo.datasource.repository.movie.MovieEntity;
import com.jpenacho.starwarsmoviesrepo.service.character.CharacterMapper;
import com.jpenacho.starwarsmoviesrepo.service.character.CharacterModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.Set;

@Mapper(uses = CharacterMapper.class, unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface MoviesMapper {
    MoviesMapper INSTANCE = Mappers.getMapper(MoviesMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "characters", source = "characters")
    @Mapping(target = "externalId", source = "starWarMovieDto.url", qualifiedByName = "extractExternalId")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    MovieModel create(StarWarMovieDto starWarMovieDto, Set<CharacterModel> characters);

    MovieEntity map(MovieModel movie);

    MovieModel map(MovieEntity movieEntity);

    @Named("extractExternalId")
    default Integer extractExternalId(String uriStr) {
        return LinkUtils.extractLastPathSegment(uriStr);
    }
}
