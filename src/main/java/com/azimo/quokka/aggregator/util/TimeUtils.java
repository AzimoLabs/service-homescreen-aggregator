package com.azimo.quokka.aggregator.util;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;

public class TimeUtils {
    public static OffsetDateTime getUtcOffsetDateTime(Instant date) {
        return OffsetDateTime.ofInstant(date, ZoneId.of("UTC"));
    }
}
