package com.jpenacho.starwarsmoviesrepo.service.character;

import com.jpenacho.starwarsmoviesrepo.datasource.external.swapi.LinkUtils;
import com.jpenacho.starwarsmoviesrepo.datasource.external.swapi.StarWarsPersonDto;
import com.jpenacho.starwarsmoviesrepo.datasource.repository.character.CharacterEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingInheritanceStrategy;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(mappingInheritanceStrategy = MappingInheritanceStrategy.AUTO_INHERIT_ALL_FROM_CONFIG, unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface CharacterMapper {
    CharacterMapper INSTANCE = Mappers.getMapper(CharacterMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "externalId", source = "url", qualifiedByName = "extractLastPathSegment")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    CharacterModel map(StarWarsPersonDto starWarsPersonDto);

    @Mapping(target = "movies", ignore = true)
    CharacterEntity map(CharacterModel character);

    CharacterModel map(CharacterEntity characterEntity);

    @Named("extractLastPathSegment")
    default Integer extractLastPathSegment(String uriStr) {
        return LinkUtils.extractLastPathSegment(uriStr);
    }
}
