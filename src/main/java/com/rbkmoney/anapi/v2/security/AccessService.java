package com.rbkmoney.anapi.v2.security;

import com.rbkmoney.anapi.v2.exception.AuthorizationException;
import com.rbkmoney.anapi.v2.exception.BouncerException;
import com.rbkmoney.anapi.v2.service.BouncerService;
import com.rbkmoney.anapi.v2.service.KeycloakService;
import com.rbkmoney.anapi.v2.service.VortigonService;
import com.rbkmoney.bouncer.base.Entity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class AccessService {

    private final VortigonService vortigonService;
    private final BouncerService bouncerService;
    private final KeycloakService keycloakService;

    @Value("${service.bouncer.auth.enabled}")
    private boolean authEnabled;

    public List<String> getAccessibleShops(String operationId, String partyId, String realm) {
        return getAccessibleShops(operationId, partyId, null, realm);
    }

    public List<String> getAccessibleShops(
            String operationId,
            String partyId,
            List<String> requestShopIds,
            String realm) {
        var shopIds = vortigonService.getShopIds(partyId, Objects.requireNonNullElse(realm, "live"));
        if (requestShopIds != null && !requestShopIds.isEmpty()) {
            shopIds = requestShopIds.stream()
                    .filter(shopIds::contains)
                    .collect(Collectors.toList());
        }
        log.info("Check the user's rights to perform the operation {}", operationId);
        var ctx = buildAnapiBouncerContext(operationId, partyId, shopIds);
        var resolution = bouncerService.getResolution(ctx);
        switch (resolution.getSetField()) {
            case FORBIDDEN: {
                if (authEnabled) {
                    throw new AuthorizationException(String.format("No rights to perform %s", operationId));
                } else {
                    log.warn("No rights to perform {}", operationId);
                    return List.copyOf(shopIds);
                }
            }
            case RESTRICTED: {
                if (authEnabled) {
                    return resolution.getRestricted().getRestrictions().getAnapi().getOp().getShops().stream()
                            .map(Entity::getId)
                            .collect(Collectors.toList());
                } else {
                    log.warn("Rights to perform {} are restricted", operationId);
                    return List.copyOf(shopIds);
                }
            }
            case ALLOWED:
                return List.copyOf(shopIds);
            default:
                throw new BouncerException(String.format("Resolution %s cannot be processed", resolution));
        }
    }

    private AnapiBouncerContext buildAnapiBouncerContext(String operationId, String partyId, List<String> shopIds) {
        var token = keycloakService.getAccessToken();
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
