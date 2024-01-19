package dev.vality.anapi.v2.config;

import dev.vality.anapi.v2.AnapiV2Application;
import dev.vality.anapi.v2.auth.utils.KeycloakOpenIdStub;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = {AnapiV2Application.class},
        properties = {"wiremock.server.baseUrl=http://localhost:${wiremock.server.port}"})
@AutoConfigureMockMvc
@AutoConfigureWireMock(port = 0)
@ExtendWith(SpringExtension.class)
public abstract class AbstractConfig {

    @Autowired
    private KeycloakOpenIdStub keycloakOpenIdStub;

    protected String generateSimpleJwt() {
        return keycloakOpenIdStub.generateJwt();
    }
}
