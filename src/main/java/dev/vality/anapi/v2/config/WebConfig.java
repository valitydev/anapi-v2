package dev.vality.anapi.v2.config;

import dev.vality.woody.api.flow.WFlow;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static dev.vality.anapi.v2.util.DeadlineUtil.*;
import static dev.vality.woody.api.trace.ContextUtils.setDeadline;

@Configuration
@SuppressWarnings({"ParameterName", "LocalVariableName"})
public class WebConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOriginPatterns("*");
            }
        };
    }

    @Bean
    public FilterRegistrationBean woodyFilter() {
        WFlow woodyFlow = new WFlow();
        Filter filter = new OncePerRequestFilter() {

            @Override
            protected void doFilterInternal(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain filterChain) throws ServletException, IOException {
                woodyFlow.createServiceFork(
                                () -> {
                                    try {
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

    private void setWoodyDeadline(HttpServletRequest request) {
        String xRequestDeadline = request.getHeader("X-Request-Deadline");
        String xRequestId = request.getHeader("X-Request-ID");
        if (xRequestDeadline != null) {
            setDeadline(getInstant(xRequestDeadline, xRequestId));
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
