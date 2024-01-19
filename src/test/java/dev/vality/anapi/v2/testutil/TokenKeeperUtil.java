package dev.vality.anapi.v2.testutil;

import dev.vality.token.keeper.AuthData;
import dev.vality.token.keeper.AuthDataStatus;
import lombok.experimental.UtilityClass;

import java.util.UUID;

import static dev.vality.anapi.v2.testutil.BouncerUtil.createContextFragment;

@UtilityClass
public class TokenKeeperUtil {

    public static AuthData createAuthData(String token) {
        return new AuthData()
                .setId(UUID.randomUUID().toString())
                .setAuthority(UUID.randomUUID().toString())
                .setToken(token)
                .setStatus(AuthDataStatus.active)
                .setContext(createContextFragment());
    }


}
