package com.rbkmoney.anapi.v2.security;

import com.rbkmoney.bouncer.starter.api.BouncerContext;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Builder
@EqualsAndHashCode(callSuper = true)
@Data
public class AnapiBouncerContext extends BouncerContext {

    private final long tokenExpiration;
    private final String tokenId;
    private final String userId;
    private final String operationId;
    private final String partyId;
    private final String shopId;
}
