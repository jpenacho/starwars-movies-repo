package com.jpenacho.starwarsmoviesrepo.datasource.external.swapi;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Log4j2
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LinkUtils {

    private static final String LINK_SEPARATOR = "/";

    public static final String PAGE_PARAMETER = "page";

    public static Integer extractLastPathSegment(String uriStr) {
        String[] path = uriStr.split(LINK_SEPARATOR);
        return Integer.parseInt(path[path.length - 1]);
    }

    public static Integer extractPageParameter(URI uri) {
        if (uri == null || StringUtils.isBlank(uri.getPath())) {
            return null;
        }

        MultiValueMap<String, String> parameters = UriComponentsBuilder.fromUri(uri).build().getQueryParams();

        try {
            return Integer.parseInt(parameters.get(PAGE_PARAMETER).get(0));

        } catch (NullPointerException | NumberFormatException exception) {
            log.warn("extractPageParameter. Error extracting page parameter!!!", exception);
            return null;
        }
    }

}
