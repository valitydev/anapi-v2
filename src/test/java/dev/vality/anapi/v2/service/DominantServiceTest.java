package dev.vality.anapi.v2.service;

import dev.vality.damsel.domain.*;
import dev.vality.damsel.domain_config_v2.RepositoryClientSrv;
import dev.vality.damsel.domain_config_v2.VersionedObject;
import dev.vality.damsel.domain_config_v2.VersionedObjectWithReferences;
import org.apache.thrift.TException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DominantServiceTest {

    @Test
    void getShopIdsMatchesPaymentInstitutionDataRealm() throws TException {
        var dominantClient = mock(RepositoryClientSrv.Iface.class);
        var service = new DominantService(dominantClient);

        when(dominantClient.checkoutObjectWithReferences(any(), any()))
                .thenReturn(partyWithReferences(shop("shop-1", 1), shop("shop-2", 1), wallet()));
        when(dominantClient.checkoutObject(any(), any()))
                .thenReturn(paymentInstitution(1, PaymentInstitutionRealm.test));

        assertEquals(Set.of("shop-1", "shop-2"),
                Set.copyOf(service.getShopIds("party-1", PaymentInstitutionRealm.test.name())));
        assertEquals(List.of(), service.getShopIds("party-1", PaymentInstitutionRealm.live.name()));
        verify(dominantClient, times(2)).checkoutObject(any(), any());
    }

    private static VersionedObjectWithReferences partyWithReferences(DomainObject... references) {
        var referencedBy = Set.of(references).stream()
                .map(domainObject -> new VersionedObject().setObject(domainObject))
                .collect(Collectors.toSet());
        return new VersionedObjectWithReferences()
                .setReferencedBy(referencedBy);
    }

    private static DomainObject shop(String shopId, int paymentInstitutionId) {
        return DomainObject.shop_config(new ShopConfigObject()
                .setRef(new ShopConfigRef(shopId))
                .setData(new ShopConfig()
                        .setPaymentInstitution(new PaymentInstitutionRef(paymentInstitutionId))));
    }

    private static DomainObject wallet() {
        return DomainObject.wallet_config(new WalletConfigObject()
                .setRef(new WalletConfigRef("wallet-1"))
                .setData(new WalletConfig()));
    }

    private static VersionedObject paymentInstitution(int id, PaymentInstitutionRealm realm) {
        return new VersionedObject()
                .setObject(DomainObject.payment_institution(new PaymentInstitutionObject()
                        .setRef(new PaymentInstitutionRef(id))
                        .setData(new PaymentInstitution().setRealm(realm))));
    }
}
