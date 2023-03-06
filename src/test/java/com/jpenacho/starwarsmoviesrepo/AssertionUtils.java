package com.jpenacho.starwarsmoviesrepo;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.assertj.core.api.Assertions;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AssertionUtils {

    public static <T, S> void assertEquals(T expected, S actual, String... fieldsToIgnore) {
        Assertions.assertThat(expected)
                .usingRecursiveComparison()
                .withComparatorForType(AssertionUtils::compareOffSetDateTimes, OffsetDateTime.class)
                .ignoringFields(fieldsToIgnore)
                .isEqualTo(actual);
    }

    public static int compareOffSetDateTimes(OffsetDateTime actual, OffsetDateTime expected) {
        if (actual == null && expected == null) {
            return 0;
        }

        if (actual == null) {
            return -1;
        }

        return expected.truncatedTo(ChronoUnit.SECONDS).toInstant()
                .compareTo(actual.truncatedTo(ChronoUnit.SECONDS).toInstant());
    }

}
