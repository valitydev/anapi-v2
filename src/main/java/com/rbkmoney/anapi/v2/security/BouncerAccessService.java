package com.rbkmoney.anapi.v2.security;

import com.rbkmoney.anapi.v2.exception.AuthorizationException;
import com.rbkmoney.anapi.v2.service.BouncerService;
import com.rbkmoney.anapi.v2.service.KeycloakService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.representations.AccessToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class BouncerAccessService {

    private final BouncerService bouncerService;
    private final KeycloakService keycloakService;

    @Value("${bouncer.auth.enabled}")
    private boolean authEnabled;

    public void checkAccess(String operationId, String partyId, List<String> shopIds) {
        log.info("Check the user's rights to perform the operation {}", operationId);
        var ctx = buildAnapiBouncerContext(operationId, partyId, shopIds);
        if (!bouncerService.havePrivileges(ctx)) {
            if (authEnabled) {
                throw new AuthorizationException(String.format("No rights to perform %s", operationId));
            } else {
                log.warn("No rights to perform {}", operationId);
            }
        }
    }

    private AnapiBouncerContext buildAnapiBouncerContext(String operationId, String partyId, List<String> shopIds) {
        AccessToken token = keycloakService.getAccessToken();
        return AnapiBouncerContext.builder()
                .operationId(operationId)
                .partyId(partyId)
                .shopIds(shopIds)
                .tokenExpiration(token.getExp())
                .tokenId(token.getId())
                .userId(token.getSubject())
                .build();
    }
}
