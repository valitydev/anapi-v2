package dev.vality.anapi.v2.security;

import dev.vality.anapi.v2.exception.AuthorizationException;
import dev.vality.anapi.v2.exception.BouncerException;
import dev.vality.anapi.v2.service.BouncerService;
import dev.vality.anapi.v2.service.KeycloakService;
import dev.vality.anapi.v2.service.VortigonService;
import dev.vality.bouncer.base.Entity;
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

    public List<String> getAccessibleShops(AccessData accessData) {
        var shopIds = vortigonService.getShopIds(accessData.getPartyId(),
                Objects.requireNonNullElse(accessData.getRealm(), "live"));
        if (accessData.getShopIds() != null && !accessData.getShopIds().isEmpty()) {
            shopIds = accessData.getShopIds().stream()
                    .filter(shopIds::contains)
                    .collect(Collectors.toList());
        }
        log.info("Check the user's rights to perform the operation {}", accessData.getOperationId());
        var ctx = buildAnapiBouncerContext(accessData, shopIds);
        var resolution = bouncerService.getResolution(ctx);
        switch (resolution.getSetField()) {
            case FORBIDDEN: {
                if (authEnabled) {
                    throw new AuthorizationException(
                            String.format("No rights to perform %s", accessData.getOperationId()));
                } else {
                    log.warn("No rights to perform {}", accessData.getOperationId());
                    return List.copyOf(shopIds);
                }
            }
            case RESTRICTED: {
                if (authEnabled) {
                    return resolution.getRestricted().getRestrictions().getAnapi().getOp().getShops().stream()
                            .map(Entity::getId)
                            .collect(Collectors.toList());
                } else {
                    log.warn("Rights to perform {} are restricted", accessData.getOperationId());
                    return List.copyOf(shopIds);
                }
            }
            case ALLOWED:
                return List.copyOf(shopIds);
            default:
                throw new BouncerException(String.format("Resolution %s cannot be processed", resolution));
        }
    }

    private AnapiBouncerContext buildAnapiBouncerContext(AccessData accessData, List<String> allowedShopIds) {
        var token = keycloakService.getAccessToken();
        return AnapiBouncerContext.builder()
                .operationId(accessData.getOperationId())
                .partyId(accessData.getPartyId())
                .shopIds(allowedShopIds)
                .fileId(accessData.getFileId())
                .reportId(accessData.getFileId())
                .tokenExpiration(token.getExp())
                .tokenId(token.getId())
                .userId(token.getSubject())
                .build();
    }
}
