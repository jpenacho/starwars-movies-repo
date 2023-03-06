package com.jpenacho.starwarsmoviesrepo.service.character;

import com.jpenacho.starwarsmoviesrepo.datasource.repository.ExternalIdDatabaseService;
import com.jpenacho.starwarsmoviesrepo.datasource.repository.character.CharacterEntity;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Log4j2
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Service
public class CharacterServiceImpl implements CharacterService {

    ExternalIdDatabaseService<CharacterEntity, UUID, Integer> characterDatabaseService;

    @Override
    public Mono<CharacterModel> create(CharacterModel newCharacter) {
        return characterDatabaseService.create(CharacterMapper.INSTANCE.map(newCharacter))
                .map(CharacterMapper.INSTANCE::map)
                .doOnNext(character -> log.debug("create. Created character={}", character));
    }

    @Override
    public Mono<CharacterModel> read(UUID id) {
        return characterDatabaseService.read(id)
                .map(CharacterMapper.INSTANCE::map)
                .doOnNext(character -> log.debug("read. Read character={}", character));
    }

    @Override
    public Mono<CharacterModel> readByExternalId(Integer externalId) {
        return characterDatabaseService.readByExternalId(externalId)
                .map(CharacterMapper.INSTANCE::map)
                .doOnNext(character -> log.debug("readByExternalId. Read character={}", character));
    }

    @Override
    public Flux<CharacterModel> list() {
        return characterDatabaseService.list()
                .map(CharacterMapper.INSTANCE::map)
                .doOnNext(character -> log.debug("list. List character={}", character));
    }

    @Override
    public Mono<CharacterModel> update(UUID id, CharacterModel updateCharacter) {
        return characterDatabaseService.update(id, CharacterMapper.INSTANCE.map(updateCharacter))
                .map(CharacterMapper.INSTANCE::map)
                .doOnNext(character -> log.debug("update. Updated character={}", character));
    }

    @Override
    public Mono<UUID> delete(UUID id) {
        return characterDatabaseService.delete(id)
                .doOnNext(deletedId -> log.debug("delete. Deleted character={}", deletedId));
    }
}
