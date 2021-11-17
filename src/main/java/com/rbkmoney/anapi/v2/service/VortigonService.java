package com.rbkmoney.anapi.v2.service;

import com.rbkmoney.anapi.v2.exception.BadRequestException;
import com.rbkmoney.anapi.v2.exception.VortigonException;
import com.rbkmoney.damsel.vortigon.PaymentInstitutionRealm;
import com.rbkmoney.damsel.vortigon.VortigonServiceSrv;
import lombok.RequiredArgsConstructor;
import org.apache.thrift.TException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VortigonService {

    private final VortigonServiceSrv.Iface vortigonClient;

    public List<String> getShopIds(String partyId, String realm) {
        try {
            return vortigonClient.getShopsIds(partyId, mapRealm(realm));
        } catch (TException e) {
            throw new VortigonException(String.format("Error while call vortigon, partyId=%s", partyId), e);
        }
    }

    private PaymentInstitutionRealm mapRealm(String realm) {
        try {
            return PaymentInstitutionRealm.valueOf(realm);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(String.format("Realm %s cannot be processed", realm));
        }
    }
}
