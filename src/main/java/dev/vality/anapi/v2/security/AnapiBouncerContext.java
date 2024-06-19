package dev.vality.anapi.v2.security;

import dev.vality.token.keeper.AuthData;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class AnapiBouncerContext {

    private final long tokenExpiration;
    private final String tokenId;
    private final String userId;
    private final String operationId;
    private final String partyId;
    private final List<String> shopIds;
    private final String reportId;
    private final String fileId;
    private final AuthData authData;

}
