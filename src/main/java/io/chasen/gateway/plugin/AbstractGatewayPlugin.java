package io.chasen.gateway.plugin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @Program: csgateway
 * @Description:
 * @Author: Chasen
 * @Create: 2024-05-29 20:15
 **/
@Slf4j
abstract class AbstractGatewayPlugin implements GatewayPlugin{
    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, GatewayPluginChain chain) {
        boolean isSupported = support(exchange);
        log.info("====> plugin[{}], support={}",this.getName(),  isSupported);
        return isSupported ? doHandle(exchange, chain) : chain.handle(exchange);
    }

    @Override
    public boolean support(ServerWebExchange exchange) {
        return doSupport(exchange);
    }

    public abstract Mono<Void> doHandle(ServerWebExchange exchange, GatewayPluginChain chain);

    public abstract boolean doSupport(ServerWebExchange exchange);
}
