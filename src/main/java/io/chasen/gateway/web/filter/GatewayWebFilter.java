package io.chasen.gateway.web.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * @Program: csgateway
 * @Description: gateway web filter
 * @Author: Chasen
 * @Create: 2024-05-26 14:34
 **/
@Component
@Slf4j
public class GatewayWebFilter implements WebFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        log.info(" ====>> CS Gateway web filter handler");
        if (exchange.getRequest().getQueryParams().getFirst("mock") == null) {
            return chain.filter(exchange);
        }
        String mock = """
                {"result":"mock"}
                """;
        // 7.组装返回响应报文
        return exchange.getResponse()
                .writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(mock.getBytes()))
                );
    }
}
