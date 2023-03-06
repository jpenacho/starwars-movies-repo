package com.jpenacho.starwarsmoviesrepo;

import com.jpenacho.starwarsmoviesrepo.exception.ResourceNotFound;
import com.jpenacho.starwarsmoviesrepo.datasource.repository.CrudDatabaseService;
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
import reactor.test.StepVerifier;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.instancio.Select.field;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MovieDataLayerTest {

    private static final int LIST_TEST_SIZE = 5;

    @Autowired
    CrudDatabaseService<MovieEntity, UUID> movieDatabaseService;

    @Autowired
    MovieRepository movieRepository;

    @BeforeEach
    void cleanDb() {
        movieRepository.deleteAll();
    }

    @Test
    void testMovieCreate() {
        MovieEntity movieEntity = Instancio.create(getMovieEntityModel());

        StepVerifier.create(movieDatabaseService.create(movieEntity))
                .consumeNextWith(saveMovieEntity -> {
                    Assertions.assertNotNull(saveMovieEntity.getId());
                    AssertionUtils.assertEquals(movieEntity, saveMovieEntity, "id");
                })
                .verifyComplete();

    }

    @Test
    void testMovieUpdate() {
        MovieEntity movieEntity = Instancio.create(getMovieEntityModel());

        MovieEntity savedMovieEntity = movieRepository.save(movieEntity);

        MovieEntity updateMovieEntity = Instancio.create(getMovieEntityModel());

        StepVerifier.create(movieDatabaseService.update(savedMovieEntity.getId(), updateMovieEntity))
                .consumeNextWith(updatedMovieEntity -> {
                    AssertionUtils.assertEquals(updateMovieEntity, updatedMovieEntity, "id", "createdAt", "updatedAt");
                    Assertions.assertEquals(savedMovieEntity.getId(), updatedMovieEntity.getId());
                    Assertions.assertEquals(0, AssertionUtils.compareOffSetDateTimes(savedMovieEntity.getCreatedAt(), updatedMovieEntity.getCreatedAt()));
                })
                .verifyComplete();
    }

    @Test
    void testMovieUpdateNotFound() {
        MovieEntity updateMovieEntity = Instancio.create(getMovieEntityModel());

        StepVerifier.create(movieDatabaseService.update(updateMovieEntity.getId(), updateMovieEntity))
                .expectError(ResourceNotFound.class);
    }

    @Test
    void testMovieGet() {
        MovieEntity movieEntity = Instancio.create(getMovieEntityModel());

        MovieEntity savedMovieEntity = movieRepository.save(movieEntity);

        StepVerifier.create(movieDatabaseService.read(savedMovieEntity.getId()))
                .consumeNextWith(readMovieEntity -> {
                    assertReadMovieEntity(savedMovieEntity, readMovieEntity);
                })
                .verifyComplete();
    }

    @Test
    void testMovieGetNotFound() {
        StepVerifier.create(movieDatabaseService.read(UUID.randomUUID()))
                .expectError(ResourceNotFound.class);
    }

    @Test
    void testMovieList() {
        Map<UUID, MovieEntity> savedMovieEntities = Instancio.stream(getMovieEntityModel())
                .limit(LIST_TEST_SIZE)
                .map(movieRepository::save)
                .collect(Collectors.toMap(MovieEntity::getId, (entry) -> entry));

        StepVerifier.create(movieDatabaseService.list())
                .expectNextCount(LIST_TEST_SIZE)
                .thenConsumeWhile(unused -> true, readMovieEntity -> {
                    MovieEntity savedMovieEntity = savedMovieEntities.get(readMovieEntity.getId());
                    assertReadMovieEntity(savedMovieEntity, readMovieEntity);
                })
                .verifyComplete();
    }

    @Test
    void testMovieDelete() {
        MovieEntity movieEntity = Instancio.create(getMovieEntityModel());

        MovieEntity savedMovieEntity = movieRepository.save(movieEntity);

        StepVerifier.create(movieDatabaseService.read(savedMovieEntity.getId()))
                .consumeNextWith(readMovieEntity -> {
                    assertReadMovieEntity(savedMovieEntity, readMovieEntity);
                })
                .verifyComplete();

        StepVerifier.create(movieDatabaseService.delete(savedMovieEntity.getId()))
                .consumeNextWith(deletedMovieEntityId -> {
                    Assertions.assertEquals(savedMovieEntity.getId(), deletedMovieEntityId);
                })
                .verifyComplete();

        StepVerifier.create(movieDatabaseService.read(savedMovieEntity.getId()))
                .expectError(ResourceNotFound.class);
    }

    private static void assertReadMovieEntity(MovieEntity savedMovieEntity, MovieEntity readMovieEntity) {
        AssertionUtils.assertEquals(savedMovieEntity, readMovieEntity, "id", "characters");
        Assertions.assertEquals(savedMovieEntity.getId(), readMovieEntity.getId());
        Assertions.assertTrue(readMovieEntity.getCharacters().isEmpty());
    }

    private static Model<MovieEntity> getMovieEntityModel() {
        return Instancio.of(MovieEntity.class)
                .ignore(field(MovieEntity::getCharacters))
                .toModel();
    }
}
