package com.jpenacho.starwarsmoviesrepo.controller.movie;

import com.jpenacho.starwarsmoviesrepo.service.movie.MovieService;
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
public class MovieControllerImpl implements MovieController {

    MovieService movieService;

    @Override
    public Mono<MovieResponse> createMovie(MovieCreateRequest movieCreaterequest) {
        return movieService.create(MoviesControllerMapper.INSTANCE.map(movieCreaterequest))
                .doFirst(() -> log.debug("createMovie. Creating movie={}", movieCreaterequest))
                .map(MoviesControllerMapper.INSTANCE::map)
                .doOnNext(createdMovieResponse -> log.debug("createMovie. Created movie={}", createdMovieResponse));
    }

    @Override
    public Flux<MovieResponse> listMovies() {
        return movieService.list()
                .map(MoviesControllerMapper.INSTANCE::map)
                .doFirst(() -> log.debug("listMovies. Listing movies"))
                .doOnNext(movieResponse -> log.debug("listMovies. Listed movie={}", movieResponse));
    }

    @Override
    public Mono<MovieResponse> getMovie(UUID id) {
        return movieService.read(id)
                .map(MoviesControllerMapper.INSTANCE::map)
                .doFirst(() -> log.debug("getMovie. Getting movie id={}", id))
                .doOnNext(movieResponse -> log.debug("getMovie. Got movie={}", movieResponse));
    }

    @Override
    public Mono<MovieResponse> updateMovie(UUID id, MovieUpdateRequest movieUpdateRequest) {
        return movieService.update(id, MoviesControllerMapper.INSTANCE.map(movieUpdateRequest))
                .doFirst(() -> log.debug("updateMovie. Updating movie ={}", movieUpdateRequest))
                .map(MoviesControllerMapper.INSTANCE::map)
                .doOnNext(updatedMovieResponse -> log.debug("updateMovie. Updated movie={}", updatedMovieResponse));
    }

    @Override
    public Mono<UUID> deleteMovie(UUID id) {
        return movieService.delete(id)
                .doFirst(() -> log.debug("deleteMovie. Deleting movie id={}", id))
                .doOnNext(deletedMovieId -> log.debug("deleteMovie. Deleted movie={}", deletedMovieId));
    }
}
