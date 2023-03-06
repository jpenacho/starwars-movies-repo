package com.jpenacho.starwarsmoviesrepo.datasource.repository.character;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CharacterRepository extends CrudRepository<CharacterEntity, UUID> {
    CharacterEntity findByExternalId(Integer externalId);
}
