package io.chasen.gateway.web.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * @Program: csgateway
 * @Description: gateway post web filter
 * @Author: Chasen
 * @Create: 2024-05-26 14:39
 **/
@Slf4j
@Component
public class GatewayPostWebFilter implements WebFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return chain.filter(exchange).doFinally(
                s -> {
                    log.info(" ====> gateway post web filter");
                exchange.getAttributes().forEach((k,v) -> {
                        log.info("{} = {}", k, v);
                    });
                }
        );
    }
}
