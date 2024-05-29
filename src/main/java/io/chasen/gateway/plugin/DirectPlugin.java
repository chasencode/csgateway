package io.chasen.gateway.plugin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @Program: csgateway
 * @Description: direct proxy plugin
 * @Author: Chasen
 * @Create: 2024-05-29 20:27
 **/
@Component("direct")
@Slf4j
public class DirectPlugin extends AbstractGatewayPlugin {

    public static final String NAME = "direct";

    private String prefix = GATEWAY_PREFIX + "/" + NAME;
    @Override
    public Mono<Void> doHandle(ServerWebExchange exchange) {
        log.info(" ====>> Direct plugin");
        String backend = exchange.getRequest().getQueryParams().getFirst("backend");
        Flux<DataBuffer> requestBody = exchange.getRequest().getBody();

        exchange.getResponse().getHeaders().add("Content-Type", "application/json");
        exchange.getResponse().getHeaders().add("cs.gw.version", "v1.0.0");

        if (backend == null || backend.isEmpty()) {
            return requestBody.flatMap(x -> exchange.getResponse().writeWith(Mono.just(x))).then();
        }

        // 5. 通过webclient post  发送请求
        WebClient client = WebClient.create(backend);
        Mono<ResponseEntity<String>> entity = client.post()
                .header("Content-Type", "application/json")
                .body(requestBody, DataBuffer.class)
                .retrieve()
                .toEntity(String.class);
        // 6.通过entity获取响报文
        Mono<String> body = entity.map(ResponseEntity::getBody);
        // 7.组装返回响应报文
        return body.flatMap(x->exchange.getResponse()
                .writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(x.getBytes())))
        );
    }

    @Override
    public boolean doSupport(ServerWebExchange exchange) {
        return false;
    }

    @Override
    public String getName() {
        return null;
    }
}
