package com.jpenacho.starwarsmoviesrepo.datasource.repository;

import reactor.core.publisher.Mono;

public interface ExternalIdDatabaseService<T,K,S> extends CrudDatabaseService<T,K>{

    Mono<T> readByExternalId (S externalId);
}
