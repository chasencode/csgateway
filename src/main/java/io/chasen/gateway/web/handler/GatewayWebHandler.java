package io.chasen.gateway.handler;

import cn.chasen.rpc.core.api.LoadBalancer;
import cn.chasen.rpc.core.api.RegistryCenter;
import cn.chasen.rpc.core.cluster.RoundRibonLoadBalancer;
import cn.chasen.rpc.core.meta.InstanceMeta;
import cn.chasen.rpc.core.meta.ServiceMeta;
import io.chasen.gateway.plugin.GatewayPlugin;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebHandler;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @Program: csgateway
 * @Description: gateway web handler
 * @Author: Chasen
 * @Create: 2024-05-26 13:39
 **/
@Component("gatewayWebHandler")
@Slf4j
public class GatewayWebHandler implements WebHandler {

    @Autowired
    List<GatewayPlugin> plugins;

    @Override
    public Mono<Void> handle(ServerWebExchange exchange) {
        log.info("====> CS Gateway web handler");
        if (plugins == null || plugins.isEmpty()) {
            String mock = """
                    {"result":"not plugin"}
                    """;
            return exchange.getResponse().writeWith(Flux.just(
                    exchange.getResponse().bufferFactory().wrap(mock.getBytes())
            ));
        }

        for (GatewayPlugin plugin : plugins) {
            if (plugin.support(exchange)) {
                return plugin.handle(exchange);
            }
        }

        String mock = """
                    {"result":"no support plugin"}
                    """;
        return exchange.getResponse().writeWith(Flux.just(
                exchange.getResponse().bufferFactory().wrap(mock.getBytes())
        ));
    }
    
}
