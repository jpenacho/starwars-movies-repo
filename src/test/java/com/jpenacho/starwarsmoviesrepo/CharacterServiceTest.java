package com.jpenacho.starwarsmoviesrepo;

import com.jpenacho.starwarsmoviesrepo.exception.ResourceNotFound;
import com.jpenacho.starwarsmoviesrepo.datasource.repository.character.CharacterEntity;
import com.jpenacho.starwarsmoviesrepo.datasource.repository.character.CharacterRepository;
import com.jpenacho.starwarsmoviesrepo.service.character.CharacterModel;
import com.jpenacho.starwarsmoviesrepo.service.character.CharacterService;
import org.instancio.Instancio;
import org.instancio.Model;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.test.StepVerifier;

import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.instancio.Select.field;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CharacterServiceTest {

    private static final int LIST_TEST_SIZE = 5;

    @Autowired
    CharacterService characterService;

    @Autowired
    CharacterRepository characterRepository;

    @BeforeEach
    void cleanDb() {
        characterRepository.deleteAll();
    }

    @Test
    void testCharacterCreate() {
        CharacterModel characterModel = Instancio.of(CharacterModel.class)
                .ignore(field(CharacterModel::getId))
                .create();

        StepVerifier.create(characterService.create(characterModel))
                .consumeNextWith(saveCharacterModel -> {
                    Assertions.assertNotNull(saveCharacterModel.getId());
                    AssertionUtils.assertEquals(saveCharacterModel, saveCharacterModel, "id");
                })
                .verifyComplete();
    }

    @Test
    void testCharacterUpdate() {
        CharacterEntity characterEntity = Instancio.create(getCharacterEntityModel());

        CharacterEntity savedCharacterEntity = characterRepository.save(characterEntity);

        CharacterModel updateCharacterModel = Instancio.create(getCharacterUpdateModel());

        StepVerifier.create(characterService.update(savedCharacterEntity.getId(), updateCharacterModel))
                .consumeNextWith(updatedCharacterEntity -> {
                    AssertionUtils.assertEquals(updateCharacterModel, updatedCharacterEntity, "id", "createdAt", "updatedAt");
                    Assertions.assertEquals(savedCharacterEntity.getId(), updatedCharacterEntity.getId());
                    Assertions.assertEquals(0, AssertionUtils.compareOffSetDateTimes(savedCharacterEntity.getCreatedAt(), updatedCharacterEntity.getCreatedAt()));
                })
                .verifyComplete();
    }

    @Test
    void testCharacterUpdateNotFound() {
        CharacterModel updateCharacterModel = Instancio.create(getCharacterUpdateModel());

        StepVerifier.create(characterService.update(UUID.randomUUID(), updateCharacterModel))
                .expectError(ResourceNotFound.class);
    }

    @Test
    void testCharacterGet() {
        CharacterEntity characterEntity = Instancio.create(getCharacterEntityModel());

        CharacterEntity savedCharacterEntity = characterRepository.save(characterEntity);

        StepVerifier.create(characterService.read(savedCharacterEntity.getId()))
                .consumeNextWith(readCharacterEntity -> {
                    assertReadCharacterEntity(savedCharacterEntity, readCharacterEntity);
                })
                .verifyComplete();
    }

    @Test
    void testCharacterGetNotFound() {
        StepVerifier.create(characterService.read(UUID.randomUUID()))
                .expectError(ResourceNotFound.class);
    }

    @Test
    void testCharacterGetByExternalId() {
        CharacterEntity characterEntity = Instancio.create(getCharacterEntityModel());

        CharacterEntity savedCharacterEntity = characterRepository.save(characterEntity);

        StepVerifier.create(characterService.readByExternalId(savedCharacterEntity.getExternalId()))
                .consumeNextWith(readCharacterEntity -> {
                    assertReadCharacterEntity(savedCharacterEntity, readCharacterEntity);
                })
                .verifyComplete();
    }

    @Test
    void testCharacterGetByExternalIdNotFound() {
        Random random = new Random();
        StepVerifier.create(characterService.readByExternalId(random.nextInt()))
                .expectError(ResourceNotFound.class);
    }

    @Test
    void testCharacterList() {
        Map<UUID, CharacterEntity> savedCharacterEntities = Instancio.stream(getCharacterEntityModel())
                .limit(LIST_TEST_SIZE)
                .map(characterRepository::save)
                .collect(Collectors.toMap(CharacterEntity::getId, (entry) -> entry));

        StepVerifier.create(characterService.list())
                .expectNextCount(LIST_TEST_SIZE)
                .thenConsumeWhile(unused -> true, readCharacterEntity -> {
                    CharacterEntity savedCharacterEntity = savedCharacterEntities.get(readCharacterEntity.getId());
                    assertReadCharacterEntity(savedCharacterEntity, readCharacterEntity);
                })
                .verifyComplete();
    }

    @Test
    void testCharacterDelete() {
        CharacterEntity characterEntity = Instancio.create(getCharacterEntityModel());

        CharacterEntity savedCharacterEntity = characterRepository.save(characterEntity);

        StepVerifier.create(characterService.read(savedCharacterEntity.getId()))
                .consumeNextWith(readCharacterEntity -> {
                    assertReadCharacterEntity(savedCharacterEntity, readCharacterEntity);
                })
                .verifyComplete();

        StepVerifier.create(characterService.delete(savedCharacterEntity.getId()))
                .consumeNextWith(deletedCharacterEntityId -> {
                    Assertions.assertEquals(savedCharacterEntity.getId(), deletedCharacterEntityId);
                })
                .verifyComplete();

        StepVerifier.create(characterService.read(savedCharacterEntity.getId()))
                .expectError(ResourceNotFound.class);
    }

    private static void assertReadCharacterEntity(CharacterEntity savedCharacterEntity, CharacterModel readCharacterModel) {
        AssertionUtils.assertEquals(savedCharacterEntity, readCharacterModel, "id", "movies");
        Assertions.assertEquals(savedCharacterEntity.getId(), readCharacterModel.getId());
    }

    private static Model<CharacterEntity> getCharacterEntityModel() {
        return Instancio.of(CharacterEntity.class)
                .ignore(field(CharacterEntity::getMovies))
                .toModel();
    }

    private static Model<CharacterModel> getCharacterUpdateModel() {
        return Instancio.of(CharacterModel.class)
                .ignore(field(CharacterModel::getId))
                .ignore(field(CharacterModel::getCreatedAt))
                .ignore(field(CharacterModel::getUpdatedAt))
                .toModel();
    }
}
