package com.mobigen.gateway.filters;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class CustomGlobalFilter extends AbstractGatewayFilterFactory<CustomGlobalFilter.Config> {

    public static class Config{}

    // // 처리시간 측정을 위해 StopWatch 선언
    // private StopWatch stopWatch;

    public CustomGlobalFilter() {
        super(Config.class);
        // stopWatch = new StopWatch("API Gateway");
    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            // PRE
            StringBuilder preSb = new StringBuilder();
            // stopWatch.start();
            preSb.append("\n[Global Filter] Request >> IP: " + request.getRemoteAddress().getAddress() + ", URI: " + request.getURI());

            // POST

            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                // stopWatch.stop();

                preSb.append("\n[Global Filter] Response << IP: " + request.getRemoteAddress().getAddress() +
                ", URI: " + request.getURI() +
                ", Code: " + response.getStatusCode());
                // "... Duration: " + stopWatch.getLastTaskTimeMillis() + " ms");

                log.info(preSb.toString());
                preSb.setLength(0);
            }));
        });
    }
    
}
