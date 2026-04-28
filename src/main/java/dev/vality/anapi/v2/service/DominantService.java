package dev.vality.anapi.v2.service;

import dev.vality.anapi.v2.exception.DominantException;
import dev.vality.damsel.domain.*;
import dev.vality.damsel.domain_config_v2.Head;
import dev.vality.damsel.domain_config_v2.RepositoryClientSrv;
import dev.vality.damsel.domain_config_v2.VersionReference;
import dev.vality.damsel.domain_config_v2.VersionedObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TException;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DominantService {

    private final RepositoryClientSrv.Iface dominantClient;

    public List<String> getShopIds(String partyId, String realm) {
        try {
            log.info("Looking for shops, partyId={}, realm={}", partyId, realm);
            List<ShopConfigObject> shopsObjects = getShopConfigObjects(partyId);
            var shopIds = shopsObjects.stream()
                    .filter(shopConfigObject -> realmMatches(realm, shopConfigObject))
                    .map(shopConfigObject -> shopConfigObject.getRef().getId()).toList();
            log.info("Found {} shops, partyId={}, realm={}", shopIds.size(), partyId, realm);
            return shopIds;
        } catch (TException e) {
            throw new DominantException(String.format("Error while call dominant, partyId=%s", partyId), e);
        }
    }

    private List<ShopConfigObject> getShopConfigObjects(String partyId) throws TException {
        var partyReference = Reference.party_config(new PartyConfigRef(partyId));
        var versionedObjectWithReferences =
                dominantClient.checkoutObjectWithReferences(VersionReference.head(new Head()), partyReference);
        var shopConfigObjects = versionedObjectWithReferences.getReferencedBy().stream()
                .map(VersionedObject::getObject)
                .filter(DomainObject::isSetShopConfig)
                .map(DomainObject::getShopConfig)
                .toList();
        log.debug("Receive shops for partyId: {}, shopConfigObjects ='{}'", partyId, shopConfigObjects);
        return shopConfigObjects;
    }

    private boolean realmMatches(String expectedRealm, ShopConfigObject shopConfigObject) {
        int realmId = shopConfigObject.getData().getPaymentInstitution().getId();
        var paymentInstitutionRealm =
                PaymentInstitutionRealm.findByValue(realmId);
        return expectedRealm.equals(paymentInstitutionRealm.name());
    }
}
