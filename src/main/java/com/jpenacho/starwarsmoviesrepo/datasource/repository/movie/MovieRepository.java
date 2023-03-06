package com.jpenacho.starwarsmoviesrepo.datasource.repository.movie;

import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface MovieRepository extends CrudRepository<MovieEntity, UUID> {
}
