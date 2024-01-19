package dev.vality.anapi.v2.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@UtilityClass
public class JwtUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String subjectFieldName = "sub";

    public static Optional<String> getSubject(String token) {
        try {
            String[] chunks = token.split("\\.");
            Base64.Decoder decoder = Base64.getUrlDecoder();
            String payload = new String(decoder.decode(chunks[1]));
            Map<String, Object> parsedPayload = objectMapper.readValue(payload, HashMap.class);
            if (parsedPayload.containsKey(subjectFieldName)) {
                return Optional.of(String.valueOf(parsedPayload.get(subjectFieldName)));
            }
        } catch (Exception e) {
            log.warn("Unable to parse jwt token while looking for subject: ", e);
        }
        return Optional.empty();
    }
}
