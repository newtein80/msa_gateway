package com.mobigen.gateway.filters.service;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class AccountsServiceCustomFilter extends AbstractGatewayFilterFactory<AccountsServiceCustomFilter.Config> {

    public static class Config{}

    // 처리시간 측정을 위해 StopWatch 선언
    private StopWatch stopWatch;

    public AccountsServiceCustomFilter() {
        super(Config.class);
        stopWatch = new StopWatch("Account Filter");
    }

    @SuppressWarnings("deprecation")
    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            // PRE
            StringBuilder preSb = new StringBuilder();
            stopWatch.start();
            preSb.append("\n[Custom Filter] Request >> IP: " + request.getRemoteAddress().getAddress() + ", URI: " + request.getURI());
            request.getHeaders().forEach((key, value) -> {
                preSb.append("\n\t[Request Header] " + key + ": " + value);
            });
            // POST

            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                stopWatch.stop();
                preSb.append("\n---------------------------------------------------------------------------");

                response.getHeaders().forEach((key, value) -> {
                    preSb.append("\n\t[Response Header] " + key + ": " + value);
                });

                preSb.append("\n[Custom Filter] Response << IP: " + request.getRemoteAddress().getAddress() +
                ", URI: " + request.getURI() +
                ", Code: " + response.getStatusCode() +
                "... Duration: " + stopWatch.getLastTaskTimeMillis() + " ms");

                log.info(preSb.toString());
                preSb.setLength(0);
            }));
        });
    }
    
}
