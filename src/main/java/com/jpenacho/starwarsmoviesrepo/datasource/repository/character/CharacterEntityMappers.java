package com.jpenacho.starwarsmoviesrepo.datasource.repository.character;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingInheritanceStrategy;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(
        mappingInheritanceStrategy = MappingInheritanceStrategy.AUTO_INHERIT_ALL_FROM_CONFIG,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface CharacterEntityMappers {
    CharacterEntityMappers INSTANCE = Mappers.getMapper(CharacterEntityMappers.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    CharacterEntity updateEntity(@MappingTarget CharacterEntity characterEntity, CharacterEntity updatedCharacterEntity);

    @Mapping(target = "movies", ignore = true)
    CharacterEntity map(CharacterEntity characterEntity);
}
