package com.rbkmoney.anapi.v2.util;

import lombok.experimental.UtilityClass;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@UtilityClass
public class CommonUtil {

    private static final Pattern deadlinePattern = Pattern.compile("\\d+(?:\\.\\d+)?(?:ms|s|m)");

    public static List<String> merge(@Nullable String id, @Nullable List<String> ids) {
        if (id != null) {
            if (ids == null) {
                ids = new ArrayList<>();
            }
            ids.add(id);
        }
        return ids;
    }

    public static long getRequestDeadlineMillis(@NotNull String requestDeadLine) {
        if (deadlinePattern.matcher(requestDeadLine).matches()) {
            //150000ms, 540s, 3.5m, etc
            if (requestDeadLine.endsWith("ms")) {
                return Long.parseLong(requestDeadLine.substring(0, requestDeadLine.length() - 3));
            }
            String duration = "PT" + requestDeadLine.toUpperCase();
            return Duration.parse(duration).toMillis();
        }

        //ISO 8601
        OffsetDateTime odt = OffsetDateTime.parse(requestDeadLine);
        return odt.toInstant().toEpochMilli() - Instant.now().toEpochMilli();

    }

}
