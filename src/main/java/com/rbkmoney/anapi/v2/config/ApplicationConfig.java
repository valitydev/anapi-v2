package com.rbkmoney.anapi.v2.config;

import com.rbkmoney.bouncer.decisions.ArbiterSrv;
import com.rbkmoney.damsel.analytics.AnalyticsServiceSrv;
import com.rbkmoney.damsel.vortigon.VortigonServiceSrv;
import com.rbkmoney.magista.MerchantStatisticsServiceSrv;
import com.rbkmoney.orgmanagement.AuthContextProviderSrv;
import com.rbkmoney.woody.api.trace.context.metadata.user.UserIdentityEmailExtensionKit;
import com.rbkmoney.woody.api.trace.context.metadata.user.UserIdentityIdExtensionKit;
import com.rbkmoney.woody.api.trace.context.metadata.user.UserIdentityRealmExtensionKit;
import com.rbkmoney.woody.api.trace.context.metadata.user.UserIdentityUsernameExtensionKit;
import com.rbkmoney.woody.thrift.impl.http.THSpawnClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.List;

@Configuration
public class ApplicationConfig {

    @Bean
    public MerchantStatisticsServiceSrv.Iface magistaClient(
            @Value("${service.magista.url}") Resource resource,
            @Value("${service.magista.networkTimeout}") int networkTimeout) throws IOException {
        return new THSpawnClientBuilder()
                .withNetworkTimeout(networkTimeout)
                .withAddress(resource.getURI())
                .build(MerchantStatisticsServiceSrv.Iface.class);
    }

    @Bean
    public AnalyticsServiceSrv.Iface analyticsClient(
            @Value("${service.analytics.url}") Resource resource,
            @Value("${service.analytics.networkTimeout}") int networkTimeout) throws IOException {
        return new THSpawnClientBuilder()
                .withNetworkTimeout(networkTimeout)
                .withAddress(resource.getURI())
                .build(AnalyticsServiceSrv.Iface.class);
    }

    @Bean
    public VortigonServiceSrv.Iface vortigonClient(
            @Value("${service.vortigon.url}") Resource resource,
            @Value("${service.vortigon.networkTimeout}") int networkTimeout) throws IOException {
        return new THSpawnClientBuilder()
                .withNetworkTimeout(networkTimeout)
                .withAddress(resource.getURI())
                .build(VortigonServiceSrv.Iface.class);
    }

    @Bean
    public AuthContextProviderSrv.Iface orgManagerClient(
            @Value("${service.orgManager.url}") Resource resource,
            @Value("${service.orgManager.networkTimeout}") int networkTimeout) throws IOException {
        return new THSpawnClientBuilder()
                .withNetworkTimeout(networkTimeout)
                .withMetaExtensions(List.of(
                        UserIdentityIdExtensionKit.INSTANCE,
                        UserIdentityEmailExtensionKit.INSTANCE,
                        UserIdentityUsernameExtensionKit.INSTANCE,
                        UserIdentityRealmExtensionKit.INSTANCE))
                .withAddress(resource.getURI())
                .build(AuthContextProviderSrv.Iface.class);
    }

    @Bean
    public ArbiterSrv.Iface bouncerClient(
            @Value("${service.bouncer.url}") Resource resource,
            @Value("${service.bouncer.networkTimeout}") int networkTimeout) throws IOException {
        return new THSpawnClientBuilder()
                .withNetworkTimeout(networkTimeout)
                .withAddress(resource.getURI())
                .build(ArbiterSrv.Iface.class);
    }
}
