package com.rbkmoney.anapi.v2.service;

import com.rbkmoney.bouncer.ctx.ContextFragment;
import com.rbkmoney.orgmanagement.AuthContextProviderSrv;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class OrgMgmtService {

    private final AuthContextProviderSrv.Iface orgMgmtClient;

    public ContextFragment getUserAuthContext(String userId) {
        try {
            return orgMgmtClient.getUserContext(userId);
        } catch (Exception e) {
            throw new RuntimeException(String.format("Can't get user auth context: userId = %s", userId), e);
        }
    }

}
