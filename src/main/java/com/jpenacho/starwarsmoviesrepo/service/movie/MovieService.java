package com.jpenacho.starwarsmoviesrepo.service.movie;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface MovieService {

    Mono<MovieModel> create(MovieModel newMovie);

    Mono<MovieModel> read(UUID id);

    Flux<MovieModel> list();

    Mono<MovieModel> update(UUID id, MovieModel updateMovie);

    Mono<UUID> delete(UUID id);
}
