package com.rbkmoney.anapi.v2.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "bouncer")
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
