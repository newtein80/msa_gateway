package com.mobigen.gateway.filters.utils;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

@Component
public class FilterUtility {

    public static final String CORRELATION_ID = "msa-correlation-id";

    /**
     * 요청 헤더에서 correlation-id를 검색하여 반환
     * @param requestHeaders 요청 헤더
     * @return CORRELATION_ID
     */
    public String getCorrelationId(HttpHeaders requestHeaders) {
        if (requestHeaders.get(CORRELATION_ID) != null) {
            List<String> requestHeaderList = requestHeaders.get(CORRELATION_ID);
            return requestHeaderList.stream().findFirst().get();
        } else {
            return null;
        }
    }

    /**
     * exchange.getRequest().mutate()를 사용하여 요청 객체를 수정
     * @param exchange 요청 객체
     * @param name
     * @param value
     * @return
     */
    public ServerWebExchange setRequestHeader(ServerWebExchange exchange, String name, String value) {
        return exchange.mutate().request(exchange.getRequest().mutate().header(name, value).build()).build();
    }

    /**
     * 요청 헤더에 CORRELATION_ID 값을 추가
     * @param exchange
     * @param correlationId
     * @return
     */
    public ServerWebExchange setCorrelationId(ServerWebExchange exchange, String correlationId) {
        return this.setRequestHeader(exchange, CORRELATION_ID, correlationId);
    }

}
