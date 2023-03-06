package com.jpenacho.starwarsmoviesrepo.controller.character;

import com.jpenacho.starwarsmoviesrepo.service.character.CharacterService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Log4j2
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RestController
public class CharacterControllerImpl implements CharacterController {
    CharacterService characterService;

    @Override
    public Mono<CharacterResponse> createCharacter(CharacterCreateRequest characterCreaterequest) {
        return characterService.create(CharacterControllerMapper.INSTANCE.map(characterCreaterequest))
                .doFirst(() -> log.debug("createCharacter. Creating character={}", characterCreaterequest))
                .map(CharacterControllerMapper.INSTANCE::map)
                .doOnNext(createdCharacterResponse -> log.debug("createCharacter. Created character={}", createdCharacterResponse));
    }

    @Override
    public Flux<CharacterResponse> listCharacters() {
        return characterService.list()
                .map(CharacterControllerMapper.INSTANCE::map)
                .doFirst(() -> log.debug("listCharacters. Listing characters"))
                .doOnNext(characterResponse -> log.debug("listCharacters. Listed character={}", characterResponse));
    }

    @Override
    public Mono<CharacterResponse> getCharacter(UUID id) {
        return characterService.read(id)
                .map(CharacterControllerMapper.INSTANCE::map)
                .doFirst(() -> log.debug("getCharacter. Getting character id={}", id))
                .doOnNext(characterResponse -> log.debug("getCharacter. Got character={}", characterResponse));
    }

    @Override
    public Mono<CharacterResponse> updateCharacter(UUID id, CharacterUpdateRequest characterUpdateRequest) {
        return characterService.update(id, CharacterControllerMapper.INSTANCE.map(characterUpdateRequest))
                .doFirst(() -> log.debug("updateCharacter. Updating character ={}", characterUpdateRequest))
                .map(CharacterControllerMapper.INSTANCE::map)
                .doOnNext(updatedCharacterResponse -> log.debug("updateCharacter. Updated character={}", updatedCharacterResponse));
    }

    @Override
    public Mono<UUID> deleteCharacter(UUID id) {
        return characterService.delete(id)
                .doFirst(() -> log.debug("deleteCharacter. Deleting character id={}", id))
                .doOnNext(deletedCharacterId -> log.debug("deleteCharacter. Deleted character={}", deletedCharacterId));
    }
}
