package com.rbkmoney.anapi.v2.service;

import com.rbkmoney.anapi.v2.exception.OrgManagerException;
import com.rbkmoney.bouncer.ctx.ContextFragment;
import com.rbkmoney.orgmanagement.AuthContextProviderSrv;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class OrgManagerService {

    private final AuthContextProviderSrv.Iface orgManagerClient;

    public ContextFragment getUserAuthContext(String userId) {
        try {
            return orgManagerClient.getUserContext(userId);
        } catch (Exception e) {
            throw new OrgManagerException(
                    String.format("Can't get user auth context on orgManager call: userId = %s", userId),
                    e);
        }
    }
}
