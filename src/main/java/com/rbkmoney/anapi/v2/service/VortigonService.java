package com.rbkmoney.anapi.v2.service;

import com.rbkmoney.anapi.v2.exception.BadRequestException;
import com.rbkmoney.damsel.vortigon.PaymentInstitutionRealm;
import com.rbkmoney.damsel.vortigon.VortigonServiceSrv;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VortigonService {

    private final VortigonServiceSrv.Iface vortigonClient;

    @SneakyThrows
    public List<String> getShopIds(String partyId, String realm) {
        return vortigonClient.getShopsIds(partyId, mapToRealm(realm));
    }

    private PaymentInstitutionRealm mapToRealm(String realm) {
        return switch (realm) {
            case "live" -> PaymentInstitutionRealm.live;
            case "test" -> PaymentInstitutionRealm.test;
            default -> throw new BadRequestException(
                    String.format("Realm %s cannot be processed", realm));
        };
    }
}
