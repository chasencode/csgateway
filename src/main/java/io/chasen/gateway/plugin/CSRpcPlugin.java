package io.chasen.gateway.plugin;

import cn.chasen.rpc.core.api.LoadBalancer;
import cn.chasen.rpc.core.api.RegistryCenter;
import cn.chasen.rpc.core.cluster.RoundRibonLoadBalancer;
import cn.chasen.rpc.core.meta.InstanceMeta;
import cn.chasen.rpc.core.meta.ServiceMeta;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @Program: csgateway
 * @Description:
 * @Author: Chasen
 * @Create: 2024-05-29 20:22
 **/
@Component("csrpc")
@Slf4j
public class CSRpcPlugin extends AbstractGatewayPlugin{
    public static final String NAME = "csrpc";

    private String prefix = GATEWAY_PREFIX + "/" + NAME;

    @Autowired
    RegistryCenter rc;

    LoadBalancer<InstanceMeta> loadBalancer = new RoundRibonLoadBalancer<>();

    @Override
    public Mono<Void> doHandle(ServerWebExchange exchange) {
        log.info(" ====>> CSRPC Gateway plugin");

        // 1. 通过请求路径或者获取服务名称
        String service = exchange.getRequest().getPath().value().substring(prefix.length());
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
        exchange.getResponse().getHeaders().add("cs.gw.plugin", getName());
        // 7.组装返回响应报文
        return body.flatMap(x->exchange.getResponse()
                .writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(x.getBytes())))
        );
    }

    @Override
    public boolean doSupport(ServerWebExchange exchange) {
        return exchange.getRequest().getPath().value().startsWith(prefix);
    }

    @Override
    public String getName() {
        return NAME;
    }
}
