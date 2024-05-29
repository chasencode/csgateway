package io.chasen.gateway.handler;

import cn.chasen.rpc.core.api.LoadBalancer;
import cn.chasen.rpc.core.api.RegistryCenter;
import cn.chasen.rpc.core.cluster.RoundRibonLoadBalancer;
import cn.chasen.rpc.core.meta.InstanceMeta;
import cn.chasen.rpc.core.meta.ServiceMeta;
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
    RegistryCenter rc;

    LoadBalancer<InstanceMeta> loadBalancer = new RoundRibonLoadBalancer<>();

    @Override
    public Mono<Void> handle(ServerWebExchange exchange) {
        log.info(" ====>> CS Gateway web handler");

        // 1. 通过请求路径或者获取服务名称
        String service = exchange.getRequest().getPath().value().substring(4);
        System.out.println("service: " + service);
        // app=app1, namespace=public, env=dev, name=cn.chasen.rpc.demo.api.UserService
        ServiceMeta serviceMeta = ServiceMeta.builder()
                .app("app1").env("dev").name(service).namespace("public").build();
        // 2. 从注册中心获取服务实例
        List<InstanceMeta> instanceMetas = rc.fetchAll(serviceMeta);
        // 3. 选择一个服务实例进行请求
        InstanceMeta choose = loadBalancer.choose(instanceMetas);
        String url =  choose.toUrl();;
//        String url = instanceMetas.get(0).toUrl();
        System.out.println("url: " + url);
        // 4. 拿到请求报文
        Flux<DataBuffer> requestBody = exchange.getRequest().getBody();


        // 5. 通过webclient post  发送请求
        WebClient client = WebClient.create(url);
        Mono<ResponseEntity<String>> entity = client.post()
                .header("Content-Type", "application/json")
                .body(requestBody, DataBuffer.class)
                .retrieve()
                .toEntity(String.class);
        // 6.通过entity获取响报文
        Mono<String> body = entity.map(ResponseEntity::getBody);
        exchange.getResponse().getHeaders().add("Content-Type", "application/json");        
        exchange.getResponse().getHeaders().add("cs.gw.version", "v1.0.0");
        // 7.组装返回响应报文
        return body.flatMap(x->exchange.getResponse()
                .writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(x.getBytes())))
        );
    }
    
}
