package io.chasen.gateway.plugin;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @Program: csgateway
 * @Description:
 * @Author: Chasen
 * @Create: 2024-05-29 21:07
 **/
public class DefaultGatewayPluginChain implements GatewayPluginChain {

    List<GatewayPlugin> plugins;

    int index = 0;

    public DefaultGatewayPluginChain(List<GatewayPlugin> plugins) {
        this.plugins = plugins;
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange) {
        return Mono.defer(() -> {
            if (index < plugins.size()) {
                GatewayPlugin plugin = plugins.get(index);
                index++;
                return plugin.handle(exchange, this);
            }
            //        for (GatewayPlugin plugin : plugins) {
//            if (plugin.support(exchange)) {
//                return plugin.handle(exchange);
//            }
//        }

            String mock = """
                    {"result":"no support plugin"}
                    """;
            return exchange.getResponse().writeWith(Flux.just(
                    exchange.getResponse().bufferFactory().wrap(mock.getBytes())
            ));
        });
    }
}
