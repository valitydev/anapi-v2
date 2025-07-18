package dev.vality.anapi.v2.config.properties;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Component
@Validated
@ConfigurationProperties(prefix = "service.bouncer")
public class BouncerProperties {

    @NotEmpty
    private String contextFragmentId;
    @NotEmpty
    private String deploymentId;
    @NotEmpty
    private String authMethod;
    @NotEmpty
    private String realm;
    @NotEmpty
    private String ruleSetId;
}
