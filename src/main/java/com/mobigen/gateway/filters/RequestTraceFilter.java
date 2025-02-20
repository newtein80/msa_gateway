package com.mobigen.gateway.filters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.mobigen.gateway.filters.utils.FilterUtility;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * 마이크로서비스 간의 요청 추적이 가능하도록 모든 요청에 대해 correlation-id를 확인하고 없을 경우 생성 및 추가하는 필터
 */
@Slf4j
@Component
public class RequestTraceFilter implements GlobalFilter { // Spring Cloud Gateway의 전역 필터
    
    @Autowired
    private FilterUtility filterUtility;

    /**
     * ServerWebExchange: 요청 및 응답 객체를 포함하여 요청 처리를 갭슐화한 객체
     * GatewayFilterChain: 다음 필터로 요청 처리를 전달하는 역할
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        HttpHeaders requestHeaders = exchange.getRequest().getHeaders();
        if (isCorrelationIdPresent(requestHeaders)) { // correlation-id가 존재하는지 확인
            log.info("msa-correlation-id found in RequestTraceFilter : {}", filterUtility.getCorrelationId(requestHeaders));
        } else {
            String correlationID = generateCorrelationId();
            exchange = filterUtility.setCorrelationId(exchange, correlationID);
            log.info("msa-correlation-id generated in RequestTraceFilter : {}", correlationID);
        }
        return chain.filter(exchange);
    }

    /**
     * 헤더에서 CORRELATION_ID 존재 여부 확인
     * @param requestHeaders 요청 헤더
     * @return CORRELATION_ID 유무
     */
    private boolean isCorrelationIdPresent(HttpHeaders requestHeaders) {
        if (filterUtility.getCorrelationId(requestHeaders) != null) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Random 문자열 생성
     * @return
     */
    private String generateCorrelationId() {
        return java.util.UUID.randomUUID().toString();
    }
}
