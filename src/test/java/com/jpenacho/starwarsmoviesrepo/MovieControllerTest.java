package com.jpenacho.starwarsmoviesrepo;

import com.jpenacho.starwarsmoviesrepo.controller.movie.MovieCreateRequest;
import com.jpenacho.starwarsmoviesrepo.controller.movie.MovieResponse;
import com.jpenacho.starwarsmoviesrepo.controller.movie.MovieUpdateRequest;
import com.jpenacho.starwarsmoviesrepo.datasource.repository.movie.MovieEntity;
import com.jpenacho.starwarsmoviesrepo.datasource.repository.movie.MovieRepository;
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
public class MovieControllerTest {

    private static final int LIST_TEST_SIZE = 5;
    private static final String MOVIES_BASE_URI = "/movies";
    private static final String MOVIES_URI_ID_PLACEHOLDER = MOVIES_BASE_URI + "/{id}";

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    MovieRepository movieRepository;

    @BeforeEach
    void cleanDb() {
        movieRepository.deleteAll();
    }

    @Test
    void testMovieCreate() {
        MovieCreateRequest movieCreateRequest = Instancio.create(MovieCreateRequest.class);

        MovieResponse savedMovieResponse = webTestClient.post()
                .uri(MOVIES_BASE_URI)
                .bodyValue(movieCreateRequest)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(MovieResponse.class)
                .returnResult()
                .getResponseBody();

        Assertions.assertNotNull(savedMovieResponse);
        Assertions.assertNotNull(savedMovieResponse.getId());
        Assertions.assertNull(savedMovieResponse.getUpdatedAt());
        AssertionUtils.assertEquals(movieCreateRequest, savedMovieResponse, "id", "created", "updated");
    }

    @Test
    void testMovieUpdate() {
        MovieEntity movieEntity = Instancio.of(getMovieEntityModel())
                .ignore(field(MovieEntity::getUpdatedAt))
                .create();

        MovieEntity savedMovieEntity = movieRepository.save(movieEntity);

        MovieUpdateRequest movieUpdateRequest = Instancio.create(MovieUpdateRequest.class);

        MovieResponse updatedMovieResponse = webTestClient.put()
                .uri(MOVIES_URI_ID_PLACEHOLDER, savedMovieEntity.getId())
                .bodyValue(movieUpdateRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(MovieResponse.class)
                .returnResult()
                .getResponseBody();

        Assertions.assertNotNull(updatedMovieResponse);
        Assertions.assertEquals(savedMovieEntity.getId(), updatedMovieResponse.getId());
        Assertions.assertNotNull(updatedMovieResponse.getUpdatedAt());
        AssertionUtils.assertEquals(movieUpdateRequest, updatedMovieResponse, "id", "created", "updated");
    }

    @Test
    void testMovieUpdateNotFound() {
        MovieUpdateRequest movieUpdateRequest = Instancio.create(MovieUpdateRequest.class);

        webTestClient.put()
                .uri(MOVIES_URI_ID_PLACEHOLDER, UUID.randomUUID())
                .bodyValue(movieUpdateRequest)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testMovieGet() {
        MovieEntity movieEntity = Instancio.of(getMovieEntityModel())
                .ignore(field(MovieEntity::getUpdatedAt))
                .create();

        MovieEntity savedMovieEntity = movieRepository.save(movieEntity);

        MovieResponse gotMovieResponse = webTestClient.get()
                .uri(MOVIES_URI_ID_PLACEHOLDER, savedMovieEntity.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody(MovieResponse.class)
                .returnResult()
                .getResponseBody();

        Assertions.assertNotNull(gotMovieResponse);
        AssertionUtils.assertEquals(savedMovieEntity, gotMovieResponse, "externalId", "characters");
    }

    @Test
    void testMovieGetNotFound() {
        webTestClient.get()
                .uri(MOVIES_URI_ID_PLACEHOLDER, UUID.randomUUID())
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testMovieList() {
        Map<UUID, MovieEntity> savedMovieEntities = Instancio.stream(getMovieEntityModel())
                .limit(LIST_TEST_SIZE)
                .map(movieRepository::save)
                .collect(Collectors.toMap(MovieEntity::getId, (entry) -> entry));

        List<MovieResponse> gotMoviesResponse = webTestClient.get()
                .uri(MOVIES_BASE_URI)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<MovieResponse>>() {})
                .returnResult()
                .getResponseBody();

        Assertions.assertNotNull(gotMoviesResponse);

        for (MovieResponse movieResponse : gotMoviesResponse) {
            Assertions.assertNotNull(movieResponse);
            AssertionUtils.assertEquals(savedMovieEntities.get(movieResponse.getId()), movieResponse, "externalId", "characters");
        }
    }

    @Test
    void testMovieDelete() {
        MovieEntity movieEntity = Instancio.create(getMovieEntityModel());

        MovieEntity savedMovieEntity = movieRepository.save(movieEntity);

        UUID deleteMovieId = webTestClient.delete()
                .uri(MOVIES_URI_ID_PLACEHOLDER, savedMovieEntity.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody(UUID.class)
                .returnResult()
                .getResponseBody();

        Assertions.assertEquals(savedMovieEntity.getId(), deleteMovieId);

        webTestClient.get()
                .uri(MOVIES_URI_ID_PLACEHOLDER, UUID.randomUUID())
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testMovieDeletedNotFound() {
        webTestClient.delete()
                .uri(MOVIES_URI_ID_PLACEHOLDER, UUID.randomUUID())
                .exchange()
                .expectStatus().isNotFound();
    }

    private static Model<MovieEntity> getMovieEntityModel() {
        return Instancio.of(MovieEntity.class)
                .ignore(field(MovieEntity::getCharacters))
                .toModel();
    }
}