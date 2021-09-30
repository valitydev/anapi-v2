package com.rbkmoney.anapi.v2.security;

public interface AccessService {

    void checkAccess(String operationId, String partyId, String shopId);

}
