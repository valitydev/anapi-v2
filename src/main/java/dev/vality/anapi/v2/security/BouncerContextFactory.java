package dev.vality.anapi.v2.security;

import dev.vality.anapi.v2.config.properties.BouncerProperties;
import dev.vality.anapi.v2.service.OrgManagerService;
import dev.vality.anapi.v2.util.JwtUtil;
import dev.vality.bouncer.base.Entity;
import dev.vality.bouncer.context.v1.*;
import dev.vality.bouncer.ctx.ContextFragmentType;
import dev.vality.bouncer.decisions.Context;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TSerializer;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
@Component
@Slf4j
public class BouncerContextFactory {

    private final BouncerProperties bouncerProperties;
    private final OrgManagerService orgManagerService;

    @SneakyThrows
    public Context buildContext(AnapiBouncerContext bouncerContext) {
        var contextFragment = buildContextFragment(bouncerContext);
        var serializer = new TSerializer();
        var fragment = new dev.vality.bouncer.ctx.ContextFragment()
                .setType(ContextFragmentType.v1_thrift_binary)
                .setContent(serializer.serialize(contextFragment));

        var context = new Context();
        context.putToFragments("anapi", fragment);
        context.putToFragments("token-keeper", bouncerContext.getAuthData().getContext());

        Optional<String> subject = JwtUtil.getSubject(bouncerContext.getAuthData().getToken());
        if (subject.isPresent()) {
            log.debug("User context fragment will be added");
            var userFragment = orgManagerService.getUserAuthContext(subject.get());
            context.putToFragments("user", userFragment);
        }
        return context;
    }

    private ContextFragment buildContextFragment(AnapiBouncerContext bouncerContext) {
        var env = buildEnvironment();
        var contextAnalyticsApi = buildAnapiContext(bouncerContext);
        var contextReports = buildReportContext(bouncerContext);
        ContextFragment fragment = new ContextFragment();
        return fragment
                .setEnv(env)
                .setAnapi(contextAnalyticsApi)
                .setReports(contextReports);
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
                        .setId(ctx.getOperationId())
                        .setParty(ctx.getPartyId() != null
                                ? new Entity().setId(ctx.getPartyId()) : null)
                        .setShop(ctx.getShopIds() != null && ctx.getShopIds().size() == 1
                                ? new Entity().setId(ctx.getShopIds().get(0)) : null)
                        .setFile(ctx.getFileId() != null
                                ? new Entity().setId(ctx.getFileId()) : null)
                        .setReport(ctx.getReportId() != null
                                ? new Entity().setId(ctx.getReportId()) : null));
    }

    private ContextReports buildReportContext(AnapiBouncerContext ctx) {
        if (ctx.getReportId() == null) {
            return null;
        }
        return new ContextReports().setReport(new Report().setId(ctx.getReportId())
                .setParty(ctx.getPartyId() != null
                        ? new Entity().setId(ctx.getPartyId()) : null)
                .setShop(ctx.getShopIds() != null && ctx.getShopIds().size() == 1
                        ? new Entity().setId(ctx.getShopIds().get(0)) : null)
                .setFiles(ctx.getFileId() != null
                        ? Set.of(new Entity().setId(ctx.getFileId())) : null));
    }
}
