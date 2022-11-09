package dev.vality.anapi.v2.service;

import dev.vality.anapi.v2.exception.BadRequestException;
import dev.vality.anapi.v2.exception.VortigonException;
import dev.vality.damsel.vortigon.PaymentInstitutionRealm;
import dev.vality.damsel.vortigon.VortigonServiceSrv;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TException;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class VortigonService {

    private final VortigonServiceSrv.Iface vortigonClient;

    public List<String> getShopIds(String partyId, String realm) {
        try {
            List<String> shops = vortigonClient.getShopsIds(partyId, mapRealm(realm));
            log.info("Received shops from vortigon: {}", shops);
            return shops;
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
