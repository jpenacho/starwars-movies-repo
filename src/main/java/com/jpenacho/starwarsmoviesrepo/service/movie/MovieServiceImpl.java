package com.jpenacho.starwarsmoviesrepo.service.movie;

import com.jpenacho.starwarsmoviesrepo.datasource.repository.CrudDatabaseService;
import com.jpenacho.starwarsmoviesrepo.datasource.repository.movie.MovieEntity;
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
public class MovieServiceImpl implements MovieService {

    CrudDatabaseService<MovieEntity, UUID> movieDatabaseService;

    @Override
    public Mono<MovieModel> create(MovieModel newMovie) {
        return movieDatabaseService.create(MoviesMapper.INSTANCE.map(newMovie))
                .map(MoviesMapper.INSTANCE::map)
                .doOnNext(movie -> log.debug("create. Created movie={}", movie));
    }

    @Override
    public Mono<MovieModel> read(UUID id) {
        return movieDatabaseService.read(id)
                .map(MoviesMapper.INSTANCE::map)
                .doOnNext(movie -> log.debug("read. Read movie={}", movie));
    }

    @Override
    public Flux<MovieModel> list() {
        return movieDatabaseService.list()
                .map(MoviesMapper.INSTANCE::map)
                .doOnNext(movie -> log.debug("list. Listed movie={}", movie));
    }

    @Override
    public Mono<MovieModel> update(UUID id, MovieModel updateMovie) {
        return movieDatabaseService.update(id, MoviesMapper.INSTANCE.map(updateMovie))
                .map(MoviesMapper.INSTANCE::map)
                .doOnNext(movie -> log.debug("update. Updated movie={}", movie));
    }

    @Override
    public Mono<UUID> delete(UUID id) {
        return movieDatabaseService.delete(id)
                .doOnNext(movie -> log.debug("delete. Deleted movie={}", movie));
    }
}
