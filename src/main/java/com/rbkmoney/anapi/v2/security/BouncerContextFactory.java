package com.rbkmoney.anapi.v2.security;

import com.rbkmoney.bouncer.context.v1.AnalyticsAPIOperation;
import com.rbkmoney.bouncer.context.v1.ContextAnalyticsAPI;
import com.rbkmoney.bouncer.context.v1.ContextFragment;
import com.rbkmoney.bouncer.context.v1.Entity;
import com.rbkmoney.bouncer.starter.AbstractBouncerContextFactory;
import com.rbkmoney.bouncer.starter.api.BouncerContext;
import com.rbkmoney.bouncer.starter.config.properties.BouncerProperties;
import org.springframework.stereotype.Component;

@Component
public class BouncerContextFactory extends AbstractBouncerContextFactory {

    public BouncerContextFactory(BouncerProperties bouncerProperties,
                                 com.rbkmoney.bouncer.starter.UserAuthContextProvider userAuthContextProvider) {
        super(bouncerProperties, userAuthContextProvider);
    }

    @Override
    protected void customizeContext(@org.jetbrains.annotations.NotNull ContextFragment contextFragment,
                                    @org.jetbrains.annotations.NotNull BouncerContext bouncerContext) {
        contextFragment.setAnapi(buildAnapiContext((AnapiBouncerContext) bouncerContext));
    }

    private ContextAnalyticsAPI buildAnapiContext(AnapiBouncerContext ctx) {
        return new ContextAnalyticsAPI()
                .setOp(new AnalyticsAPIOperation()
                        .setShop(new Entity().setId(ctx.getShopId()))
                        .setParty(new Entity().setId(ctx.getPartyId()))
                        .setId(ctx.getOperationId()));
    }

}
