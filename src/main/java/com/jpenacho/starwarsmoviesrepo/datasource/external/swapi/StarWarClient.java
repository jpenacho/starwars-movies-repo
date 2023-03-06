package com.jpenacho.starwarsmoviesrepo.datasource.external.swapi;

import reactor.core.publisher.Flux;

public interface StarWarClient {

    Flux<StarWarMovieDto> getMovies();

    Flux<StarWarsPersonDto> getPeople();
}
