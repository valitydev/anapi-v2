package dev.vality.anapi.v2.config;

import dev.vality.woody.api.flow.WFlow;
import dev.vality.woody.api.trace.context.metadata.user.UserIdentityEmailExtensionKit;
import dev.vality.woody.api.trace.context.metadata.user.UserIdentityIdExtensionKit;
import dev.vality.woody.api.trace.context.metadata.user.UserIdentityRealmExtensionKit;
import dev.vality.woody.api.trace.context.metadata.user.UserIdentityUsernameExtensionKit;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static dev.vality.anapi.v2.util.DeadlineUtil.*;
import static dev.vality.woody.api.trace.ContextUtils.setCustomMetadataValue;
import static dev.vality.woody.api.trace.ContextUtils.setDeadline;

@Slf4j
@Configuration
@SuppressWarnings({"ParameterName", "LocalVariableName"})
public class WebConfig {

    @Bean
    public FilterRegistrationBean woodyFilter() {
        WFlow woodyFlow = new WFlow();
        Filter filter = new OncePerRequestFilter() {

            @Override
            protected void doFilterInternal(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain filterChain) {
                woodyFlow.createServiceFork(
                        () -> {
                            try {
                                var auth = SecurityContextHolder.getContext().getAuthentication();
                                if (auth instanceof JwtAuthenticationToken) {
                                    addWoodyContext((JwtAuthenticationToken) auth);
                                }
                                setWoodyDeadline(request);

                                filterChain.doFilter(request, response);
                            } catch (IOException | ServletException e) {
                                sneakyThrow(e);
                            }
                        }
                )
                        .run();
            }

            private <E extends Throwable, T> T sneakyThrow(Throwable t) throws E {
                throw (E) t;
            }
        };

        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(filter);
        filterRegistrationBean.setOrder(-50);
        filterRegistrationBean.setName("woodyFilter");
        filterRegistrationBean.addUrlPatterns("*");
        return filterRegistrationBean;
    }

    private void addWoodyContext(JwtAuthenticationToken token) {
        setCustomMetadataValue(UserIdentityIdExtensionKit.KEY, token.getToken().getClaimAsString(JwtClaimNames.SUB));
        setCustomMetadataValue(UserIdentityUsernameExtensionKit.KEY,
                ((Jwt)token.getPrincipal()).getClaimAsString("preferred_username"));
        setCustomMetadataValue(UserIdentityEmailExtensionKit.KEY, token.getToken().getClaimAsString("email"));
        setCustomMetadataValue(UserIdentityRealmExtensionKit.KEY, extractRealm(token.getToken()));
    }

    private String extractRealm(Jwt token) {
        var iss =  token.getClaimAsString("iss");
        return iss.substring(iss.lastIndexOf("/"));
    }

    private void setWoodyDeadline(HttpServletRequest request) {
        String xRequestDeadline = request.getHeader("X-Request-Deadline");
        String xRequestId = request.getHeader("X-Request-ID");
        if (xRequestDeadline != null) {
            try {
                setDeadline(getInstant(xRequestDeadline, xRequestId));
            } catch (Exception e) {
                log.warn("Unable to parse 'X-Request-Deadline' header value '{}'", xRequestDeadline);
            }
        }
    }

    private Instant getInstant(String xRequestDeadline, String xRequestId) {
        Instant instant;
        if (containsRelativeValues(xRequestDeadline, xRequestId)) {
            instant = Instant.now()
                    .plus(extractMilliseconds(xRequestDeadline, xRequestId), ChronoUnit.MILLIS)
                    .plus(extractSeconds(xRequestDeadline, xRequestId), ChronoUnit.MILLIS)
                    .plus(extractMinutes(xRequestDeadline, xRequestId), ChronoUnit.MILLIS);
        } else {
            instant = Instant.parse(xRequestDeadline);
        }
        return instant;
    }
}
