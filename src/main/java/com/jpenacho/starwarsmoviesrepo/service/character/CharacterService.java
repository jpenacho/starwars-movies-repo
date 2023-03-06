package com.jpenacho.starwarsmoviesrepo.service.character;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface CharacterService {

    Mono<CharacterModel> create(CharacterModel newCharacter);

    Mono<CharacterModel> read(UUID id);

    Mono<CharacterModel> readByExternalId(Integer externalId);

    Flux<CharacterModel> list();

    Mono<CharacterModel> update(UUID id, CharacterModel updateCharacter);

    Mono<UUID> delete(UUID id);
}
