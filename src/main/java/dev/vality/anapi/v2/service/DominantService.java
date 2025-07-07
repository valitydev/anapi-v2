package dev.vality.anapi.v2.service;

import dev.vality.anapi.v2.exception.DominantException;
import dev.vality.damsel.domain.*;
import dev.vality.damsel.domain_config_v2.RepositoryClientSrv;
import dev.vality.damsel.domain_config_v2.VersionReference;
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
            List<ShopConfigRef> shopsRefs = getShopConfigRefs(partyId);
            List<ShopConfigObject> shopsObjects = getShopConfigObjects(shopsRefs);
            var shopIds = shopsObjects.stream()
                    .filter(shopConfigObject -> realmMatches(realm, shopConfigObject))
                    .map(shopConfigObject -> shopConfigObject.getData().getId()).toList();
            log.info("Found {} shops, partyId={}, realm={}", shopIds.size(), partyId, realm);
            return shopIds;
        } catch (TException e) {
            throw new DominantException(String.format("Error while call dominant, partyId=%s", partyId), e);
        }
    }

    private List<ShopConfigRef> getShopConfigRefs(String partyId) throws TException {
        var partyReference = Reference.party_config(new PartyConfigRef(partyId));
        var party = dominantClient.checkoutObject(new VersionReference(), partyReference);
        return party.getObject().getPartyConfig().getData().getShops();
    }

    private List<ShopConfigObject> getShopConfigObjects(List<ShopConfigRef> shopConfigRefs) throws TException {
        var shopConfigs = dominantClient.checkoutObjects(new VersionReference(),
                shopConfigRefs.stream().map(Reference::shop_config).toList());
        return shopConfigs.stream().map(domainObject -> domainObject.getObject().getShopConfig()).toList();
    }

    private boolean realmMatches(String expectedRealm, ShopConfigObject shopConfigObject) {
        int realmId = shopConfigObject.getData().getPaymentInstitution().getId();
        var paymentInstitutionRealm =
                PaymentInstitutionRealm.findByValue(realmId);
        return expectedRealm.equals(paymentInstitutionRealm.name());
    }
}
