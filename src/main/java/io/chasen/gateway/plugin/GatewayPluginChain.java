package io.chasen.gateway.plugin;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @Program: csgateway
 * @Description:
 * @Author: Chasen
 * @Create: 2024-05-29 21:05
 **/
interface GatewayPluginChain {

    Mono<Void> handle(ServerWebExchange exchange);
}
