package com.jpenacho.starwarsmoviesrepo.datasource.repository.movie;

import com.jpenacho.starwarsmoviesrepo.exception.ResourceNotFound;
import com.jpenacho.starwarsmoviesrepo.datasource.repository.CrudDatabaseService;
import com.jpenacho.starwarsmoviesrepo.datasource.repository.TransactionService;
import lombok.AccessLevel;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Log4j2
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Service
public class MovieDatabaseServiceImpl extends TransactionService implements CrudDatabaseService<MovieEntity, UUID> {

    MovieRepository movieRepository;

    protected MovieDatabaseServiceImpl(PlatformTransactionManager transactionManager, MovieRepository movieRepository) {
        super(transactionManager);
        this.movieRepository = movieRepository;
    }

    @Override
    public Mono<MovieEntity> read(UUID id) {
        return readOnlyTx(() -> execRead(id));
    }

    @SneakyThrows
    private MovieEntity execRead(UUID id) {
        return movieRepository.findById(id)
                .map(MovieEntityMappers.INSTANCE::map)
                .orElseThrow(ResourceNotFound::new);
    }

    @Override
    public Flux<MovieEntity> list() {
        return startTx(this::execList)
                .flatMapIterable(list -> list);
    }

    private Set<MovieEntity> execList() {
        Iterable<MovieEntity> characterEntities = movieRepository.findAll();

        return StreamSupport.stream(characterEntities.spliterator(), false)
                .map(MovieEntityMappers.INSTANCE::map)
                .collect(Collectors.toSet());
    }

    @Override
    public Mono<MovieEntity> create(MovieEntity newMovieEntity) {
        return startTx(() -> execSave(newMovieEntity));
    }

    private MovieEntity execSave(MovieEntity newMovieEntity) {
        MovieEntity savedMovieEntity = movieRepository.save(newMovieEntity);
        return MovieEntityMappers.INSTANCE.map(savedMovieEntity);
    }

    @Override
    public Mono<MovieEntity> update(UUID id, MovieEntity updatedMovieEntity) {
        return startTx(() -> execUpdate(id, updatedMovieEntity));
    }

    @SneakyThrows
    private MovieEntity execUpdate(UUID id, MovieEntity updatedMovieEntity) {
        MovieEntity movieEntity = movieRepository.findById(id)
                .orElseThrow(ResourceNotFound::new);

        MovieEntityMappers.INSTANCE.updateEntity(movieEntity, updatedMovieEntity);

        MovieEntity patchedMovieEntity = execSave(movieEntity);

        return MovieEntityMappers.INSTANCE.map(patchedMovieEntity);
    }

    @Override
    public Mono<UUID> delete(UUID id) {
        return startTx(() -> execDelete(id));
    }

    @SneakyThrows
    private UUID execDelete(UUID id) {
        MovieEntity movieEntity = movieRepository.findById(id)
                .orElseThrow(ResourceNotFound::new);

        movieRepository.delete(movieEntity);

        return id;
    }
}
