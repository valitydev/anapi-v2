package dev.vality.anapi.v2.security;

import dev.vality.anapi.v2.config.properties.BouncerProperties;
import dev.vality.anapi.v2.service.KeycloakService;
import dev.vality.anapi.v2.service.OrgManagerService;
import dev.vality.bouncer.base.Entity;
import dev.vality.bouncer.context.v1.*;
import dev.vality.bouncer.ctx.ContextFragmentType;
import dev.vality.bouncer.decisions.Context;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.thrift.TSerializer;
import org.keycloak.representations.AccessToken;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class BouncerContextFactory {

    private final BouncerProperties bouncerProperties;
    private final OrgManagerService orgManagerService;
    private final KeycloakService keycloakService;

    @SneakyThrows
    public Context buildContext(AnapiBouncerContext bouncerContext) {
        var contextFragment = buildContextFragment(bouncerContext);
        var serializer = new TSerializer();
        var fragment = new dev.vality.bouncer.ctx.ContextFragment()
                .setType(ContextFragmentType.v1_thrift_binary)
                .setContent(serializer.serialize(contextFragment));
        var userFragment = orgManagerService.getUserAuthContext(
                keycloakService.getAccessToken().getSubject());
        var context = new Context();
        context.putToFragments(bouncerProperties.getContextFragmentId(), fragment);
        context.putToFragments("user", userFragment);
        return context;
    }

    private ContextFragment buildContextFragment(AnapiBouncerContext bouncerContext) {
        var env = buildEnvironment();
        var accessToken = keycloakService.getAccessToken();
        var contextAnalyticsApi = buildAnapiContext(bouncerContext);
        return new ContextFragment()
                .setAuth(buildAuth(bouncerContext, accessToken))
                .setEnv(env)
                .setAnapi(contextAnalyticsApi);
    }

    private Auth buildAuth(AnapiBouncerContext bouncerContext, AccessToken accessToken) {
        var auth = new Auth();
        var authScopeSet = bouncerContext.getShopIds().stream()
                .map(shopId -> new AuthScope()
                        .setParty(new Entity().setId(bouncerContext.getPartyId()))
                        .setShop(new Entity().setId(shopId)))
                .collect(Collectors.toSet());
        return auth.setToken(new Token().setId(accessToken.getId()))
                .setMethod(bouncerProperties.getAuthMethod())
                .setExpiration(Instant.ofEpochSecond(accessToken.getExp()).toString())
                .setScope(authScopeSet);
    }

    private Environment buildEnvironment() {
        var deployment = new Deployment()
                .setId(bouncerProperties.getDeploymentId());
        return new Environment()
                .setDeployment(deployment)
                .setNow(Instant.now().toString());
    }

    private ContextAnalyticsAPI buildAnapiContext(AnapiBouncerContext ctx) {
        return new ContextAnalyticsAPI()
                .setOp(new AnalyticsAPIOperation()
                        .setId(ctx.getOperationId()));
    }
}
