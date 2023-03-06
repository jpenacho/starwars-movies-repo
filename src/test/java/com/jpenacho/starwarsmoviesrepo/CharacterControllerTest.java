package com.jpenacho.starwarsmoviesrepo;

import com.jpenacho.starwarsmoviesrepo.controller.character.CharacterCreateRequest;
import com.jpenacho.starwarsmoviesrepo.controller.character.CharacterResponse;
import com.jpenacho.starwarsmoviesrepo.controller.character.CharacterUpdateRequest;
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
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.instancio.Select.field;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CharacterControllerTest {

    private static final int LIST_TEST_SIZE = 5;
    private static final String CHARACTERS_BASE_URI = "/characters";
    private static final String CHARACTERS_URI_ID_PLACEHOLDER = CHARACTERS_BASE_URI + "/{id}";

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    CharacterRepository characterRepository;

    @BeforeEach
    void cleanDb() {
        characterRepository.deleteAll();
    }

    @Test
    void testCharacterCreate() {
        CharacterCreateRequest characterCreateRequest = Instancio.create(CharacterCreateRequest.class);

        CharacterResponse savedCharacterResponse = webTestClient.post()
                .uri(CHARACTERS_BASE_URI)
                .bodyValue(characterCreateRequest)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(CharacterResponse.class)
                .returnResult()
                .getResponseBody();

        Assertions.assertNotNull(savedCharacterResponse);
        Assertions.assertNotNull(savedCharacterResponse.getId());
        Assertions.assertNull(savedCharacterResponse.getUpdatedAt());
        AssertionUtils.assertEquals(characterCreateRequest, savedCharacterResponse, "id", "created", "updated");
    }

    @Test
    void testCharacterUpdate() {
        CharacterEntity characterEntity = Instancio.of(getCharacterEntityModel())
                .ignore(field(CharacterEntity::getUpdatedAt))
                .create();

        CharacterEntity savedCharacterEntity = characterRepository.save(characterEntity);

        CharacterUpdateRequest characterUpdateRequest = Instancio.create(CharacterUpdateRequest.class);

        CharacterResponse updatedCharacterResponse = webTestClient.put()
                .uri(CHARACTERS_URI_ID_PLACEHOLDER, savedCharacterEntity.getId())
                .bodyValue(characterUpdateRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CharacterResponse.class)
                .returnResult()
                .getResponseBody();

        Assertions.assertNotNull(updatedCharacterResponse);
        Assertions.assertEquals(savedCharacterEntity.getId(), updatedCharacterResponse.getId());
        Assertions.assertNotNull(updatedCharacterResponse.getUpdatedAt());
        AssertionUtils.assertEquals(characterUpdateRequest, updatedCharacterResponse, "id", "created", "updated");
    }

    @Test
    void testCharacterUpdateNotFound() {
        CharacterUpdateRequest characterUpdateRequest = Instancio.create(CharacterUpdateRequest.class);

        webTestClient.put()
                .uri(CHARACTERS_URI_ID_PLACEHOLDER, UUID.randomUUID())
                .bodyValue(characterUpdateRequest)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testCharacterGet() {
        CharacterEntity characterEntity = Instancio.of(getCharacterEntityModel())
                .ignore(field(CharacterEntity::getUpdatedAt))
                .create();

        CharacterEntity savedCharacterEntity = characterRepository.save(characterEntity);

        CharacterResponse gotCharacterResponse = webTestClient.get()
                .uri(CHARACTERS_URI_ID_PLACEHOLDER, savedCharacterEntity.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody(CharacterResponse.class)
                .returnResult()
                .getResponseBody();

        Assertions.assertNotNull(gotCharacterResponse);
        AssertionUtils.assertEquals(savedCharacterEntity, gotCharacterResponse, "externalId", "movies");
    }

    @Test
    void testCharacterGetNotFound() {
        webTestClient.get()
                .uri(CHARACTERS_URI_ID_PLACEHOLDER, UUID.randomUUID())
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testCharacterList() {
        Map<UUID, CharacterEntity> savedCharacterEntities = Instancio.stream(getCharacterEntityModel())
                .limit(LIST_TEST_SIZE)
                .map(characterRepository::save)
                .collect(Collectors.toMap(CharacterEntity::getId, (entry) -> entry));

        List<CharacterResponse> gotCharactersResponse = webTestClient.get()
                .uri(CHARACTERS_BASE_URI)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<CharacterResponse>>() {})
                .returnResult()
                .getResponseBody();

        Assertions.assertNotNull(gotCharactersResponse);

        for (CharacterResponse characterResponse : gotCharactersResponse) {
            Assertions.assertNotNull(characterResponse);
            AssertionUtils.assertEquals(savedCharacterEntities.get(characterResponse.getId()), characterResponse, "externalId", "movies");
        }
    }

    @Test
    void testCharacterDelete() {
        CharacterEntity characterEntity = Instancio.create(getCharacterEntityModel());

        CharacterEntity savedCharacterEntity = characterRepository.save(characterEntity);

        UUID deleteCharacterId = webTestClient.delete()
                .uri(CHARACTERS_URI_ID_PLACEHOLDER, savedCharacterEntity.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody(UUID.class)
                .returnResult()
                .getResponseBody();

        Assertions.assertEquals(savedCharacterEntity.getId(), deleteCharacterId);

        webTestClient.get()
                .uri(CHARACTERS_URI_ID_PLACEHOLDER, UUID.randomUUID())
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testCharacterDeletedNotFound() {
        webTestClient.delete()
                .uri(CHARACTERS_URI_ID_PLACEHOLDER, UUID.randomUUID())
                .exchange()
                .expectStatus().isNotFound();
    }

    private static Model<CharacterEntity> getCharacterEntityModel() {
        return Instancio.of(CharacterEntity.class)
                .ignore(field(CharacterEntity::getMovies))
                .toModel();
    }
}