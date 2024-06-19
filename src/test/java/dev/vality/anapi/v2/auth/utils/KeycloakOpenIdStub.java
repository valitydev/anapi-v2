package dev.vality.anapi.v2.auth.utils;

public class KeycloakOpenIdStub {

    private final String issuer;
    private final JwtTokenBuilder jwtTokenBuilder;

    public KeycloakOpenIdStub(JwtTokenBuilder jwtTokenBuilder) {
        this.jwtTokenBuilder = jwtTokenBuilder;
        this.issuer = "test/realms/test";
    }

    public String generateJwt(String... roles) {
        return jwtTokenBuilder.generateJwtWithRoles(issuer, roles);
    }
}
