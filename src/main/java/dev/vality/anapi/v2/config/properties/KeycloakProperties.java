package dev.vality.anapi.v2.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@Component
@Validated
@ConfigurationProperties(prefix = "keycloak")
public class KeycloakProperties {

    @NotEmpty
    private String realm;
    @NotEmpty
    private String authServerUrl;
    @NotEmpty
    private String resource;

    private Integer notBefore;

    @NotEmpty
    private String sslRequired;

    private String realmPublicKey;

    private String realmPublicKeyPath;

}
