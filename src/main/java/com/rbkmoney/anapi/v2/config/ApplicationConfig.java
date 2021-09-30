package com.rbkmoney.anapi.v2.config;

import com.rbkmoney.damsel.vortigon.VortigonServiceSrv;
import com.rbkmoney.magista.MerchantStatisticsServiceSrv;
import com.rbkmoney.woody.thrift.impl.http.THSpawnClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;

@Configuration
public class ApplicationConfig {

    @Bean
    public MerchantStatisticsServiceSrv.Iface magistaClient(
            @Value("${service.magista.url}") Resource resource,
            @Value("${service.magista.networkTimeout}") int networkTimeout
    ) throws IOException {
        return new THSpawnClientBuilder()
                .withNetworkTimeout(networkTimeout)
                .withAddress(resource.getURI()).build(MerchantStatisticsServiceSrv.Iface.class);
    }

    @Bean
    public VortigonServiceSrv.Iface vortigonClient(
            @Value("${service.vortigon.url}") Resource resource,
            @Value("${service.vortigon.networkTimeout}") int networkTimeout
    ) throws IOException {
        return new THSpawnClientBuilder()
                .withNetworkTimeout(networkTimeout)
                .withAddress(resource.getURI()).build(VortigonServiceSrv.Iface.class);
    }

}
