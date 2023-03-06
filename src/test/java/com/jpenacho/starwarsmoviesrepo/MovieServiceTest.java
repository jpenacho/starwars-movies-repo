package com.jpenacho.starwarsmoviesrepo;

import com.jpenacho.starwarsmoviesrepo.exception.ResourceNotFound;
import com.jpenacho.starwarsmoviesrepo.datasource.repository.movie.MovieEntity;
import com.jpenacho.starwarsmoviesrepo.datasource.repository.movie.MovieRepository;
import com.jpenacho.starwarsmoviesrepo.service.movie.MovieModel;
import com.jpenacho.starwarsmoviesrepo.service.movie.MovieService;
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
public class MovieServiceTest {

    private static final int LIST_TEST_SIZE = 5;

    @Autowired
    MovieService movieService;

    @Autowired
    MovieRepository movieRepository;

    @BeforeEach
    void cleanDb() {
        movieRepository.deleteAll();
    }

    @Test
    void testMovieCreate() {
        MovieModel movieModel = Instancio.of(MovieModel.class)
                .ignore(field(MovieModel::getId))
                .ignore(field(MovieModel::getCharacters))
                .create();

        StepVerifier.create(movieService.create(movieModel))
                .consumeNextWith(saveMovieModel -> {
                    Assertions.assertNotNull(saveMovieModel.getId());
                    AssertionUtils.assertEquals(saveMovieModel, saveMovieModel, "id");
                })
                .verifyComplete();

    }

    @Test
    void testMovieUpdate() {
        MovieEntity movieEntity = Instancio.create(getMovieEntityModel());

        MovieEntity savedMovieEntity = movieRepository.save(movieEntity);

        MovieModel updateMovieModel = Instancio.create(getMovieUpdateModel());

        StepVerifier.create(movieService.update(savedMovieEntity.getId(), updateMovieModel))
                .consumeNextWith(updatedMovieEntity -> {
                    AssertionUtils.assertEquals(updateMovieModel, updatedMovieEntity, "id", "createdAt", "updatedAt");
                    Assertions.assertEquals(savedMovieEntity.getId(), updatedMovieEntity.getId());
                    Assertions.assertEquals(0, AssertionUtils.compareOffSetDateTimes(savedMovieEntity.getCreatedAt(), updatedMovieEntity.getCreatedAt()));
                })
                .verifyComplete();
    }

    @Test
    void testMovieUpdateNotFound() {
        MovieModel updateMovieModel = Instancio.create(getMovieUpdateModel());

        StepVerifier.create(movieService.update(UUID.randomUUID(), updateMovieModel))
                .expectError(ResourceNotFound.class);
    }

    @Test
    void testMovieGet() {
        MovieEntity movieEntity = Instancio.create(getMovieEntityModel());

        MovieEntity savedMovieEntity = movieRepository.save(movieEntity);

        StepVerifier.create(movieService.read(savedMovieEntity.getId()))
                .consumeNextWith(readMovieEntity -> {
                    assertReadMovieEntity(savedMovieEntity, readMovieEntity);
                })
                .verifyComplete();
    }

    @Test
    void testMovieGetNotFound() {
        StepVerifier.create(movieService.read(UUID.randomUUID()))
                .expectError(ResourceNotFound.class);
    }

    @Test
    void testMovieList() {
        Map<UUID, MovieEntity> savedMovieEntities = Instancio.stream(getMovieEntityModel())
                .limit(LIST_TEST_SIZE)
                .map(movieRepository::save)
                .collect(Collectors.toMap(MovieEntity::getId, (entry) -> entry));

        StepVerifier.create(movieService.list())
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

        StepVerifier.create(movieService.read(savedMovieEntity.getId()))
                .consumeNextWith(readMovieEntity -> {
                    assertReadMovieEntity(savedMovieEntity, readMovieEntity);
                })
                .verifyComplete();

        StepVerifier.create(movieService.delete(savedMovieEntity.getId()))
                .consumeNextWith(deletedMovieEntityId -> {
                    Assertions.assertEquals(savedMovieEntity.getId(), deletedMovieEntityId);
                })
                .verifyComplete();

        StepVerifier.create(movieService.read(savedMovieEntity.getId()))
                .expectError(ResourceNotFound.class);
    }

    private static void assertReadMovieEntity(MovieEntity savedMovieEntity, MovieModel readMovieModel) {
        AssertionUtils.assertEquals(savedMovieEntity, readMovieModel, "id", "characters");
        Assertions.assertEquals(savedMovieEntity.getId(), readMovieModel.getId());
    }

    private static Model<MovieEntity> getMovieEntityModel() {
        return Instancio.of(MovieEntity.class)
                .ignore(field(MovieEntity::getCharacters))
                .toModel();
    }

    private static Model<MovieModel> getMovieUpdateModel() {
        return Instancio.of(MovieModel.class)
                .ignore(field(MovieModel::getId))
                .ignore(field(MovieModel::getCreatedAt))
                .ignore(field(MovieModel::getUpdatedAt))
                .ignore(field(MovieModel::getCharacters))
                .toModel();
    }
}