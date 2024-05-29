package io.chasen.gateway.filter;

import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @Program: csgateway
 * @Description:
 * @Author: Chasen
 * @Create: 2024-05-29 21:34
 **/
@Component("defaultFilter")
public class DefaultFilter implements GatewayFilter{
    @Override
    public Mono<Void> filter(ServerWebExchange exchange) {
        exchange.getRequest().getHeaders()
                .forEach((k,v) -> System.out.println(k + ":" + v));
        return Mono.empty();
    }
}
