package com.jpenacho.starwarsmoviesrepo;

import com.jpenacho.starwarsmoviesrepo.datasource.external.swapi.LinkUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.Random;

import static com.jpenacho.starwarsmoviesrepo.datasource.external.swapi.LinkUtils.PAGE_PARAMETER;

public class LinkUtilsTest {

    private static final String ENDPOINT = "https://endpoint.dev";
    private static final String TEST_URL = ENDPOINT + "/api/path/";

    @Test
    @RepeatedTest(10)
    void testExtractLastPathSegment() {
        Random random = new Random();

        int id = random.nextInt();

        String link = String.format(TEST_URL + "%d/", id);

        Assertions.assertEquals(id, LinkUtils.extractLastPathSegment(link));
    }

    @Test
    @RepeatedTest(10)
    void testExtractPageParameter() {
        Random random = new Random();

        int id = random.nextInt();

        String link = String.format(TEST_URL + "?" + PAGE_PARAMETER + "=%d", id);

        Assertions.assertEquals(id, LinkUtils.extractPageParameter(URI.create(link)));
    }

    @Test
    void testExtractPageParameterUriNull() {
        Assertions.assertEquals(null, LinkUtils.extractPageParameter(null));
    }

    @Test
    void testExtractPageParameterNoQuery() {
        Assertions.assertEquals(null, LinkUtils.extractPageParameter(URI.create(TEST_URL)));
    }

    void testExtractPageParameterNoPath() {
        Assertions.assertEquals(null, LinkUtils.extractPageParameter(URI.create(ENDPOINT)));
    }

    @Test
    void testExtractPageParameterIsNotAnInt() {

        String link = String.format(TEST_URL + "?" + PAGE_PARAMETER + "=%s", "query");

        Assertions.assertEquals(null, LinkUtils.extractPageParameter(URI.create(link)));
    }
}
