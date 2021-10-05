package com.rbkmoney.anapi.v2.config;

import com.rbkmoney.anapi.v2.AnapiV2Application;
import com.rbkmoney.anapi.v2.auth.utils.KeycloakOpenIdStub;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = {AnapiV2Application.class},
        properties = {
                "wiremock.server.baseUrl=http://localhost:${wiremock.server.port}",
                "keycloak.auth-server-url=${wiremock.server.baseUrl}/auth"})
@AutoConfigureMockMvc
@AutoConfigureWireMock(port = 0)
@ExtendWith(SpringExtension.class)
public abstract class AbstractKeycloakOpenIdAsWiremockConfig {

    @Autowired
    private KeycloakOpenIdStub keycloakOpenIdStub;

    @BeforeAll
    public static void setUp(@Autowired KeycloakOpenIdStub keycloakOpenIdStub) throws Exception {
        keycloakOpenIdStub.givenStub();
    }

    protected String generateInvoicesReadJwt() {
        return keycloakOpenIdStub.generateJwt("invoices:read");
    }
}
