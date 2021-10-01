package com.rbkmoney.anapi.v2.service;

import com.rbkmoney.anapi.v2.config.properties.BouncerProperties;
import com.rbkmoney.anapi.v2.exception.BouncerException;
import com.rbkmoney.anapi.v2.security.AnapiBouncerContext;
import com.rbkmoney.anapi.v2.security.BouncerContextFactory;
import com.rbkmoney.bouncer.decisions.ArbiterSrv;
import com.rbkmoney.bouncer.decisions.Context;
import com.rbkmoney.bouncer.decisions.Judgement;
import com.rbkmoney.bouncer.decisions.Resolution;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BouncerService {

    private final BouncerProperties bouncerProperties;
    private final BouncerContextFactory bouncerContextFactory;
    private final ArbiterSrv.Iface bouncerClient;

    public boolean havePrivileges(AnapiBouncerContext bouncerContext) {
        log.debug("Check access with bouncer context: {}", bouncerContext);
        Context context = bouncerContextFactory.buildContext(bouncerContext);
        log.debug("Built thrift context: {}", context);
        try {
            Judgement judge = bouncerClient.judge(bouncerProperties.getRuleSetId(), context);
            log.debug("Have judge: {}", judge);
            Resolution resolution = judge.getResolution();
            log.debug("Resolution: {}", resolution);
            return resolution.isSetAllowed();
        } catch (TException e) {
            throw new BouncerException("Error while call bouncer", e);
        }
    }
}
