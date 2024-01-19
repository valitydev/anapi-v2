package dev.vality.anapi.v2.auth;

import dev.vality.anapi.v2.auth.utils.JwtTokenBuilder;
import dev.vality.anapi.v2.auth.utils.KeycloakOpenIdStub;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakOpenIdTestConfiguration {

    @Bean
    public KeycloakOpenIdStub keycloakOpenIdStub(JwtTokenBuilder jwtTokenBuilder) {
        return new KeycloakOpenIdStub(jwtTokenBuilder);
    }
}
