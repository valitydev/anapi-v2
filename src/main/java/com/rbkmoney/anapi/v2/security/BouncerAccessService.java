package com.rbkmoney.anapi.v2.security;

import com.rbkmoney.anapi.v2.service.KeycloakService;
import com.rbkmoney.bouncer.starter.api.BouncerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class BouncerAccessService implements AccessService {

    private final BouncerService bouncerService;
    private final KeycloakService keycloakService;

    @Value("${bouncer.auth.enabled}")
    private Boolean authEnabled;

    @Override
    public void checkAccess(String operationId, String partyId, String shopId) {
        log.info("Check the user's rights to perform the operation $operationId");
        var ctx = buildAnapiBouncerContext(operationId, partyId, shopId);
        if (!bouncerService.havePrivileges(ctx)) {
            if (authEnabled) {
                //TODO: custom exception;
            } else {
                log.warn("No rights to perform $operationId");
            }
        }
    }

    private AnapiBouncerContext buildAnapiBouncerContext(String operationId, String partyId, String shopId) {
        //TODO: Keycloak fields
        return AnapiBouncerContext.builder()
                .operationId(operationId)
                .partyId(partyId)
                .shopId(shopId)
                .build();
    }
}
