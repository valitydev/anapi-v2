package com.rbkmoney.anapi.v2.security;

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

}
