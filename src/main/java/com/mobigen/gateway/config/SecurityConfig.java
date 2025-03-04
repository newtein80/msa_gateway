package com.mobigen.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Configuration
public class SecurityConfig {
    // }
    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity serverHttpSecurity) {
        serverHttpSecurity.authorizeExchange(
            exchanges -> //exchanges.pathMatchers(HttpMethod.GET).permitAll()
            exchanges.pathMatchers("/mobigen/accounts/**").hasRole("ACCOUNTS")
                .pathMatchers("/mobigen/cards/**").hasRole("CARDS")
                .pathMatchers("/mobigen/loans/**").hasRole("LOANS"))
                .oauth2ResourceServer(oAuth2ResourceServerSpec -> oAuth2ResourceServerSpec
                        .jwt(jwtSpec -> jwtSpec.jwtAuthenticationConverter(grantedAuthoritiesExtractor())));
        serverHttpSecurity.csrf(csrfSpec -> csrfSpec.disable());
        return serverHttpSecurity.build();
    }

    // 모든 요청을 로깅하는 WebFilter 추가
    // @Bean
    // public WebFilter securityLoggingFilter() {
    //     return (ServerWebExchange exchange, WebFilterChain chain) -> {
    //         log.info("Request URI: {}", exchange.getRequest().getURI());
    //         return chain.filter(exchange)
    //             .doOnSuccess(aVoid -> log.info("Response Status: {}", exchange.getResponse().getStatusCode()))
    //             .doOnError(throwable -> log.error("Error occurred: ", throwable));
    //     };
    // }

    private Converter<Jwt, Mono<AbstractAuthenticationToken>> grantedAuthoritiesExtractor() {
        JwtAuthenticationConverter jwtAuthenticationConverter =
                new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter
                (new KeycloakRoleConverter());
        return new ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter);
    }
}
