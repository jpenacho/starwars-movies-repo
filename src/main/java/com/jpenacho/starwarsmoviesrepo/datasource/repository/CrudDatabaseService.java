package com.jpenacho.starwarsmoviesrepo.datasource.repository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CrudDatabaseService<T, K> {

    Mono<T> read(K id);

    Flux<T> list();

    Mono<T> create(T newEntity);

    Mono<T> update(K id, T updatedEntity);

    Mono<K> delete(K id);
}
