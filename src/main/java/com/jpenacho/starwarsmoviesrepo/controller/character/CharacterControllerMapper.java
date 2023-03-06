package com.jpenacho.starwarsmoviesrepo.controller.character;

import com.jpenacho.starwarsmoviesrepo.service.character.CharacterModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingInheritanceStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(mappingInheritanceStrategy = MappingInheritanceStrategy.AUTO_INHERIT_ALL_FROM_CONFIG, unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface CharacterControllerMapper {
    CharacterControllerMapper INSTANCE = Mappers.getMapper(CharacterControllerMapper.class);

    CharacterResponse map(CharacterModel character);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "externalId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    CharacterModel map(CharacterCreateRequest characterCreateRequest);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "externalId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    CharacterModel map(CharacterUpdateRequest characterUpdateRequest);
}
