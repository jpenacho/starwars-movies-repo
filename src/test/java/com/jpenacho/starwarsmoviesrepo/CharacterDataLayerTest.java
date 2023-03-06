package com.jpenacho.starwarsmoviesrepo;

import com.jpenacho.starwarsmoviesrepo.exception.ResourceNotFound;
import com.jpenacho.starwarsmoviesrepo.datasource.repository.ExternalIdDatabaseService;
import com.jpenacho.starwarsmoviesrepo.datasource.repository.character.CharacterEntity;
import com.jpenacho.starwarsmoviesrepo.datasource.repository.character.CharacterRepository;
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
public class CharacterDataLayerTest {

    private static final int LIST_TEST_SIZE = 5;

    @Autowired
    ExternalIdDatabaseService<CharacterEntity, UUID, Integer> characterDatabaseService;

    @Autowired
    CharacterRepository characterRepository;

    @BeforeEach
    void cleanDb() {
        characterRepository.deleteAll();
    }

    @Test
    void testCharacterCreate() {
        CharacterEntity characterEntity = Instancio.create(getCharacterEntityModel());

        StepVerifier.create(characterDatabaseService.create(characterEntity))
                .consumeNextWith(saveCharacterEntity -> {
                    Assertions.assertNotNull(saveCharacterEntity.getId());
                    AssertionUtils.assertEquals(characterEntity, saveCharacterEntity, "id");
                })
                .verifyComplete();

    }

    @Test
    void testCharacterUpdate() {
        CharacterEntity characterEntity = Instancio.create(getCharacterEntityModel());

        CharacterEntity savedCharacterEntity = characterRepository.save(characterEntity);

        CharacterEntity updateCharacterEntity = Instancio.create(getCharacterEntityModel());

        StepVerifier.create(characterDatabaseService.update(savedCharacterEntity.getId(), updateCharacterEntity))
                .consumeNextWith(updatedCharacterEntity -> {
                    AssertionUtils.assertEquals(updateCharacterEntity, updatedCharacterEntity, "id", "createdAt", "updatedAt");
                    Assertions.assertEquals(savedCharacterEntity.getId(), updatedCharacterEntity.getId());
                    Assertions.assertEquals(0, AssertionUtils.compareOffSetDateTimes(savedCharacterEntity.getCreatedAt(), updatedCharacterEntity.getCreatedAt()));
                })
                .verifyComplete();
    }

    @Test
    void testCharacterUpdateNotFound() {
        CharacterEntity updateCharacterEntity = Instancio.create(getCharacterEntityModel());

        StepVerifier.create(characterDatabaseService.update(updateCharacterEntity.getId(), updateCharacterEntity))
                .expectError(ResourceNotFound.class);
    }

    @Test
    void testCharacterGet() {
        CharacterEntity characterEntity = Instancio.create(getCharacterEntityModel());

        CharacterEntity savedCharacterEntity = characterRepository.save(characterEntity);

        StepVerifier.create(characterDatabaseService.read(savedCharacterEntity.getId()))
                .consumeNextWith(readCharacterEntity -> {
                    assertReadCharacterEntity(savedCharacterEntity, readCharacterEntity);
                })
                .verifyComplete();
    }

    @Test
    void testCharacterGetNotFound() {
        StepVerifier.create(characterDatabaseService.read(UUID.randomUUID()))
                .expectError(ResourceNotFound.class);
    }

    @Test
    void testCharacterGetByExternalId() {
        CharacterEntity characterEntity = Instancio.create(getCharacterEntityModel());

        CharacterEntity savedCharacterEntity = characterRepository.save(characterEntity);

        StepVerifier.create(characterDatabaseService.readByExternalId(savedCharacterEntity.getExternalId()))
                .consumeNextWith(readCharacterEntity -> {
                    assertReadCharacterEntity(savedCharacterEntity, readCharacterEntity);
                })
                .verifyComplete();
    }

    @Test
    void testCharacterGetByExternalIdNotFound() {
        Random random = new Random();
        StepVerifier.create(characterDatabaseService.readByExternalId(random.nextInt()))
                .expectError(ResourceNotFound.class);
    }

    @Test
    void testCharacterList() {
        Map<UUID, CharacterEntity> savedCharacterEntities = Instancio.stream(getCharacterEntityModel())
                .limit(LIST_TEST_SIZE)
                .map(characterRepository::save)
                .collect(Collectors.toMap(CharacterEntity::getId, (entry) -> entry));

        StepVerifier.create(characterDatabaseService.list())
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

        StepVerifier.create(characterDatabaseService.read(savedCharacterEntity.getId()))
                .consumeNextWith(readCharacterEntity -> {
                    assertReadCharacterEntity(savedCharacterEntity, readCharacterEntity);
                })
                .verifyComplete();

        StepVerifier.create(characterDatabaseService.delete(savedCharacterEntity.getId()))
                .consumeNextWith(deletedCharacterEntityId -> {
                    Assertions.assertEquals(savedCharacterEntity.getId(), deletedCharacterEntityId);
                })
                .verifyComplete();

        StepVerifier.create(characterDatabaseService.read(savedCharacterEntity.getId()))
                .expectError(ResourceNotFound.class);
    }

    private static void assertReadCharacterEntity(CharacterEntity savedCharacterEntity, CharacterEntity readCharacterEntity) {
        AssertionUtils.assertEquals(savedCharacterEntity, readCharacterEntity, "id", "characters");
        Assertions.assertEquals(savedCharacterEntity.getId(), readCharacterEntity.getId());
        Assertions.assertNull(readCharacterEntity.getMovies());
    }

    private static Model<CharacterEntity> getCharacterEntityModel() {
        return Instancio.of(CharacterEntity.class)
                .ignore(field(CharacterEntity::getMovies))
                .toModel();
    }
}
