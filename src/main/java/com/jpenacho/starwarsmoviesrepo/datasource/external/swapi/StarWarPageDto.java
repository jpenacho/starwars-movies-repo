package com.jpenacho.starwarsmoviesrepo.datasource.external.swapi;

import lombok.Value;

import java.net.URI;
import java.util.List;

@Value
public class StarWarPageDto<T extends StarWarDto> {
    URI next;
    URI previous;
    Integer count;
    List<T> results;
}