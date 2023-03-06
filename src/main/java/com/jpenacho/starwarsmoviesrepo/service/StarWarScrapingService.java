package com.jpenacho.starwarsmoviesrepo.service;

import com.jpenacho.starwarsmoviesrepo.datasource.external.swapi.LinkUtils;
import com.jpenacho.starwarsmoviesrepo.datasource.external.swapi.StarWarClient;
import com.jpenacho.starwarsmoviesrepo.datasource.external.swapi.StarWarMovieDto;
import com.jpenacho.starwarsmoviesrepo.service.character.CharacterMapper;
import com.jpenacho.starwarsmoviesrepo.service.character.CharacterService;
import com.jpenacho.starwarsmoviesrepo.service.movie.MovieModel;
import com.jpenacho.starwarsmoviesrepo.service.movie.MovieService;
import com.jpenacho.starwarsmoviesrepo.service.movie.MoviesMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Collections;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class StarWarScrapingService {

    StarWarClient starWarClient;

    CharacterService characterService;

    MovieService movieService;

    @EventListener(ApplicationReadyEvent.class)
    public void fetchStarWarData() {
        fetchPeopleAndStore()
                .then(fetchMoviesAndStore())
                .then()
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe();
    }

    private Mono<Void> fetchMoviesAndStore() {
        return starWarClient.getMovies()
                .flatMap(this::decorateMovie)
                .doOnNext(movie -> log.debug("fetchPeopleAndStore. movie= {}", movie))
                .flatMap(movieService::create)
                .then();
    }

    private Mono<MovieModel> decorateMovie(StarWarMovieDto starWarMovieDto) {
        return Flux.fromIterable(starWarMovieDto.getCharacters())
                .flatMap(link -> characterService.readByExternalId(LinkUtils.extractLastPathSegment(link)))
                .collect(Collectors.toSet())
                .map(characterModels -> MoviesMapper.INSTANCE.create(starWarMovieDto, characterModels))
                .switchIfEmpty(Mono.fromSupplier((() -> MoviesMapper.INSTANCE.create(starWarMovieDto, Collections.emptySet()))));
    }

    private Mono<Void> fetchPeopleAndStore() {
        return starWarClient.getPeople()
                .map(CharacterMapper.INSTANCE::map)
                .doOnNext(person -> log.debug("fetchPeopleAndStore. person= {}", person))
                .flatMap(characterService::create)
                .then();

    }
}
