package com.jpenacho.starwarsmoviesrepo.datasource.external.swapi;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.IntFunction;

import static com.jpenacho.starwarsmoviesrepo.datasource.external.swapi.LinkUtils.PAGE_PARAMETER;

@Log4j2
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Service
public class StarWarClientImpl implements StarWarClient {

    static final String PEOPLE_URI = "/people/";
    static final String FILMS_URI = "/films/";

    WebClient webClient;

    public StarWarClientImpl(WebClient.Builder webClientBuilder, ExternalConfigurations externalConfigurations) {
        this.webClient = webClientBuilder.baseUrl(externalConfigurations.getStarWarBaseUrl())
                .build();
    }

    @Override
    public Flux<StarWarMovieDto> getMovies() {
        return readAll(page -> performRequest(FILMS_URI, page, new ParameterizedTypeReference<StarWarPageDto<StarWarMovieDto>>() {}))
                .doOnNext(starWarMovieDto -> log.debug("getMovies. starWarMovieDto={}", starWarMovieDto));
    }

    @Override
    public Flux<StarWarsPersonDto> getPeople() {
        return readAll(page -> performRequest(PEOPLE_URI, page, new ParameterizedTypeReference<StarWarPageDto<StarWarsPersonDto>>() {}))
                .doOnNext(starWarsPersonDto -> log.debug("getPeople. starWarsPersonDto={}", starWarsPersonDto));
    }

    private <T> Mono<T> performRequest(String uri, int page, ParameterizedTypeReference<T> parameterizedTypeReference) {
        return this.webClient.get().uri(uriBuilder ->
                        uriBuilder.path(uri)
                                .queryParam(PAGE_PARAMETER, page)
                                .build())
                .retrieve().bodyToMono(parameterizedTypeReference);
    }

    private <T extends StarWarDto> Flux<T> readAll(IntFunction<Mono<StarWarPageDto<T>>> getAllFunction) {
        return getAllFunction.apply(1).expand(pageDto -> {
                    log.debug("readAll. paginated result params [size={}; total={}]", new Object[]{pageDto.getResults().size(), pageDto.getCount()});
                    log.debug("readAll. paginated result params [previous={}; next={}]", new Object[]{pageDto.getPrevious(), pageDto.getNext()});

                    Integer nextPage = LinkUtils.extractPageParameter(pageDto.getNext());
                    if (nextPage == null) {
                        return Mono.empty();
                    }

                    return getAllFunction.apply(nextPage);
                })
                .flatMapIterable(StarWarPageDto::getResults);
    }

}
