package dev.vality.anapi.v2.config;

import dev.vality.anapi.v2.AnapiV2Application;
import dev.vality.anapi.v2.auth.utils.KeycloakOpenIdStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.wiremock.spring.EnableWireMock;

@SuppressWarnings("LineLength")
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = {AnapiV2Application.class},
        properties = {
                "spring.security.oauth2.resourceserver.url=${wiremock.server.baseUrl}",
                "spring.security.oauth2.resourceserver.jwt.issuer-uri=${wiremock.server.baseUrl}/auth/realms/" +
                        "${spring.security.oauth2.resourceserver.jwt.realm}"})
@AutoConfigureMockMvc
@EnableWireMock
@ExtendWith(SpringExtension.class)
public abstract class AbstractKeycloakOpenIdAsWiremockConfig {

    @Autowired
    private KeycloakOpenIdStub keycloakOpenIdStub;

    @BeforeEach
    public void setUp(@Autowired KeycloakOpenIdStub keycloakOpenIdStub) throws Exception {
        keycloakOpenIdStub.givenStub();
    }

    protected String generateSimpleJwt() {
        return keycloakOpenIdStub.generateJwt();
    }
}
